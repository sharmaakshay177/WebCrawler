package org.example.webcrawler.model

import io.circe.{Encoder, Json}
import org.jsoup.nodes.Element

case class Elements(head: Element, body: Element)
object Elements {
  implicit val encodeElements: Encoder[Elements] = new Encoder[Elements] {
    override def apply(a: Elements): Json = Json.obj(
      ("head", Json.fromString(a.head.toString)),
      ("body", Json.fromString(a.body.toString))
    )
  }
}

case class CrawledData(title: String, head: String, body: String, elements: Elements)
object CrawledData {
  implicit val encodeCrawledData: Encoder[CrawledData] = Encoder.forProduct4("title", "head", "body", "elements"){
    case CrawledData(title, head, body, elements) => (title, head, body, elements)
  }
}
