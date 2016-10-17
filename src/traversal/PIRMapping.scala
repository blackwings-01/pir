package pir.graph.traversal
import pir.graph._
import pir._
import pir.codegen.Printer
import pir.misc._
import pir.graph.mapper._
import scala.util.{Try, Success, Failure}

object MapPrinter extends Printer { 
  override val stream = newStream(Config.mapFile)
  def printMap(mapping:PIRMap)(implicit design:Design) = {
    if (Config.debug) {
      emitTitleComment(s"Mapping")
      mapping.printPMap(this, design)
    }
  }

  def printException(e:PIRException) = {
    if (Config.debug) {
      emitTitleComment("Mapping Exceptions:")
      emitln(s"$e ${e.msg}")
    }
  }
}

class PIRMapping(implicit val design: Design) extends Traversal{

  var mapping:PIRMap = _
  var success = false

  def fail = !success

  val siMapper = new ScalarInMapper()
  val sramMapper = new SRAMMapper()
  val stageMapper = new StageMapper()
  val outputMapper = new OutputMapper()
  val viMapper = new VecInMapper()
  val ctrlMapper = new CtrlMapper()
  val regAlloc = new RegAlloc() {
    override def finPass(cu:InnerController)(m:M):M = { stageMapper.map(cu, m) }
  }
  val ctrMapper = new CtrMapper() { 
    override def finPass(cu:InnerController)(m:M):M = { 
      var cmap = ctrlMapper.map(cu, m)
      regAlloc.map(cu, cmap)
    }
  }
  val cuMapper:CUMapper = CUMapper(outputMapper, viMapper, { case (m:PIRMap) =>
    try {
      m.clmap.map.foldLeft(m) { case (pm, (ctrler, _)) =>
        var cmap = pm
        cmap = siMapper.map(ctrler, cmap)
        ctrler match {
          case cu:InnerController => 
            cmap = sramMapper.map(cu, cmap)
            cmap = ctrMapper.map(cu, cmap)
            //cmap = stageMapper.map(cu, cmap)
          case t:Top => ctrlMapper.map(t, cmap)
          case _ => assert(false, s"Unknown ctrler:$ctrler")
        }
        cmap
      }
    } catch {
      case e:MappingException => throw PassThroughException(cuMapper, e, m)
      case e:Throwable => throw e 
    } 
  })

  override def reset = {
    mapping = null
    success = false
  }

  override def traverse = {
    Try(mapping=cuMapper.map(PIRMap.empty)) match {
      case Success(_) =>
        success = true
        info(s"Mapping succeeded") 
        MapPrinter.printMap(mapping)
      case Failure(e) =>
        success = false
        info(s"Mapping failed")
        e match {
          case PassThroughException(mapper, e, m) =>
            mapping = m
            MapPrinter.printMap(mapping)
            MapPrinter.printException(e)
          case e:MappingException =>
            MapPrinter.printException(e)
          case e:PIRException => 
            MapPrinter.printException(e)
          case e => throw e 
        }
    }
    MapperLogger.close
    MapPrinter.close
  } 

  override def finPass = {
    MapperLogger.close
    info("Finishing PIR Mapping")
  }
}
