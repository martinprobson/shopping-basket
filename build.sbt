
ThisBuild / scalaVersion     := "3.3.6"
ThisBuild / version          := "0.0.1-SNAPSHOT"
ThisBuild / organization     := "net.martinprobson"

val circeVersion = "0.14.14"
val circe = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-literal"
).map(_ % circeVersion withSources() withJavadoc())

val http4sVersion = "0.23.30"
val http4s = Seq(
  "org.http4s" %% "http4s-circe",
  "org.http4s" %% "http4s-ember-server",
  "org.http4s" %% "http4s-ember-client",
  "org.http4s" %% "http4s-dsl"
).map(_ % http4sVersion withSources() withJavadoc())

val fs2Version = "3.12.0"
val fs2 = Seq(
    "co.fs2" %% "fs2-core",
    "co.fs2" %% "fs2-io"
).map(_ % fs2Version withSources() withJavadoc())

val logging = Seq(
  "org.slf4j" % "slf4j-api" % "2.0.17",
  "ch.qos.logback" % "logback-classic" % "1.5.18",
  "ch.qos.logback" % "logback-core" % "1.5.18",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
)

val test = Seq(
  "org.scalactic" %% "scalactic" % "3.2.19" % Test withSources() withJavadoc(),
  "org.scalatest" %% "scalatest" % "3.2.19" % Test withSources() withJavadoc(),
  "org.scalamock" %% "scalamock" % "7.4.1" % Test withSources() withJavadoc()
)

lazy val root = (project in file("."))
  .settings( name := "shopping-basket" )
  .settings( libraryDependencies ++= logging,
    libraryDependencies ++= http4s,
    libraryDependencies ++= circe,
    libraryDependencies ++= fs2,
    libraryDependencies ++=  test
  )

scalacOptions ++= Seq(
  "-deprecation",     // Emit warning and location for usages of deprecated APIs.
  "-explaintypes",    // Explain type errors in more detail.
  "-Xfatal-warnings" // Fail the compilation if there are any warnings.
)

javacOptions ++= Seq("-source", "17", "-target", "17", "-Xlint")

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "MANIFEST.MF")       => MergeStrategy.discard
  case n if n.startsWith("reference.conf") => MergeStrategy.concat
  case _                                   => MergeStrategy.first
}
