package pir
package pass

trait PIRTraversal extends PIRPass with PIRWorld with prism.traversal.Traversal { self:prism.traversal.HierarchicalTraversal =>
  def top = compiler.top
}
trait DFSTopDownTopologicalTraversal extends PIRTraversal with prism.traversal.DFSTopDownTopologicalTraversal
trait BFSTopDownTopDownTopologicalTraversal extends PIRTraversal with prism.traversal.BFSTopDownTopDownTopologicalTraversal
trait BFSBottomUpTopologicalTraversal extends PIRTraversal with prism.traversal.BFSBottomUpTopologicalTraversal
trait DFSBottomUpTopologicalTraversal extends PIRTraversal with prism.traversal.DFSBottomUpTopologicalTraversal

trait UnitTraversal extends prism.traversal.UnitTraversal
trait ChildFirstTraversal extends PIRTraversal with prism.traversal.ChildFirstTraversal
trait SiblingFirstTraversal extends PIRTraversal with prism.traversal.SiblingFirstTraversal
