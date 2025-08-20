import java.util.UUID

lazy val root = project.in(file("."))
  .withId("sbt-findsecbugs")
  .enablePlugins(SbtPlugin, ScriptedPlugin)
  .settings(
    name := "sbt-findsecbugs",
    version := "0.19",
    description := "The Spotbugs tool, with Findbugs security plugin, wrapped in an sbt plugin",
    organization := "nl.codestar",
    organizationName := "Codestar powered by Sopra Steria",
    organizationHomepage := Some(url("https://codestar.nl")),
    homepage := Some(url("https://codestar.nl/sbt-findsecbugs")),

    scalaVersion := "2.12.18",
    pluginCrossBuild / sbtVersion := {
      scalaBinaryVersion.value match {
        case "2.12" => "1.11.4" // set minimum version
      }
    },
    scalacOptions ++= Seq(
      "-encoding", "UTF8",
      "-Xfatal-warnings",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint",
      "-Ywarn-dead-code",
      "-Ywarn-adapted-args"
    ),
    console / initialCommands := """import nl.codestar.sbt.findsecbugs._""",
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/code-star/sbt-findsecbugs"),"scm:git@github.com:code-star/sbt-findsecbugs.git")
    ),
    developers := List(
      Developer(
        "jeanmarc",
        "Jean-Marc van Leerdam",
        "jean-marc.vanleerdam@soprasteria.com",
        url("https://soprasteria.com")
      )
    ),
    credentials ++= Seq(
      Credentials(
        "GnuPG Key ID",
        "gpg",
        System.getenv("PGP_KEYID"), // key identifier
        "ignored" // this field is ignored; passwords are supplied by pinentry
      ),
      Credentials(
        "Sonatype Nexus Repository Manager",
        "oss.sonatype.org",
        System.getenv("SONATYPE_USER"),
        System.getenv("SONATYPE_PASSWORD") // Use environment variable for security
      ),
      Credentials(
        "Sonatype Nexus Repository Manager",
        "central.sonatype.com",
        System.getenv("SONATYPE_USER"),
        System.getenv("SONATYPE_PASSWORD") // Use environment variable for security
      ),
      Credentials(
        "central-snapshots",
        "central.sonatype.com",
        System.getenv("SONATYPE_USER"),
        System.getenv("SONATYPE_PASSWORD") // Use environment variable for security
      )
    )
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

ThisBuild / sonaDeploymentName := {
    val o = organization.value
    val n = name.value
    val v = version.value
    val uuid = UUID.randomUUID().toString.take(8)
    s"$o:$n:$v:$uuid"
  }
