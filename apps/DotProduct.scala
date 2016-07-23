import pir.graph._
import pir.graph.{MemoryController => MemCtrl, MetaPipeline => MetaPipe}
import pir.graph
import pir.codegen._
import pir.Design
import pir.PIRApp
import pir.PIRMisc._

/* Example PIR using block (User facing PIR)*/
object DotProduct extends PIRApp {
  def main(args: String*) = {
    val tileSize = Const(4l)
    val dataSize = ArgIn()
    val output = ArgOut()

    // Pipe.fold(dataSize by tileSize par outerPar)(out){ i =>
    val outer = ComputeUnit(name="outer", parent="Top", tpe=MetaPipeline){ implicit PL =>
      val ds = ScalarIn(dataSize)
      CounterChain(name="i", ds.out by tileSize)
    }
    // b1 := v1(i::i+tileSize)
    val tileLoadA = MemCtrl (name="tileLoadA", parent=outer, dram="A"){ implicit PL =>
      val ic = CounterChain.copy(outer, "i")
      val it = CounterChain(name="it", Const(0) until tileSize by Const(1))
      val s0::_ = Stages(1)
      Stage(s0, op1=it(0), op2=ic(0), op=FixAdd, result=PL.vecOut(s0))
    }
    // b2 := v2(i::i+tileSize)
    val tileLoadB = MemCtrl (name="tileLoadB", parent=outer, dram="B"){ implicit PL =>
      val ic = CounterChain.copy(outer, "i")
      val it = CounterChain(name="it", Const(0) until tileSize by Const(1))
      val s0::_ = Stages(1)
      Stage(s0, op1=it(0), op2=ic(0), op=FixAdd, result=PL.vecOut(s0))
    }
    //Pipe.reduce(tileSize par innerPar)(Reg[T]){ii => b1(ii) * b2(ii) }{_+_}
    ComputeUnit (name="inner", parent=outer, tpe=Pipe) { implicit PL =>
      
      // StateMachines / CounterChain
      val ii = CounterChain(tileSize by Const(1l)) //Local
      val itA = CounterChain.copy(tileLoadA, "it")
      val itB = CounterChain.copy(tileLoadB, "it")

      val s0::s1::s2::_ = Stages(3)
      // SRAMs
      val A = SRAM(size=32, write=tileLoadA, readAddr=ii(0), writeAddr=itA(0))
      val B = SRAM(size=32, write=tileLoadB, readAddr=ii(0), writeAddr=itB(0))
      // ScalarBuffers
      val out = ScalarOut(output)

      // Pipeline Stages 
      Stage(s0, op1=A.load, op2=B.load, op=FixMul, result=PL.reduce(s0))
      Stage.reduce(s1, op=FixAdd) 
      Stage(s2, op1=PL.reduce(s1), op=Bypass, result=PL.scalarOut(s2, output)) 
      //Last stage can be removed if PL.reduce and PL.scalarOut map to the same register
    }
  }

}
