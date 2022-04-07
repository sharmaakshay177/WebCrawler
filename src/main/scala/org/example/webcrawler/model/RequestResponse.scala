package org.example.webcrawler.model

case class UrlResponse(url: String, data: String)

case class RequestResponse(result: List[UrlResponse], error: Option[String])
