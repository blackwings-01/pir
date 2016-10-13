package pir

import pir.graph._
import graph.mapper._
import pir.codegen.{Printer, Logger}
import scala.language.implicitConversions

package object misc extends Logger {
  implicit def pr_to_inport(pr:PipeReg):InPort = pr.in
  implicit def pr_to_outport(pr:PipeReg):OutPort = pr.out
  implicit def sram_to_outport(sram:SRAM):OutPort = sram.readPort
  implicit def ctr_to_port(ctr:Counter):OutPort = ctr.out
  implicit def const_to_port(const:Const):OutPort = const.out
  implicit def mExcep_to_string(e:MappingException):String = e.toString
  implicit def range_to_bound(r:Range)(implicit design:Design) = r by Const("1d") 
  implicit def sRange_to_bound(r:scala.collection.immutable.Range)(implicit design:Design): (OutPort, OutPort, OutPort) =
    (Const(s"${r.min}i").out, Const(s"${r.max+1}i").out, Const(s"${r.step}i").out)
}

package object typealias {
  // PIR Nodes 
  type Node  = pir.graph.Node
  type CL    = pir.graph.Controller
  type ICL   = pir.graph.InnerController
  type OCL   = pir.graph.OuterController
  type CU    = pir.graph.ComputeUnit
  type SCL   = pir.graph.SpadeController
  type TT    = pir.graph.TileTransfer
  type MC    = pir.graph.MemoryController
  type PRIM  = pir.graph.Primitive
  type Reg   = pir.graph.Reg
  type PR    = pir.graph.PipeReg
  type SRAM  = pir.graph.SRAM
  type CC    = pir.graph.CounterChain
  type Ctr   = pir.graph.Counter
  type FU    = pir.graph.FuncUnit
  type ST    = pir.graph.Stage
  type EST   = pir.graph.EmptyStage
  type WAST  = pir.graph.WAStage
  type RDST  = pir.graph.ReduceStage
  type ACST  = pir.graph.AccumStage
  type SI    = pir.graph.ScalarIn
  type SO    = pir.graph.ScalarOut
  type VI    = pir.graph.VecIn
  type DVI   = pir.graph.DummyVecIn
  type VO    = pir.graph.VecOut
  type DVO   = pir.graph.DummyVecOut
  type I     = pir.graph.Input
  type PT    = pir.graph.Port
  type IP    = pir.graph.InPort
  type OP    = pir.graph.OutPort
  type CB    = pir.graph.CtrlBox
  type LUT   = pir.graph.LUT
  type TOLUT = pir.graph.TokenOutLUT
  type EnLUT = pir.graph.EnLUT
  type UC    = pir.graph.UDCounter
  type Const = pir.graph.Const
  type Top   = pir.graph.Top
  // Spade Nodes
  type PNode     = pir.plasticine.graph.Node
  type PCL       = pir.plasticine.graph.Controller
  type PCU       = pir.plasticine.graph.ComputeUnit
  type PTT       = pir.plasticine.graph.TileTransfer
  type PTop      = pir.plasticine.graph.Top
  type PNE       = pir.plasticine.graph.NetworkElement
  type PReg      = pir.plasticine.graph.Reg
  type PPR       = pir.plasticine.graph.PipeReg
  type PCtr      = pir.plasticine.graph.Counter
  type PSRAM     = pir.plasticine.graph.SRAM
  type PFU       = pir.plasticine.graph.FuncUnit
  type PST       = pir.plasticine.graph.Stage
  type PEST      = pir.plasticine.graph.EmptyStage
  type PFUST     = pir.plasticine.graph.FUStage
  type PWAST     = pir.plasticine.graph.WAStage
  type PRDST     = pir.plasticine.graph.ReduceStage
  type PSI       = pir.plasticine.graph.ScalarIn
  type PSO       = pir.plasticine.graph.ScalarOut
  type PPT       = pir.plasticine.graph.Port
  type PIP       = pir.plasticine.graph.InPort[PNode]
  type POP       = pir.plasticine.graph.OutPort[PNode]
  type PIO[S<:PNode]       = pir.plasticine.graph.IO[S]
  type PRMPT     = pir.plasticine.graph.RMPort
  type PBS       = pir.plasticine.graph.Bus
  type PIB       = pir.plasticine.graph.InBus[PNE]
  type POB       = pir.plasticine.graph.OutBus[PNE]
  type PCB       = pir.plasticine.graph.CtrlBox
  type PLUT      = pir.plasticine.graph.LUT
  type PEnLUT    = pir.plasticine.graph.EnLUT
  type PTDLUT     = pir.plasticine.graph.TokenDownLUT
  type PTOLUT    = pir.plasticine.graph.TokenOutLUT
  type PUC       = pir.plasticine.graph.UDCounter
  type PSB       = pir.plasticine.graph.SwitchBox
  type Stagable  = pir.plasticine.graph.Stagable
  type PConst    = pir.plasticine.graph.Const
  type PConstVal = pir.plasticine.graph.ConstVal
}