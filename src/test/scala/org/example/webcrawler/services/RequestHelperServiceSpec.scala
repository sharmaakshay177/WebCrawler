package org.example.webcrawler.services

import io.circe.generic.auto._
import io.circe.syntax._
import org.scalatest.freespec.AnyFreeSpecLike
import org.scalatest.matchers.should.Matchers
import org.example.webcrawler.generators.TestData._

class RequestHelperServiceSpec extends AnyFreeSpecLike with Matchers {

  private val service = new RequestHelperService

  "RequestHelperService" - {
    "should be able to parse json body to request body" in {
      val requestBodyExpected = genRequestBody.sampled

      val jsonNoSpaces = requestBodyExpected.asJson.noSpaces
      val json2Spaces  = requestBodyExpected.asJson.spaces2
      val json4Spaces  = requestBodyExpected.asJson.spaces4

      service.getRequestBody(jsonNoSpaces).toOption.get shouldBe requestBodyExpected
      service.getRequestBody(json2Spaces).toOption.get shouldBe requestBodyExpected
      service.getRequestBody(json4Spaces).toOption.get shouldBe requestBodyExpected
    }

    "should be able to convert RequestResponse to json" in {
      val requestResponse = genRequestResponse.sampled
      service.getResponse(requestResponse) shouldBe requestResponse.asJson.noSpaces
    }
  }

}
