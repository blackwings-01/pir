package pir.pass

import pir._
import spade.simulation.Simulator

class SpadeSimulator(implicit design: PIR) extends Pass {

  def shouldRun = Config.simulate && design.pirMapping.succeeded
  lazy val simulator = new Simulator()

  override def reset = {
    super.reset
    simulator.reset
  }

  override def initPass = {
    super.initPass
    simulator.initPass
  }

  addPass {
    simulator.run(design.mapping.get)
  }

  override def finPass = {
    simulator.finPass
  }
}

