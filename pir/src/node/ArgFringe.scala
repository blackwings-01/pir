package pir.node

case class ArgFringe(topController:Controller)(implicit design:PIRDesign) extends GlobalContainer {

  lazy val argInController = ArgInController().setParent(topController)
  lazy val argOutController = ArgOutController().setParent(topController)

  lazy val hostRead = HostRead().setParent(this).ctrl(argOutController)

}

case class ArgInDef()(implicit design:PIRDesign) extends Def
case class HostRead()(implicit design:PIRDesign) extends Def

trait PIRArgFringe {
  implicit def dramAddress_to_access(dramAddr:DramAddress)(implicit design:PIRDesign) = {
    ReadMem(dramAddr)
  }
}