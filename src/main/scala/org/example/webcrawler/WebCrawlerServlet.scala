package org.example.webcrawler

import com.typesafe.scalalogging.StrictLogging
import org.example.webcrawler.services.RequestHelperService
import org.scalatra.ScalatraServlet

class WebCrawlerServlet(service: RequestHelperService) extends ScalatraServlet with StrictLogging {

  before() {
    contentType = "json"
  }

  post("/api/crawl") {
    logger.info(s"Request Received for ${request.getRequestURL}")

    val body = request.body
    service.getRequestBody(body).fold(
      error => response.sendError(400, s"Bad Request Error: ${error.getMessage}"),
      body => ???
    )
  }

}
