package pir.util

import pir._
import pir.node._

import pirc.util._
import pirc.collection.mutable._

trait PIRMetadata extends Metadata { self:PIR =>

  object nameOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = String
    override def reset = {} // set during graph construction
  }

  object indexOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Int
  }

  object vecOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = VectorIO[_]
  }

  object forRead extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Boolean
    override def apply(k:K):V = {
      super.get(k).getOrElse(false)
    }
    override def check(rec:(K,V)):Unit = {}
  }

  object forWrite extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Boolean
    override def apply(k:K):V = {
      super.get(k).getOrElse(false)
    }
    override def check(rec:(K,V)):Unit = {}
  }

  object isHead extends MOneToOneMap with MetadataMaps {
    type K = Controller
    type V = Boolean
  }

  object isLast extends MOneToOneMap with MetadataMaps {
    type K = Controller
    type V = Boolean
  }

  object isStreaming extends MOneToOneMap with MetadataMaps {
    type K = Controller
    type V = Boolean
    //Allow redefine
    override def check(rec:(K,V)):Unit = {}
  }

  object isPipelining extends MOneToOneMap with MetadataMaps {
    type K = Controller
    type V = Boolean
    //Allow redefine
    override def check(rec:(K,V)):Unit = {}
  }

  /* DramAddrGen of MemoryController*/
  object dagOf extends MBiOneToOneMap with MetadataMaps {
    type K = Controller
    type V = Controller
  }

  /* SramAddrGen of MemoryController*/
  object sagOf extends MBiOneToOneMap with MetadataMaps {
    type K = Controller
    type V = Controller
  }

  /* Number of children stages on the critical path */
  object lengthOf extends MOneToOneMap with MetadataMaps {
    type K = Controller
    type V = Int 
  }

  // Including current CU. From current to top
  object ancestorsOf extends MOneToOneMap with MetadataMaps {
    type K = Controller
    type V = List[Controller]
    //Allow redefine
    override def check(rec:(K,V)):Unit = {}
  }

  // Including current CU. From current to leaf children 
  object descendentsOf extends MOneToOneMap with MetadataMaps {
    type K = Controller
    type V = List[Controller]
    //Allow redefine
    override def check(rec:(K,V)):Unit = {}
  }

  object writersOf extends MOneToOneMap with MetadataMaps {
    type K = OnChipMem 
    type V = List[Controller]
    override def check(rec:(K,V)):Unit = {}
  }

  object readersOf extends MOneToOneMap with MetadataMaps {
    type K = OnChipMem 
    type V = List[Controller]
    override def check(rec:(K,V)):Unit = {}
  }

  /* counters for read address calculation. 
   * inner counter comes first */
  object readCChainsOf extends MOneToOneMap with MetadataMaps {
    type K = ComputeUnit
    type V = List[CounterChain]
    override def check(rec:(K,V)):Unit = {}
  }

  /* counters for write address calculation. 
   * inner counter comes first */
  object writeCChainsOf extends MOneToOneMap with MetadataMaps {
    type K = ComputeUnit 
    type V = List[CounterChain]
    override def check(rec:(K,V)):Unit = {}
  }

  /* counters for computation
   * inner counter comes first */
  object compCChainsOf extends MOneToOneMap with MetadataMaps {
    type K = ComputeUnit 
    type V = List[CounterChain]
    override def check(rec:(K,V)):Unit = {}
  }

  object swapReadCChainOf extends MOneToOneMap with MetadataMaps {
    type K = MultiBuffer
    type V = CounterChain
  }

  object swapWriteCChainOf extends MOneToOneMap with MetadataMaps {
    type K = MultiBuffer
    type V = CounterChain
  }

  object accumCounterOf extends MOneToOneMap with MetadataMaps {
    type K = AccumPR 
    type V = Counter
  }

  object localCChainOf extends MOneToOneMap with MetadataMaps {
    type K = Controller 
    type V = CounterChain
  }

  object contentionOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Int
  }

  object cycleOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Long
  }

  object totalCycleOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Long
  }

  object iterOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Long
  }

  object constOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Int
  }

  object parOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Int
  }

  object rparOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Int
  }

  object wparOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Int
  }

  object boundOf extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = AnyVal
    override def check(rec:(K,V)):Unit = {}
  }

  object isTailCollector extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Boolean
    override def apply(k:K):V = {
      super.get(k).getOrElse(false)
    }
  }

  object isHeadSplitter extends MOneToOneMap with MetadataMaps {
    type K = Node
    type V = Boolean
    override def apply(k:K):V = {
      super.get(k).getOrElse(false)
    }
  }

  object backPressureOf extends MOneToOneMap with MetadataMaps {
    type K = OnChipMem
    type V = Boolean
    override def check(rec:(K,V)):Unit = {}
  }

}

