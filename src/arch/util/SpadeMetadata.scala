package pir.spade.util

import pir.util.maps._
import pir.spade.graph._
import pir.spade.config._
import pir.spade.main._
import scala.collection.mutable.Map
import scala.collection.mutable.Set
import scala.collection.mutable.ListBuffer

trait SpadeMetadata { self:Spade =>
  /* Coordinate of a spade node. Used for pisa and dot codegen */
  object coordOf extends MOneToOneMap { 
    type K = Node
    type V = (Int, Int)
  }
  
  /* Index of a spade node. Used for pisa codegen */
  object indexOf extends MOneToOneMap {
    type K = Node
    type V = Int
  }
  
  /* Can a counter be the inner most counter in a chain */
  object isInnerCounter extends MOneToOneMap {
    type K = Node
    type V = Boolean 
  }

  /* GridNetork of GlobalIO */
  object networkOf extends MOneToOneMap {
    type K = GlobalIO[_<:PortType, _] 
    type V = GridNetwork
  }

  /* Name of nodes */
  object nameOf extends MOneToOneMap {
    type K = Node
    type V = String 
  }

  object notFullOffset extends MOneToOneMap {
    type K = OnChipMem
    type V = Int
    override def apply(k:K):V = {
      super.get(k).getOrElse(0)
    }
  }

  object bufferSizeOf extends MOneToOneMap {
    type K = OnChipMem
    type V = Int
  }

  object delayOf extends MOneToOneMap {
    type K = Delay[_]
    type V = Int
    override def apply(k:K):V = {
      super.get(k).getOrElse(0)
    }
  }

  object validOf extends MOneToOneMap {
    type K = GlobalOutput[_<:PortType, Module] 
    type V = Output[_<:PortType, Module]
  }

}
