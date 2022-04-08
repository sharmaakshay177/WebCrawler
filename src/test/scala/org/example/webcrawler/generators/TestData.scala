package org.example.webcrawler.generators

import org.example.webcrawler.model.{CrawledData, Elements, RequestBody, RequestResponse, UrlResponse}
import org.jsoup.nodes.Element
import org.scalacheck.Gen

object TestData {

  implicit class Sampled[T](gen: Gen[T]) {
    def sampled: T = gen.sample.get
  }

  def genString(n: Int): Gen[String] = Gen.stringOfN(n, Gen.alphaChar).map(_.mkString)

  def genFixedString: Gen[String] = Gen.stringOfN(10, Gen.alphaChar).map(_.mkString)

  def tags: Gen[String] = Gen.oneOf(Seq("a", "href", "img", "param", "picture"))

  def genElement: Gen[Element] = for {
    tag <- tags
  } yield new Element(tag)

  def genElements: Gen[Elements] = for {
    head <- genElement
    body <- genElement
  } yield Elements(head, body)

  def genCrawledData: Gen[CrawledData] =
    for {
      title   <- genString(20)
      head    <- genString(50)
      body    <- genString(50)
      element <- genElements
    } yield CrawledData(title, head, body, element)

  def genUrl: Gen[String] = for {
    size    <- Gen.choose(6, 15)
    urlName <- genString(size)
  } yield s"https://$urlName.com"

  def genUrls: Gen[List[String]] = for {
    listSize <- Gen.choose(1, 5)
    listUrls <- Gen.listOfN(listSize, genUrl)
  } yield listUrls

  def genRequestBody: Gen[RequestBody] = for {
    urls <- genUrls
  } yield RequestBody(urls)

  def genUrlResponse: Gen[UrlResponse] = for {
    url         <- genUrl
    crawledData <- genCrawledData
  } yield UrlResponse(url, crawledData)

  def size: Int = Gen.choose(1, 3).sample.get

  def genRequestResponse: Gen[RequestResponse] = for {
    urlResponses <- Gen.listOfN(size, genUrlResponse)
  } yield RequestResponse(urlResponses, genString(20).sample)

}
