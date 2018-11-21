package pir

package mapper

import pir.node._
import pir.pass._
import spade.param._
import prism.collection.immutable._
import prism.util._
import prism.mapper._

case class AFGCost(prefix:Boolean=false) extends PrefixCost[AFGCost]
case class MCCost(prefix:Boolean=false) extends PrefixCost[MCCost]
case class SRAMCost(count:Int=0, bank:Int=0, size:Int=0) extends QuantityCost[SRAMCost]
case class FIFOCost(sfifo:Int=0, vfifo:Int=0) extends QuantityCost[FIFOCost]
case class InputCost(sin:Int=0, vin:Int=0) extends QuantityCost[InputCost]
case class OutputCost(sout:Int=0, vout:Int=0) extends QuantityCost[OutputCost]
case class StageCost(quantity:Int=0) extends QuantityCost[StageCost]
case class LaneCost(quantity:Int=1) extends MaxCost[LaneCost]
case class OpCost(set:Set[Opcode]=Set.empty) extends SetCost[Opcode,OpCost]

trait CUCostUtil extends PIRPass with CostUtil with RuntimeAnalyzer with Memorization { self =>
  implicit class CostOp(x:Any) {
    def getCost[C<:Cost[C]:ClassTag]:C = x match {
      case x:CUMap.V => self.getCost(x.params.get, classTag[C]).as[C]
      case x => self.getCost(x, classTag[C]).as[C]
    }
  }

  private def getCost(x:Any, ct:ClassTag[_]) = memorize("getCost", (x, ct)) { case (x, ct) => compCost(x, ct) }

  protected def switch[C<:Cost[C]:ClassTag](x:Any, ct:ClassTag[_])(cfunc:PartialFunction[Any, C]) = if (ct == classTag[C] && cfunc.isDefinedAt(x)) Some(cfunc(x)) else None

  protected def compCost(x:Any, ct:ClassTag[_]) = {
    switch[AFGCost](x,ct) {
      case n:GlobalContainer => AFGCost(n.isInstanceOf[ArgFringe])
      case n:Parameter => AFGCost(n.isInstanceOf[ArgFringeParam])
    } orElse switch[MCCost](x,ct) {
      case n:GlobalContainer => MCCost(n.isInstanceOf[DRAMFringe])
      case n:Parameter => MCCost(n.isInstanceOf[MCParam])
    } orElse switch[SRAMCost](x,ct) {
      case n:GlobalContainer =>
        val srams = n.collectDown[SRAM]()
        val sramSize = srams.map { sram => sram.depth.get * sram.size }.maxOption.getOrElse(0)
        val nBanks = srams.map { _.nBanks }.maxOption.getOrElse(0)
        SRAMCost(srams.size, nBanks, sramSize)
      case n:CUParam => SRAMCost(n.sramParam.count, n.sramParam.bank, n.sramParam.sizeInWord)
      case n => SRAMCost(0,0,0)
    } orElse switch[FIFOCost](x,ct) {
      case n:GlobalContainer => 
        val fifos = n.collectDown[FIFO]()
        val (vfifos, sfifos) = fifos.partition { _.getVec > 1 }
        val ctxs = n.collectDown[Context]()
        val fcost = FIFOCost(sfifos.size, vfifos.size)
        ctxs.map { _.getCost[FIFOCost] }.fold(fcost) { _ + _ }
      case n:Context =>
        val fifos = n.collectDown[BufferRead]()
        val (vfifos, sfifos) = fifos.partition { _.getVec > 1 }
        FIFOCost(sfifos.size, vfifos.size)
      case n:CUParam =>
        FIFOCost(n.fifoParamOf("word").fold(0){_.count}, n.fifoParamOf("vec").fold(0){_.count})
      case n:Parameter => FIFOCost(0,0)
    } orElse switch[InputCost](x,ct) {
      case n:GlobalContainer => 
        val gins = n.collectDown[GlobalInput]()
        val (vins, sins) = gins.partition { _.getVec > 1 }
        InputCost(sins.size, vins.size)
      case n:CUParam => InputCost(n.numSin, n.numVin)
      case n:Parameter => InputCost(100,100)
    } orElse switch[OutputCost](x,ct) {
      case n:GlobalContainer => 
        val outs = n.collectDown[GlobalOutput]()
        val (vouts, souts) = outs.partition { _.getVec > 1 }
        OutputCost(souts.size, vouts.size)
      case n:CUParam => OutputCost(n.numSout, n.numVout)
      case n:Parameter => OutputCost(100,100)
    } orElse switch[StageCost](x,ct) {
      case n:GlobalContainer => 
        val ctxs = n.collectDown[Context]()
        ctxs.map { _.getCost[StageCost] }.fold(StageCost()) { _ + _ }
      case n:Context =>
        val ops = n.collectDown[OpNode]()
        StageCost(ops.size)
      case n:CUParam => 
        StageCost(n.numStage)
      case n:Parameter => StageCost(0)
    } orElse switch[LaneCost](x,ct) {
      case n:GlobalContainer => 
        val ctxs = n.collectDown[Context]()
        ctxs.map { _.getCost[LaneCost] }.fold(LaneCost()) { _ + _ }
      case n:Context =>
        val inner = n.collectDown[Controller]().minOptionBy { _.ctrl.get }
        LaneCost(inner.map { _.getVec }.getOrElse(1))
      case n:CUParam => LaneCost(n.numLane)
      case n:Parameter => LaneCost(1)
    } orElse switch[OpCost](x,ct) {
      case n:GlobalContainer => 
        val ctxs = n.collectDown[Context]()
        ctxs.map { _.getCost[OpCost] }.fold(OpCost()) { _ + _ }
      case n:Context =>
        val ops = n.collectDown[OpDef]()
        OpCost(ops.map { _.op }.toSet)
      case n:CUParam => OpCost(n.ops)
      case n:Parameter => OpCost(Set.empty)
    } getOrElse {
      throw PIRException(s"Don't know how to compute $ct of $x")
    }
  }

}
