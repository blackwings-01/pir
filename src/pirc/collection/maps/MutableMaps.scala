package pirc.collection.mutable

import pirc._
import pirc.collection._
import pirc.exceptions._

import scala.collection.mutable.Map
import scala.collection.mutable.Set

trait MMap extends UniMap {
  override type M = Map[K,VV]
  def clear = { map.clear }
}

trait MBiMap extends BiMap with MMap {
  override type IM = Map[V, KK]
  override def clear = { super.clear; imap.clear }
}

trait MOneToOneMap extends OneToOneMap with MMap {
  override type M = Map[K, VV]
  val map:Map[K, VV] = Map.empty
  def update(n:K, v:V):Unit = { check((n,v)); map += (n -> v) }
  def getOrElseUpdate(n:K, v:VV):VV = map.getOrElseUpdate(n,v)
}

trait MBiOneToOneMap extends MOneToOneMap with BiOneToOneMap with MBiMap {
  override type IM = Map[V, KK]
  val imap:IM = Map.empty
  override def update(n:K, v:V):Unit = { check((n,v)); super.update(n, v); imap += (v -> n) }
}

trait MOneToManyMap extends OneToManyMap with MMap {
  override type VV = Set[V]
  override type M = Map[K, VV]
  val map:Map[K, VV] = Map.empty
  def update(n:K, v:V):Unit = map.getOrElseUpdate(n, Set[V]()) += v
}

trait MBiOneToManyMap extends MOneToManyMap with BiOneToManyMap with MBiMap {
  override type IM = Map[V, KK]
  val imap:IM = Map.empty
  override def update(n:K, v:V):Unit = { check((n,v)); super.update(n,v); imap += (v -> n) } 
}

trait MBiManyToOne extends MOneToOneMap with BiManyToOneMap with MMap {
  override type KK = Set[K]
  override type IM = Map[V, KK]
  val imap:IM = Map.empty
  override def update(n:K, v:V):Unit = { check((n,v)); super.update(n,v); imap.getOrElseUpdate(v, Set[K]()) += n } 
}

trait MBiManyToMany extends MOneToManyMap with BiManyToManyMap with MBiMap {
  override type KK = Set[K]
  override type IM = Map[V, KK]
  val imap:IM = Map.empty
  override def update(n:K, v:V):Unit = { super.update(n,v); imap.getOrElseUpdate(v, Set[K]()) += n } 
}
