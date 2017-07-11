package pir.graph

import scala.collection.mutable.Set
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import scala.math.max
import pir._
import pir.graph._

class Range (s:OutPort, e:OutPort) {
  val start:OutPort = s
  val end:OutPort = e
  def by(step:OutPort) = (start, end, step)
}


trait Port extends Node {
  val src:Node
  def isOutput = this.isInstanceOf[OutPort]
  def isInput = this.isInstanceOf[InPort]
  def asOutput = this.asInstanceOf[OutPort]
  def asInput = this.asInstanceOf[InPort]
}
trait InPort extends Port {
  override val name=None
  override val typeStr = "InPort"
  var from:OutPort = _
  def isConnected = from!=null
  def connect(o:OutPort) = { 
    if (isConnected) assert(from == o, s"${this}(id=$id) is already connected to ${from} but trying to reconnect to $o")
    from = o; 
    if (!o.to.contains(this)) o.to += this
  }
  def isConnectedTo(o:OutPort) = { from == o }
  def disconnect = { if (isConnected) from.to -= this; from = null }

  def isCtrlIn:Boolean = this match {
    case ci:CtrlInPort => ci.isCtrlIn
    case _ => false
  }
  def asCtrl = this.asInstanceOf[CtrlInPort]
}
object InPort {
  def apply[S<:Node](s:S)(implicit design:Design):InPort = new {override val src:S = s} with InPort
  def apply[S<:Node](s:S, toStr: => String)(implicit design:Design):InPort = {
    new {override val src:S = s} with InPort {override def toString = toStr}
  }
}
/**
 * A type representing a group of wires in pir
 */
trait OutPort extends Port {
  val to:ListBuffer[InPort] = new ListBuffer[InPort]()
  def isConnected = to.size!=0
  def isConnectedTo(i:InPort) = { to.contains(i) }
  def disconnect = { to.foreach { _.disconnect}; assert(to.isEmpty) }
  override val name=None
  override val typeStr = "OutPort"
  def width(implicit design:Design) = design.arch.wordWidth
  def by(step:OutPort)(implicit design:Design) = (Const(0).out, this, step)
  def until(max:OutPort) = new Range(this, max)

  def isCtrlOut:Boolean = this match {
    case co:CtrlOutPort => co.isCtrlOut
    case _ => false
  }
  def asCtrl = this.asInstanceOf[CtrlOutPort]
}
object OutPort {
  def apply(s:Node)(implicit design:Design):OutPort = new {override val src = s} with OutPort
  def apply(s:Node, toStr: => String)(implicit design:Design):OutPort = {
    new {override val src = s} with OutPort { override def toString = toStr }
  }
  def apply(s:Node, t:InPort, toStr: => String)(implicit design:Design):OutPort = {
    new {override val src = s} with OutPort { override def toString = toStr; t.connect(this)}
  }
}

/* SRAM Ports */
trait RdAddrInPort extends InPort { override val src:SRAMOnRead }
object RdAddrInPort {
  def apply(s:SRAMOnRead, toStr: => String)(implicit design:Design):RdAddrInPort = {
    new {override val src = s} with RdAddrInPort {override def toString = toStr}
  }
}
trait WtAddrInPort extends InPort { override val src:SRAMOnWrite }
object WtAddrInPort {
  def apply(s:SRAMOnWrite, toStr: => String)(implicit design:Design):WtAddrInPort = {
    new {override val src = s} with WtAddrInPort {override def toString = toStr}
  }
}
trait WriteInPort extends InPort { override val src:OnChipMem }
object WriteInPort {
  def apply(s:OnChipMem, toStr: => String)(implicit design:Design):WriteInPort = {
    new {override val src = s} with WriteInPort {override def toString = toStr}
  }
}
trait ReadOutPort extends OutPort { override val src:OnChipMem }
object ReadOutPort {
  def apply(s:OnChipMem, toStr: => String)(implicit design:Design):ReadOutPort = {
    new {override val src = s} with ReadOutPort {override def toString = toStr}
  }
}
/* Inner Counter En Port */
trait EnInPort extends InPort with CtrlInPort { 
  override val src:Counter
}
object EnInPort {
  def apply(s:Counter, toStr: => String)(implicit design:Design):EnInPort = {
    new {override val src = s} with EnInPort {override def toString = toStr}
  }
}
/* Outer Counter Done Port */
trait DoneOutPort extends OutPort with CtrlOutPort { 
  override val src:Counter
}
object DoneOutPort {
  def apply(s:Counter, toStr: => String)(implicit design:Design):DoneOutPort = {
    new {override val src = s} with DoneOutPort {override def toString = toStr}
  }
}

trait CtrlPort extends Port {
  def ctrler:Controller = src match {
    case p:Primitive => p.ctrler
    case top:Top => top
    case mc:MemoryController => mc
  }
}
trait CtrlInPort extends InPort with CtrlPort { 
  override def isCtrlIn:Boolean = { 
    isConnected && from.asInstanceOf[CtrlOutPort].ctrler != ctrler
  }
}
object CtrlInPort {
  def apply[S<:Node](s:S, toStr: => String)(implicit design:Design):CtrlInPort = {
    new {override val src:S = s} with CtrlInPort {override def toString = toStr}
  }
}
trait CtrlOutPort extends OutPort with CtrlPort { 
  override def isCtrlOut:Boolean = { to.exists{ _.asInstanceOf[CtrlInPort].ctrler != ctrler } }
}
object CtrlOutPort {
  def apply(s:Node, toStr: => String)(implicit design:Design):CtrlOutPort = {
    new {override val src = s} with CtrlOutPort { override def toString = toStr }
  }
}