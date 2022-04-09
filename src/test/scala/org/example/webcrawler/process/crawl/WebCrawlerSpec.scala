package org.example.webcrawler.process.crawl

import org.example.webcrawler.ErrorMessage
import org.example.webcrawler.generators.TestData._
import org.mockito.Mockito.when
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock

class WebCrawlerSpec extends AnyFreeSpecLike with Matchers {

  private val goodUrl        = "https://google.com"
  private val dummyUrl       = "https://url-not-exist.com"
  private val crawlerService: WebCrawler = mock[WebCrawler]

  "Web Crawler" - {
    "should return url response if url call went well" in {
      val urlResponse = genUrlResponse.sampled.copy(url = goodUrl)
      when(crawlerService.getScrappedData(goodUrl)).thenReturn(Right(urlResponse))
      crawlerService.getScrappedData(goodUrl).toOption.get shouldBe urlResponse
    }

    "should return error string if not able to get data" in {
      val errorMessage = s"Error Encountered while scraping $dummyUrl, Error :NotFound"
      when(crawlerService.getScrappedData(dummyUrl)).thenReturn(Left(errorMessage))
      crawlerService.getScrappedData(dummyUrl).left.toOption.get shouldBe a[ErrorMessage]
    }
  }

}
