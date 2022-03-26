[![CircleCI](https://circleci.com/gh/code-star/sbt-findsecbugs.png)](https://circleci.com/gh/code-star/sbt-findsecbugs)

# sbt-findsecbugs
An SBT plugin for FindSecurityBugs

# Usage
Add to your `plugins.sbt`:

```
"nl.codestar" % "sbt-findsecbugs" % "(current version)"
```

(You can find the current version [here](https://github.com/code-star/sbt-findsecbugs/releases).)

Optionally, override the default SpotBugs version to use for your project with

```
findSecBugsSpotBugsVersion := "4.6.0"
```

![spotbugs release badge]

You can now run `sbt findSecBugs`.

# Configuration

The plugin has the following settings:

| Setting                         | Default                                             | Meaning                                                                                                                                                            |
|---------------------------------|-----------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `findSecBugsExcludeFile`        | `None`                                              | Optionally provide a SpotBugs [exclusion file](https://spotbugs.readthedocs.io/en/latest/filter.html).                                                             |
| `findSecBugsFailOnMissingClass` | `true`                                              | Consider the 'missing class' flag as failure or not. Set this to 'false' in case you expect and want to ignore missing class messages during the check.            |
| `findSecBugsParallel`           | `true`                                              | In a multimodule build, whether to run the security check for all submodules in parallel. If you run into memory issues, it might help to set this to `false`.     |
| `findSecBugsPriorityThreshold`  | `Priority.Low`                                      | Set the priority threshold. Bug instances must be at least as important as this priority to be reported. Possible values: `High`, `Normal`, `Low`, `Experimental`. |
| `findSecBugsSpotBugsVersion`    | `4.2.2`                                             | Version of the SpotBugs tool to use.                                                                                                                               |
| `findSecBugs / artifactPath`    | `crossTarget.value / "findsecbugs" / "report.html"` | Output path for the resulting report.                                                                                                                              |
| `findSecBugs / forkOptions`     | derived from other settings                         | Configuration for the forked JVM. Uses the corresponding settings (`findSecBugs / javaOptions`).                                                                   |

# For developers

## Tests
The plugin can be tested manually by running `sbt findSecBugs` in the `test-project` subdirectory.
The plugin has automated tests which can be run with `sbt scripted`.

## Release
To release a new version:
* Get a [bintray](https://bintray.com) account and make sure you're a member of the [`code-star`](https://bintray.com/code-star) organization.
* Run `sbt publish`

[spotbugs release badge]: https://maven-badges.herokuapp.com/maven-central/com.github.spotbugs/spotbugs/badge.svg?subject=Latest%20spotbugs&color=yellowgreen
