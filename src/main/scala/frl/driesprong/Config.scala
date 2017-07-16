package frl.driesprong

/**
  * Created by Fokko Driesprong on 15/07/2017.
  */
object Config {
  val kafkaLocation =  "localhost:9092"

  val topic = "datastream"
  val steps = 120
  val windowSize = 30

  val generatorSlopeUpperBound = 22.0
  val generatorInterceptUpperBound = 1925.0
}
