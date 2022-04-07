import org.example.webcrawler.WebCrawlerServlet
import org.example.webcrawler.services.RequestHelperService
import org.scalatra.LifeCycle

import javax.servlet.ServletContext

class ScalatraBootstrap extends LifeCycle {
  // initialize services used by servlet
  val service = new RequestHelperService

  override def init(context: ServletContext): Unit =
    context.mount(new WebCrawlerServlet(service), "/webcrawler/*")
}
