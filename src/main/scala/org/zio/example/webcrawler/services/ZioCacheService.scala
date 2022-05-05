package org.zio.example.webcrawler.services

import org.zio.example.webcrawler.model.CrawledResponse
import zio.{Function1ToLayerOps, UIO, URLayer, ZIO}

import scala.collection.concurrent.TrieMap

trait ZioCacheService {
  def get(url: String): UIO[Option[CrawledResponse]]

  def putEntry(url: String, record: CrawledResponse): UIO[Unit]
}

object ZioCacheService {
  def get(url: String): ZIO[ZioCacheService, Throwable, Option[CrawledResponse]] =
    ZIO.serviceWithZIO[ZioCacheService](_.get(url))

  def putEntry(url: String, record: CrawledResponse): ZIO[ZioCacheService, Throwable, Unit] =
    ZIO.serviceWithZIO[ZioCacheService](_.putEntry(url, record))
}

final case class ZioCacheServiceLive(cache: TrieMap[String, CrawledResponse] = TrieMap.empty[String, CrawledResponse])
    extends ZioCacheService {

  override def get(url: String): UIO[Option[CrawledResponse]] = UIO.succeed(cache.get(url))

  override def putEntry(url: String, record: CrawledResponse): UIO[Unit] =
    UIO.succeed(cache.put(url, record)) *> ZIO.unit
}

object ZioCacheServiceLive {
  val layer: URLayer[TrieMap[String, CrawledResponse], ZioCacheService] =
    (ZioCacheServiceLive.apply _).toLayer[ZioCacheService]
}
