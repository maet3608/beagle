package quuux.beagle

import scala.math.min

/**
 * Splits text strings into overlapping fragments of length n.
 * Used to calculate string similarities.
 * @author Stefan Maetschke
 */


/**
 * Creates n-gram sets and calculates similarities between sets.
 */
object NGrams {
  type NGramSet = Set[String]

  /** returns an n-gram set */
  def apply(text:String, n:Int):NGramSet =
    Set()++text.toLowerCase.replaceAll("\\W+","_").sliding(n)

   def apply(text:String, n:Set[Int] = Set(3,4,6)):NGramSet =
     n.map(NGrams(text,_)).reduceLeft(_ | _)

  /** returns the similarity score [0,1] between two n-gram sets */
  def score(set1:NGramSet, set2:NGramSet):Double = {
    val n = min(set1.size, set2.size)
    if(n > 0) set1.intersect(set2).size/n.toDouble else 0.0
  }

  
  /** just an example */
  def main(args:Array[String]) {
    val set1 = NGrams("a test")
    val set2 = NGrams("Another,Test")
    println(set1)
    println(set2)
    println(NGrams.score(set1,set2))
  }
}