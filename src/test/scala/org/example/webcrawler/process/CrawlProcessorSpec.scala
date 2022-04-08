package org.example.webcrawler.process

import org.example.webcrawler.generators.TestData._
import org.example.webcrawler.model.RequestResponse
import org.example.webcrawler.process.cache.CacheService
import org.example.webcrawler.process.crawl.WebCrawler
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers

class CrawlProcessorSpec extends AnyFreeSpecLike with Matchers {

  private val webCrawler   = new WebCrawler
  private val cacheService = new CacheService
  private val crawlProcess = new CrawlProcessor(webCrawler, cacheService)

  "Web crawler" - {
    "should be get a valid RequestResponse by making a call and updating in cache as well" in {
      val goodUrl     = "https://google.com"
      val requestBody = genRequestBody.sampled.copy(urls = List(goodUrl))

      cacheService.cache.size shouldBe 0
      val response = crawlProcess.getScrapedDataForUrl(requestBody)
      response shouldBe a[RequestResponse]
      response.error shouldBe None
      cacheService.cache.size shouldBe 1
    }

    "should be able to return from cache" in {
      val urlResponse = genUrlResponse.sampled
      val requestBody = genRequestBody.sampled.copy(urls = List(urlResponse.url))

      cacheService.cache.put(urlResponse.url, urlResponse)
      cacheService.cache.size shouldBe 1
      crawlProcess.getScrapedDataForUrl(requestBody) shouldBe a[RequestResponse]
      cacheService.cache.size shouldBe 1
    }
  }

}
