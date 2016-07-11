import dhdl.graph._
import dhdl.graph.{MemoryController => MemCtrl, MetaPipeline => MetaPipe}
import dhdl.graph
import dhdl.codegen._
import dhdl.Design

object DotProduct extends Design {

  def main(args: String*) = {
    val tileSize = Const(4l)
    val dataSize = ArgIn()

    // Pipe.fold(dataSize by tileSize par outerPar)(out){ i =>
    val outer = ComputeUnit(parent="Top", tpe=Sequential){ implicit PR =>
      CounterChain(name="i", dataSize by tileSize)
    }
    // b1 := v1(i::i+tileSize)
    val tileLoadA = MemCtrl (name="A", parent=outer, dram="A"){ implicit PR =>
      val ic = CounterChain.copy(outer, "i")
      CounterChain(name="it", ic(0) until Const(-1) by Const(1))
    }
    // b2 := v2(i::i+tileSize)
    val tileLoadB = MemCtrl (name="B", parent=outer, dram="B"){ implicit PR =>
      val ic = CounterChain.copy(outer, "i")
      CounterChain(name="it", ic(0) until Const(-1) by Const(1))
    }
    //Pipe.reduce(tileSize par innerPar)(Reg[T]){ii => b1(ii) * b2(ii) }{_+_}
    ComputeUnit (name="inner", parent=outer, tpe=Pipe) { implicit PR =>
      
      // StateMachines / CounterChain
      val ii = CounterChain(tileSize by Const(1l)) //Local
      val itA = CounterChain.copy(tileLoadA, "it")
      val itB = CounterChain.copy(tileLoadB, "it")

      val s0::s1::s2::_ = Stages(3)
      // SRAMs
      val A = SRAM(size=32, write=tileLoadA, readAddr=ii(0), writeAddr=itA(0))
      val B = SRAM(size=32, write=tileLoadB, readAddr=ii(0), writeAddr=itB(0))

      // Pipeline Stages 
      Pipeline {
        Stage(s0, op1=A.load, op2=B.load, op=FixMul, result=PR.reduce(s0))
        Stage.reduce(s1, op=FixAdd) 
        Stage(s2, op1=PR.reduce(s1), op=Bypass, result=PR.temp(s0)) 
      }
      //Last stage can be removed if PR.reduce and PR.scalarOut map to the same register
    }
  }

}
