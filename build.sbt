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

lazy val root = (project in file("."))
  .settings(
    name := "WebCrawler"
  )

// dependencies
libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "org.jsoup" % "jsoup" % jsoupVersion,
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.json4s" %% "json4s-jackson" % Json4sVersion,
  "javax.servlet" % "javax.servlet-api" % javaXServlet % "provided",
  "com.typesafe.scala-logging" %% "scala-logging" % ScalaLoggingVersion,
  "org.scalamock" %% "scalamock" % scalaMockVersion % Test,
  "org.scalatest" %% "scalatest" % scalaTest % Test,
  "org.scalactic" %% "scalactic" % scalaTest,
  "org.scalacheck" %% "scalacheck" % scalaCheckVersion % "test",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion
)

// see to refactor
//libraryDependencies ++= Seq(
//  "io.circe" %% "circe-core",
//  "io.circe" %% "circe-generic",
//  "io.circe" %% "circe-parser"
//).map(_ % circeVersion)