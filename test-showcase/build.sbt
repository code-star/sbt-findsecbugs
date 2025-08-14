
lazy val root = project.in(file("."))
  .settings(
    name := "test-showcase",
    version := "0.0.1-SNAPSHOT",
    description := "A showcase multi-project for sbt-findsecbugs",

    scalaVersion := "2.12.18"
  ).aggregate(
    caseIgnoredWithIssues,
    caseNoIssues,
    caseWithIssues
  )

lazy val caseIgnoredWithIssues = project.in(file("case_ignored_with_issues"))
  .disablePlugins(FindSecBugs)

lazy val caseNoIssues = project.in(file("case_no_issues"))

lazy val caseWithIssues = project.in(file("case_with_issues"))

