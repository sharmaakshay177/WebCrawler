package org.example.webcrawler.model

case class UrlResponse(url: String, data: CrawledData)
case class RequestResponse(result: List[UrlResponse], error: Option[String])
