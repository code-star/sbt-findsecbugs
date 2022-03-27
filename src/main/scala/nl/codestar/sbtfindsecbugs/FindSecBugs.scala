package nl.codestar.sbtfindsecbugs

import java.io.File

import sbt._
import Keys._
import Priority._

object FindSecBugs extends AutoPlugin {
  private val exitCodeOk: Int = 0
  private val exitCodeClassesMissing: Int = 2

  private val findSecBugsPluginOrganization = "com.h3xstream.findsecbugs"
  private val findSecBugsPluginName = "findsecbugs-plugin"

  private val FindSecBugsConfig = sbt.config("findsecbugs")
    .describedAs("Classpath configuration for SpotBugs")
  private val FindSecBugsTag = Tags.Tag("findSecBugs")

  override def trigger = AllRequirements

  object autoImport {
    lazy val findSecBugsExcludeFile = settingKey[Option[File]]("The FindBugs exclude file for findsecbugs")
    lazy val findSecBugsFailOnMissingClass = settingKey[Boolean]("Consider 'missing class' flag as error")
    lazy val findSecBugsParallel = settingKey[Boolean]("Perform FindSecurityBugs check in parallel (or not)")
    lazy val findSecBugsPriorityThreshold = settingKey[Priority]("Set the priority threshold. Bug instances must be at least as important as this priority to be reported")
    lazy val findSecBugsSpotBugsVersion = settingKey[String]("Version of the SpotBugs tool to use")
    lazy val findSecBugsPluginVersion = settingKey[String]("Version of the FindSecurityBugs plugin to use")
    lazy val findSecBugs = taskKey[Unit]("Perform FindSecurityBugs check")
  }

  import autoImport._

  override lazy val projectSettings: Seq[Def.Setting[_]] =
    inConfig(FindSecBugsConfig)(Defaults.configSettings) ++
      inTask(findSecBugs)(Seq(
        forkOptions := Defaults.forkOptionsTask.value,
        connectInput := true,
        javaOptions += "-Xmx1024m"
      )) ++ Seq(
        findSecBugsExcludeFile := None,
        findSecBugsFailOnMissingClass := true,
        findSecBugsParallel := true,
        findSecBugsPriorityThreshold := Low,
        findSecBugsSpotBugsVersion := "4.2.2",
        findSecBugsPluginVersion := "1.11.0",
        concurrentRestrictions in Global ++= (if (findSecBugsParallel.value) Nil else Seq(Tags.exclusive(FindSecBugsTag))),
        ivyConfigurations += FindSecBugsConfig,
        libraryDependencies ++= Seq(
          "com.github.spotbugs" % "spotbugs" % findSecBugsSpotBugsVersion.value % FindSecBugsConfig,
          findSecBugsPluginOrganization % findSecBugsPluginName % findSecBugsPluginVersion.value % FindSecBugsConfig,
          "org.slf4j" % "slf4j-simple" % "1.8.0-beta4" % FindSecBugsConfig
        ),
        findSecBugs := (findSecBugsTask tag FindSecBugsTag).value,
        artifactPath in findSecBugs := crossTarget.value / "findsecbugs" / "report.html"
      )

  private def findSecBugsTask() = Def.task {
    def commandLineClasspath(classpathFiles: Seq[File]): String = PathFinder(classpathFiles.filter(_.exists)).absString
    lazy val log = Keys.streams.value.log
    lazy val output = (artifactPath in findSecBugs).value
    lazy val classpath = commandLineClasspath((dependencyClasspath in FindSecBugsConfig).value.files)
    lazy val auxClasspath = commandLineClasspath((dependencyClasspath in Compile).value.files)
    lazy val classDirs = (products in Compile).value
    lazy val excludeFile = findSecBugsExcludeFile.value

    lazy val updateReport = update.value
    lazy val pluginList: String = findPluginJar(updateReport).getOrElse(
      sys.error(
        s"Failed to find resolved JAR for $findSecBugsPluginOrganization.$findSecBugsPluginName:${findSecBugsPluginVersion.value}")
    ).getAbsolutePath
    lazy val forkOptions0 = (findSecBugs / forkOptions).value
      // can't do this through settings - `streams` is a task.
      .withOutputStrategy(LoggedOutput(new FindBugsLogger(log)))

    IO.createDirectory(output.getParentFile)
    IO.withTemporaryDirectory { tempdir =>
      val includeFile = createIncludesFile(tempdir)
      val filteredClassDirs = classDirs.filter(_.exists)
      if (filteredClassDirs.nonEmpty) {
        val filteredClassDirsStr = filteredClassDirs.map(cd => s"'$cd'").mkString(", ")
        log.info(s"Performing FindSecurityBugs check of $filteredClassDirsStr...")
        val opts = List("-cp", classpath, "edu.umd.cs.findbugs.LaunchAppropriateUI", "-textui",
          "-exitcode", "-html:plain.xsl", "-output", output.getAbsolutePath, "-nested:true",
          "-auxclasspath", auxClasspath, s"-${findSecBugsPriorityThreshold.value.name}", "-effort:max", "-pluginList", pluginList,
          "-noClassOk") ++
          List("-include", includeFile.getAbsolutePath) ++
          excludeFile.toList.flatMap(f => List("-exclude", f.getAbsolutePath)) ++
          filteredClassDirs.map(_.getAbsolutePath)
        val result = Fork.java(forkOptions0, opts)
        result match {
          case `exitCodeOk` =>
            //noop
          case `exitCodeClassesMissing` if !findSecBugsFailOnMissingClass.value =>
            //noop
          case _ =>
            sys.error(s"Security issues found. Please review them in $output")
        }
      } else {
        val classDirsStr = classDirs.map(cd => s"'$cd'").mkString(", ")
        log.warn(s"Class directory list ($classDirsStr) contains no existing directories, not running scan")
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

  private def findPluginJar(updateReport: UpdateReport): Option[File] =
    updateReport.configuration(FindSecBugsConfig)
      .flatMap(_.modules.find { resolvedModule =>
        // We don't compare the revisions, etc. - resolution can change those.
        resolvedModule.module.organization == findSecBugsPluginOrganization &&
        resolvedModule.module.name == findSecBugsPluginName
      })
      .flatMap(_.artifacts.collectFirst {
        case (artifact, file) if artifact.`type` == Artifact.DefaultType => file
      })

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
