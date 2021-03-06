package pir.graph

import scala.collection.mutable.Set
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map
import scala.math.max
import pir.Design
import pir.graph._
import pir.util.enums._
import pir.exceptions._
import pir.util._
import scala.reflect.runtime.universe._
import pir.pass.ForwardRef
import pir.util._

abstract class Controller(implicit design:Design) extends Module {
  implicit def ctrler:this.type = this
  import pirmeta._

  val sinMap = Map[Scalar, ScalarIn]()
  val soutMap = Map[Scalar, ScalarOut]()
  val vinMap = Map[Vector, VecIn]()
  val voutMap = Map[Vector, VecOut]()

  def sins = sinMap.values.toList
  def souts = soutMap.values.toList
  def vins = vinMap.values.toList 
  def vouts = voutMap.values.toList

  def newSin(s:Scalar):ScalarIn = sinMap.getOrElseUpdate(s, ScalarIn(s))
  def newSout(s:Scalar):ScalarOut = soutMap.getOrElseUpdate(s,ScalarOut(s))
  def newSout(name:String, s:Scalar):ScalarOut = soutMap.getOrElseUpdate(s, ScalarOut(name, s))
  def newVin(v:Vector):VecIn = vinMap.getOrElseUpdate(v,VecIn(v))
  def newVout(v:Vector):VecOut = voutMap.getOrElseUpdate(v, VecOut(v))
  def newVout(name:String, v:Vector):VecOut = voutMap.getOrElseUpdate(v, VecOut(name, v))

  def cins:List[InPort] = ctrlBox.ctrlIns
  def couts:List[OutPort] = ctrlBox.ctrlOuts 

  // No need to consider scalar after bundling
  def readers:List[Controller] = voutMap.keys.flatMap {
    _.readers.map{ _.ctrler }
  }.toList
  def writers:List[Controller] = vinMap.keys.map(_.writer.ctrler).toList

  def ctrlBox:CtrlBox

  val _children = ListBuffer[ComputeUnit]()
  def children:List[ComputeUnit] = _children.toList
  def removeChild(c:ComputeUnit) = { _children -= c }
  def addChildren(c:ComputeUnit) = { if (!_children.contains(c)) _children += c }

  private val _consumed = ListBuffer[MultiBuffer]()
  private val _produced = ListBuffer[MultiBuffer]()
  def consume(mem:MultiBuffer) = _consumed += mem
  def produce(mem:MultiBuffer) = _produced += mem
  def consumed = _consumed.toList
  def produced = _produced.toList
  def trueConsumed = consumed.filter { _.trueDep }
  def trueProduced = produced.filter { _.trueDep }
  def writtenMems:List[OnChipMem] = {
    (souts ++ vouts).flatMap{_.readers.flatMap{ _.out.to }}.map{_.src}.collect{ case ocm:OnChipMem => ocm }.toList
  }

  def length = lengthOf(this)
  def ancestors: List[Controller] = ancestorsOf(this)
  def descendents: List[Controller] = descendentsOf(this)
  def isHead = pirmeta.isHead(this)
  def isLast = pirmeta.isLast(this)
  def isUnitStage = isHead && isLast
  def isStreaming = pirmeta.isStreaming(this)
  def isPipelining = pirmeta.isPipelining(this)

  def isMP = this.isInstanceOf[MemoryPipeline]
  def isSC = this.isInstanceOf[StreamController]
  def isSeq = this.isInstanceOf[Sequential]
  def isMeta = this.isInstanceOf[MetaPipeline]
  def isCU = this.isInstanceOf[ComputeUnit]
  def asCU = this.asInstanceOf[ComputeUnit]
  def asCL = this.asInstanceOf[Controller]
  def asICL = this.asInstanceOf[InnerController]
  def asMP = this.asInstanceOf[MemoryPipeline]

  def cloneType(name:String):Controller = {
    cloneType(Some(name))
  }

  def cloneType(name:Option[String] = None):Controller = {
    val clone = this match {
      case _:Sequential => new Sequential(Some(s"${this}_${name.getOrElse("clone")}"))
      case _:MetaPipeline => new MetaPipeline(Some(s"${this}_${name.getOrElse("clone")}"))
      case _:StreamController => new StreamController(Some(s"${this}_${name.getOrElse("clone")}"))
      case _ => throw PIRException(s"Cannot clone $this")
    }
    design.top.addCtrler(clone)
    clone
  }
}

