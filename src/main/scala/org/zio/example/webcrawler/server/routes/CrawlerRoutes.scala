package org.zio.example.webcrawler.server.routes

import org.zio.example.webcrawler.AppError
import org.zio.example.webcrawler.model.RequestBody
import org.zio.example.webcrawler.model.RequestBody._
import org.zio.example.webcrawler.services.WebCrawlerService
import zhttp.http._
import zio.ZIO
import zio.json._

object CrawlerRoutes {

  val routes: Http[WebCrawlerService, Throwable, Request, Response] =
    Http.collectZIO[Request] {
      case Method.GET -> !! / "ping" =>
        for {
          resp <- WebCrawlerService.pong
        } yield Response.json(resp)

      case req @ Method.POST -> !! / "crawl" =>
        for {
          body <- req.bodyAsString.orElseFail(AppError.MissingBodyError)
          req  <- ZIO.from(body.fromJson[RequestBody]).mapError(AppError.JsonDecodingError)
          resp <- WebCrawlerService.getPayloadResponse(req.urls)
        } yield Response.json(resp.toJson)
    }

}
