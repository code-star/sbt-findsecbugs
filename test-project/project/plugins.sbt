// build root project
lazy val root = Project("plugins", file(".")).dependsOn(ProjectRef(awesomeOS, "sbt-findsecbugs"))

// depends on the awesomeOS project
lazy val awesomeOS = file("..").getAbsoluteFile
