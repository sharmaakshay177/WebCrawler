package org.zio.example.webcrawler.services

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.zio.example.webcrawler.model._
import zio.{Function1ToLayerOps, Ref, Task, UIO, URLayer, ZIO}

import scala.collection.mutable.ListBuffer
import scala.util.Try

// https://scalac.io/blog/introduction-to-programming-with-zio-functional-effects/

trait WebCrawlerService {
  def getPayloadResponse(urls: List[String]): Task[EntirePayload]
  def pong: UIO[String]
}

object WebCrawlerService {

  def getPayloadResponse(urls: List[String]): ZIO[WebCrawlerService, Throwable, EntirePayload] =
    ZIO.serviceWithZIO[WebCrawlerService](_.getPayloadResponse(urls))

  def pong: ZIO[WebCrawlerService, Nothing, String] =
    ZIO.serviceWithZIO[WebCrawlerService](_.pong)
}

final case class WebCrawlerServiceLive(cache: ZioCacheService) extends WebCrawlerService {

  override def pong: UIO[String] = UIO.succeed("pong")

  override def getPayloadResponse(urls: List[String]): Task[EntirePayload] =
    for {
      effects   <- ZIO.succeed(urls.map(Task.attempt(_)))
      processed <- Task.foreachPar(effects)(_.map(cacheOrCall))
      interim <- Task.attempt(
        processed.map(
          _.fold[ConvertedResponse](
            err => ConvertedResponse(Left(Some(err.getMessage))),
            resp => ConvertedResponse(Right(resp))
          )
        )
      )
      all            <- ZIO.collectAll(interim)
      localResponses <- Ref.make(ListBuffer.empty[CrawledResponse])
      localErrors    <- Ref.make(ListBuffer.empty[String])
      payload = EntirePayload(localResponses, localErrors)
      _ <- collate(all, payload)
    } yield payload

  private def collate(convertedResponse: List[ConvertedResponse], payload: EntirePayload): UIO[Unit] = {
    def errorHelper(error: Option[String], payload: EntirePayload): UIO[Unit] =
      for {
        oldErrorList   <- payload.errors.get
        errorExtracted <- ZIO.attempt(error.get) orElse ZIO.succeed("")
        errorListNew = if (errorExtracted == "") oldErrorList else oldErrorList.addOne(errorExtracted)
        _ <- payload.errors.set(errorListNew)
      } yield ()

    def responseHelper(response: CrawledResponse, payload: EntirePayload): UIO[Unit] =
      for {
        oldResponseList <- payload.responses.get
        _               <- payload.responses.set(oldResponseList.addOne(response))
      } yield ()

    for {
      _ <- UIO.foreach(convertedResponse)(_.responseOrError match {
        case Left(value)  => errorHelper(value, payload)
        case Right(value) => responseHelper(value, payload)
      })
    } yield ()
  }

  private def getCrawledData(url: String): Task[CrawledResponse] =
    for {
      document        <- scrapUrl(url)
      crawledResponse <- prepareData(url, document)
      _               <- cache.putEntry(url, crawledResponse)
    } yield crawledResponse

  private def cacheOrCall(url: String): Task[CrawledResponse] =
    for {
      cacheHit <- cache.get(url)
      resp     <- ZIO.attempt(cacheHit.get) orElse getCrawledData(url)
    } yield resp

  private def scrapUrl(url: String): Task[Document] =
    ZIO.fromTry(Try(Jsoup.connect(url).get()))

  private def prepareData(url: String, document: Document): Task[CrawledResponse] =
    for {
      elements        <- ZIO.succeed(Elements(document.head(), document.body()))
      crawledData     <- ZIO.succeed(CrawledData(document.title(), elements))
      crawledResponse <- ZIO.succeed(CrawledResponse(url, crawledData))
    } yield crawledResponse
}

object WebCrawlerServiceLive {
  val layer: URLayer[ZioCacheService, WebCrawlerService] = (WebCrawlerServiceLive.apply _).toLayer[WebCrawlerService]
}
