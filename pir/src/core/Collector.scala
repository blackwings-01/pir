package pir

import pir.node._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Stack

trait Collector { design:PIR =>

  private var nextSym = 0
  def nextId = {nextSym += 1; nextSym }

  //TODO use collect to implement this
  private val nodeStack = Stack[(Node => Boolean, ListBuffer[Node])]()
  val toUpdate = ListBuffer[(() => Unit)]()
  val allNodes = ListBuffer[Node]()

  def reset = {
    nodeStack.foreach { case (f,i) => i.clear() }
    nodeStack.clear()
    allNodes.clear()
    nodeStack.push(((n:Node) => true), allNodes)
    toUpdate.clear()
    nextSym = 0
  }

  def addNode(n: Node) = { 
    nodeStack.foreach { case (f,i) => if (f(n)) i += n }
  }

  def removeNode(n:Node):Unit = {
    nodeStack.foreach { case (f,i) => if (f(n)) i -= n }
    n match {
      case n:OuterController => 
        design.top.removeCtrler(n)
        n.cchains.foreach { cc => design.removeNode(cc) }
        n.children.foreach { child =>
          child.parent(n.parent)
          n.parent.foreach { _.addChildren(child) }
          n.removeChild(child)
        }
        n.cchains.foreach { cc => design.removeNode(cc) }
        n.parent.foreach { _.removeChild(n) }
        n.removeParent
      case n:InnerController =>
        design.top.removeCtrler(n)
        n.parent.foreach { _.removeChild(n) }
        n.removeParent
      case n:CounterChain =>
        n.counters.foreach { ctr => design.removeNode(ctr) }
      case n:Counter =>
        design.removeNode(n.min.from)
        design.removeNode(n.max.from)
        design.removeNode(n.step.from)
        design.removeNode(n.min)
        design.removeNode(n.max)
        design.removeNode(n.step)
      case n:LocalMem =>
        design.removeNode(n.readPort)
        design.removeNode(n.writePort.from)
        design.removeNode(n.writePort)
      case n:GlobalInput =>
        design.removeNode(n.out)
        n.variable.removeReader(n)
        if (n.variable.readers.isEmpty) {
          design.removeNode(n.variable.writer)
          design.removeNode(n.variable)
        }
      case n:GlobalOutput => throw new Exception(s"TODO")
      case n:Input =>
        n.disconnect
      case n:Output =>
        n.disconnect
      case _ =>
    }
  }
  def removeNodes(ns:List[Node]) = { ns.foreach { n => removeNode(n) } }

  //def addBlock(block: => Any, f1:Node => Boolean, filters: Node => Boolean *):List[List[Node]] = {
  //  nodeStack.push((f1, ListBuffer[Node]()))
  //  filters.foreach { f => 
  //    nodeStack.push( (f, ListBuffer[Node]()) )
  //  }
  //  block
  //  (0 to filters.size).foldLeft(List[List[Node]]()) { case (a, i) =>
  //    nodeStack.pop()._2.toList :: a 
  //  }
  //}

  def addBlock[T](block: => Any, filter: Node => Boolean):List[T] = {
    nodeStack.push((filter, ListBuffer[Node]()))
    block
    nodeStack.pop()._2.toList.asInstanceOf[List[T]]
  }

  def addBlock[T1, T2](block: => Any,
                       f1: Node => Boolean,
                       f2: Node => Boolean
                       ):(List[T1], List[T2]) = {
    nodeStack.push((f1, ListBuffer[Node]()))
    nodeStack.push((f2, ListBuffer[Node]()))
    block
    val l2 = nodeStack.pop()._2.toList.asInstanceOf[List[T2]]
    val l1 = nodeStack.pop()._2.toList.asInstanceOf[List[T1]]
    (l1, l2)
  }

  def addBlock[T1, T2, T3](block: => Any,
                       f1: Node => Boolean, 
                       f2: Node => Boolean,
                       f3: Node => Boolean
                       ):(List[T1], List[T2], List[T3]) = {
    nodeStack.push((f1, ListBuffer[Node]()))
    nodeStack.push((f2, ListBuffer[Node]()))
    nodeStack.push((f3, ListBuffer[Node]()))
    block
    val l3 = nodeStack.pop()._2.toList.asInstanceOf[List[T3]]
    val l2 = nodeStack.pop()._2.toList.asInstanceOf[List[T2]]
    val l1 = nodeStack.pop()._2.toList.asInstanceOf[List[T1]]
    (l1, l2, l3)
  }

  def addBlock[T1, T2, T3, T4](block: => Any,
                       f1: Node => Boolean, 
                       f2: Node => Boolean,
                       f3: Node => Boolean,
                       f4: Node => Boolean
                       ):(List[T1], List[T2], List[T3], List[T4]) = {
    nodeStack.push((f1, ListBuffer[Node]()))
    nodeStack.push((f2, ListBuffer[Node]()))
    nodeStack.push((f3, ListBuffer[Node]()))
    nodeStack.push((f4, ListBuffer[Node]()))
    block
    val l4 = nodeStack.pop()._2.toList.asInstanceOf[List[T4]]
    val l3 = nodeStack.pop()._2.toList.asInstanceOf[List[T3]]
    val l2 = nodeStack.pop()._2.toList.asInstanceOf[List[T2]]
    val l1 = nodeStack.pop()._2.toList.asInstanceOf[List[T1]]
    (l1, l2, l3, l4)
  }
  def addBlock[T1, T2, T3, T4, T5](block: => Any,
                       f1: Node => Boolean, 
                       f2: Node => Boolean,
                       f3: Node => Boolean,
                       f4: Node => Boolean,
                       f5: Node => Boolean
                       ):(List[T1], List[T2], List[T3], List[T4], List[T5]) = {
    nodeStack.push((f1, ListBuffer[Node]()))
    nodeStack.push((f2, ListBuffer[Node]()))
    nodeStack.push((f3, ListBuffer[Node]()))
    nodeStack.push((f4, ListBuffer[Node]()))
    nodeStack.push((f5, ListBuffer[Node]()))
    block
    val l5 = nodeStack.pop()._2.toList.asInstanceOf[List[T5]]
    val l4 = nodeStack.pop()._2.toList.asInstanceOf[List[T4]]
    val l3 = nodeStack.pop()._2.toList.asInstanceOf[List[T3]]
    val l2 = nodeStack.pop()._2.toList.asInstanceOf[List[T2]]
    val l1 = nodeStack.pop()._2.toList.asInstanceOf[List[T1]]
    (l1, l2, l3, l4, l5)
  }
  def addBlock[T1, T2, T3, T4, T5, T6](block: => Any,
                       f1: Node => Boolean, 
                       f2: Node => Boolean,
                       f3: Node => Boolean,
                       f4: Node => Boolean,
                       f5: Node => Boolean,
                       f6: Node => Boolean
                       ):(List[T1], List[T2], List[T3], List[T4], List[T5], List[T6]) = {
    nodeStack.push((f1, ListBuffer[Node]()))
    nodeStack.push((f2, ListBuffer[Node]()))
    nodeStack.push((f3, ListBuffer[Node]()))
    nodeStack.push((f4, ListBuffer[Node]()))
    nodeStack.push((f5, ListBuffer[Node]()))
    nodeStack.push((f6, ListBuffer[Node]()))
    block
    val l6 = nodeStack.pop()._2.toList.asInstanceOf[List[T6]]
    val l5 = nodeStack.pop()._2.toList.asInstanceOf[List[T5]]
    val l4 = nodeStack.pop()._2.toList.asInstanceOf[List[T4]]
    val l3 = nodeStack.pop()._2.toList.asInstanceOf[List[T3]]
    val l2 = nodeStack.pop()._2.toList.asInstanceOf[List[T2]]
    val l1 = nodeStack.pop()._2.toList.asInstanceOf[List[T1]]
    (l1, l2, l3, l4, l5, l6)
  }

  def lazyUpdate(f: => Unit):Unit = { toUpdate += f _ }

}
