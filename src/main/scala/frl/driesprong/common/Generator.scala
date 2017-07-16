package frl.driesprong.common

import frl.driesprong.Config
import frl.driesprong.definitions.{GroundTruth, Message}

import scala.util.Random

/**
  * Created by Fokko Driesprong on 14/07/2017.
  */
object Generator {


  def getNowInSeconds: Int = {
    import java.util.Calendar
    val cal = Calendar.getInstance
    cal.set(Calendar.MILLISECOND, 0)
    (cal.getTimeInMillis / 1000L).toInt
  }

  def truncateTime(timestamp: Int): Int = {
    // This function will map the data to the window it belongs
    timestamp - (timestamp % 30)
  }


  def generate(): Unit = {
    val now = truncateTime(getNowInSeconds)
    val rnd = new Random()

    // Create one hour of data
    for (step <- 0 until Config.steps) {
      val baseDate = now + (Config.windowSize * step)
      val a = rnd.nextDouble() * Config.generatorInterceptUpperBound
      val b = rnd.nextDouble() * Config.generatorSlopeUpperBound

      Data.unsendMessages ++= (0 until Config.windowSize).map(sample => {
        // They all fall in the same interval
        val eventTime = baseDate + sample
        val ingestionTime = baseDate + sample + (Config.windowSize * Math.abs(rnd.nextGaussian())).toInt

        val y = a + (b * sample * (1.0 + rnd.nextGaussian() * 0.05))
        Message(eventTime = eventTime, ingestionTime = ingestionTime, y)
      })

      Data.groundTruth += (baseDate -> GroundTruth(a, b))
    }
  }

  def clear(): Any = {
    Data.groundTruth.clear()
    Data.unsendMessages.clear()
  }
}
