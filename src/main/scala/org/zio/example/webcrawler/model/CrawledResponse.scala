package org.zio.example.webcrawler.model

import org.jsoup.nodes.Element
import zio.Ref
import zio.json.{DeriveJsonCodec, JsonCodec}

import scala.collection.mutable.ListBuffer

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

final case class ConvertedResponse(responseOrError: Either[Option[String], CrawledResponse])

final case class EntirePayload(val responses: Ref[ListBuffer[CrawledResponse]], val errors: Ref[ListBuffer[String]])
object EntirePayload {
  implicit val codec: JsonCodec[EntirePayload] = DeriveJsonCodec.gen[EntirePayload]
}

// Requests
final case class RequestBody(urls: List[String])
object RequestBody {
  implicit val codec: JsonCodec[RequestBody] = DeriveJsonCodec.gen[RequestBody]
}
