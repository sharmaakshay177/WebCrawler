package org.zio.example.webcrawler.services

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.zio.example.webcrawler.model.{CrawledData, CrawledResponse, Elements, EntirePayloadResponse}
import zio.{Function1ToLayerOps, Task, UIO, URLayer, ZIO}

import scala.util.Try

// https://scalac.io/blog/introduction-to-programming-with-zio-functional-effects/

trait WebCrawlerService {
  def getCrawledData(url: String): Task[CrawledResponse]
  def getPayloadResponse(urls: List[String]): Task[EntirePayloadResponse]
  def pong: UIO[String]
}

object WebCrawlerService {
  def getCrawledData(url: String): ZIO[WebCrawlerService, Throwable, CrawledResponse] =
    ZIO.serviceWithZIO[WebCrawlerService](_.getCrawledData(url))

  def getPayloadResponse(urls: List[String]): ZIO[WebCrawlerService, Throwable, EntirePayloadResponse] =
    ZIO.serviceWithZIO[WebCrawlerService](_.getPayloadResponse(urls))

  def pong: ZIO[WebCrawlerService, Nothing, String] =
    ZIO.serviceWithZIO[WebCrawlerService](_.pong)
}

final case class WebCrawlerServiceLive(cache: ZioCacheService) extends WebCrawlerService {

  override def pong: UIO[String] = UIO.succeed("pong")

  override def getPayloadResponse(
    urls: List[String],
    responses: List[CrawledResponse] = List.empty[CrawledResponse]
  ): Task[EntirePayloadResponse] = {
    ??? // what to do here ?
  }

  override def getCrawledData(url: String): Task[CrawledResponse] =
    for {
      document        <- scrapUrl(url)
      crawledResponse <- prepareData(url, document)
    } yield crawledResponse

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
