package org.example.webcrawler

import com.typesafe.scalalogging.StrictLogging
import org.example.webcrawler.process.CrawlProcessor
import org.example.webcrawler.services.RequestHelperService
import org.scalatra.ScalatraServlet

class WebCrawlerServlet(service: RequestHelperService, process: CrawlProcessor)
    extends ScalatraServlet
    with StrictLogging {

  before() {
    contentType = "json"
  }

  post("/api/crawl") {
    logger.info(s"Request Received for ${request.getRequestURL}")

    service
      .getRequestBody(request.body)
      .fold(
        error => {
          logger.debug(s"Error While parsing request body: ${error.getMessage}")
          response.sendError(400, "Bad Request Body")
        },
        body => {
          response.setStatus(200)
          service.getResponse(process.getScrapedDataForUrl(body))
        }
      )
  }

}
