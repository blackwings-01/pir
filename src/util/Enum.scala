package pir.util

package object enums {

  sealed trait Op 
  case object Mux extends Op
  case object Bypass extends Op
  
  sealed trait FixOp extends Op 
  case object FixAdd extends FixOp 
  case object FixSub extends FixOp 
  case object FixMul extends FixOp 
  case object FixDiv extends FixOp 
  case object FixMin extends FixOp
  case object FixMax extends FixOp 
  case object FixLt  extends FixOp
  case object FixLeq extends FixOp
  case object FixEql extends FixOp
  case object FixNeq extends FixOp
  case object FixMod extends FixOp
  case object FixSra extends FixOp
  case object FixNeg extends FixOp
  
  sealed trait FltOp extends Op 
  case object FltAdd extends FltOp 
  case object FltSub extends FltOp 
  case object FltMul extends FltOp 
  case object FltDiv extends FltOp 
  case object FltMin extends FltOp 
  case object FltMax extends FltOp 
  case object FltLt  extends FltOp
  case object FltLeq extends FltOp
  case object FltEql extends FltOp
  case object FltNeq extends FltOp
  case object FltExp extends FltOp
  case object FltAbs extends FltOp
  case object FltLog extends FltOp
  case object FltSqrt extends FltOp
  case object FltNeg extends FltOp 

  sealed trait BitOp extends Op 
  case object BitAnd extends BitOp // &
  case object BitOr  extends BitOp // |

  val fixOps:List[FixOp] = 
    List(FixAdd, FixSub, FixMul, FixDiv, FixMin, FixMax, FixLt, FixLeq, FixEql,
        FixNeq, FixMod, FixSra)
  val fltOps:List[FltOp] = 
    List(FltAdd, FltSub, FltMul, FltDiv, FltMin, FltMax, FltLt, FltLeq, FltEql,
      FltNeq, FltExp, FltAbs)
  val bitOps:List[BitOp] = 
    List(BitAnd, BitOr)

  val ops:List[Op] = fixOps ++ fltOps ++ bitOps ++ List(Mux, Bypass) 

  //sealed trait CtrlType 
  //case object Pipe extends CtrlType
  //case object Sequential extends CtrlType
  //case object MetaPipeline extends CtrlType
  
  sealed trait MCType {
    def isDense:Boolean = {
      this match {
        case TileLoad | TileStore => true
        case Gather | Scatter => false
      }
    }
    def isSparse:Boolean = {
      this match {
        case Gather | Scatter => true
        case TileLoad | TileStore => false
      }
    }
    def isLoad:Boolean = {
      this match {
        case TileLoad | Gather => true
        case TileStore | Scatter => false
      }
    }
    def isStore:Boolean = {
      this match {
        case TileStore | Scatter => true 
        case TileLoad | Gather => false
      }
    }
  }
  case object TileLoad extends MCType 
  case object TileStore extends MCType 
  case object Scatter extends MCType 
  case object Gather extends MCType 
  
  sealed trait Banking
  case class Strided(stride:Int) extends Banking
  case class Diagonal(stride1:Int, stride2:Int) extends Banking
  case class Duplicated() extends Banking
  case class NoBanking() extends Banking

  //sealed trait SramMode
  //case object Fifo extends SramMode
  //case object FifoOnWrite extends SramMode 
  //case object Sram extends SramMode
}
