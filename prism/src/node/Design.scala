package prism.node

import prism._
import prism.util._

import scala.collection.mutable

trait Design extends Serializable {
  implicit val design:this.type = this
  private var nextSym = 1
  def nextId = if (staging) {nextSym += 1; nextSym } else -1
  var staging = true
}
