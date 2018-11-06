package pir
package node

import prism.graph._
import prism.graph.implicits._

trait MemoryNode extends PIRNode {
  /*  ------- Metadata -------- */
  val inits = Metadata[List[Any]]("inits")
  val dims = Metadata[List[Int]]("dims", default=List(1))
  val banks = Metadata[List[Int]]("banks", default=List(1))
  val depth = Metadata[Int]("depth", default=1)
  def getBanks = banks.get
  def getDepth = depth.get
  def bankDims = getBanks.size
}

abstract class Memory(implicit env:Env) extends MemoryNode with DefNode[PIRNode] {

  /*  ------- Fields -------- */
  val in = new InputField[List[Access]]("in")
  val out = new OutputField[List[Access]]("out")

  override def asInput = Some(in)
}

case class Reg()(implicit env:Env) extends Memory
case class FIFO()(implicit env:Env) extends Memory
case class SRAM()(implicit env:Env) extends Memory
case class RegFile()(implicit env:Env) extends Memory
case class LUT()(implicit env:Env) extends Memory

case class Top()(implicit env:Env) extends PIRNode {
  val topCtrl = ControlTree("Pipelined")
  val hostInCtrl = ControlTree("Sequenced").setParent(topCtrl)
  val hostOutCtrl = ControlTree("Sequenced").setParent(topCtrl)

  lazy val argFringe = this.collectDown[ArgFringe]().head
  lazy val hostRead = argFringe.collectDown[HostRead]().head
  lazy val hostWrite = argFringe.collectDown[HostWrite]().head
}

trait GlobalContainer extends PIRNode
case class ArgFringe()(implicit env:Env) extends GlobalContainer {
  val hostInCtrler = new ChildField[HostInController, HostInController]("hostInController")
  val hostOutCtrler = new ChildField[HostOutController, HostOutController]("hostOutController")
}
case class MemoryContainer()(implicit env:Env) extends GlobalContainer
case class Context()(implicit env:Env) extends PIRNode

trait Def extends PIRNode with DefNode[PIRNode] {
  final val out = new OutputField[List[PIRNode]]("out")
}

case class Const(value:Any)(implicit env:Env) extends Def
case class OpDef(op:String)(implicit env:Env) extends Def {
  val input = new InputField[List[PIRNode]]("input")
}
case class RegAccumOp(op:String)(implicit env:Env) extends Def {
  val in = new InputField[PIRNode]("input")
  val en = new InputField[Set[PIRNode]]("en")
  val first = new InputField[PIRNode]("first")
}
case class HostRead()(implicit env:Env) extends Def {
  val input = new InputField[List[PIRNode]]("input")
}
case class HostWrite()(implicit env:Env) extends Def
case class TokenRead()(implicit env:Env) extends Def {
  val input = new InputField[List[BufferRead]]("input")
}
case class CountAck()(implicit env:Env) extends Def {
  val input = new InputField[List[PIRNode]]("input")
}
case class Counter(par:Int)(implicit env:Env) extends Def {
  /*  ------- Fields -------- */
  val min = new InputField[PIRNode]("min")
  val step = new InputField[PIRNode]("step")
  val max = new InputField[PIRNode]("max")
  def iters = this.collectOut[CounterIter]().sortBy { _.i }
  def valids = this.collectOut[CounterValid]().sortBy { _.i }
}

case class CounterIter(i:Option[Int])(implicit env:Env) extends Def {
  val counter = new InputField[Counter]("counter")
}
case class CounterValid(i:Option[Int])(implicit env:Env) extends Def {
  val counter = new InputField[Counter]("counter")
}

abstract class Controller(implicit env:Env) extends PIRNode {
  /*  ------- Fields -------- */
  val en = new InputField[Option[PIRNode]]("en")
  val parentEn = new InputField[Option[PIRNode]]("parentEn")

  val valid = new ChildField[ControllerValid, ControllerValid]("cchain")
  val done = new ChildField[ControllerDone, ControllerDone]("cchain")
}
case class ControllerDone()(implicit env:Env) extends Def
case class ControllerValid()(implicit env:Env) extends Def

case class HostInController()(implicit env:Env) extends Controller
case class HostOutController()(implicit env:Env) extends Controller
case class UnitController()(implicit env:Env) extends Controller
case class LoopController()(implicit env:Env) extends Controller {
  /*  ------- Fields -------- */
  val cchain = new ChildField[Counter, List[Counter]]("cchain")
}
case class DramController()(implicit env:Env) extends Controller

trait MemoryUtil extends CollectorImplicit {

  implicit class MemUtil(n:Memory) {
    def inAccess = n.collect[Access](visitGlobalIn _)
    def outAccess = n.collect[Access](visitGlobalOut _)
    def accesses = inAccess ++ outAccess

    def isFIFO = n match {
      case n:FIFO => true
      case _ => false
    }
  }

  //def isReg(n:PIRNode) = n match {
    //case n:Reg => true
    //case n:ArgIn => true
    //case n:DramAddress => true
    //case n:ArgOut => true
    //case n:TokenIn => true
    //case n:TokenOut => true
    //case n => false
  //}

  //def isRemoteMem(n:PIRNode) = n match {
    //case (_:SRAM)  => true
    //case n:FIFO if writersOf(n).size > 1 => true
    //case n:RegFile => true
    //case n:LUT => true
    //case _ => false
  //}

  //def isLocalMem(n:PIRNode) = !isRemoteMem(n)

  //def isControlMem(n:Memory) = n match {
    //case n:TokenIn => true
    //case n:TokenOut => true
    //case StreamIn("ack") => true
    //case _ => false
  //}

  //def isBackPressure(n:Primitive) = n match {
    //case n:NotFull => true
    //case n:DataReady => true
    //case _ => false
  //}

}
