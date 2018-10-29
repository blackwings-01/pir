package spade.test

import prism.test._
import prism.graph._
import prism.graph.implicits._
import scalapb.json4s.JsonFormat

class ParamTest extends UnitTest { self =>

  // Param with ScalaPB
  "ParamTest1" should "success" in {
    import spade.param3._
    val topParam = TopParam(
      wordWidth=32,
      vecWidth=16,
      clockFrequency=1000000000,
      burstSize=512,
      centrolPattern=AsicPattern()
    )
    //println(topParam.ins.size) == 0
    //val topParam = TopParam.defaultInstance
    //val nodes = topParam.accumIn({ n => false}, logger=Some(prism.util.ConsoleLogger))
    //println(JsonFormat.toJsonString(topParam))
    //println(topParam.toJsonString)
  }

  // Param with prism Node
  "ParamTest2" should "success" in {
    import spade.param2._
    val topParam = TopParam()
    val loader = new DefaultParamLoader {
      def getOpt[T](name:String):Option[T] = 
        Map(
          "word"->17,
          "pattern"->"checkerboard",
          "pcu-vfifo"->11
        ).get(name).asInstanceOf[Option[T]]
    }
    assert(loader.topParam.wordWidth==17)
    assert(loader.topParam.pattern.asInstanceOf[Checkerboard].cu1.fifoParamOf("vec").get.count==11)
  }

}