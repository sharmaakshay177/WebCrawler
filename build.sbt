ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

// versions
val javaXServlet = "4.0.1"
val ScalatraVersion = "2.8.2"
val ScalaLoggingVersion = "3.9.4"
val Json4sVersion = "4.0.4"
val circeVersion = "0.14.1"
val scalaTest = "3.2.11"
val scalaCheckVersion = "1.15.4"
val jacksonVersion = "2.13.2"
val scalaMockVersion = "5.2.0"
val jsoupVersion = "1.14.3"
val caffeineCache = "3.0.6"
val AkkaVersion = "2.6.19"

lazy val root = (project in file("."))
  .settings(
    name := "WebCrawler"
  )

// dependencies
libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "com.github.ben-manes.caffeine" % "caffeine" % caffeineCache,
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "org.apache.logging.log4j" % "log4j-core" % "2.17.2",
  "org.scalatestplus" %% "mockito-3-4" % "3.2.10.0" % Test,
  "org.slf4j" % "slf4j-simple" % "1.7.36",
  "org.jsoup" % "jsoup" % jsoupVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "com.typesafe" % "config" % "1.4.2",
  "org.json4s" %% "json4s-jackson" % Json4sVersion,
  "javax.servlet" % "javax.servlet-api" % javaXServlet % "provided",
  "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion,
  "org.scalamock" %% "scalamock" % scalaMockVersion % Test,
  "org.scalatest" %% "scalatest" % scalaTest % Test,
  "org.scalactic" %% "scalactic" % scalaTest,
  "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
  "org.eclipse.jetty" % "jetty-webapp" % "11.0.8" % "container",
  "org.eclipse.jetty.orbit" % "javax.servlet" % "3.0.0.v201112011016" %     "container;provided;test" artifacts Artifact("javax.servlet", "jar", "jar"),
)

// see to refactor
//libraryDependencies ++= Seq(
//  "io.circe" %% "circe-core",
//  "io.circe" %% "circe-generic",
//  "io.circe" %% "circe-parser"
//).map(_ % circeVersion)
enablePlugins(SbtTwirl)
enablePlugins(JettyPlugin)
//addSbtPlugin("org.scalatra.sbt" % "scalatra-sbt" % "0.4.0")