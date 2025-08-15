// build root project
lazy val root = Project("test-project", file(".")) dependsOn(ProjectRef(localSecbugPlugin, "sbt-findsecbugs"))

// depends on the localSecbugPlugin project (which is the parent project)
lazy val localSecbugPlugin = file("..").getAbsoluteFile
