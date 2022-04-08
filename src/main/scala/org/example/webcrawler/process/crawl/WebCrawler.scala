package org.example.webcrawler.process.crawl

import com.typesafe.scalalogging.StrictLogging
import org.example.webcrawler.ErrorMessage
import org.example.webcrawler.model.{CrawledData, Elements, UrlResponse}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.util.{Failure, Success, Try}

class WebCrawler extends StrictLogging {

  private def scrapUrl(url: String): Either[ErrorMessage, Document] =
    Try(Jsoup.connect(url).get()) match {
      case Failure(e)   => Left(s"Error Encountered while scraping $url, Error :${e.getMessage}")
      case Success(doc) => Right(doc)
    }

  def getScrappedData(url: String): Either[ErrorMessage, UrlResponse] = {
    val scrappedData = scrapUrl(url)
    scrappedData match {
      case Left(errorMessage) => Left(errorMessage)
      case Right(doc)         => Right(UrlResponse(url, prepareData(doc)))
    }
  }

  private def prepareData(document: Document): CrawledData =
    CrawledData(
      title = document.title(),
      head = document.head().data(),
      body = document.body().data(),
      elements = Elements(head = document.head(), body = document.body())
    )

}
