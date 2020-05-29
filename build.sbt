enablePlugins(SbtPlugin)

name := "sbt-findsecbugs"
organization := "nl.codestar"
version := "0.16"
description := "The Findbugs security plugin wrapped in a sbt plugin"

scalaVersion := "2.12.8"
scalacOptions ++= Seq("-encoding", "UTF8", "-Xfatal-warnings",
  "-deprecation", "-feature", "-unchecked", "-Xlint",
  "-Ywarn-dead-code", "-Ywarn-adapted-args"
)

scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
scriptedBufferLog := false

bintrayRepository := "sbt-findsecbugs"
bintrayOrganization := Some("code-star")

licenses += ("MIT", url("https://opensource.org/licenses/MIT"))

publishMavenStyle := false
