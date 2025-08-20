// build project
lazy val plugins = project.in(file("."))
  .dependsOn(ProjectRef(localSecbugPlugin, "sbt-findsecbugs"))

// depends on the localSecbugPlugin project (which is the parent project)
lazy val localSecbugPlugin = file("..").getAbsoluteFile
