package org.example.webcrawler.model

import org.jsoup.nodes.Element

case class Elements(head: Element, body: Element)
case class CrawledData(title: String, head: String, body: String, elements: Elements)
