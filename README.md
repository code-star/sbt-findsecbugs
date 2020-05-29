[![CircleCI](https://circleci.com/gh/code-star/sbt-findsecbugs.png)](https://circleci.com/gh/code-star/sbt-findsecbugs)

# sbt-findsecbugs
An SBT plugin for FindSecurityBugs

# Usage
Add to your `plugins.sbt`: `"nl.codestar" % "sbt-findsecbugs" % "(current version)"`

(You can find the current version [here](https://github.com/code-star/sbt-findsecbugs/releases).)

You can now run `sbt findSecBugs`.

# Configuration

sbt-findsecbugs has one setting:

| Setting                         | Default                                             | Meaning                                                                                                                                                           |
|---------------------------------|-----------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `findSecBugsExcludeFile`        | `None`                                              | Optionally provide a SpotBugs [exclusion file](https://spotbugs.readthedocs.io/en/latest/filter.html).                                                            |
| `findSecBugsFailOnMissingClass` | `true`                                              | Consider the 'missing class' flag as failure or not. Set this to 'false' in case you excpect and want to ignore missing class messages during the check.          |
| `findSecBugsParallel`           | `true`                                              | In a multimodule build, whether to run the security check for all submodules in parallel. If you run into memory issues, it might help to set this to `false`.    |
| `findSecBugsPriorityThreshold`  | `Priority.Normal`                                   | Set the priority threshold. Bug instances must be at least as important as this priority to be reported. Possible values: `High`, `Normal`, `Low`, `Experimental`.|
| `findSecBugs / artifactPath`    | `crossTarget.value / "findsecbugs" / "report.html"` | Output path for the resulting report.                                                                                                                             |
| `findSecBugs / forkOptions`     | derived from other settings                         | Configuration for the forked JVM. Uses the corresponding settings (`findSecBugs / javaOptions`).                                                                  |

# For developers of sbt-findsecbugs

## Tests
The plugin can be tested manually by running `sbt findSecBugs` in the test-project
The plugin has automated test which can be run by this command `sbt scripted`

## Release
To release a new version:
* Get a [bintray](https://bintray.com) account and make sure you're a member of the [`code-star`](https://bintray.com/code-star) organization.
* Run `sbt publish`

