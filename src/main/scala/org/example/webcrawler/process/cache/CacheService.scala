package org.example.webcrawler.process.cache

import com.github.benmanes.caffeine.cache.Caffeine
import com.typesafe.scalalogging.StrictLogging
import org.example.webcrawler.model.UrlResponse

import java.util.concurrent.TimeUnit

/** Caffeine Cache Service with time based eviction policy, taking in consideration that data will be changed for a site
  * in 30 min customizable expiry
  */

class CacheService(expireAfter: Int = 30) extends StrictLogging {

  // todo - create a cache
  private val cache = Caffeine
    .newBuilder()
    .expireAfterAccess(expireAfter, TimeUnit.MINUTES)
    .maximumSize(100)
    .build()
}
