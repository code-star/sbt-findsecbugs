enablePlugins(SbtPlugin)

name := "sbt-findsecbugs"
organization := "nl.codestar"
version := "0.18-SNAPSHOT"
description := "The Spotbugs tool, with Findbugs security plugin, wrapped in an sbt plugin"

scalaVersion := "2.12.18"
scalacOptions ++= Seq(
  "-encoding", "UTF8",
  "-Xfatal-warnings",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-adapted-args"
)

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false

licenses += ("MIT", url("https://opensource.org/licenses/MIT"))

publishMavenStyle := false

publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}

usePgpKeyHex("F379713EFB5A4C0CF021AB847A9F591F7E30B737")