package org.zio.example.webcrawler.server

import org.zio.example.webcrawler.AppError
import org.zio.example.webcrawler.services.{
  WebCrawlerService,
  WebCrawlerServiceLive,
  ZioCacheService,
  ZioCacheServiceLive
}
import zhttp.http._
import zhttp.service.Server
import zio.{ZIOAppDefault, _}

object CrawlerServer extends ZIOAppDefault {

  val handledApp: Http[WebCrawlerService with ZioCacheService, Nothing, Request, Response] = {
    import routes._
    CrawlerRoutes.routes.catchAll {
      case AppError.MissingBodyError =>
        Http.text("MISSING BODY").setStatus(Status.BAD_REQUEST)
      case AppError.JsonDecodingError(message) =>
        Http.text(s"JSON DECODING ERROR: $message").setStatus(Status.BAD_REQUEST)
    }
  }

  override val run: ZIO[Any, Throwable, Nothing] =
    Server
      .start(8080, handledApp)
      .provide(
        WebCrawlerServiceLive.layer,
        ZioCacheServiceLive.layer
      )
}
