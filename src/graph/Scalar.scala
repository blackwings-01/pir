package pir.graph

import pir.Design
import pir.graph._

import scala.collection.mutable.Set
import scala.collection.mutable.ListBuffer

/* Register declared outside CU for communication between two CU. Only a symbol to keep track of
 * the scalar value, not a real register */
case class Scalar(val name:Option[String])(implicit design: Design) extends Node {
  override val typeStr = "Scalar"
  val writers:Set[Controller] = Set[Controller]() 
  val readers:Set[Controller] = Set[Controller]() 
  def addReader(r:Controller) = { readers += r; this }
  def addWriter(w:Controller) = { writers += w; this }
}
object Scalar {
  def apply(name:String)(implicit design: Design):Scalar = Scalar(Some(name)) 
  def apply()(implicit design: Design):Scalar = Scalar(None) 
}

trait ArgIn extends Scalar{ override val typeStr = "ArgIn" }
object ArgIn {
  def apply() (implicit design: Design):Scalar = new Scalar(None) with ArgIn
  def apply(name:String) (implicit design: Design):Scalar = new Scalar(Some(name)) with ArgOut
}

trait ArgOut extends Scalar{ override val typeStr = "ArgOut" }
object ArgOut {
  def apply() (implicit design: Design):Scalar = new Scalar(None) with ArgOut
  def apply(name:String) (implicit design: Design):Scalar = new Scalar(Some(name)) with ArgOut
}

case class Vector(val name:Option[String])(implicit design: Design) extends Node {
  override val typeStr = "Vector"
  val writers:Set[Controller] = Set[Controller]() 
  val readers:Set[Controller] = Set[Controller]() 
  def addReader(r:Controller) = { readers += r; this }
  def addWriter(w:Controller) = { writers += w; this }
}
object Vector {
  def apply(name:String)(implicit design: Design):Vector = Vector(Some(name)) 
  def apply()(implicit design: Design):Vector = Vector(None) 
}
