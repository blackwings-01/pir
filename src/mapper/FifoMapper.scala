package pir.mapper
import pir.{PIR, Config}
import pir.util.typealias._
import pir.pass.PIRMapping
import pir.node.{PipeReg => PR, VecInPR, LoadPR}
import spade.node.{PipeReg => PPR}
import spade.util._
import spade.main._
import pirc.exceptions._
import pirc.enums._
import pir.util.PIRMetadata
import spade.node._

import scala.collection.mutable.ListBuffer
import scala.collection.immutable.Set
import scala.collection.immutable.HashMap
import scala.collection.immutable.Map

class FifoMapper(implicit val design:PIR) extends Mapper with LocalRouter {
  type N = LMem
  type R = PLMem
  val typeStr = "FifoMapper"
  override def debug = Config.debugSFifoMapper
  import pirmeta.{indexOf => _, _}
  import spademeta._

  def finPass(cu:CL)(m:M):M = m 

  def constrain(n:N, r:R, map:M):M = {
    var mp = map
    mp = mp.setPM(n, r)
    mp = mapOutPort(n.readPort, r.readPort, mp)
    mp = mapInPort(n.writePort, r.writePort, mp)
    mp = mapMux(n.writePortMux, r.writePortMux, mp)
    mp
  }

  def resFunc(n:N, m:M, triedRes:List[R]):List[R] = emitBlock(s"resFunc(${n.ctrler}.$n)"){
    n match {
      case n:SMem => sResFunc(n, m, triedRes)
      case n:VFIFO => vResFunc(n, m, triedRes)
    }
  }

  def vResFunc(n:VFIFO, m:M, triedRes:List[R]):List[R] = {
    val vis = pir.util.collectIn[VI](n.writePort)
    assert(vis.size==1, s"Vector FIFO can only have a single writer! ${n.ctrler}.$n's writer ${vis}")
    val pvi = m.vimap(vis.head)
    val rs = spade.util.collectOut[R](pvi)
    assert(rs.size==1, s"$pvi connect to multiple vfifos=${rs}")
    List(rs.head)
  }

  def sResFunc(n:SMem, m:M, triedRes:List[R]):List[R] = {
    val cu = n.ctrler
    val pcu = m.pmmap(cu)
    val reses = cu match {
      case cu:MC if n.name.get=="data" => pcu.sbufs.filter { sbuf => nameOf(sbuf) == s"s${n.name.get}" }
      case cu:MC if cu.mctpe==TileLoad => pcu.sbufs.filter { sbuf => nameOf(sbuf) == s"r${n.name.get}" }
      case cu:MC if cu.mctpe==TileStore => pcu.sbufs.filter { sbuf => nameOf(sbuf) == s"w${n.name.get}" }
      case cu => pcu.sbufs
    }
    dprintln(s"MC filtered reses=[${reses.mkString(",")}]")
    reses.diff(triedRes).filterNot{ r => m.pmmap.contains(r) }
  }

  def map(cu:CU, pirMap:M):M = {
    log(cu) {
      bind[R,N,M](
        allNodes=cu.smems,
        initMap=pirMap, 
        constrain=constrain _,
        resFunc=resFunc _, //(n, m, triedRes) => List[R]
        finPass=(m:M) => m
      )
    }
  }

  def map(cu:CL, pirMap:M):M = {
    cu match {
      case cu:CU => map(cu, pirMap)
      case cu => finPass(cu)(pirMap)
    }
  }
}
