package pir.mapper

import prism.collection.immutable._

trait Constrain {
  type K
  type V
  type FG <: FactorGraphLike[K,V,FG]
  implicit def fgct:ClassTag[FG]
  override def toString = this.getClass.getSimpleName.replace("$","")
  def prune(fg:FG)(implicit pass:PIRPass):EOption[FG]
  def prune(pmap:PIRMap)(implicit pass:PIRPass):EOption[PIRMap] = {
    pmap.flatMap[FG](field => prune(field))
  }
}
trait PrefixConstrain extends Constrain {
  def prefixKey(cuP:K)(implicit pass:PIRPass):Boolean
  def prefixValue(cuS:V)(implicit pass:PIRPass):Boolean
  def prune(fg:FG)(implicit pass:PIRPass):EOption[FG] = {
    import pass.{pass => _, _}
    fg.filter { case (cuP,cuS) =>
      val factor = prefixKey(cuP) == prefixValue(cuS)
      pass.dbg(s"$this ${quote(cuP)} -> ${quote(cuS)} factor=$factor")
      factor
    }
  }
}
trait QuantityConstrain extends Constrain {
  def numPNodes(cuP:K)(implicit pass:PIRPass):Int
  def numSnodes(cuS:V)(implicit pass:PIRPass):Int
  def prune(fg:FG)(implicit pass:PIRPass):EOption[FG] = {
    import pass.{pass => _, _}
    fg.filter { case (cuP,cuS) =>
      val np = numPNodes(cuP)
      val ns = numSnodes(cuS)
      val factor = np <= ns
      pass.dbg(s"$this ${quote(cuP)} -> ${quote(cuS)} factor=$factor pnodes=$np snodes=$ns")
      factor
    }
  }
}
trait ArcConsistencyConstrain extends Constrain {
  def prune(fg:FG)(implicit pass:PIRPass):EOption[FG] = {
    import pass.{pass => _, _}
    flatFold(fg.freeKeys,fg) { case (fg, k) => ac3[K,V,FG](fg, k) }
  }
  def ac3[K,V,FG<:FactorGraphLike[K,V,FG]](fg:FG, k:K):EOption[FG] = {
    flatFold(fg(k),fg) { case (fg, v) =>
      val neighbors = fg.freeKeys(v).filterNot { _ == k }
      val nfg = fg.set(k,v)
      nfg match {
        case Left(_) => fg - (k,v)
        case Right(nfg) =>
          flatFold(neighbors, fg) { case (fg, neighbor) => 
            if (ac3[K,V,FG](nfg, neighbor).isLeft) fg - (k,v) else Right(fg)
          }
      }
    }
  }
}