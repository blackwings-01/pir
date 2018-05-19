package pir
package codegen

import pir.node._
import spade.node._
import prism.collection.mutable._
import sys.process._

class PlastisimConfigCodegen(implicit compiler: PIR) extends PlastisimCodegen {
  import pirmeta._

  val fileName = s"$compiler.psim"
  lazy val SPATIAL_HOME = Config.SPATIAL_HOME.getOrElse(s"Please set SPATIAL_HOME for using trace!")
  lazy val appPath = s"${SPATIAL_HOME}${separator}gen${separator}${compiler.name}"
  lazy val tracePath = s"${appPath}${separator}traces"

  // Execution of codegen
  override def runPass = {
    super.runPass
    linkGroupOf.values.toSet.foreach { link => emitLink(link) }
    emitNetwork
  }

  override def emitNode(n:N) = n match {
    case n:Node => emitNetworkNode(n)
    case n => super.visitNode(n)
  }

  def emitNetworkNode(n:Node) = {
    val cuP = globalOf(n).get
    emitNodeBlock(s"node ${quote(n)} # ${quote(cuP)}") {
      emitNodeSpecs(n)
      emitInLinks(n)
      emitOutLinks(n)
    }
  }

  def emitNodeSpecs(n:ContextEnable) = {
    val cuP = globalOf(n).get
    cuP match {
      case cuP:DramFringe if PIRConfig.loadTrace =>
        val path = s"${tracePath}${separator}${nameOf(cuP)}.trace"
        if (exists(path)) {
          val size = cuP.collectDown[StreamOut]().filter { _.field == "size" }.head
          emitln(s"offset_trace = ${path}")
          emitln(s"size = ${boundOf(size)}")
        } else {
          err(s"trace file for ${cuP} at ${path} does not exist!")
        }
      case cuP:FringeStreamIn =>
        emitln(s"start_at_tokens = 1024 # number of stream inputs")
      case cuP:FringeStreamOut =>
        emitln(s"start_at_tokens = 1024 # number of stream outputs")
      case cuP:pir.node.ArgFringe =>
        ctrlOf(n) match {
          case _:ArgInController =>
            emitln(s"start_at_tokens = 1")
          case _:ArgOutController =>
            emitln(s"stop_after_tokens = 1")
        }
      case cuP =>
        emitln(s"lat = ${latencyOf(n).get}")
    }
  }

  def emitNodeBlock(n:Any)(block: => Unit) = dbgblk(s"emitNodeBlock($n)") {
    emitBlock(s"$n", b=NoneBraces)(block)
  }

  override def emitComment(msg:String) = emitln(s"# $msg")

  def emitNetwork = {
    if (isDynamic(topS)) {
      val topParam = compiler.arch.topParam.asInstanceOf[DynamicMeshTopParam]
      import topParam._
      topParam.networkParams.foreach { networkParam =>
        val tp = networkParam.bct
        val nr = numTotalRows
        val nc = numTotalCols
        val sq = math.max(nr, nc)
        emitNodeBlock(s"net ${quote(tp)}net") {
          emitln(s"cfg = mesh_generic.cfg")
          emitln(s"dim[0] = $sq")
          emitln(s"dim[1] = $sq")
          emitln(s"num_classes = ${networkParam.numVirtualClasses}")
        }
      }
    }
  }

  def emitLink(n:Link) = dbgblk(s"emitLink(${quote(n)})") {
    val srcs = srcsOf(n)
    val dsts = dstsOf(n)
    val isStatic = isStaticLink(n)
    val linkstr = if (isStatic) "" else "net"

    emitNodeBlock(s"${linkstr}link ${quote(n)}") {
      val tp = pinTypeOf(n)
      emitln(s"type = ${quote(tp)}")
      srcs.zipWithIndex.foreach { case (src,idx) =>
        emitln(s"src[$idx] = ${quote(src)}")
      }
      dsts.zipWithIndex.foreach { case (dst,idx) =>
        emitln(s"dst[$idx] = ${quote(dst)}")
      }
      if (isStatic) {
        srcs.zipWithIndex.foreach { case (src, srcIdx) =>
          dsts.zipWithIndex.foreach { case (dst, dstIdx) =>
            emitln(s"lat[$srcIdx, $dstIdx] = ${staticLatencyOf(src, dst).get}")
          }
        }
      } else {
        emitln(s"net = ${quote(tp)}net")
        val vc = assertUnify(n, "vc") { mem => vcmap.mappedValue(mem) }
        emitln(s"class = $vc")
        val sids = srcs.map(src => globalOf(src).get.id)
        val dids = dsts.map(dst => globalOf(dst).get.id)
        sids.zipWithIndex.foreach { case (sid, idx) =>
          emitln(s"src_id[$idx] = $sid")
        }
        dids.zipWithIndex.foreach { case (did, idx) =>
          emitln(s"dst_id[$idx] = $did")
        }
        val saddrs = srcs.map(src => addrOf(src).get)
        val daddrs = dsts.map(dst => addrOf(dst).get)
        saddrs.zipWithIndex.foreach { case (saddr, idx) =>
          emitln(s"saddr[$idx] = $saddr")
        }
        daddrs.zipWithIndex.foreach { case (daddr, idx) =>
          emitln(s"daddr[$idx] = $daddr")
        }
      }
    }
  }

  def emitInLinks(n:Node) = dbgblk(s"emitInLinks($n)") {
    inlinksOf(n).zipWithIndex.foreach { case ((link, reads), idx) =>
      emitln(s"link_in[$idx] = ${quote(link)}")
      emitln(s"scale_in[$idx] = ${constItersOf(reads)}")
      emitln(s"buffer[$idx]=${bufferSizeOf(reads).get}")
    }
  }

  def emitOutLinks(n:Node) = dbgblk(s"emitOutLinks($n)") {
    outlinksOf(n).zipWithIndex.foreach { case ((link, writes), idx) =>
      emitln(s"link_out[$idx] = ${quote(link)}")
      emitln(s"scale_out[$idx] = ${constItersOf(writes)}")
    }
  }

}
