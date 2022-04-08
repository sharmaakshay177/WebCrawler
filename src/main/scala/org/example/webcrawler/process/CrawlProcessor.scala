package org.example.webcrawler.process

import com.typesafe.scalalogging.StrictLogging
import org.example.webcrawler.model.{RequestBody, RequestResponse, UrlResponse}
import org.example.webcrawler.process.cache.CacheService
import org.example.webcrawler.process.crawl.WebCrawler

import scala.collection.mutable.ListBuffer

/** takes cache and webCrawler and checks the content in cache first if found or content is invalid based on
  * invalidation strategy it will hit the webcrawler to get data and and return the response form here
  */

class CrawlProcessor(webCrawler: WebCrawler, cacheService: CacheService) extends StrictLogging {

  private val errorList: ListBuffer[String]      = ListBuffer.empty[String]
  private val responses: ListBuffer[UrlResponse] = ListBuffer.empty[UrlResponse]

  def getScrapedDataForUrl(requestBody: RequestBody): RequestResponse = {
    logger.info(s"Request Body Received :$requestBody")
    requestBody.urls.foreach { url =>
      cacheService.cache.get(url) match {
        case Some(cacheResponse) =>
          logger.info(s"Cache Hit :$cacheResponse")
          responses.addOne(cacheResponse)
        case None                =>
          // sync call will go from here for now !!
          logger.info(s"Calling Crawler Service")
          webCrawler.getScrappedData(url) match {
            case Left(errorString) => errorList.addOne(errorString)
            case Right(urlResp) =>
              logger.info(s"UrlResponse Received :$urlResp, Added to Cache !!")
              cacheService.cache.put(url, urlResp)
              responses.addOne(urlResp)
          }
      }
    }
    logger.info(s"Errors Encountered :${errorList.mkString}")
    logger.info(s"Responses Gathered :${responses.mkString}")
    RequestResponse(responses.toList, Option(errorList.mkString))
  }

}
