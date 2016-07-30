package pir.graph.traversal

import pir._
import pir.PIRMisc._
import pir.plasticine.graph._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Set
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap
import java.io.File

class SpadePrinter(implicit design: Design) extends Traversal with Printer {

  override val stream = newStream(Config.spadeFile) 
  
  override def traverse = {
    design.arch.ctrlers.foreach { ctrler => emitBlock(s"${ctrler}") {
      emitBlock(s"scalarins") {
        ctrler.sins.foreach { si => 
          emitln(s"${si.in.ms}")
          emitln(s"${si.out.mt}")
        }
      }
      emitBlock(s"scalarouts") {
        ctrler.souts.foreach { so =>
          emitln(s"${so.in.ms}")
          emitln(s"${so.out.mt}")
        }
      }
      emitBlock(s"vecins") {
        ctrler.vins.foreach { vi =>
          emitln(s"${vi.ms}")
          vi.outports.foreach { op =>
            emitln(s"${op.mt}")
          }
        }
      }
      emitln(s"vecouts: ")
      ctrler.vouts.foreach { vout =>
        emitln(s"${vout.mt}")
        vout.inports.foreach { ip =>
          emitln(s"${ip.ms}")
        }
      }
      ctrler match {
        case top:Top =>
        case cu:ComputeUnit =>
          emitBlock(s"srams") {
            cu.srams.foreach{ s => 
              emitBlock(s"${s}") {
              emitln(s"${s.writeAddr.ms}")
              emitln(s"${s.readAddr.ms}")
              emitln(s"${s.writePort.ms}")
              emitln(s"${s.readPort.mt}")
              }
            }
          }
          emitBlock(s"ctrs") {
            cu.ctrs.foreach{ c =>
              emitBlock(s"${c}") {
                emitln(s"${c.min.ms}")
                emitln(s"${c.max.ms}")
                emitln(s"${c.step.ms}")
                emitln(s"${c.out.mt}")
              }
            }
          }
          emitBlock(s"piperegs") {
            cu.pregs.foreach { pr =>
              emitln(s"(${pr.in.ms}, ${pr.out.mt})")
            }
          }
          emitln(s"reduce: ${cu.reduce.mt}")
        }
      }
    }
  }

  override def finPass = {
    close
    info(s"Finishing Spade Config Printing in ${getPath}")
  }

}
