package org.example.webcrawler.services

import io.circe.Error
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.example.webcrawler.model.{RequestBody, RequestResponse}

class HelperService {
  def getRequestBody(body: String): Either[Error, RequestBody] = decode[RequestBody](body)
  def getResponse(requestResponse: RequestResponse): String    = requestResponse.asJson.noSpaces
}
