package pir
package util

import pir.node._
import prism.collection.mutable._

//TODO: use macro to set names
class PIRMetadata extends Metadata {

  /* User defined string name of for an IR. */
  val nameOf = new OneToOneMap[IR, String] with MetadataMap
  nameOf.setName("nameOf")

  /* Source context of the IR from upper compiler */
  val srcCtxOf = new OneToOneMap[IR, String] with MetadataMap {
    override def get(k:K):Option[V] = k match {
      case k:ContextEnable => get(ctrlOf(k))
      case k:ComputeContext => ctxEnOf(k).flatMap { ctxEn => get(ctxEn) }
      case k:GlobalContainer => 
        super.get(k).orElse {
          ctrlOf.get(k).flatMap { ctrl => get(ctrl) }.orElse {
            val remoteMems = k.collectDown[Memory]().filter { m => isRemoteMem(m) }
            val ctxs = k.collectDown[ComputeContext]()
            val scs = (remoteMems ++ ctxs).flatMap { n => get(n) }
            if (scs.isEmpty) None else Some(scs.mkString("\n"))
          }
        }
      case k => super.get(k)
    }
  }
  srcCtxOf.setName("srcCtxOf")

  /*
   * Whether a memory is a accumulator. Set by spatial
   * */
  val isAccum = new OneToOneMap[Memory, Boolean] with MetadataMap {
    override def apply(k:K):V = get(k).getOrElse(false)
  }
  isAccum.setName("isAccum")

  /*
   * Antidependencies between accesses
   * */
  val antiDepsOf = new OneToManyMap[PIRNode, PIRNode] with MetadataMap {
    override def migrateKey(from:K, to:K, v:V, logger:Option[Logging]) = {
      dbg(logger, s"$name.migrateKey $from -> $v => $to -> $v")
      update(to, v)
    }
    override def migrateValue(from:V, to:V, k:K, logger:Option[Logging]) = {
      dbg(logger, s"$name.migrateValue $k -> $from => $k -> $to")
      update(k, to)
    }
  }
  antiDepsOf.setName("antiDepsOf")

  /* 
   * For ComputeNode: Controller associated with a node. 
   * For memory, it's the lca controller of controller of all its
   * accesses 
   * */
  //val ctrlOf = new BiManyToOneMap[PIRNode, Controller] with MetadataMap
  val ctrlOf = new OneToOneMap[PIRNode, Controller] with MetadataMap {
    override def mirrorKey(from:K, to:K, v:V, logger:Option[Logging]) = {
      removeKey(to)
      super.mirrorKey(from, to, v, logger)
    }
  }
  ctrlOf.setName("ctrlOf")

  /*
   * Defined on accesses of Memory. Child of the ctrlOf(mem) on ancesstor path of the access if
   * ctrlOf(mem) != ctrlOf(access). Otherwise it's the ctrlOf(access) 
   * */
  val topCtrlOf = new BiManyToOneMap[PIRNode, Controller] with MetadataMap
  topCtrlOf.setName("topCtrlOf")

  /*
   * User annotation on variable value
   * */
  val boundOf = new OneToOneMap[PIRNode, Any] with MetadataMap
  boundOf.setName("boundOf")

  /*
   * Static dimension of dram
   * */
  val staticDimsOf = new OneToOneMap[IR, List[Int]] with MetadataMap
  staticDimsOf.setName("staticDimsOf")

  /*
   * bufferDepth of on chip mem
   * */
  val bufferDepthOf = new OneToOneMap[Memory, Int] with MetadataMap
  bufferDepthOf.setName("bufferDepthOf")

  /*
   * output parallelization of a node
   * */
  val parOf =  new OneToOneMap[Any, Int] with MetadataMap
  parOf.setName("parOf")

  /*
   * Number of iterations the node is running
   * */
  val itersOf =  new OneToOneMap[Any, Option[Long]] with MetadataMap
  itersOf.setName("itersOf")

  /*
   * Number of accumulative iterations before the node is active/done again with respect to enable of the
   * ContextEnable
   * */
  val scaleOf =  new OneToOneMap[Any, Option[Long]] with MetadataMap {
    override def mirror(orig:Any, clone:Any, logger:Option[Logging]=None):Unit = {}
  }
  scaleOf.setName("scaleOf")

  /*
   * Number of iterations before the node is active through out the execution of the program
   * */
  val countsOf =  new OneToOneMap[Any, Option[Long]] with MetadataMap {
    override def mirror(orig:Any, clone:Any, logger:Option[Logging]=None):Unit = {}
  }
  countsOf.setName("countsOf")


  /* 
   * If a node is mirrored, originOf points to the original copy
   * */
  val originOf = new OneToOneMap[PIRNode, PIRNode] with MetadataMap { // Many to one
    override def mirror(orig:Any, clone:Any, logger:Option[Logging]=None):Unit = {}
    override def migrateValue(from:V, to:V, k:K, logger:Option[Logging]) = {
      remove(k, from)
      super.migrateValue(from, to, k, logger)
    }
  }
  originOf.setName("originOf")

  /* ------------- Plastsim metadata (start) ---------- */
  val linkGroupOf = new OneToOneMap[Memory, Set[Memory]] with MetadataMap
  linkGroupOf.setName("linkGroupOf")
  /* ------------- Plastsim metadata (start) ---------- */

  var pirMap:EOption[PIRMap] = Right(PIRMap.empty)

}

