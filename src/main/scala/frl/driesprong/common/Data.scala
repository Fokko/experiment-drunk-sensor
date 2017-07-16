package frl.driesprong.common

import frl.driesprong.definitions.{GroundTruth, Message, Response}
import org.slf4j.LoggerFactory

import scala.collection.mutable


/**
  * Created by Fokko Driesprong on 15/07/2017.
  */
object Data {
  private val logger = LoggerFactory.getLogger(getClass)

  val groundTruth: mutable.HashMap[Int, GroundTruth] = mutable.HashMap[Int, GroundTruth]()

  val unsendMessages: mutable.PriorityQueue[Message] = mutable.PriorityQueue[Message]()(
    // Take the smallest date first
    Ordering.by((_: Message).ingestionTime).reverse
  )

  def checkResult(response: Response): Boolean = {
    logger.debug(s"Got: $response")

    if (groundTruth.contains(response.eventTime)) {
      val gt = groundTruth(response.eventTime)

      // Be friendly
      if (Math.round(gt.a) == Math.round(response.intercept) && Math.round(gt.b) == Math.round(response.slope)) {
        logger.warn(s"${response.submittedBy} found the correct answer for ${response.eventTime}, VO!")

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
