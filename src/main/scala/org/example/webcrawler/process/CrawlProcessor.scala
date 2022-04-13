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
    for (url <- requestBody.urls) {
      val cacheHit = cacheService.cache.get(url)
      if (cacheHit.nonEmpty) {
        logger.info(s"Cache Hit :${cacheHit.get}")
        responses.addOne(cacheHit.get)
      } else {
        logger.info(s"Calling Crawler Service")
        webCrawler.getScrappedData(url) match {
          case Left(errorMessage) => errorList.addOne(errorMessage)
          case Right(urlResp) =>
            logger.info(s"UrlResponse Received :$urlResp, Added to Cache !!")
            cacheService.cache.put(url, urlResp)
            responses.addOne(urlResp)
        }
      }
    }

    logger.info(s"Errors Encountered :${errorList.mkString}")
    logger.info(s"Responses Gathered :${responses.mkString}")
    RequestResponse(responses.toList, if (errorList.nonEmpty) Some(errorList.mkString) else None)
  }

}
