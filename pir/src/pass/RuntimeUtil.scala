package pir
package pass

import pir.node._

trait RuntimeUtil extends ConstantPropogator { self:PIRPass =>
  import pirmeta._
  import spademeta._

  def minByWithBound[A,B:Ordering](list:Iterable[A], bound:B)(lambda:A => B):B = {
    list.foldLeft[Option[B]](None) { 
      case (Some(`bound`), x) => Some(bound)
      case (Some(currMin), x) => Some(List(currMin, lambda(x)).min)
      case (None, x) => Some(lambda(x))
    }.getOrElse(bound)
  }

  def getParOf(x:Controller):Int = parOf.getOrElseUpdate(x) {
    dbgblk(s"getParOf($x)") {
      x match {
        case x:UnitController => 1
        case x:TopController => 1
        case x:LoopController => getParOf(x.cchain)
        case x:ArgInController => 1
        case x:ArgOutController => 1
        case DramController(size, par) => par
        case x:StreamController => 1
      }
    }
  }

  /*
   * For controller, itersOf is the number of iteration the current controller runs before saturate
   * */
  def getItersOf(n:Controller):Option[Long] = itersOf.getOrElseUpdate(n) {
    dbgblk(s"getItersOf(${quote(n)})") {
      n match {
        case x:UnitController => Some(1)
        case x:TopController => Some(1)
        case x:LoopController => getItersOf(x.cchain)
        case x:ArgInController => Some(1)
        case x:ArgOutController => Some(1)
        case DramController(size, par) => 
          size.map { size =>
            val wordSize = size / 4
            wordSize / par
          }
        case x:StreamController => None
      }
    }
  }

  def getCountsOf(n:Controller):Option[Long] = countsOf.getOrElseUpdate(n) {
    dbgblk(s"getCountsOf(${quote(n)})") { 
      val parentCount = n.parent.map { parent => getCountsOf(parent) }.getOrElse(Some(1l))
      val iters = getItersOf(n)
      zipMap(parentCount, iters) { case (parentCount, iters) => parentCount * iters }
    }
  }

  def getParOf(x:PIRNode):Int = parOf.getOrElseUpdate(x) {
    dbgblk(s"getParOf($x)") {
      x match {
        case x:ControlNode => 1
        case x:Counter => x.par
        case x:CounterChain => getParOf(x.counters.last)
        case Def(n, ReduceOp(op, input)) => getParOf(input) / 2 
        case Def(n, AccumOp(op, input)) => getParOf(input)
        case n:ReduceAccumOp => 1
        case n:Container => n.collectDown[Primitive]().map { d => getParOf(d) }.max
        case x:LocalLoad => getParOf(ctrlOf(x))
        case x:ProcessDramCommand => getParOf(ctrlOf(x))
        case x:Primitive => 
          if (x.deps.isEmpty) {
            getParOf(ctrlOf(x))
          } else {
            x.deps.map { dep => getParOf(dep) }.max
          }
      }
    }
  }


  /*
   * For PIR nodes, itersOf is iteration interval between activation of the nodes with respect to
   * local contextEnable
   * */
  def getItersOf(n:PIRNode):Option[Long] = itersOf.getOrElseUpdate(n) {
    dbgblk(s"getItersOf(${quote(n)})") {
      n match {
        case cchain:CounterChain => getItersOf(cchain.outer)
        case Def(ctr:Counter, Counter(min, max, step, par)) =>
          val cmin = getBoundAs[Int](min, logger=Some(this))
          val cmax = getBoundAs[Int](max, logger=Some(this))
          val cstep = getBoundAs[Int](step, logger=Some(this))
          dbg(s"ctr=${quote(ctr)} cmin=$cmin, cmax=$cmax, cstep=$cstep par=$par")
          val iters = zipMap(cmin, cmax, cstep) { case (cmin, cmax, cstep) =>
            if ((cmax - cmin) % (cstep * par) != 0)
              warn(s"(max=$cmax - min=$cmin) % (step=$cstep * par=$par) != 0 for ${quote(ctr)}")
            (cmax - cmin) / (cstep * par)
          }
          val enIters = ctr.getEnable.map { en => getItersOf(en) }.getOrElse(Some(1l))
          dbg(s"iters=$iters, enIters=$enIters")
          zipMap(iters, enIters) { case (iters, enIters) => iters * enIters }
        case Def(n, CounterDone(ctr)) => getItersOf(ctr)
        case n:DramControllerDone => getItersOf(ctrlOf(n))
        case n:ContextEnable => Some(1l)
        case n:LocalAccess => getItersOf(accessNextOf(n))
        case n:GlobalOutput => getItersOf(validOf(n))
        case n:Primitive => 
          val deps = n.deps.filterNot { dep => isBackPressure(dep) || dep.isInstanceOf[Memory] }
          minByWithBound[Primitive, Option[Long]](deps, Some(1l)) { dep => getItersOf(dep) }
      }
    }
  }

  def getCountsOf(n:PIRNode):Option[Long] = countsOf.getOrElseUpdate(n) {
    dbgblk(s"getCountsOf(${quote(n)})") { 
      n match {
        case n:ContextEnable => getCountsOf(ctrlOf(n))
        case n:Primitive if within[ComputeContext](n) =>
          zipMap(getCountsOf(ctxEnOf(n).get), getItersOf(n)) { case (ctxEnCounts, iters) =>
            ctxEnCounts / iters
          }
        case n:LUT => Some(1l)
        case n:Memory =>
          val count = assertUnify(writersOf(n), "writerCounts") { writer => getCountsOf(writer) }
          if (!isFIFO(n)) {
            val ctrlCount = getCountsOf(ctrlOf(n))
            zipMap(count, ctrlCount) { case (count, ctrlCount) =>
              assert(ctrlCount == count, s"$n writerCounts($count) != ctrlCount($ctrlCount)")
            }
          } else {
            val readerCounts = assertUnify(readersOf(n), "readerCounts") { reader => getCountsOf(reader) }
            zipMap(count, readerCounts) { case (count, readerCounts) =>
              assert(readerCounts == count, s"$n writerCounts($count) != readerCounts($readerCounts)")
            }
          }
          count
      }
    }
  }

  def isBackPressure(n:Primitive) = n match {
    case n:NotFull => true
    case n:DataReady => true
    case _ => false
  }

}
