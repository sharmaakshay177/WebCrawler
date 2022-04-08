package org.example.webcrawler.process

import com.typesafe.scalalogging.StrictLogging
import org.example.webcrawler.model.{RequestResponse, UrlResponse}
import org.example.webcrawler.process.cache.WebCrawler

import scala.collection.mutable.ListBuffer

/** takes cache and webCrawler and checks the content in cache first if found or content is invalid based on
  * invalidation strategy it will hit the webcrawler to get data and and return the response form here
  */

// todo - update this to add cache as well, and make cache call
class CrawlProcessor(webCrawler: WebCrawler) extends StrictLogging {

  private val errorList: ListBuffer[String]      = ListBuffer.empty[String]
  private val responses: ListBuffer[UrlResponse] = ListBuffer.empty[UrlResponse]

  def getScrapedDataForUrl(urls: List[String]): RequestResponse = {
    urls.map(url => webCrawler.getScrappedData(url)).foreach {
      case Left(errorMessage) => errorList.addOne(errorMessage)
      case Right(urlResp)     => responses.addOne(urlResp)
    }
    RequestResponse(responses.toList, Option(errorList.mkString))
  }

}
