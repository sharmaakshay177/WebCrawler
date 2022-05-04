package org.zio.example

package object webcrawler {
  sealed trait AppError extends Throwable
  object AppError {
    case class CrawlingError(message: String)     extends AppError
    case class JsonDecodingError(message: String) extends AppError
    case object MissingBodyError                  extends AppError
  }
}
