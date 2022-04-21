package org.example.webcrawler.process

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, ActorSystem, Behavior, Scheduler}
import akka.util.Timeout
import org.example.webcrawler.ErrorMessage
import org.example.webcrawler.model.{RequestResponse, UrlResponse}
import org.example.webcrawler.process.MasterActor.{GetCrawledData, SlaveActorMessages}
import org.example.webcrawler.process.cache.CacheService
import org.example.webcrawler.process.crawl.WebCrawler

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

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

object MainActorHandler {
  sealed trait RequestResponse
  final case class ToCrawl(urls: List[String]) extends RequestResponse
  final case class EntireResponse(responses: List[UrlResponse], errors: Option[String]) extends RequestResponse
  final case class ErrorResponse(errorMessage: String) extends RequestResponse
  final case class AggregatedResponse(responses: List[UrlResponse], errors: Option[String]) extends RequestResponse
  implicit val timeout: Timeout = Timeout(5.seconds)

  def apply(): Behavior[RequestResponse] =
    Behaviors.receive { (context, message) =>
      message match {
        case ToCrawl(urls) =>
          val masterActor = context.spawn(MasterActor(), "MasterActor")
          context.ask(masterActor, ref => GetCrawledData(urls, ref)) {
            case Failure(exception) =>
              ErrorResponse(exception.getMessage)
            case Success(EntireResponse(responses, errors)) =>
              AggregatedResponse(responses, errors)
          }
          Behaviors.same
      }
    }

  def main(args: Array[String]): Unit = {
    val urls = List("https://google.com", "https://github.com/")
    val system: ActorSystem[RequestResponse] = ActorSystem(MainActorHandler(), "MasterActorSystem")

    import akka.actor.typed.scaladsl.AskPattern._
    import akka.util.Timeout

    // implicit timeout
    implicit val timeout: Timeout = 3.seconds
    // implicit actor system
    implicit val system2: ActorSystem[_] = system
    // result
    val result: Future[MainActorHandler.RequestResponse] = ???


    // https://www.baeldung.com/scala/akka-request-response
    // https://doc.akka.io/docs/akka/current/typed/interaction-patterns.html
  }

}

object MasterActor {
  type ScrappedResponse = Either[ErrorMessage, UrlResponse]

  sealed trait MasterActorMessages
  final case class GetCrawledData(urls: List[String], replyTo: ActorRef[MainActorHandler.RequestResponse]) extends MasterActorMessages
  final case class CrawledResponse(response: ScrappedResponse, fromSlave: ActorRef[SlaveActorMessages])
      extends MasterActorMessages
  final case class CacheHit(response: UrlResponse, fromSlave: ActorRef[SlaveActorMessages]) extends MasterActorMessages
  final case class AggregateResponse(replyTo: ActorRef[MasterActorMessages]) extends MasterActorMessages

  sealed trait SlaveActorMessages
  final case class CrawlData(url: String, replyTo: ActorRef[MasterActorMessages]) extends SlaveActorMessages
  final case object StopSlave                                                     extends SlaveActorMessages


  def apply(): Behavior[MasterActorMessages] = aggregatedResponse(List.empty, List.empty)

  def aggregatedResponse(responses: List[UrlResponse], errors: List[ErrorMessage]): Behavior[MasterActorMessages] =
    Behaviors.receive { (context, message) =>
      message match {
        case GetCrawledData(urls, replyToMain) =>
          val replyTo = context.self
          context.log.info(s"Current Master Actor Ref :${replyTo.path}")
          urls.foreach { url =>
            val slaveActor = context.spawn(SlaveActor(), encodeName(url))
            context.log.info(s"Slave Actor: ${slaveActor.path} Created To Serve Url :$url")
            slaveActor ! MasterActor.CrawlData(url, replyTo)
          }
          replyToMain ! MainActorHandler.EntireResponse(responses, if (errors.nonEmpty) Some(errors.mkString) else None)
          Behaviors.same
        case CrawledResponse(response, fromSlave) =>
          context.log.info(s"Crawled Response Received from Slave :${fromSlave.path}")
          response match {
            case Left(errMessage) =>
              context.log.info(s"Error Received from slave errMessage")
              aggregatedResponse(responses, errMessage :: errors)
            case Right(urlResp) =>
              context.log.info(s"Response Received from slave urlResp")
              aggregatedResponse(urlResp :: responses, errors)
          }
          fromSlave ! StopSlave
          Behaviors.same
        case CacheHit(response, fromSlave) =>
          context.log.info(s"Cache Hit Received from Slave :${fromSlave.path} with response response")
          aggregatedResponse(response :: responses, errors)
          fromSlave ! StopSlave
          Behaviors.same
      }
    }

  def encodeName(urlName: String): String =
    URLEncoder.encode(urlName, StandardCharsets.UTF_8.name())

}