ThisBuild / scalaVersion := "3.3.6"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / organization := "net.martinprobson"

val logging = Seq(
  "org.slf4j" % "slf4j-api" % "2.0.17",
  "ch.qos.logback" % "logback-classic" % "1.5.18",
  "ch.qos.logback" % "logback-core" % "1.5.18",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"
)

val test = Seq(
  "org.scalactic" %% "scalactic" % "3.2.19" % Test withSources () withJavadoc (),
  "org.scalatest" %% "scalatest" % "3.2.19" % Test withSources () withJavadoc (),
  "org.scalamock" %% "scalamock" % "7.4.1" % Test withSources () withJavadoc (),
  "com.disneystreaming" %% "weaver-cats" % "0.8.4" % Test withSources() withJavadoc(),
  "com.disneystreaming" %% "weaver-scalacheck" % "0.8.4" % Test withSources() withJavadoc()
)

val catsEffectVersion = "3.6.3"

val catseffect = Seq("org.typelevel" %% "cats-effect" % catsEffectVersion withSources () withJavadoc ())

lazy val root = (project in file("."))
  .settings(name := "shopping-basket")
  .settings(libraryDependencies ++= logging, libraryDependencies ++= catseffect, libraryDependencies ++= test)

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-explaintypes", // Explain type errors in more detail.
  "-Xfatal-warnings" // Fail the compilation if there are any warnings.
)

Compile / run / fork := true

testFrameworks += new TestFramework("weaver.framework.CatsEffect")

javacOptions ++= Seq("-source", "17", "-target", "17", "-Xlint")

assembly / assemblyMergeStrategy := {
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case n if n.startsWith("reference.conf") => MergeStrategy.concat
  case _                                   => MergeStrategy.first
}
