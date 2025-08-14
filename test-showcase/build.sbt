
lazy val root = project.in(file("."))
  .settings(
    name := "test-showcase",
    version := "0.0.1-SNAPSHOT",
    description := "A showcase multi-project for sbt-findsecbugs",

    scalaVersion := "2.12.18"
  ).aggregate(
    caseNoIssues,
    caseWithIssues
  )

lazy val caseIgnoredWithIssues = project.in(file("caseIgnoredWithIssues"))
  .disablePlugins(FindSecBugs)

lazy val caseNoIssues = project.in(file("caseNoIssues"))

lazy val caseWithIssues = project.in(file("casewithIssues"))

