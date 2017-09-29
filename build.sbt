sbtPlugin := true

name := "sbt-findsecbugs"
organization := "nl.codestar"
version := "0.7-SNAPSHOT"
description := "The Findbugs security plugin wrapped in a sbt plugin"

scalaVersion := "2.12.3"
scalacOptions ++= Seq("-encoding", "UTF8", "-Xfatal-warnings",
  "-deprecation", "-feature", "-unchecked", "-Xlint",
  "-Ywarn-dead-code", "-Ywarn-adapted-args"
)

libraryDependencies += "com.google.code.findbugs" % "findbugs" % "3.0.1"
libraryDependencies += "com.google.code.findbugs" % "jsr305" % "3.0.1"
libraryDependencies += "com.h3xstream.findsecbugs" % "findsecbugs-plugin" % "1.6.0"

// Scripted - sbt plugin tests

//import bintray.Keys._

//bintrayPublishSettings

licenses += ("MIT", url("https://opensource.org/licenses/MIT"))
publishMavenStyle := false
//repository in bintray := "sbt-plugins"
//bintrayOrganization in bintray := None
