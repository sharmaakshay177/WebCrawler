package org.zio.example.webcrawler.services

import org.zio.example.webcrawler.model.CrawledResponse
import zio.{Function1ToLayerOps, Task, URLayer, ZIO}

import scala.collection.concurrent.TrieMap

trait ZioCacheService {
  def get(url: String): Task[Option[CrawledResponse]]

  def putEntry(url: String, record: CrawledResponse): Task[Unit]
}

object ZioCacheService {
  def get(url: String): ZIO[ZioCacheService, Throwable, Option[CrawledResponse]] =
    ZIO.serviceWithZIO[ZioCacheService](_.get(url))

  def putEntry(url: String, record: CrawledResponse): ZIO[ZioCacheService, Throwable, Unit] =
    ZIO.serviceWithZIO[ZioCacheService](_.putEntry(url, record))
}

final case class ZioCacheServiceLive(cache: TrieMap[String, CrawledResponse] = TrieMap.empty[String, CrawledResponse])
    extends ZioCacheService {

  override def get(url: String): Task[Option[CrawledResponse]] = ZIO.attempt(cache.get(url))

  override def putEntry(url: String, record: CrawledResponse): Task[Unit] =
    ZIO.attempt(cache.put(url, record)) *> ZIO.unit
}

object ZioCacheServiceLive {
  val layer: URLayer[TrieMap[String, CrawledResponse], ZioCacheService] =
    (ZioCacheServiceLive.apply _).toLayer[ZioCacheService]
}
