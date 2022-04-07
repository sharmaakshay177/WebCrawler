package org.example.webcrawler.process.cache

import com.typesafe.scalalogging.StrictLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class WebCrawler(url: String) extends StrictLogging {

  private val scrapingResponse: Option[Document] = None

  def scrapUrl: WebCrawler = {
    val doc = Jsoup.connect(url).get()
    // temp to make compiler happy !!
    this
  }

}
