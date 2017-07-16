package frl.driesprong

import akka.actor.{Actor, ActorLogging}
import cakesolutions.kafka.KafkaConsumer
import cakesolutions.kafka.akka.KafkaConsumerActor.{Confirm, Subscribe, Unsubscribe}
import cakesolutions.kafka.akka.{ConsumerRecords, KafkaConsumerActor}
import frl.driesprong.common.Data
import frl.driesprong.definitions.Response
import org.apache.kafka.common.serialization.StringDeserializer

object ConsumerActor {
  private val extractor = ConsumerRecords.extractor[String, String]
}


class ConsumerActor extends Actor with ActorLogging {

  implicit val formats = org.json4s.DefaultFormats

  import ConsumerActor._
  import org.json4s._
  import org.json4s.jackson.JsonMethods._

  private val kafkaConsumerActor = context.actorOf(
    KafkaConsumerActor.props(
      consumerConf = KafkaConsumer.Conf(
        keyDeserializer = new StringDeserializer,
        valueDeserializer = new StringDeserializer,
        groupId = "group",
        bootstrapServers = Config.kafkaLocation
      ),
      actorConf = KafkaConsumerActor.Conf(),
      self
    ),
    "KafkaConsumer"
  )

  override def preStart() = {
    super.preStart()
    kafkaConsumerActor ! Subscribe.AutoPartition(Seq("response"))
  }

  override def postStop() = {
    kafkaConsumerActor ! Unsubscribe
    super.postStop()
  }

  override def receive: Receive = {
    case extractor(consumerRecords) =>

      consumerRecords.pairs.foreach {
        case (None, message) => Data.checkResult(parse(message).extract[Response])
        case _ => log.error("Received unknown message")
      }

      // By committing *after* processing we get at-least-once-processing, but that's OK here because we can identify duplicates by their timestamps
      kafkaConsumerActor ! Confirm(consumerRecords.offsets, commit = true)

  }
}