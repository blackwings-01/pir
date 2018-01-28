package pir.newnode

import pir._

import pirc._
import pirc.enums._
import pirc.util._

import scala.collection.mutable
import scala.language.existentials
import scala.math.max
import scala.reflect._
import scala.reflect.runtime.universe._

trait IR extends prism.node.IR { 
  var name:Option[String] = None
  def name(n:String):this.type = { this.name = Some(n); this }
  def name(n:Option[String]):this.type = { this.name = n; this }

  def ctrl(ctrler:Any)(implicit design:PIR):this.type = {
    (this, ctrler) match {
      case (self:Controller, ctrler:Controller) => self.setParent(ctrler)
      case (self:ComputeContext, ctrler:Controller) => self.setControl(ctrler)
      case (self:Controller, top:Top) => top.topController = self
      case (_,_) =>
    }
    this
  }
}

abstract class Node(implicit design:PIR) extends prism.node.Node[Node] with IR { self =>
  design.addNode(this)
  type P = Container
  type A = Module
  override def ins:List[Input]
  override def outs:List[Output]
  override def ios:List[IO] = ins ++ outs
}

abstract class Container(implicit design:PIR) extends Node with prism.node.SubGraph[Node] { self =>
  override def ins:List[Input] = super.ins.asInstanceOf[List[Input]]
  override def outs:List[Output] = super.outs.asInstanceOf[List[Output]]
}

abstract class Module(implicit design: PIR) extends Node with prism.node.Atom[Node] { self =>
  override def ins:List[Input] = super.ins.asInstanceOf[List[Input]]
  override def outs:List[Output] = super.outs.asInstanceOf[List[Output]]

  def connect(io:IO)(implicit design:PIR):IO = {
    io match {
      case io:Input => new Output()(this, design).connect(io)
      case io:Output => new Input()(this, design).connect(io)
    }
  }

  override def connectFields(x:Any)(implicit design:Design):Any = {
    implicit val pir = design.asInstanceOf[PIR]
    x match {
      case x:Def => this.connect(x.out)
      case x:Memory => this.connect(x.out) // StoreDef override this function. it connects to Memory.in
      case x:Counter => this.connect(x.out)
      case x => super.connectFields(x)
    }
  }
}

abstract class IO(override val src:Module)(implicit design:PIR) extends prism.node.Edge[Node]() {
  override type A = Module
  override def connect(p:prism.node.Edge[Node]):this.type = {
    err(this.isInstanceOf[Input] && this.isConnected && !this.isConnectedTo(p), s"$this is already connected to ${connected}, reconnecting to $p")
    super.connect(p)
  }
}
class Input(implicit src:Module, design:PIR) extends IO(src) with prism.node.Input[Node] {
  type E = Output
  def from = connected.head
  override def connect(e:prism.node.Edge[Node]):this.type = {
    val p = e.asInstanceOf[E]
    super.connect(p)
  }
}
class Output(implicit src:Module, design:PIR) extends IO(src) with prism.node.Output[Node] {
  type E = Input
  def to = connected
  override def connect(e:prism.node.Edge[Node]):this.type = {
    val p = e.asInstanceOf[E]
    super.connect(p)
  }
}

case class DRAM()(implicit design:PIR) extends IR

case class ArgInFringe()(implicit design:PIR) extends Module

abstract class Memory(implicit design:PIR) extends Module { self =>
  val in = new Input
  val out = new Output
}

case class SRAM(size:Int, banking:Banking)(implicit design:PIR) extends Memory
case class FIFO(size:Int)(implicit design:PIR) extends Memory

case class Reg(init:Option[AnyVal])(implicit design:PIR) extends Memory
object Reg {
  def apply(init:AnyVal)(implicit design:PIR):Reg = Reg(Some(init))
  def apply()(implicit design:PIR):Reg = Reg(None)
}

case class ArgIn(init:Option[AnyVal])(implicit design:PIR) extends Memory
object ArgIn {
  def apply(init:AnyVal)(implicit design:PIR):ArgIn = ArgIn(Some(init))
  def apply()(implicit design:PIR):ArgIn = ArgIn(None)
}

case class ArgOut(init:Option[AnyVal])(implicit design:PIR) extends Memory
object ArgOut {
  def apply(init:AnyVal)(implicit design:PIR):ArgOut = ArgOut(Some(init))
  def apply()(implicit design:PIR):ArgOut = ArgOut(None)
}

case class StreamIn(field:String)(implicit design:PIR) extends Memory
case class StreamOut(field:String)(implicit design:PIR) extends Memory

case class RetimingFIFO()(implicit design:PIR) extends Memory

trait GlobalContainer extends Container { self => }

case class CUContainer(contains:Node*)(implicit design:PIR) extends GlobalContainer

case class FringeContainer(dram:DRAM, contains:Node*)(implicit design:PIR) extends GlobalContainer

case class ArgContainer()(implicit design:PIR) extends GlobalContainer {
  val argInFringe = ArgInFringe().setParent(this)
}

case class Top()(implicit design: PIR) extends Container { 
  var topController:Controller = _

  val argContainer = ArgContainer().setParent(this)

  val argIns = mutable.ListBuffer[ArgIn]()
  val argOuts = mutable.ListBuffer[ArgOut]()
  val dramAddresses = mutable.Map[DRAM, ArgIn]()

  def argIn(init:AnyVal)(implicit design:PIR) = {
    val reg = ArgIn(init).setParent(this)
    argIns += reg
    argContainer.argInFringe.connect(reg.in)
    reg
  }

  def argOut(init:AnyVal)(implicit design:PIR) = {
    val reg = ArgOut(init)
    argOuts += reg
    argContainer.addChild(reg)
    reg
  }

  def dramAddress(dram:DRAM)(implicit design:PIR) = {
    val reg = ArgIn().setParent(this)
    reg.name(s"DramAddr${reg.id}")
    dramAddresses += dram -> reg
    argContainer.argInFringe.connect(reg.in)
    LoadDef(List(reg), None)
  }

}

trait ComputeContext extends Node { self =>
  var _controller:Controller = _
  def setControl(c:Controller):this.type = { _controller = c; this }
  def ctrl:Controller = _controller
}

case class Controller(style:ControlStyle, level:ControlLevel, cchain:CounterChain)(implicit design:PIR) extends prism.node.SubGraph[Controller] with IR {
  override def toString = s"$style$id[${name.get}]"
  type P = Controller
  def isInnerControl = children.isEmpty 
}

case class CounterChain(counters:List[Counter])(implicit design:PIR) extends Container with ComputeContext
object CounterChain {
  def unit(implicit design:PIR) = {
    CounterChain(List(Counter(Const(0), Const(1), Const(1), 1)))
  }
}

case class Counter(min:Def, max:Def, step:Def, par:Int)(implicit design:PIR) extends Module with ComputeContext {
  val out = new Output
}

abstract class Def(implicit design:PIR) extends Module with ComputeContext { self =>
  def depDefs:Set[Def] = deps.collect { case d:Def => d } 
  def localDepDefs = localDeps.collect { case d:Def => d } 
  def depedDefs:Set[Def] = depeds.collect { case d:Def => d } 
  def localDepedDefs = localDepeds.collect { case d:Def => d } 

  private val _out = new Output
  def out = _out
}

trait StageDef extends Def
trait LocalLoad extends Def
object LocalLoad {
  def unapply(n:Any) = n match {
    case LoadDef(mems, addrs) => Some((mems, addrs))
    case MemLoad(mem, addrs) => Some((List(mem), addrs))
    case _ => None
  }
}
trait LocalStore extends Def
object LocalStore {
  def unapply(n:Any) = n match {
    case StoreDef(mems, addrs, data) => Some((mems, addrs, data))
    case MemStore(mem, addrs, data) => Some((List(mem), addrs, data))
    case _ => None
  }
}

case class IterDef(counter:Counter, offset:Option[Int])(implicit design:PIR) extends Def 
case class OpDef(op:Op, inputs:List[Def])(implicit design:PIR) extends StageDef
case class ReduceDef(op:Op, input:Def)(implicit design:PIR) extends StageDef
case class AccumDef(op:Op, input:Def, accum:Def)(implicit design:PIR) extends StageDef

// Generated IR from spatial
case class LoadDef(mems:List[Memory], addrs:Option[List[Def]])(implicit design:PIR) extends LocalLoad
case class StoreDef(mems:List[Memory], addrs:Option[List[Def]], data:Def)(implicit design:PIR) extends LocalStore

// Lowered IR
case class MemLoad(mem:Memory, addrs:Option[List[Def]])(implicit design:PIR) extends LocalLoad
case class MemStore(mem:Memory, addrs:Option[List[Def]], data:Def)(implicit design:PIR) extends LocalStore

// IR's doesn't matter in spatial. such as valid for counters. Should be dead code eliminated
case class DummyDef()(implicit design:PIR) extends Def
case class Const[T<:AnyVal](value:T)(implicit design:PIR) extends Def

sealed trait IOType extends Enum
case object Vector extends IOType
case object Scalar extends IOType
case object Control extends IOType

sealed trait ControlStyle extends Enum
case object InnerPipe extends ControlStyle
case object SeqPipe extends ControlStyle
case object MetaPipe extends ControlStyle
case object StreamPipe extends ControlStyle

sealed trait ControlLevel extends Enum
case object InnerControl extends ControlLevel
case object OuterControl extends ControlLevel
