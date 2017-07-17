package frl.driesprong.common

import frl.driesprong.definitions.{GroundTruth, Message, Response}
import org.slf4j.LoggerFactory

import scala.collection.mutable


/**
  * Created by Fokko Driesprong on 15/07/2017.
  */
object Data {
  private val logger = LoggerFactory.getLogger(getClass)
  private val winners: mutable.HashMap[Int, String] = mutable.HashMap[Int, String]()


  val groundTruth: mutable.HashMap[Int, GroundTruth] = mutable.HashMap[Int, GroundTruth]()

  val unsendMessages: mutable.PriorityQueue[Message] = mutable.PriorityQueue[Message]()(
    // Take the smallest date first
    Ordering.by((_: Message).ingestionTime).reverse
  )

  private def printScores(): Unit = {
    val localSum: mutable.HashMap[String, Int] = mutable.HashMap[String, Int]()

    for (winner <- winners.values) {
      if (localSum.contains(winner)) {
        localSum(winner) = 0
      }
      localSum(winner) = localSum(winner) + 1
    }

    var scores = "Scores:\n"
    for ((k, v) <- localSum) {
      scores += s"\t$k: $v"
    }
    println(scores)
  }

  def checkResult(response: Response): Boolean = {
    logger.debug(s"Got: $response")

    if (groundTruth.contains(response.eventTime)) {
      val gt = groundTruth(response.eventTime)

      // Be friendly
      if (Math.round(gt.a) == Math.round(response.intercept) && Math.round(gt.b) == Math.round(response.slope)) {


        if (winners.contains(response.eventTime)) {
          logger.warn(s"${response.submittedBy} found AS FIRST correct answer for ${response.eventTime}, one point for you!")
        } else {
          logger.warn(s"${response.submittedBy} found the correct answer for ${response.eventTime}, VO!")
        }

        true
      } else {
        logger.info(s"Submitted (${response.intercept}, ${response.slope}) did not equal (${gt.a}, ${gt.b})")

        false
      }
    } else {
      logger.info(s"Could not find ground truth for ${response.eventTime}")

      false
    }
  }
}
