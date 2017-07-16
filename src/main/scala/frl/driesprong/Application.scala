package frl.driesprong

import akka.actor.{ActorSystem, Props}
import cakesolutions.kafka.KafkaProducer.Conf
import cakesolutions.kafka.{KafkaProducer, KafkaProducerRecord}
import frl.driesprong.common.{Data, Generator}
import frl.driesprong.definitions.Message
import org.apache.kafka.common.serialization.StringSerializer
import org.json4s._
import org.json4s.jackson.JsonMethods._
import org.slf4j.LoggerFactory

import scala.annotation.tailrec

/**
  * Created by Fokko Driesprong on 14/07/2017.
  */
object Application extends App {
  private val logger = LoggerFactory.getLogger(getClass)

  val system = ActorSystem("KafkaActor")

  implicit val formats = org.json4s.DefaultFormats

  // Start consumer
  system.actorOf(Props[ConsumerActor], name = "KafkaConsumer")

  // Start producer
  val producer = KafkaProducer(
    Conf(new StringSerializer(), new StringSerializer(), bootstrapServers = Config.kafkaLocation)
  )

  @tailrec
  def processQueue(message: Message): Unit = {
    logger.debug(s"Waiting, next one in ${message.ingestionTime - Generator.getNowInSeconds} seconds..")
    if (message.ingestionTime <= Generator.getNowInSeconds) {
      val res = Extraction.decompose(message)
      val encodedMessage = compact(render(res))

      logger.debug(s"Sending: $encodedMessage")

      producer.send(
        KafkaProducerRecord(Config.topic, None, encodedMessage)
      )
      if (Data.unsendMessages.nonEmpty) {
        processQueue(Data.unsendMessages.dequeue)
      }
    } else {
      Thread.sleep(1000)
      processQueue(message)
    }
  }

  // Generate data
  Generator.generate()

  // Process the generated data by producing it to Kafka
  processQueue(Data.unsendMessages.dequeue)

  logger.warn(s"Done sending, application will shutdown...")
}
