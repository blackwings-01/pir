package pir.test

import pir.{Design, Config}
import pir.typealias._
import pir.misc._
import pir.graph._
import pir.graph.enums._
import pir.graph.traversal.{CUDotPrinter}
import pir.plasticine.main._
import pir.plasticine.config._
import pir.plasticine.graph.{ComputeUnit => PCU, Top => PTop, SwitchBoxes, Node => PNode }
import pir.graph.mapper._
import pir.graph.traversal._
import scala.language.reflectiveCalls

import org.scalatest._
import scala.util.{Try, Success, Failure}

class CUSwitchMapperTest extends UnitTest with Metadata {

  def genSwitchNetworkConfig = new Spade {
    val numLanes = 4
    val numRowCUs = 4
    val numColCUs = 4
    val numRCUs = numRowCUs * numColCUs
    val numVins = 4
    val numRegs = 20
    val wordWidth = 32
    val top = PTop(numLanes, 0, 0)
    val ttcus = Nil
    val switchBoxes = SwitchBoxes(numRowCUs+1, numColCUs+1, numLanes)
    override val sbs = switchBoxes.flatten 
    for (i <- 0 until switchBoxes.size) {
      for (j <- 0 until switchBoxes.head.size) {
        coordOf(switchBoxes(i)(j)) = (i,j) 
      }
    }
    val cuArray = List.tabulate(numRowCUs, numColCUs) { case (i, j) =>
      ConfigFactory.genRCU(numLanes, numVins, 0, numRegs).coord(i,j)
    }
    /* Network Constrain */ 
    ConfigFactory.genSwitchNetwork(cuArray, switchBoxes)
    val rcus = cuArray.flatten
  }

  def quote(pne:PNE)(implicit spade:Spade) = PNode.quote(pne)

  lazy val design = new Design {
    // PNodes
    implicit override val arch = genSwitchNetworkConfig
    val mapper:CUSwitchMapper = new CUSwitchMapper(new ScalarOutMapper())
    def checkRange(start:PCU, min:Int, max:Int, shouldContain:List[PCU], shouldNotContain:List[PCU]) = {
      def cuCons(pcu:PCU, path:CUSwitchMapper.Path) = (path.size >= min) && (path.size < max) && (pcu!=start)
      def sbCons(psb:PSB, path:CUSwitchMapper.Path) = (path.size < max)
      val result = mapper.advance(start, cuCons _, sbCons _)
      // println(s"start: ${quote(start)}")
      //result.foreach { case (to, path) =>
      //  println(s"- hop:${path.size} to:${quote(to)} path:${CUSwitchMapper.quote(path)}")
      //}
      //println(s"number of options: ${result.size}")
      val neighbors = result.map(_._1)
      shouldContain.foreach { c =>
        assert(neighbors.contains(c))
      }
      shouldNotContain.foreach { c =>
        assert(!neighbors.contains(c))
      }
    }
    new CUDotPrinter("TestSwitch.dot").print((arch.cus, arch.sbs))
  }

  "SwitchBox Connection 1 hop" should "success" in {
    val arr = design.arch.cuArray
    val shouldContain = List(arr(2)(1), arr(1)(2));
    design.checkRange(arr(1)(1), 1, 2, shouldContain, design.arch.cus.diff(shouldContain))
  }
  "SwitchBox Connection 2 hop" should "success" in {
    val arr = design.arch.cuArray
    val shouldContain = List(arr(2)(0), arr(2)(1), arr(2)(2))
    design.checkRange(arr(1)(1), 2, 3, shouldContain, design.arch.cus.diff(shouldContain))
  }
  "SwitchBox Connection 3 hop" should "success" in {
    val arr = design.arch.cuArray
    val shouldContain = List(arr(1)(0), arr(2)(0), arr(3)(0),
                         arr(2)(1), arr(3)(1),
                         arr(1)(2), arr(2)(2), arr(3)(2),
                         arr(2)(3))
    design.checkRange(arr(1)(1), 3, 4, shouldContain, design.arch.cus.diff(shouldContain))
  }
  "SwitchBox Connection 4 hop" should "success" in {
    val arr = design.arch.cuArray
    val shouldNotContain = List(arr(0)(3), arr(1)(1), arr(2)(1))
    design.checkRange(arr(1)(1), 4, 5, design.arch.cus.diff(shouldNotContain), shouldNotContain)
  }

  "SwitchBox Connection 5 hop" should "success" in {
    val arr = design.arch.cuArray
    val shouldNotContain = List(arr(1)(1))
    design.checkRange(arr(1)(1), 1, 7, design.arch.cus.diff(shouldNotContain), shouldNotContain)
  }

  "SwitchBox Connection 5 Compare BFS advance with DFS advance" should "success" in {
    val arr = design.arch.cuArray
    val start = arr(1)(1); val min = 1; val max = 7
    def cuCons(pcu:PCU, path:CUSwitchMapper.Path) = (path.size >= min) && (path.size < max) && (pcu!=start)
    def sbCons(psb:PSB, path:CUSwitchMapper.Path) = (path.size < max)
    val result1 = design.mapper.advanceBFS(start, cuCons _, sbCons _)
    val result2 = design.mapper.advanceDFS(start, cuCons _, sbCons _)
    result1 should equal (result2)
  }

  "SwitchBox Mapping" should "success" in {
    new Design {
      top = Top()
      // Nodes
      val vts = List.fill(5)(Vector())
      val c0 = Pipeline("c0", top, Nil){ implicit CU => 
        CU.vecOut(vts(0)) 
      }
      val c1 = Pipeline("c1", top, Nil){ implicit CU => 
        CU.vecIn(vts(0))
        CU.vecOut(vts(1)) 
      }
      val c2 = Pipeline("c2", top, Nil){ implicit CU => 
        CU.vecIn(vts(1))
        CU.vecIn(vts(0))
        CU.vecOut(vts(2)) 
      }
      val c3 = Pipeline("c3", top, Nil){ implicit CU => 
        CU.vecIn(vts(2))
        CU.vecOut(vts(3)) 
        CU.vecIn(vts(1))
      }
      val c4 = Pipeline("c4", top, Nil){ implicit CU => 
        CU.vecIn(vts(1))
        CU.vecIn(vts(3))
      }
      val cus = c0::c1::c2::c3::c4::Nil
      top.updateFields(cus, Nil, Nil, vts, Nil)
      // PNodes
      implicit override val arch = genSwitchNetworkConfig
      // Mapping
      val mapper:CUSwitchMapper = new CUSwitchMapper(new ScalarOutMapper())

      new PIRNetworkDotPrinter().run
      Try {
        mapper.mapCUs(arch.cus, cus, PIRMap.empty, (m:PIRMap) => m)
      } match {
        case Success(mapping) => 
          new CUDotPrinter("TestSwitchMapping.dot").print((arch.cus, arch.sbs), mapping)
        case Failure(e) => 
          new CUDotPrinter("TestSwitchMapping.dot").print((arch.cus, arch.sbs)); throw e
      }
    }
  }

  "SwitchBox Mapping - DotProduct" should "success" in {
    new Design {
      top = Top()
      // Nodes
      val vts = List.fill(13)(Vector())
      val c00 = Pipeline("c00", top, Nil){ implicit CU => 
        CU.vecOut(vts(5)) 
      }
      val c01 = Pipeline("c01", top, Nil){ implicit CU => 
        CU.vecOut(vts(6)) 
      }
      val c0 = Pipeline("c0", top, Nil){ implicit CU => 
        CU.vecOut(vts(0)) 
        CU.vecIn(vts(5))
        CU.vecIn(vts(6))
      }
      val c10 = Pipeline("c10", top, Nil){ implicit CU => 
        CU.vecOut(vts(7)) 
      }
      val c11 = Pipeline("c11", top, Nil){ implicit CU => 
        CU.vecOut(vts(8)) 
      }
      val c1 = Pipeline("c1", top, Nil){ implicit CU => 
        CU.vecIn(vts(7))
        CU.vecIn(vts(8))
        CU.vecOut(vts(1)) 
      }
      val c20 = Pipeline("c20", top, Nil){ implicit CU => 
        CU.vecOut(vts(9)) 
      }
      val c21 = Pipeline("c21", top, Nil){ implicit CU => 
        CU.vecOut(vts(10)) 
      }
      val c2 = Pipeline("c2", top, Nil){ implicit CU => 
        CU.vecOut(vts(2)) 
        CU.vecIn(vts(9))
        CU.vecIn(vts(10))
      }
      val c30 = Pipeline("c30", top, Nil){ implicit CU => 
        CU.vecOut(vts(11)) 
      }
      val c31 = Pipeline("c31", top, Nil){ implicit CU => 
        CU.vecOut(vts(12)) 
      }
      val c3 = Pipeline("c3", top, Nil){ implicit CU => 
        CU.vecOut(vts(3)) 
        CU.vecIn(vts(11))
        CU.vecIn(vts(12))
      }
      val c4 = Pipeline("c4", top, Nil){ implicit CU => 
        CU.vecIn(vts(0))
        CU.vecIn(vts(1))
        CU.vecIn(vts(2))
        CU.vecIn(vts(3))
      }
      val cus = c00::c01::c0::c10::c11::c1::c20::c21::c2::c30::c31::c3::c4::Nil
      top.updateFields(cus, Nil, Nil, vts, Nil)
      // PNodes
      implicit override val arch = genSwitchNetworkConfig
      // Mapping
      val mapper:CUSwitchMapper = new CUSwitchMapper(new ScalarOutMapper())

      new PIRNetworkDotPrinter().run
      Try {
        mapper.mapCUs(arch.cus, cus, PIRMap.empty, (m:PIRMap) => m)
      } match {
        case Success(mapping) => 
          new CUDotPrinter("TestDotProduct.dot").print((arch.cus, arch.sbs), mapping)
        case Failure(e) => 
          new CUDotPrinter("TestDotProduct.dot").print((arch.cus, arch.sbs)); throw e
      }
    }
  }

  "SwitchBox Mapping: Dependency out of order" should "success" in {
    new Design {
      top = Top()
      // Nodes
      val vts = List.fill(5)(Vector())
      val c0 = Pipeline("c0", top, Nil){ implicit CU => 
        CU.vecOut(vts(0)) 
      }
      val c1 = Pipeline("c1", top, Nil){ implicit CU => 
        CU.vecIn(vts(0))
        CU.vecOut(vts(1)) 
      }
      val c2 = Pipeline("c2", top, Nil){ implicit CU => 
        CU.vecIn(vts(1))
        CU.vecIn(vts(0))
        CU.vecOut(vts(2)) 
      }
      val c3 = Pipeline("c3", top, Nil){ implicit CU => 
        CU.vecIn(vts(1))
        CU.vecIn(vts(2))
        CU.vecOut(vts(3)) 
      }
      val c4 = Pipeline("c4", top, Nil){ implicit CU => 
        CU.vecIn(vts(1))
        CU.vecIn(vts(3))
      }
      val cus = (c0::c1::c2::c3::c4::Nil).reverse
      top.updateFields(cus, Nil, Nil, vts, Nil)
      // PNodes
      implicit override val arch = genSwitchNetworkConfig
      // Mapping
      val mapper:CUSwitchMapper = new CUSwitchMapper(new ScalarOutMapper())
      new PIRNetworkDotPrinter().run
      Try {
        mapper.mapCUs(arch.cus, cus, PIRMap.empty, (m:PIRMap) => m)
      } match {
        case Success(mapping) => 
          new CUDotPrinter("TestOODependency.dot").print((arch.cus, arch.sbs), mapping)
        case Failure(e) => 
          new CUDotPrinter("TestOODependency.dot").print((arch.cus, arch.sbs)); throw e
      }
      MapperLogger.close
    }
  }

}

