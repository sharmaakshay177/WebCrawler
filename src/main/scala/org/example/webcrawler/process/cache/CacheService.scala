package org.example.webcrawler.process.cache

import org.example.webcrawler.model.UrlResponse

import scala.collection.concurrent.TrieMap

/** Caffeine Cache Service with time based eviction policy, taking in consideration that data will be changed for a site
  * in 30 min customizable expiry
  *
  * to replicate for now will use TrieMap
  */

class CacheService {

  /** this can be enhanced by adding custom method if required
    */
  val cache: TrieMap[String, UrlResponse] = TrieMap.empty[String, UrlResponse]
}
