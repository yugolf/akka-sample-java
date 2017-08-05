name := """reactive-akka-sample-java"""

version := "1.0"

scalaVersion := "2.12.2"

scalacOptions ++= Seq("-feature")
javacOptions ++= Seq("-encoding", "UTF-8")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.3",
  "com.typesafe.akka" %% "akka-slf4j" % "2.5.3",
  "ch.qos.logback" % "logback-classic" % "1.1.3")

fork in run := true
