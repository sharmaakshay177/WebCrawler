package org.example.webcrawler.process

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior}
import org.example.webcrawler.ErrorMessage
import org.example.webcrawler.model.{RequestResponse, UrlResponse}
import org.example.webcrawler.process.MasterActor.SlaveActorMessages
import org.example.webcrawler.process.cache.CacheService
import org.example.webcrawler.process.crawl.WebCrawler

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/** A Base actor which will create a child actor for fetching the crawled response per url wise. Once child actor has
  * completed its working base actor will handle that.
  */

object SlaveActor {
  private val webCrawler = new WebCrawler
  private val cache      = new CacheService

  def apply(): Behavior[SlaveActorMessages] =
    Behaviors.receive { (context, message) =>
      message match {
        case MasterActor.CrawlData(url, replyTo) =>
          val cacheHit = cache.cache.get(url)
          if (cacheHit.nonEmpty) {
            context.log.info(s"Response found in Cache for url :$url")
            replyTo ! MasterActor.CacheHit(cacheHit.get, context.self)
          } else {
            context.log.info(s"Calling Web Crawler service for url :$url")
            val crawledData = webCrawler.getScrappedData(url)
            replyTo ! MasterActor.CrawledResponse(crawledData, context.self)
          }
          Behaviors.same
        case MasterActor.StopSlave =>
          context.log.info(s"Stop Signal Received for Slave :${context.self.path}, Stopping Slave !!!")
          Behaviors.stopped
      }
    }
}

// todo - actors are working, see how we can aggregate the responses

object MasterActor {
  type ScrappedResponse = Either[ErrorMessage, UrlResponse]

  sealed trait MasterActorMessages
  final case class GetCrawledData(urls: List[String]) extends MasterActorMessages
  final case class CrawledResponse(response: ScrappedResponse, fromSlave: ActorRef[SlaveActorMessages])
      extends MasterActorMessages
  final case class CacheHit(response: UrlResponse, fromSlave: ActorRef[SlaveActorMessages]) extends MasterActorMessages
  final case object AggregateResponse                                                       extends MasterActorMessages

  sealed trait SlaveActorMessages
  final case class CrawlData(url: String, replyTo: ActorRef[MasterActorMessages]) extends SlaveActorMessages
  final case object StopSlave                                                     extends SlaveActorMessages

  def apply(): Behavior[MasterActorMessages] = aggregatedResponse(List.empty, List.empty)

  // not a nice way !!!
  var aggregatedData: RequestResponse = _

  def aggregatedResponse(responses: List[UrlResponse], errors: List[ErrorMessage]): Behavior[MasterActorMessages] =
    Behaviors.receive { (context, message) =>
      message match {
        case GetCrawledData(urls) =>
          val replyTo = context.self
          context.log.info(s"Current Master Actor Ref :${replyTo.path}")
          urls.foreach { url =>
            val slaveActor = context.spawn(SlaveActor(), encodeName(url))
            context.log.info(s"Slave Actor: ${slaveActor.path} Created To Serve Url :$url")
            slaveActor ! MasterActor.CrawlData(url, replyTo)
          }
          Behaviors.same
        case CrawledResponse(response, fromSlave) =>
          context.log.info(s"Crawled Response Received from Slave :${fromSlave.path}")
          response match {
            case Left(errMessage) =>
              context.log.info(s"Error Received :$errMessage")
              aggregatedResponse(responses, errMessage :: errors)
            case Right(urlResp) =>
              context.log.info(s"Response Received :$urlResp")
              aggregatedResponse(urlResp :: responses, errors)
          }
          fromSlave ! StopSlave
          Behaviors.same
        case CacheHit(response, fromSlave) =>
          context.log.info(s"Cache Hit Received from Slave :${fromSlave.path} with response :$response")
          aggregatedResponse(response :: responses, errors)
          fromSlave ! StopSlave
          Behaviors.same
        case AggregateResponse =>
          context.log.info(s"Aggregating Response")
          aggregatedData = RequestResponse(responses, if (errors.nonEmpty) Some(errors.mkString) else None)
          Behaviors.same
      }
    }

  def encodeName(urlName: String): String =
    URLEncoder.encode(urlName, StandardCharsets.UTF_8.name())

  // todo -  run this are the init from a main method
  def main(args: Array[String]): Unit = {
    // to test only
    val system: ActorSystem[MasterActor.MasterActorMessages] = ActorSystem(MasterActor(), "MasterActorSystem")

    system ! GetCrawledData(List("https://google.com", "https://github.com/"))
    system ! AggregateResponse
  }

}
