package pir.mapper

import pir.node._
import spade.node._

abstract class CUConstrain extends Constrain {
  type K = CUMap.K
  type V = CUMap.V
  type FG = CUMap
  val fgct:ClassTag[FG] = classTag[FG]
}
trait CUPrefixConstrain extends CUConstrain with PrefixConstrain
trait CUQuantityConstrain extends CUConstrain with QuantityConstrain
object CUArcConsistencyConstrain extends CUConstrain with ArcConsistencyConstrain
object AFGConstrain extends CUPrefixConstrain  {
  def prefixKey(cuP:K)(implicit pass:PIRPass):Boolean = isAFG(cuP)
  def prefixValue(cuS:V)(implicit pass:PIRPass):Boolean = cuS.isInstanceOf[spade.node.ArgFringe]
}
object DFGConstrain extends CUPrefixConstrain {
  def prefixKey(cuP:K)(implicit pass:PIRPass):Boolean = isDFG(cuP)
  def prefixValue(cuS:V)(implicit pass:PIRPass):Boolean = cuS.isInstanceOf[MC]
}
object SramConstrain extends CUQuantityConstrain {
  def numPNodes(cuP:K)(implicit pass:PIRPass):Int = cuP.collectDown[pir.node.SRAM]().size
  def numSnodes(cuS:V)(implicit pass:PIRPass):Int = cuS.collectDown[spade.node.SRAM]().size
}
object ControlFIFOConstrain extends CUQuantityConstrain {
  def numPNodes(cuP:K)(implicit pass:PIRPass):Int = cuP.collectDown[pir.node.FIFO]().filter(n => isBit(n)).size
  def numSnodes(cuS:V)(implicit pass:PIRPass):Int = cuS.collectDown[spade.node.FIFO[_]]().filter(n => isBit(n)).size
}
object ScalarFIFOConstrain extends CUQuantityConstrain {
  def numPNodes(cuP:K)(implicit pass:PIRPass):Int = cuP.collectDown[pir.node.FIFO]().filter(n => isWord(n)).size
  def numSnodes(cuS:V)(implicit pass:PIRPass):Int = cuS.collectDown[spade.node.FIFO[_]]().filter(n => isWord(n)).size
}
object VectorFIFOConstrain extends CUQuantityConstrain {
  def numPNodes(cuP:K)(implicit pass:PIRPass):Int = cuP.collectDown[pir.node.FIFO]().filter(n => isVector(n)).size
  def numSnodes(cuS:V)(implicit pass:PIRPass):Int = cuS.collectDown[spade.node.FIFO[_]]().filter(n => isVector(n)).size
}
object StageConstrain extends CUQuantityConstrain {
  def numPNodes(cuP:K)(implicit pass:PIRPass):Int = cuP.collectDown[StageDef]().size
  def numSnodes(cuS:V)(implicit pass:PIRPass):Int = cuS.collectDown[Stage]().size
}
object LaneConstrain extends CUQuantityConstrain {
  def numPNodes(cuP:K)(implicit pass:PIRPass):Int = cuP.collectDown[StageDef]().map(s => parOf(s).get).reduceOption{ _ max _}.getOrElse(1)
  def numSnodes(cuS:V)(implicit pass:PIRPass):Int = cuS.collectDown[SIMDUnit]().headOption.map{_.param.numLanes}.getOrElse(1)
}