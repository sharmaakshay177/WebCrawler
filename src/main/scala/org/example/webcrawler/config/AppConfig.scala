package org.example.webcrawler.config

case class AppConfig(port: Int, webBase: String, assetDirectory: String, env: String) {
  def getCompleteWebBase: String = s"http://$webBase"
}

object AppConfig {
  import com.typesafe.config.ConfigFactory
  def live: AppConfig = {
    val config = ConfigFactory.parseResources("application.conf")
    AppConfig(
      config.getInt("port"),
      config.getString("webBase"),
      config.getString("assetDirectory"),
      config.getString("env")
    )
  }
}
