package pir
package codegen

import pir.node._
import prism.graph._
import prism.codegen._
import scala.collection.mutable

class TungstenPIRGen(implicit design:PIR) extends TungstenCodegen 
  with TungstenTopGen 
  with TungstenCtxGen 
  with TungstenOpGen
  with TungstenMemGen

trait TungstenCodegen extends PIRTraversal with DFSTopDownTopologicalTraversal with CppCodegen {
  override def dirName = buildPath(super.dirName, s"../tungsten") 
  val forward = true
  val fileName = "Top.h"

  override def quote(n:Any) = n match {
    case n:Iterable[_] => 
      s"{${n.map(quote).mkString(",")}}"
    case n => super.quote(n)
  }

  override def visitIn(n:N) = n match {
    case n:BufferRead => super.visitIn(n).filterNot{_.isInstanceOf[BufferWrite]}
    case n => super.visitIn(n)
  }

  override def visitOut(n:N) = n match {
    case n:BufferWrite => super.visitOut(n).filterNot{_.isInstanceOf[BufferRead]}
    case n => super.visitOut(n)
  }

  implicit class CtxUtil(ctx:Context) {
    def reads:Seq[BufferRead] = ctx.collectDown[BufferRead]()
    def writes:Seq[BufferWrite] = ctx.collectDown[BufferWrite]().filter { write =>
      val writeCtx = write.ctx.get
      write.out.T.exists { _.ctx.get != writeCtx }
    }
    def ctrs:Seq[Counter] = ctx.collectDown[Counter]()
    def ctrler(ctrl:ControlTree) = {
      assertOne(
        ctx.collectDown[Controller]().filter { _.ctrl.get == ctrl }, 
        s"$ctx.ctrler with ($ctrl)"
      )
    }
  }

  def quoteRef(n:PIRNode) = {
    if (n.getVec > 1) s"${n}[i]" else s"${n}"
  }

  def emitEn(en:Input with Field[_]):Unit = {
    val src = en.src
    val ens = en.neighbors
    val enName = s"${src}_${en.name}"
    emitln(s"float $enName = ${ens.map { _.toString}.foldLeft("1"){ case (prev,b) => s"$prev & $b" }};")
    en.src match {
      case n:BufferWrite =>
        emitln(s"${enName} &= ${n.ctx.get.ctrler(n.getCtrl).valid.T};")
      case n:InAccess =>
        emitln(s"${enName} &= ${n.ctx.get.ctrler(n.getCtrl).valid.T};")
      case _ =>
    }
  }

  def emitVec(n:PIRNode)(rhs:Any) = {
    val vec = n.getVec
    if (vec > 1) {
      emitln(s"float $n[${vec}] = {};")
      emitBlock(s"for (int i = 0; i < ${vec}; i++)") {
        emitln(s"$n[i] = ${rhs}")
      }
    } else {
      emitln(s"float ${n} = ${rhs}")
    }
  }

}