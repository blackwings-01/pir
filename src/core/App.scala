package pir

import pir.node._
import pir.util._

import spade._
import spade.arch._

import pirc._
import pirc.util._

import scala.language.implicitConversions

trait PIRApp extends PIR {
  var arch:Spade = SN2x2
  override def name:String = this.getClass().getSimpleName().replace("$","")
  
  def setArgs(args: Array[String]):Unit = {
    args.foreach { 
      case arg if arg.contains("--arch") => arch = ConfigFactory.getArch(arg.split("=")(1))
      case arg if arg.contains("--") => 
        Config.setOption(arg.replace("--","").trim)
        PIRConfig.setOption(arg.replace("--","").trim)
        SpadeConfig.setOption(arg.replace("--","").trim)
      case arg if arg.contains("=") =>
        val k::v::_ = arg.split("=").toList
        top.argIns.filter {_.name==Some(k)}.foreach { argIn =>
          argIn.bound(toValue(v))
        }
      case arg =>
    }
  }

  def dramDefault = arch.dram.dramDefault

  def setDram(start:Int, array:Iterable[AnyVal]) = {
    array.zipWithIndex.foreach { case (a, i) => dramDefault(start + i) = a }
  }

  def main(top:Top): Any 
  def main(args: String): Unit = main(args.split(" "))
  def main(args: Array[String]): Unit = {
    info(s"args=[${args.mkString(", ")}]")
    reset
    top = Top().updateBlock(main) 
    setArgs(args)
    endInfo(s"Finishing graph construction for ${this}")
    run
  }
}

