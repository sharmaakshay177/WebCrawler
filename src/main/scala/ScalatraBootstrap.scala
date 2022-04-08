import com.typesafe.scalalogging.StrictLogging
import org.example.webcrawler.WebCrawlerServlet
import org.example.webcrawler.process.CrawlProcessor
import org.example.webcrawler.process.crawl.WebCrawler
import org.example.webcrawler.services.RequestHelperService
import org.scalatra.LifeCycle

import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle with StrictLogging {
  // initialize services used by servlet
  val service   = new RequestHelperService
  val processor = new CrawlProcessor(new WebCrawler)

  override def init(context: ServletContext): Unit =
    context.mount(new WebCrawlerServlet(service, processor), "/webcrawler/*")
}
