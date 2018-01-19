package prism.traversal

import pirc._
import prism.node._

import scala.reflect._
import scala.reflect.runtime.universe._
import scala.collection.mutable

trait Traversal extends GraphTraversal {
  type N<:Node[N]

  /*
   * Visit from buttom up
   * */
  def visitUp(n:N):List[N] = n.parent.toList

  /*
   * Visit subgraph
   * */
  def visitDown(n:N):List[N] = n.children

  /*
   * Visit inputs of a node
   * */
  def visitIn(n:N):List[N] = {
    n.localDeps.toList
  }

  /*
   * Visit outputs of a node 
   * */
  def visitOut(n:N):List[N] = n.localDepeds.toList

  def allNodes(n:N) = n.parent.toList.flatMap { parent => parent.children }
}

trait GraphTraversal {
  type N
  type T

  val visited = mutable.ListBuffer[N]()

  def isVisited(n:N) = visited.contains(n)

  def reset = {
    visited.clear
  }

  def visitFunc(n:N):List[N]

  def visitNode(n:N, prev:T):T = traverse(n, prev)

  def traverseNode(n:N, prev:T):T = {
    visited += n
    visitNode(n, prev)
  }

  def traverse(n:N, zero:T):T
}

trait GraphSchedular extends GraphTraversal { self =>
  type N
  type T = List[N]

  def visitFunc(n:N):List[N]

  override def visitNode(n:N, prev:T):T = super.visitNode(n, prev:+n)

  final def schedule(n:N) = {
    reset
    traverseNode(n, Nil)
  }
}


trait DFSTraversal extends GraphTraversal {
  override def traverse(n:N, zero:T):T = {
    var prev = zero 
    var nexts = visitFunc(n).filterNot(isVisited)
    // Cannot use fold left because graph might be changing while traversing
    while (nexts.nonEmpty) {
      prev = traverseNode(nexts.head, prev)
      nexts = visitFunc(n).filterNot(isVisited)
    }
    prev
  }

}

trait BFSTraversal extends GraphTraversal {

  val queue = mutable.Queue[N]()

  override def reset = {
    super.reset
    queue.clear
  }

  override def traverse(n:N, zero:T):T = {
    var prev = zero 
    queue ++= visitFunc(n).filterNot(isVisited)
    while (queue.nonEmpty) {
      val next = queue.dequeue()
      if (!isVisited(next)) return traverseNode(next, prev)
    }
    return prev
  }
}

trait TopologicalTraversal extends GraphTraversal {
  def allNodes(n:N):List[N]
  def depFunc(n:N):List[N]
  def isDepFree(n:N) = depFunc(n).filterNot(isVisited).isEmpty
  def visitFunc(n:N):List[N] = visitDepFree(allNodes(n))
  /*
   * Return dependent free nodes in allNodes. 
   * Break cycle by pick the node with fewest dependency
   * */
  def visitDepFree(allNodes:List[N]):List[N] = {
    val unvisited = allNodes.filterNot(isVisited)
    if (unvisited.isEmpty) return Nil
    val depFree = unvisited.filter(isDepFree)
    if (depFree.nonEmpty) return depFree
    List(unvisited.sortBy { n => depFunc(n).filterNot(isVisited).size }.head)
  }
}

trait HiearchicalTraversal extends Traversal with GraphTraversal {

  def visitChild(n:N):List[N] = n.children
  def visitFunc(n:N):List[N] = Nil 

  def traverseChildren(n:N, zero:T):T = {
    var prev = zero
    var childs = visitChild(n).filterNot(isVisited)
    while (childs.nonEmpty) {
      prev = traverseNode(childs.head, prev)
      childs = visitChild(n).filterNot(isVisited)
    }
    prev
  }

}

trait ChildFirstTraversal extends DFSTraversal with HiearchicalTraversal {
  override def traverse(n:N, zero:T):T = {
    super.traverse(n, traverseChildren(n, zero))
  }
  override def traverseChildren(n:N, zero:T):T = {
    val res = super.traverseChildren(n, zero)
    val unvisited = n.children.filterNot(isVisited) 
    assert(unvisited.isEmpty, 
      s"traverseChildren:$n Not all children are visited unvisited=${unvisited}")
    res
  }
}

trait ChildLastTraversal extends BFSTraversal with HiearchicalTraversal {
  override def traverse(n:N, zero:T):T = {
    traverseChildren(n, super.traverse(n, zero))
  }
}

trait HiearchicalTopologicalTraversal extends TopologicalTraversal with ChildFirstTraversal {
  override def visitChild(n:N) = {
    n match {
      case n:SubGraph[_] => visitDepFree(super.visitChild(n.asInstanceOf[N]))
      case n => Nil
    }
  }
  override def visitFunc(n:N):List[N] = super[TopologicalTraversal].visitFunc(n)
}

import scala.collection.JavaConverters._
trait GraphTransformer extends GraphTraversal {
  type N<:Node[N]
  type P<:SubGraph[N] with N
  type A<:Atom[N] with N
  type D <: Design
  type T = Unit

  def removeNode(node:N) = {
    node.ios.foreach { io => io.disconnect }
    node.parent.foreach { parent =>
      parent.removeChild(node)
      node.unsetParent
      (parent.children.filterNot { _ == node } :+ parent).foreach(removeUnusedIOs)
    }
  }

  def swapParent(node:N, newParent:N) = {
    node.parent.foreach { parent =>
      parent.removeChild(node)
    }
    node.setParent(newParent.asInstanceOf[node.P])
  }

  /*
   * Given a node that was originally connected to from, swap the connection to node to
   * at the same io port.
    * Assume from and to have the same IO interface
   * */
  def swapConnection[A1<:A](node:A, from:A1, to:A1) = {
    assert(from.ios.size == to.ios.size)
    val connected = node.ios.flatMap { io =>
      val fromios = io.connected.filter { _.src == from }
      if (fromios.nonEmpty) Some((io, fromios))
      else None
    }
    assert(connected.nonEmpty, s"$node is not connected to $from")
    connected.foreach { case (io, fromios) =>
      fromios.foreach { fromio =>
        val index = from.ios.indexOf(fromio)
        val toio = to.ios(index)
        io.disconnectFrom(fromio.asInstanceOf[io.E])
        io.connect(toio.asInstanceOf[io.E])
      }
    }
  }

  def removeUnusedIOs(node:N) = {
    node.ios.foreach { io => if (!io.isConnected) io.src.removeEdge(io) }
  }

  override def visitNode(n:N, prev:T):T = transform(n)

  def transform(n:N):Unit = super.visitNode(n, ())

  def traverseNode(n:N):Unit = traverseNode(n, ())

  // default input is not mirrored
  def mirrorArg(n:N, arg:N)(implicit ct:ClassTag[N], design:D):(N, List[N]) = (arg, Nil)

  def mirror[T<:N](n:T)(implicit ct:ClassTag[N], design:D):(T, List[N]) = {
    val values = n.values :+ design
    //TODO: n.getClass.getConstructor(values.map{_.getClass}:_*).newInstance(values.map{
    // Some how this compiles but gives runtime error for not able to find the constructor when values contain Int type since
    // field.getClass returns java.lang.Integer type but getConstructor expects typeOf[Int]
    val constructor = n.getClass.getConstructors()(0) 
    val (args, prevs) = values.map { // Only works with a single constructor
      case arg:N => mirrorArg(n, arg) 
      case arg => (arg,Nil)
    }.unzip
    val m = constructor.newInstance(args.map(_.asInstanceOf[Object]):_*).asInstanceOf[T]
    (m, prevs.flatten :+ m)
  }
}

trait GraphCollector extends Traversal {

  private def newTraversal[M<:N:ClassTag](vf:N => List[N]) = new BFSTraversal {
    type T = (Iterable[M], Int)
    type N = GraphCollector.this.N
    override def visitNode(n:N, prev:T):T = {
      visited += n
      val (prevRes, depth) = prev 
      n match {
        case n:M if depth > 0 => (prevRes ++ List(n), depth - 1)
        case _ if depth == 0 => (prevRes, 0)
        case _ => super.visitNode(n, (prevRes, depth - 1))
      }
    }
    def visitFunc(n:N):List[N] = vf(n)
  }
 
  def collectUp[M<:N:ClassTag](n:N, depth:Int=10):Iterable[M] = {
    newTraversal(visitUp _).traverse(n, (Nil, depth))._1
  }

  def collectDown[M<:N:ClassTag](n:N, depth:Int=10):Iterable[M] = {
    newTraversal(visitDown _).traverse(n, (Nil, depth))._1
  }

  def collectIn[M<:N:ClassTag](n:N, depth:Int=10):Iterable[M] = {
    newTraversal(visitIn _).traverse(n, (Nil, depth))._1
  }

  def collectOut[M<:N:ClassTag](n:N, depth:Int=10):Iterable[M] = {
    newTraversal(visitOut _).traverse(n, (Nil, depth))._1
  }

}

