package org.example.webcrawler.process.crawl

import org.example.webcrawler.ErrorMessage
import org.example.webcrawler.model.UrlResponse
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers

class WebCrawlerSpec extends AnyFreeSpecLike with Matchers {

  private val goodUrl        = "https://google.com"
  private val dummyUrl       = "https://url-not-exist.com"
  private val crawlerService = new WebCrawler

  "Web Crawler" - {
    "should return url response if url call went well" in {
      crawlerService.getScrappedData(goodUrl).toOption.get shouldBe a[UrlResponse]
    }

    "should return error string if not able to get data" in {
      crawlerService.getScrappedData(dummyUrl).left.toOption.get shouldBe a[ErrorMessage]
    }
  }

}
