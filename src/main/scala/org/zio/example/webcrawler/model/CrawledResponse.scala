package org.zio.example.webcrawler.model

import org.jsoup.nodes.Element
import zio.json.{DeriveJsonCodec, JsonCodec}

// Responses
final case class Elements(head: Element, body: Element)
object Elements {
  implicit val codec: JsonCodec[Elements] = DeriveJsonCodec.gen[Elements]
}

final case class CrawledData(title: String, elements: Elements)
object CrawledData {
  implicit val codec: JsonCodec[CrawledData] = DeriveJsonCodec.gen[CrawledData]
}

final case class CrawledResponse(url: String, crawledData: CrawledData)
object CrawledResponse {
  implicit val codec: JsonCodec[CrawledResponse] = DeriveJsonCodec.gen[CrawledResponse]
}

final case class EntirePayloadResponse(responses: List[CrawledResponse], error: Option[String])
object EntirePayloadResponse {
  implicit val codec: JsonCodec[EntirePayloadResponse] = DeriveJsonCodec.gen[EntirePayloadResponse]
}

// Requests
final case class RequestBody(urls: List[String])
object RequestBody {
  implicit val codec: JsonCodec[RequestBody] = DeriveJsonCodec.gen[RequestBody]
}