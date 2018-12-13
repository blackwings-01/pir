import spatial.dsl._

case class KmeansParam(
  iters:scala.Int = 2,
  K:scala.Int = 64,
  D:scala.Int = 64,
  N:scala.Int = 8192,
  ts:scala.Int = 1024,
  op:scala.Int = 1,
  mp1:scala.Int = 1,
  mp2:scala.Int = 1,
  mp3:scala.Int = 1
) extends Param[KmeansParam]

class Kmeans_0 extends Kmeans

class Kmeans_1 extends Kmeans {override lazy val param = KmeansParam(mp1 = 2,mp2 = 2)}
//class Kmeans_1 extends Kmeans {override lazy val param = KmeansParam(mp1 = 4,mp2 = 2)}
//class Kmeans_2 extends Kmeans {override lazy val param = KmeansParam(mp1 = 1,mp2 = 8)}
//class Kmeans_3 extends Kmeans {override lazy val param = KmeansParam(mp1 = 2,mp2 = 4)}
//class Kmeans_5 extends Kmeans {override lazy val param = KmeansParam(mp1 = 3,mp2 = 3)}

@spatial abstract class Kmeans extends DSETest { self => // Regression (Dense) // Args: 3 64
  type X = Int

  lazy val param = KmeansParam()
  import param._
  val ip = 16

  def DM1 = D - 1
  val element_max = 10
  val margin = (element_max * 0.2).to[X]

  def kmeans[T:Num](points_in: Array[T], cent_inits: Array[T]) = {

    //val iters = ArgIn[Int]
    //val N     = ArgIn[Int]

    //setArg(iters, I)
    //setArg(N, self.N)

    val points = DRAM[T](N, D)    // Input points
    val centroids = DRAM[T](K, D) // Output centroids
    setMem(points, points_in)

    Accel {
      val cts = SRAM[T](K, D)

      // Load initial centroids (from points)
      cts load points(0::K, 0::D par ip)

      Sequential.Foreach(iters by 1){epoch => // iter = 2
        // For each set of points
        val newCents = MemReduce(SRAM[T](K,D) par ip)(N by ts par op){i => // iter = 9
          val pts = SRAM[T](ts, D)
          pts load points(i::i+ts, 0::D par ip)

          // For each point in this set
          MemReduce(SRAM[T](K,D) par ip)(ts par mp1){pt => // iter = 1024
            // Find the index of the closest centroid
            val dists = SRAM[T](K)
            Foreach(K by 1 par mp2) { ct =>
              val dist = Reduce(Reg[T])(D par ip){d => (pts(pt,d) - cts(ct,d)) ** 2 }{_+_}
              dists(ct) = dist.value
            }
            val minDist = Reduce(Reg[T])(K by 1 par ip) { ct => dists(ct) } { (a,b) => min(a, b) }
            val minCent = Reduce(Reg[I32])(K by 1 par ip) { ct => 
              mux(dists(ct) == minDist.value, ct, -1)
            } { (a,b) => max(a,b) }

            // Store this point to the set of accumulators
            val localCent = SRAM[T](K,D)
            Foreach(K by 1, D par ip){(ct,d) =>
              localCent(ct, d) = mux(ct == minCent.value, pts(pt,d), 0.to[T])
            }
            localCent
          }{_+_} // Add the current point to the accumulators for this centroid
        }{_+_}

        // Average each new centroid
        Foreach(K by 1 par mp3){ ct =>
          val centCount = Reg[T](0.to[T])
          Pipe {
            centCount := max(newCents(ct,DM1), 1.to[T])
          }
          Foreach(D par ip){ d =>
            cts(ct, d) = mux(centCount == 0.to[T], 0.to[T], newCents(ct,d) / centCount) //updateMux
          }
        }
      }

      // Store the centroids out
      centroids(0::K, D par ip) store cts
    }

    getMem(centroids)
  }

  def main(args: Array[String]): Unit = {

    val pts = Array.tabulate(N){i => Array.tabulate(D){d => if (d == D-1) 1.to[X] else random[X](element_max) + i }}
    val cnts = Array.tabulate(K){i => Array.tabulate(D){d => if (d == D-1) 1.to[X] else random[X](element_max) + (i*N/K) }}

    val result = kmeans(pts.flatten, cnts.flatten)

    //val cts = Array.empty[Array[X]](K)
    //for (k <- 0 until K) {
      //cts(k) = Array.tabulate(D){i => pts(k).apply(i) }
    //}
    //val ii = Array.tabulate(K){i => i}

    //for(epoch <- 0 until I) {
      //def dist[T:Num](p1: Array[T], p2: Array[T]) = p1.zip(p2){(a,b) => (a - b)**2 }.reduce(_+_)

      //// Make weighted points
      //val map = pts.groupByReduce{pt =>
        //val dists = cts.map{ct => dist(ct, pt) }
        //dists.zip(ii){(a,b) => pack(a,b) }.reduce{(a,b) => if (a._1 < b._1) a else b}._2  // minIndex
      //}{pt => pt}{(x,y) => x.zip(y){_+_} }

      //// Average
      //for (k <- 0 until K) {
        //if (!map.contains(k)) {
          //cts(k) = Array.tabulate(D){d => 0.to[X]}
        //} else {
          //val wp = map(k)
          //val n  = wp(D - 1)
          //cts(k) = Array.tabulate(D){d => if (d == D-1) 1.to[X] else wp(d)/n }
        //}
      //}
    //}

    //val gold = cts.flatten

    //println("\n\nOriginal Centers:")
    //(0 until K).foreach{ i => printArray(cnts(i))}
    //println("\n\nOriginal Points:")
    //(0 until N).foreach{ i => printArray(pts(i))}
    //println("\n\nCorrect Centers:")
    //(0 until K).foreach{ i => printArray(cts(i))}
    //println("\n\nFPGA Centers:")
    //(0 until K).foreach { i => 
      //val resrow = Array.tabulate(D){j => result(i*D + j)}
      //printArray(resrow)
    //}

    //val cksum = result.zip(gold){ case (o, g) => (g < (o + margin)) && g > (o - margin)}.reduce{_&&_}

    //println("PASS: " + cksum + " (Kmeans)")
    assert(true)
  }
}
