package pir.spade.graph

import pir.util.enums._
import pir.util.misc._
import pir.spade.main._
import pir.spade.config.ConfigFactory
import pir.spade.simulation._
import pir.spade.util._
import pir.exceptions._

import scala.language.reflectiveCalls
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.{Map => MMap}
import scala.collection.mutable.Set

case class PreloadScalarComputeParam(
  override val sbufSize:Int = 16,
  override val numSouts:Int = 4,
  override val numRegs:Int = 16,
  override val numCtrs:Int = 6,
  override val muxSize:Int = 10,
  override val numUDCs:Int = 4
) extends ScalarComputeUnitParam(
  sbufSize = sbufSize,
  numSins = ConfigFactory.plasticineConf.sinUcu,
  numSouts = numSouts,
  numRegs  = numRegs,
  numStages = ConfigFactory.plasticineConf.stagesUcu,
  muxSize = muxSize,
  numCtrs  = numCtrs,
  numUDCs  = numUDCs  
) with PreLoadSpadeParam

class ScalarComputeUnitParam (
  val sbufSize:Int = 16,
  val numSins:Int = 4,
  val numSouts:Int = 4,
  val numRegs:Int = 16,
  val numStages:Int = 6,
  val numCtrs:Int = 6,
  val muxSize:Int = 10,
  val numUDCs:Int = 4
) extends ComputeUnitParam() {
  val numVins:Int = 0
  val numVouts:Int = 0
  val vbufSize:Int = 0
  val numSRAMs:Int = 0
  val sramSize:Int = 0
  override lazy val numLanes:Int = 1

  /* Parameters */
  def config(cu:ScalarComputeUnit)(implicit spade:Spade) = {
    cu.addRegstages(numStage=numStages, numOprds=3, fixOps ++ bitOps ++ otherOps)
    assert(cu.sins.size >= numSins, s"sins=${cu.sins.size} numSins=${numSins}")
    assert(cu.souts.size >= numSouts, s"souts=${cu.souts.size} numSouts=${numSouts}")
    cu.numScalarBufs(numSins)
    cu.mems.foreach(_.writePortMux.addInputs(muxSize))
    cu.color(1, AccumReg)
    cu.color(0 until numCtrs, CounterReg)
    cu.color(7 until 7 + cu.numScalarBufs, ScalarInReg)
    cu.color(8 until 8 + cu.souts.size, ScalarOutReg)
    cu.color(12 until 12 + cu.numVecBufs, VecInReg)
    cu.genConnections
  }
}
/* A spetial type of CU used for memory loader/storer */
class ScalarComputeUnit(override val param:ScalarComputeUnitParam=new ScalarComputeUnitParam())(implicit spade:Spade) 
  extends ComputeUnit(param) {
  override val typeStr = "scu"
  lazy val ctrlBox:InnerCtrlBox = new InnerCtrlBox()
  override def config = param.config(this)
}
