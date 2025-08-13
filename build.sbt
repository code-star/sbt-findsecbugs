enablePlugins(SbtPlugin)

name := "sbt-findsecbugs"

ThisBuild / organization := "nl.codestar"
ThisBuild / version := "0.18-SNAPSHOT"
ThisBuild / description := "The Spotbugs tool, with Findbugs security plugin, wrapped in an sbt plugin"

ThisBuild / organizationName := "Codestar powered by Sopra Steria"
ThisBuild / organizationHomepage := Some(url("https://codestar.nl"))
ThisBuild / homepage := Some(url("https://codestar.nl/sbt-findsecbugs"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/code-star/sbt-findsecbugs"),"scm:git@github.com:code-star/sbt-findsecbugs.git")
)

ThisBuild / developers := List(
  Developer(
    "jeanmarc",
    "Jean-Marc van Leerdam",
    "jean-marc.vanleerdam@soprassteria.com",
    url("https://soprasteria.com")
  )
)

ThisBuild / scalaVersion := "2.12.18"
ThisBuild / scalacOptions ++= Seq(
  "-encoding", "UTF8",
  "-Xfatal-warnings",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint",
  "-Ywarn-dead-code",
  "-Ywarn-adapted-args"
)

ThisBuild / scriptedLaunchOpts := { scriptedLaunchOpts.value ++
  Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
}
ThisBuild / scriptedBufferLog := false

ThisBuild / licenses += ("MIT", url("https://opensource.org/licenses/MIT"))

ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishMavenStyle := true

ThisBuild / publishTo := {
  val centralSnapshots = "https://central.sonatype.com/repository/maven-snapshots/"
  if (isSnapshot.value) Some("central-snapshots" at centralSnapshots)
  else localStaging.value
}

ThisBuild / credentials += Credentials(
  "GnuPG Key ID",
  "gpg",
  System.getenv("PGP_KEYID"), // key identifier
  "ignored" // this field is ignored; passwords are supplied by pinentry
)

ThisBuild / credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "oss.sonatype.org",
  System.getenv("SONATYPE_USER"),
  System.getenv("SONATYPE_PASSWORD") // Use environment variable for security
)

ThisBuild / credentials += Credentials(
  "Sonatype Nexus Repository Manager",
  "central.sonatype.com",
  System.getenv("SONATYPE_USER"),
  System.getenv("SONATYPE_PASSWORD") // Use environment variable for security
)

ThisBuild / credentials += Credentials(
  "central-snapshots",
  "central.sonatype.com",
  System.getenv("SONATYPE_USER"),
  System.getenv("SONATYPE_PASSWORD") // Use environment variable for security
)

lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin, ScriptedPlugin)
  .settings(
    name := "sbt-findsecbugs",
    version := "0.18-SNAPSHOT",
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.11.2" // set minimum version
      }
    },
    console / initialCommands := """import nl.codestar.sbt.findsecbugs._"""
  )