package pir
package pass

import pir.node._
import prism.graph._
import spade.param._

class MemoryLowering(implicit compiler:PIR) extends BufferAnalyzer {

  override def runPass = {
    pirTop.collectDown[Memory]().foreach(lowerMem)
  }

  def lowerMem(mem:Memory):Unit = dbgblk(s"lowerMem($mem)"){
    val accesses = mem.accesses
    accesses.foreach { access =>
      dbg(s"access=$access order=${access.order.v}")
    }
    var cannotToBuffer = accesses.exists { _.isInstanceOf[BanckedAccess] }
    // If read access is branch dependent, the ctx cannot block on the input for its activation
    cannotToBuffer |= mem.outAccesses.exists { _.en.T.nonEmpty }
    cannotToBuffer |= mem.inAccesses.size > 1
    if (mem.isFIFO) cannotToBuffer |= mem.outAccesses.size > 1
    if (cannotToBuffer) {
      createMemCtx(mem)
    } else {
      lowerToBuffer(mem)
    }
  }

  def createMemCtx(mem:Memory) = {
    val memCU = within(mem.parent.get.as[PIRNode]) { MemoryContainer() }
    within(memCU) {
      // Create Memory Context
      swapParent(mem, memCU)
      val accesses = mem.accesses
      accesses.foreach { access =>
        access.getVec
        val accessCtx = Context()
        swapParent(access, accessCtx)
        access match {
          case access:BankedRead => 
            flattenBankAddr(access)
            bufferOutput(access.out)
          case access:BankedWrite => 
            flattenBankAddr(access)
            //bufferInput(access.bank)
            //bufferInput(access.offset)
            bufferInput(access.data)
          case access:MemRead =>
            bufferOutput(access.out)
          case access:MemWrite =>
            bufferInput(access.data)
            val writeEns = access.en.T
            dbg(s"writeEns=$writeEns")
            val fromValid = writeEns.forall { case en:CounterValid => true }
            if (!fromValid) bufferInput(access.en)
        }
      }
      sequencedScheduleBarrierInsertion(mem)
      multiBufferBarrierInsertion(mem)
      fifoBarrierInsertion(mem)
      //enforceProgramOrder(mem)
      enforceDataDependency(mem)
    }
  }

  def flattenBankAddr(access:BanckedAccess):Unit = {
    if (access.bank.T.size == 1) return
    val mem = access.mem.T
    within(access.parent.get.as[PIRNode]) {
      def flattenND(inds:List[Edge], dims:List[Int]):Edge = {
        if (inds.size==1) return inds.head
        assert(inds.size == dims.size, s"flattenND inds=$inds dims=$dims have different size")
        val i::irest = inds
        val d::drest = dims
        OpDef(FixFMA).input(i,Const(drest.product), flattenND(irest, drest)).out
      }
      val fbank = flattenND(access.bank.connected.toList, mem.banks.get)
      dbg(s"flattenBankAddr ${access.bank.T} => $fbank")
      access.bank.disconnect
      access.bank(fbank)
    }
  }

    // Insert token for sequencial control dependency
  def sequencedScheduleBarrierInsertion(mem:Memory) = {
    dbgblk(s"sequencedScheduleBarrierInsertion($mem)") {
      val ctrls = mem.accesses.flatMap { a => a.getCtrl :: a.getCtrl.ancestors }.distinct.asInstanceOf[Seq[ControlTree]]
      ctrls.foreach { ctrl =>
        if (ctrl.schedule == "Sequenced") {
          val accesses = ctrl.children.flatMap { c => 
            val childCtrl = c.as[ControlTree]
            val childAccesses = mem.accesses.filter { a => 
              a.getCtrl.isDescendentOf(childCtrl) || a.getCtrl == childCtrl
            }
            if (childAccesses.nonEmpty) Some((childCtrl, childAccesses)) else None
          }
          if (accesses.nonEmpty) {
            dbgblk(s"Insert token for sequenced schedule of $ctrl") {
              accesses.sliding(2, 1).foreach{
                case List((fromCtrl, from), (toCtrl, to)) =>
                  from.foreach { fromAccess =>
                    to.foreach { toAccess =>
                      dbg(s"Insert token between $fromAccess ($fromCtrl) and $toAccess ($toCtrl)")
                      insertToken(fromAccess.ctx.get, toAccess.ctx.get, fromCtrl, toCtrl).depth(1)
                    }
                  }
                case _ =>
              }
            }
          }
        }
      }
    }
  }

  def multiBufferBarrierInsertion(mem:Memory):Unit = {
    if (mem.depth.get == 1) return
    dbgblk(s"multiBufferBarrierInsertion($mem)") {
      val accesses = mem.accesses.filter { _.port.nonEmpty }
      val ctrlMap = leastMatchedPeers(accesses.map { _.getCtrl} ).get
      // Connect access.done
      accesses.foreach { access =>
        val ctrl = ctrlMap(access.getCtrl).as[ControlTree]
        access.done(ctrlDone(ctrl, access.ctx.get))
      }
      val portMap = mem.accesses.groupBy { access =>
        access.port.v.get.get
      }
      val portIds = portMap.keys.toList.sorted
      portIds.sliding(2,1).foreach {
        case List(fromid, toid) =>
          portMap(fromid).foreach { fromAccess =>
            portMap(toid).foreach { toAccess =>
              dbg(s"Insert token for multibuffer between $fromAccess and $toAccess")
              val token = insertToken(
                fromAccess.ctx.get, 
                toAccess.ctx.get, 
                ctrlMap(fromAccess.getCtrl).as[ControlTree], 
                ctrlMap(toAccess.getCtrl).as[ControlTree]
              )
              val depth = toid - fromid + 1
              dbg(s"$token.depth = $depth")
              token.depth(depth)
            }
          }
        case _ =>
      }
    }
  }

  def enforceDataDependency(mem:Memory):Unit = dbgblk(s"enforceDataDependency($mem)"){
    val accesses = mem.accesses.filter { _.port.nonEmpty }
    accesses.groupBy { _.port.get }.foreach { case (port, accesses) =>
      val (inAccesses, outAccesses) =  accesses.partition { _.isInstanceOf[InAccess] }
      inAccesses.foreach { inAccess =>
        outAccesses.foreach { outAccess =>
          dbg(s"Insert token for data dependency between $inAccess and $outAccess")
          val token = insertToken(
            inAccess.ctx.get, 
            outAccess.ctx.get, 
            inAccess.getCtrl.as[ControlTree], 
            outAccess.getCtrl.as[ControlTree]
          )
          if (token.depth.isEmpty) {
            token.depth(1)
          }
          if (inAccess.order.get > outAccess.order.get) {
            dbg(s"$token.initToken = true")
            token.initToken := true
          }
        }
      }
    }
  }

  def fifoBarrierInsertion(mem:Memory):Unit = {
    if (!mem.isFIFO) return
    dbgblk(s"fifoBarrierInsertion($mem)") {
      val w = assertOne(mem.inAccesses, s"$mem.inAccesses")
      val r = assertOne(mem.outAccesses, s"$mem.outAccesses")
      insertToken(w.ctx.get,r.ctx.get,w.getCtrl, r.getCtrl)
    }
  }

  def enforceProgramOrder(mem:Memory) = {
    dbgblk(s"enforceProgramOrder($mem)") {
      val accesses = mem.accesses
       //Insert token between accesses based on program order
      val sorted = accesses.sortBy { _.order.get }
      sorted.sliding(2, 1).foreach {
        case List(a, b) => insertToken(a.ctx.get,b.ctx.get,a.getCtrl, b.getCtrl)
        case List(a) =>
      }
       //Insert token for loop carried dependency
      val lcaCtrl = leastCommonAncesstor(accesses.map(_.ctrl.get)).get
      (lcaCtrl::lcaCtrl.descendents).foreach { ctrl =>
        if (ctrl.as[ControlTree].ctrler.get.isInstanceOf[LoopController]) {
          val accesses = sorted.filter { a => a.ctrl.get.isDescendentOf(ctrl) || a.ctrl == ctrl }
          if (accesses.nonEmpty) {
            dbg(s"$ctrl accesses = ${accesses}")
            zipOption(accesses.head.to[ReadAccess], accesses.last.to[WriteAccess]).foreach { case (r, w) =>
              val token = insertToken(w.ctx.get, r.ctx.get, w.getCtrl, r.getCtrl)
              dbg(s"$token.initToken = true")
              token.initToken := true
            }
          }
        }
      }
    }
  }

  def lowerToBuffer(mem:Memory) = {
    dbg(s"Lower $mem to InputBuffer")
    mem.outAccesses.foreach { outAccess =>
      within(outAccess.parent.get.as[PIRNode]) {
        val inAccess = mem.inAccesses.head.as[MemWrite]
        val (enq, deq) = compEnqDeq(inAccess.ctrl.get, outAccess.ctrl.get, mem.isFIFO, inAccess.ctx.get, outAccess.ctx.get)
        val write = within(inAccess.parent.get.as[PIRNode], inAccess.ctrl.get) {
          allocate[BufferWrite]{ write => 
            write.data.evalTo(inAccess.data.neighbors) &&
            write.en.evalTo(inAccess.en.neighbors) && 
            write.done.evalTo(enq)
          } {
            val write = BufferWrite().data(inAccess.data.connected).mirrorMetas(inAccess).en(inAccess.en.T).done(enq)
            dbg(s"create $write.data(${inAccess.data.neighbors}).done(${write.done.T})")
            write
          }
        }
        val read = within(outAccess.parent.get.as[PIRNode], outAccess.ctrl.get) {
          BufferRead(mem.isFIFO).in(write.out).mirrorMetas(mem).mirrorMetas(outAccess).done(deq)
        }
        dbg(s"create $read.in(${write}).done($deq)")
        if (inAccess.order.get > outAccess.order.get ) {
          dbg(s"$read.initToken = true")
          read.initToken := true
        }
        outAccess.depeds.foreach { deped =>
          swapInput(deped, outAccess.as[Def].out, read.out)
        }
      }
    }
  }

}
