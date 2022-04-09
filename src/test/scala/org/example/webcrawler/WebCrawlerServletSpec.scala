package org.example.webcrawler

import io.circe.generic.auto._
import io.circe.syntax._
import org.example.webcrawler.generators.TestData._
import org.example.webcrawler.model.RequestResponse
import org.example.webcrawler.process.CrawlProcessor
import org.example.webcrawler.process.cache.CacheService
import org.example.webcrawler.process.crawl.WebCrawler
import org.example.webcrawler.services.HelperService
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar.mock
import org.scalatra.test.scalatest.ScalatraFreeSpec

class WebCrawlerServletSpec extends ScalatraFreeSpec {

  val service = new HelperService
  val mockedCrawler: WebCrawler = mock[WebCrawler]
  val process = new CrawlProcessor(mockedCrawler, new CacheService)
  val mockServlet = new WebCrawlerServlet(service, process)

  addServlet(mockServlet, "/*")

  "POST /api/crawl should return 400 on invalid body" in  {
    val invalidBodyJson = """{"url":["https://google.com"]}""".getBytes
    post("/api/crawl", invalidBodyJson) {
      status shouldEqual 400
    }
  }

  "POST /api/crawl should return a 200 and RequestResponse on valid body" in {

    val urlList = List("https://google.com")
    val validRequestBody = genRequestBody.sampled.copy(urls = urlList)
    val jsonBody = validRequestBody.asJson.noSpaces.getBytes

    val urlResponse = genUrlResponse.sampled.copy(url = urlList.head)
    when(mockedCrawler.getScrappedData(urlList.head)).thenReturn(Right(urlResponse))

    val jsonSent = RequestResponse(List(urlResponse), None).asJson.noSpaces

    post("/api/crawl", jsonBody) {
      status shouldEqual 200
      response.getContentType() shouldBe "json;charset=utf-8"
      response.body shouldBe jsonSent
    }
  }

  override def header = ???
}
