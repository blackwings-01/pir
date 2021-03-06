package pir.codegen

import pir.{Design, Config}
import pir.codegen._
import pir.util._
import pir.util.typealias._
import pir.mapper.{PIRMap}
import pir.exceptions._
import pir.util.misc._
import pir.spade.main._
import pir.spade.graph._
import pir.spade.util._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Set
import scala.collection.immutable.{Set => ISet}
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap
import java.io.File
import scala.reflect.runtime.universe._
import sys.process._
import scala.language.postfixOps
import scala.language.existentials

abstract class CUDotPrinter(fn:String, open:Boolean)(implicit design:Design) extends Codegen with DotCodegen {
  import spademeta._

  val scale:Int

  def io(prt:Routable):GridIO[_<:PortType, Routable]

  override lazy val stream = if (design.mapping.isDefined) newStream(fn) else newStream(fn, design.arch)

  trait Mode
  object OnlyOCU extends Mode
  object AllCU extends Mode
  object NoOCU extends Mode

  val mode:Mode = AllCU 
  //val mode:Mode = OnlyOCU 
  
  def linkColor = Color("indianred1") 
  def color(prt:PRT):Color = prt match {
    case pscu:PSCU => Color("palevioletred1")
    case pmcu:PMCU => Color("lightseagreen")
    //case pmu:PMU => Color("lightseagreen")
    case pocu:POCU => Color("orange")
    case pcu:PCU => Color("dodgerblue")
    case pmc:PMC => Color("forestgreen")
    case psb:PSB => linkColor 
    case ptop:PTop => Color("indianred1")
  }

  override def quote(n:Any):String = {
    n match {
      case (n,b) =>
        val bottom = b.asInstanceOf[Boolean]
        n match {
          case ptop:PTop if (bottom) => s"""${quote(ptop)}_bottom"""
          case ptop:PTop if (!bottom) => s"""${quote(ptop)}_top"""
        }
      case n => super[DotCodegen].quote(n)
    }
  }

  def print:Unit = { print(design.mapping) }

  def print(mapping:Option[PIRMap]):Unit = {
    emitBlock("digraph G") {
      design.arch match {
        case pn:PointToPointNetwork =>
        case sn:SwitchNetwork =>
          //emitln(s"splines=ortho;")
          val prts = mode match {
            case NoOCU =>
              sn.prts.filterNot{_.isInstanceOf[OuterComputeUnit]}
            case OnlyOCU =>
              sn.prts.filter{ prt => prt.isInstanceOf[ScalarComputeUnit] && prt.isInstanceOf[OuterComputeUnit]}
            case AllCU =>
              sn.prts
          }
          prts.foreach { prt =>
            emitPRTs(prt, mapping )
            val ins =  mode match {
              case NoOCU =>
                io(prt).ins.filterNot{ in => in.fanIns.head.src.isInstanceOf[OuterComputeUnit]}
              case OnlyOCU =>
                io(prt).ins.filter{ in => in.fanIns.head.src.isInstanceOf[ScalarComputeUnit] && in.fanIns.head.src.isInstanceOf[OuterComputeUnit]}
              case AllCU =>
                io(prt).ins
            }
            ins.foreach { in => emitInput(in, mapping) }
          }
      }
    }
    close
    if (open) { 
      s"out/bin/run -c ${getPath} &".replace(".dot", "") !
    }
  }

  def emitPRTs(prt:PRT, mapping:Option[PIRMap]) = {
    val spade = design.arch.asInstanceOf[SwitchNetwork]
    import spade.param._
    var attr = DotAttr().shape(Mrecord)
    def mappedLabel(prt:PCL):String = {
      mapping.fold(quote(prt)) { mp => mp.clmap.pmap.get(prt).fold(quote(prt)) { cl => s"${quote(prt)}|$cl"} }
    }
    val recs = ListBuffer[String]()
    prt match {
      case ptop:PTop => recs += s"$ptop" 
      case pcl:PCL => 
        def ports(dir:String) = {
          var ins = io(pcl).inAt(dir).map{io => s"<$io> $io(${indexOf(io)})"}
          var outs = io(pcl).outAt(dir).map{io => s"<$io> $io(${indexOf(io)})"}
          val maxLength = Math.max(ins.size, outs.size)
          ins = ins ++ List.fill(maxLength-ins.size){""}
          outs = outs ++ List.fill(maxLength-outs.size){""}
          val ios = ins.zip(outs).flatMap{case (i,o) => 
            if (dir=="S" || dir=="E") List(o,i)
            else List(i,o)
          }
          ios.mkString("|")
        }
        recs += s"{${ports("NW")}  | ${ports("N")}          | ${ports("NE")}}"
        recs += s"{{${ports("W")}} | {${mappedLabel(pcl)}}  | {${ports("E")}}}"
        recs += s"{${ports("SW")}  | ${ports("S")}          | ${ports("SE")}}"
      case psb:PSB => 
        recs += mapping.flatMap { mp => 
          if (io(psb).ins.exists( in => mp.fimap.contains(in))) { 
            val xbar = io(psb).outs.flatMap { out => 
              mp.fimap.get(out.ic).map{ inic => 
                val in = inic.src.asInstanceOf[PI[PModule]]; s"i-${indexOf(in)} -\\> o-${indexOf(out)}"
              }
            }.mkString(s"|") 
            Some(s"${quote(psb)}|${xbar}")
          } else {
            None
          }
        }.getOrElse(quote(psb))
    }
    val label = s"{${recs.mkString("|")}}"
    coordOf.get(prt).foreach { case (x,y) =>
      prt match {
        case pscu:PSCU if (x<0) | (x>=numCols) => attr.pos((x*scale, (y-0.3)*scale))
        case pmc:PMC => attr.pos((x*scale, (y-0.7)*scale))
        case pocu:POCU => attr.pos(((x-0.3)*scale, (y-0.3)*scale))
        case psb:PSB => attr.pos(((x-0.5)*scale, (y-0.5)*scale))
        case ptop:PTop =>
        case pcu => attr.pos((x*scale, y*scale))
      }
    }
    mapping.foreach { mp => 
      prt match {
        case prt:PCL =>
          if (mp.clmap.pmap.contains(prt) || io(prt).ins.exists( in => mp.fimap.contains(in)))
            attr.style(filled).fillcolor(color(prt))
        case prt =>
          if (io(prt).ins.exists(in => mp.fimap.contains(in)))
            attr.style(filled).fillcolor(color(prt))
      }
    }
    prt match {
      case ptop:PTop => s"$ptop" 
        emitNode(quote(ptop, false), label, DotAttr.copy(attr).pos( (numCols/2-1)*scale+scale/2, numRows*scale))
        emitNode(quote(ptop, true), label, DotAttr.copy(attr).pos( (numCols/2-1)*scale+scale/2, -scale))
      case _ =>
        emitNode(prt, label, attr)
    }
  }

  def emitInput(pin:PGI[PRT], mapping:Option[PIRMap])(implicit design:Design) = {
    val prt:PRT = pin.src
    pin.fanIns.foreach { po =>
      val pout = po.asGlobal
      val attr = DotAttr()
      mapping.foreach { m => 
        if (m.fimap.get(pin).fold(false){ _ == pout }) {
          attr.color(linkColor).style(bold)
          val label = ListBuffer[String]()
          m.vimap.pmap.get(pin).foreach { ins =>
            ins.foreach {
              case vi:VI => label += s"${vi.vector}"
              case si:SI => label += s"${si.scalar}"
              case ip:IP => label += s"${ip}"
            }
            //pout.src match {
              //case cl@(_:PCU | _:PTop | _:PMC) if m.clmap.pmap.contains(cl) =>
                //in match {
                  //case vi:VI => s"${vi.vector}"
                  //case si:SI => s"${si.vector}"
                  ////case op:OP => ""
                //}
              //case psb:PSB => s"\n(o-${indexOf(pout)})"
            //}
            //val cl = m.clmap.pmap(prt)
            //in match {
              //case op:OP =>
                //val to = op.to.filter{_.asInstanceOf[CIP].ctrler==cl}
                //label += s"to:[${to.mkString(",\n")}]\nfrom:${op}" 
              //case _ =>
            //}
          }
          m.vomap.pmap.get(pout).foreach { out =>
            out match {
              case vo:VO => label += s"${vo.vector}"
              case so:SO => label += s"${so.scalar}"
              case op:OP => label += s"${op}"
            }
          }
          attr.label(label.mkString("\n"))
        }
      }
      val to = pin.src match {
        case psb:PSB => s"$psb"
        case ptop:PTop => quote(ptop, coordOf(pout.src)._2==0)
        case _ => s"$prt:$pin"
      }
      val from = pout.src match {
        case from:PSB =>
          attr.label.foreach { l => attr.label(l + s"\n(o-${indexOf(pout)})") }
          s"$from"
        case from:PTop =>
          spade match {
            case sn:SwitchNetwork =>
              val bottom = coordOf(pin.src)._2==0 
              quote(from, bottom)
            case pn:PointToPointNetwork => quote(from)
          }
        case from => s"$from:$pout"
      }
      emitEdge(from, to, attr)
    }
  }

  addPass {
    print
  }

  override def finPass = {
    close
    endInfo(s"Finishing $name in ${getPath}...")
  }

}

class CUCtrlDotPrinter(file:String, open:Boolean)(implicit design:Design)
  extends CUDotPrinter(file, open) { 
  def shouldRun = Config.debug

  def this(file:String)(implicit design:Design) = this(file, false)
  def this(open:Boolean)(implicit design:Design) = this(Config.spadeCtrlNetwork, open)
  def this()(implicit design:Design) = this(false)

  val scale = 20

  def io(prt:Routable) = prt.ctrlIO
}

class CUScalarDotPrinter(file:String, open:Boolean)(implicit design:Design) 
  extends CUDotPrinter(file, open) { 
  def shouldRun = Config.debug

  def this(file:String)(implicit design:Design) = this(file, false)
  def this(open:Boolean)(implicit design:Design) = this(Config.spadeScalarNetwork, open)
  def this()(implicit design:Design) = this(false)
  
  val scale = 15

  def io(prt:Routable) = prt.scalarIO

}

class CUVectorDotPrinter(file:String, open:Boolean)(implicit design:Design) 
  extends CUDotPrinter(file, open) { 
  def shouldRun = Config.debug

  def this(file:String)(implicit design:Design) = this(file, false)
  def this(open:Boolean)(implicit design:Design) = this(Config.spadeVectorNetwork, open)
  def this()(implicit design:Design) = this(false)
  
  val scale = 15

  def io(prt:Routable) = prt.vectorIO
}
