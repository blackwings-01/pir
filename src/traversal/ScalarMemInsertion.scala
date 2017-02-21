package pir.graph.traversal
import pir.graph._
import pir._
import pir.misc._
import pir.graph.mapper.PIRException
import pir.codegen.Logger

import scala.collection.mutable.Set
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map
import scala.collection.mutable.Queue

class ScalarMemInsertion(implicit val design: Design) extends Traversal with Logger {

  override val stream = newStream(s"ScalarMemInsertion.log")

  def leastCommonAncestor(reader:ComputeUnit, writer:ComputeUnit):Controller = {
    reader.ancestors.intersect(writer.ancestors).head
  }

  def insertScalarMem(sin:ScalarIn):ScalarMem = {
    val cu = sin.ctrler.asInstanceOf[ComputeUnit]
    val mem:ScalarMem = cu.parent match {
      case parent:StreamController if parent.children.contains(sin.writer.ctrler) => // insert fifo
        cu.getRetimingFIFO(sin.scalar).asInstanceOf[ScalarFIFO]
      case _ => cu.getScalarBuffer(sin.scalar)
    }
    emitln(s"sin:$sin sin.out:${sin.out} sin.out.to:[${sin.out.to.mkString(",")}]")
    sin.out.to.foreach { ip =>
      val op = ip.src match {
        case s:Stage if s.prev.isInstanceOf[EmptyStage] => mem.load
        case s:Stage => cu.load(s.prev.get, mem).out
        case _ => mem.load
      }
      ip.disconnect
      ip.connect(op)
      emitln(s"disconnect $ip, connect $ip to $op  $ip ${ip.src}")
    }
    sin.out.disconnect
    mem.wtPort(sin.out)
    emitln(s"Insert $mem in $cu from:$sin to:${mem.readPort.to.mkString(",")}")
    mem
  }

  override def traverse:Unit = {
    design.top.compUnits.foreach { cu =>
      emitBlock(s"$cu") {
        cu.sins.foreach { sin =>
          val mem = insertScalarMem(sin)
        }
      }
    }
  } 

  override def finPass = {
    misc.info("Finishing scalar buffer insertion")
  }

}
