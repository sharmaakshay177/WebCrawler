package org.example.webcrawler.model

import io.circe.Encoder

case class UrlResponse(url: String, data: CrawledData)
object UrlResponse {
  implicit val encodeUrl: Encoder[UrlResponse] = Encoder.forProduct2("url", "data"){
    case UrlResponse(url, data) => (url, data)
  }
}

case class RequestResponse(result: List[UrlResponse], error: Option[String])
