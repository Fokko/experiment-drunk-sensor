package frl.driesprong

import breeze.linalg.{DenseMatrix, DenseVector}
import frl.driesprong.common.{Data, Generator, Linear}
import frl.driesprong.definitions.{Message, Response}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

import scala.annotation.tailrec

/**
  * Created by Fokko Driesprong on 15/07/2017.
  */
class DataSanityChecks extends FlatSpec with Matchers with BeforeAndAfter {

  before {
    Generator.generate()
  }

  after {
    Generator.clear()
  }

  "The generator" should "contain the expected number of entries" in {
    Data.unsendMessages.length should be(Config.windowSize * Config.steps)
    Data.groundTruth.keySet.size should be(Config.steps)
  }

  def truncateTime(timestamp: Int): Int = {
    // This function will map the data to the window it belongs
    timestamp - (timestamp % 30)
  }

  "Based on the generator we" should "be able to get the ground truth" in {
    val byKey = Data.unsendMessages
      .toList
      .groupBy(msg => truncateTime(msg.eventTime))

    for (key <- byKey.keys) {
      // Take the first upcoming message
      val messages = byKey(key).sortBy(_.eventTime)

      val baseTime = truncateTime(messages.head.eventTime)

      val x = messages.map(msg => (msg.eventTime - baseTime).toDouble).toArray
      val y = messages.map(_.value).toArray

      val Y = DenseVector(y)
      val X = DenseVector(x)

      val newX = DenseMatrix.ones[Double](x.length, 2)
      newX(::, 1) := X

      val W = Linear.Regression(Y, newX)

      val w0 = W(0)
      val w1 = W(1)

      println(s"Got w0 $w0, w1 $w1")

      // Floor it because someties of the noise we get some offsets
      assert(Math.ceil(w0) >= 0.0)
      assert(Math.floor(w0) <= Config.generatorInterceptUpperBound)
      assert(Math.ceil(w1) >= 0.0)
      assert(Math.floor(w1) <= Config.generatorSlopeUpperBound)
    }
  }


  @tailrec
  private def sampleUntilAnswer(messages: List[Message], baseTime: Int, num: Int = 3): Option[Int] =
    if (num >= messages.length) {
      None
    } else {
      val subset = messages.take(num)

      val x = subset.map(msg => (msg.eventTime - baseTime).toDouble).toArray
      val Y = DenseVector(subset.map(_.value).toArray)
      val X = DenseMatrix.ones[Double](num, 2)
      X(::, 1) := DenseVector(x)

      val W = Linear.Regression(Y, X)

      val response = Response(eventTime = baseTime, intercept = W(0), slope = W(1))

      if (Data.checkResult(response)) {
        Some(num)
      } else {
        sampleUntilAnswer(messages, baseTime, num + 1)
      }
    }


  "Based on the data we" should "be should be able to get the answer with ~90% of the data" in {
    val byKey = Data.unsendMessages
      .toList
      .groupBy(msg => truncateTime(msg.eventTime))

    val samplesRequired = byKey.keys.toList.map(key => {
      // Take the first upcoming message
      val messages = byKey(key)

      assert(messages.size == Config.windowSize)

      val baseTime = truncateTime(messages.head.eventTime)

      sampleUntilAnswer(messages, baseTime)
    })

    val determined = samplesRequired.filter(_.isDefined).map(_.get)

    val avgSamples = determined.sum.toDouble / determined.size.toDouble

    println(s"${determined.size} of ${byKey.size} could be solved with an average of $avgSamples samples")

  }

}
