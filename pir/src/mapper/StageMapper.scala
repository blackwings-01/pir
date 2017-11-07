package pir.mapper

import pir._
import pir.util.typealias._
import pir.node.{Const, ScalarOutPR, VecOutPR}

import spade._
import spade.node.{Const => PConst, PipeReg => PPR, _}

import pirc.enums._
import pirc.exceptions._

class StageMapper(implicit val design:PIR) extends Mapper with LocalRouter {

  type R = PST
  type N = ST
  val typeStr = "StageMapper"
  override def debug = PIRConfig.debugSTMapper
  override val exceptLimit = 200

  def finPass(cu:CL)(m:M):M = m

  def map(ctrler:CL, cuMap:M):M = ctrler match {
    case cu:ICL => map(cu, cuMap)
    case cu => finPass(cu)(cuMap)
  }
  
  def map(cu:ICL, cuMap:M):M = {
    if (cu.stages.isEmpty) return cuMap
    log[M](cu) {
      var mp = cuMap
      val pcu = mp.pmmap(cu).asCU
      val nodes = cu.stages
      val reses = pcu.stages
      def oor(pnodes:List[R], nodes:List[N], m:M) = OutOfResource(s"Not enough Stages in ${pcu} to map ${cu}.", pnodes, nodes, m)
      mp = bindInOrder(reses, nodes, mp, List(mapStage _), finPass(cu) _, oor _)
      mp = stageFowarding(pcu, mp)
      mp
    }
  }

  /* Forward liveOut regs in pstages that doesn't have stage corresponding to them */
  def stageFowarding(pcu:PCU, cuMap:M):M = {
    var mp = cuMap
    var preLiveOuts:Set[Reg] = Set.empty
    pcu.stages.foreach { pstage =>
      val ppregs:Set[PReg] = preLiveOuts.flatMap {reg => mp.rcmap(reg) }
      if (mp.pmmap.contains(pstage)) {
        val stage = mp.pmmap.to[ST](pstage)
        preLiveOuts = stage.liveOuts 
      } else {
        pstage.prs.foreach { ppr =>
          if (ppregs.contains(ppr.reg)) mp = mp.setFI(ppr.in, pstage.prev.get.get(ppr.reg).out)
        }
      }
    }
    pcu.stages.last.prs.foreach { case ppr@PPR(ps, pr) =>
      mp.rcmap.get(pr).foreach { regs =>
        regs.foreach { reg =>
          reg match {
            case ScalarOutPR(so) =>
              mp.vomap.get(so).foreach { _.foreach { pso => mp = mp.setFI(pso.ic, ppr.out.sliceHead.out) } }
            case VecOutPR(vo) =>
              mp.vomap.get(vo).foreach { _.foreach { pvo => mp = mp.setFI(pvo.ic, ppr.out) } }
            case _ =>
          }
        }
      }
    }
    mp
  }

  def typeMismatch(n:N, p:R, map:M):Unit = {
    throw MappingException(map, s"Type Mismatch: $n cannot be mapped to $p")
  }

  def checkStageType(n:N, p:R, map:M):Unit = {
    n match {
      //case s:WAST => p match { case p:PWAST => ; case p => typeMismatch(p) }
      //case s:RAST => p match { case p:PRAST => ; case p => typeMismatch(p) }
      case s:RDST => p match { case p:PRDST => ; case p => typeMismatch(s, p, map) }
      case _ =>
    }
  }

  def mapStage(n:N, p:R, map:M):M = {
    log[M](s"Try $n -> ${quote(p)}") {
      var mp = map
      checkStageType(n, p, mp)
      mp = mp.setPM(n, p)
      mp = mapFUOut(n, p, mp)
      mp = mapPROut(n, p, mp)
      mp = mapFUIn(n, p, mp)
      mp = mapPRIn(n, p, mp)
      mp
    }
  }

  def mapFUIn(n:ST, p:PST, map:M):M = {
    var mp = map
    val fu = n.fu.get
    val pfu = p.funcUnit 
    // Check Operand 
    fu.operands.zipWithIndex.foreach { case (oprd,i) =>
      mp = mapInput(oprd, pfu.operands(i), mp)
    }
    mp
  }

  def mapFUOut(n:ST, p:PST, map:M):M = {
    var mp = map
    val fu = n.fu.get
    val pfu = p.funcUnit 
    // Check Operation 
    if (!pfu.ops.contains(fu.op)) throw OpNotSupported(p, n, mp)
    // Check Result 
    mp = mapOutput(fu.out, pfu.out, mp)
    mp 
  }

  def mapPRIn(stage:ST, pstage:PST, map:M):M = {
    var mp = map
    // single reg can be mapped to multiple preg because of broadcast at the output
    // for each of these pprs, mapp their corresponding inputs
    stage.prs.foreach { pr =>
      mp.rcmap(pr.reg).foreach { preg =>
        val ppr = pstage.get(preg)
        if (!mp.ipmap.contains(pr.in)) mp = mp.setIP(pr.in, ppr.in)
        mp = mapFanIn(pr.in, ppr.in, mp)
      }
    }
    mp
  }

  def mapPROut(stage:ST, pstage:PST, map:M):M = {
    var mp = map
    val nres = pstage.prs.size
    val nnode = stage.prs.size
    if (nnode > nres) throw OutOfResource(s"Not enough PipeReg in ${pstage} to map ${stage}.", pstage.prs, stage.prs, mp)

    val rcmap = mp.rcmap
    stage.prs.foreach { pr =>
      if (stage.liveOuts.contains(pr.reg)) {
        rcmap(pr.reg).foreach { preg =>
          val ppr = pstage.get(preg)
          mp = mapOutput(pr.out, ppr.out, mp)
        }
      }
    }
    mp
  }
}
case class OpNotSupported(ps:PST, s:ST, mp:PIRMap)(implicit mapper:Mapper, design:PIR) extends MappingException(mp) {
  override val msg = s"${ps}:[${ps.param.ops.mkString(",")}] doesn't support op:${s.fu.get.op} in ${s}"
}
//case class OutOfOperand(ps:PST, s:ST, pnodes:List[PI[_<:PModule]], nodes:List[IP], mp:PIRMap)(implicit mapper:Mapper, design:PIR) 
  //extends OutOfResource(mapper, s"Not enough operands in ${ps} to map ${s}.", pnodes, nodes, mp)
case class StageRouting(n:ST, p:PST, mp:PIRMap)(implicit mapper:Mapper, design:PIR) extends MappingException(mp) {
  override val msg = s"Fail to map ${n} to ${p}"
}