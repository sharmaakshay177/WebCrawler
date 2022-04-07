package org.example.webcrawler.services

import io.circe.generic.auto._
import io.circe.syntax._
import org.example.webcrawler.model.{RequestBody, RequestResponse, UrlResponse}
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers

class RequestHelperServiceSpec extends AnyFreeSpecLike with Matchers {

  private val service = new RequestHelperService

  "RequestHelperService" - {
    "should be able to parse json body to request body" in {
      val requestBodyExpected = RequestBody(
        List("https://google.com", "https://github.com", "https://sharmaakshay177.github.io/personal-website/")
      )

      val jsonNoSpaces = requestBodyExpected.asJson.noSpaces
      val json2Spaces  = requestBodyExpected.asJson.spaces2
      val json4Spaces  = requestBodyExpected.asJson.spaces4

      service.getRequestBody(jsonNoSpaces).toOption.get shouldBe requestBodyExpected
      service.getRequestBody(json2Spaces).toOption.get shouldBe requestBodyExpected
      service.getRequestBody(json4Spaces).toOption.get shouldBe requestBodyExpected
    }

    "should be able to convert RequestResponse to json" in {
      val urlResponse     = UrlResponse("https://google.com", "some-data-from-crawler")
      val requestResponse = RequestResponse(List(urlResponse), None)
      val responseJson    = """{"result":[{"url":"https://google.com","data":"some-data-from-crawler"}],"error":null}"""

      service.getResponse(requestResponse) shouldBe responseJson
    }
  }

}
