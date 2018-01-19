package nl.codestar.sbtfindsecbugs

import java.io.File

import sbt._
import Keys._

object FindSecBugs extends AutoPlugin {
  private val findsecbugsPluginVersion = "1.7.1"

  private val FindsecbugsConfig = sbt.config("findsecbugs")
  private val FindSecBugsTag = Tags.Tag("findSecBugs")

  override def trigger = AllRequirements

  object autoImport {
    lazy val findSecBugsParallel = settingKey[Boolean]("Perform FindSecurityBugs check in parallel (or not)")
    lazy val findSecBugs = taskKey[Unit]("Perform FindSecurityBugs check")
  }

  import autoImport._

  override lazy val projectSettings = Seq(
    findSecBugsParallel := true,
    concurrentRestrictions in Global ++= (if (findSecBugsParallel.value) Nil else Seq(Tags.exclusive(FindSecBugsTag))),
    ivyConfigurations += FindsecbugsConfig,
    libraryDependencies ++= Seq(
      "com.github.spotbugs" % "spotbugs" % "3.1.1",
      "com.h3xstream.findsecbugs" % "findsecbugs-plugin" % findsecbugsPluginVersion),
    findSecBugs := (findSecBugsTask tag FindSecBugsTag).value
  )

  private def findSecBugsTask() = Def.task {
    def commandLineClasspath(classpathFiles: Seq[File]): String = PathFinder(classpathFiles.filter(_.exists)).absString
    lazy val log = Keys.streams.value.log
    lazy val output = crossTarget.value / "findsecbugs" / "report.html"
    lazy val classpath = commandLineClasspath((dependencyClasspath in Compile).value.files)
    lazy val auxClasspath = commandLineClasspath((dependencyClasspath in Compile).value.files)
    lazy val ivyHome = ivyPaths(_.ivyHome).value.getOrElse(Path.userHome / ".ivy2")
    lazy val pluginList = s"${ivyHome.absolutePath}/cache/com.h3xstream.findsecbugs/findsecbugs-plugin/jars/findsecbugs-plugin-$findsecbugsPluginVersion.jar"
    lazy val classDir = (classDirectory in Compile).value
    lazy val jHome = javaHome.value

    IO.createDirectory(output.getParentFile)
    IO.withTemporaryDirectory { tempdir =>
      val includeFile = createIncludesFile(tempdir)

      if (classDir.exists && !classDir.list().isEmpty) {
        log.info(s"Performing FindSecurityBugs check of '$classDir'...")
        val findBugsLogger = new FindBugsLogger(log)
        val forkOptions = ForkOptions(
          javaHome = jHome,
          outputStrategy = Option(LoggedOutput(findBugsLogger)),
          bootJars = Vector.empty[java.io.File],
          workingDirectory = None,
          runJVMOptions = Vector.empty[String],
          connectInput = true,
          envVars = Map.empty[String, String])

        val result = Fork.java(
          forkOptions,
          List("-Xmx1024m", "-cp", classpath, "edu.umd.cs.findbugs.LaunchAppropriateUI", "-textui",
            "-exitcode", "-html:plain.xsl", "-output", output.getAbsolutePath, "-nested:true",
            "-auxclasspath", auxClasspath, "-low", "-effort:max", "-pluginList", pluginList,
            "-include", includeFile.getAbsolutePath, classDir.getAbsolutePath))

        if (result != 0) sys.error(s"Security issues found. Please review them in $output")
      }
      else {
        log.warn(s"The directory $classDir does not exist or is empty, not running scan")
      }
    }
  }

  private def createIncludesFile(tempdir: sbt.File): sbt.File = {
    val includeFile = tempdir / "include.xml"
    val includeXml =
      """
        |<FindBugsFilter>
        |    <Match>
        |        <Bug category="SECURITY"/>
        |    </Match>
        |</FindBugsFilter>
      """.stripMargin
    IO.write(includeFile, includeXml)
    includeFile
  }

  /**
    * FindBugs logs everyting to stderr, even when everything was succesful.
    * This logger makes that logging a little bit smarter.
    */
  class FindBugsLogger(underlying: Logger) extends Logger {
    override def log(level: Level.Value, message: => String): Unit = (level, message.toLowerCase) match {
      case (Level.Debug, _) =>
        underlying.log(Level.Debug, message)
      case (_, s) if s.contains("error") =>
        underlying.log(Level.Error, message)
      case (_, s) if s.contains("warning") =>
        underlying.log(Level.Warn, message)
      case _ =>
        underlying.log(Level.Info, message)
    }
    override def trace(t: => Throwable): Unit = underlying.trace(t)
    override def success(message: => String): Unit = underlying.success(message)
  }

}
