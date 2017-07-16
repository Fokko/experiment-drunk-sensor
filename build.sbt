name := "experimentation-day-clash-of-the-titans"

version := "1.0"

scalaVersion := "2.12.2"

resolvers += Resolver.bintrayRepo("cakesolutions", "maven")

resolvers += "Artima Maven Repository" at "http://repo.artima.com/releases"

resolvers += "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/"

libraryDependencies += "net.cakesolutions" %% "scala-kafka-client" % "0.11.0.0"

libraryDependencies += "net.cakesolutions" %% "scala-kafka-client-akka" % "0.11.0.0"

libraryDependencies += "org.json4s" %% "json4s-jackson" % "3.5.2"

libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"

libraryDependencies += "org.scalanlp" %% "breeze" % "0.13.1"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.3"

