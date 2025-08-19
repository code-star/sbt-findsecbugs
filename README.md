[![CircleCI](https://circleci.com/gh/code-star/sbt-findsecbugs.png)](https://circleci.com/gh/code-star/sbt-findsecbugs)

# sbt-findsecbugs
An SBT plugin to run [SpotBugs](https://spotbugs.github.io/) with [FindSecurityBugs](https://find-sec-bugs.github.io/) plugin in your SBT build.

# Usage
Add the following line to your `project/plugins.sbt`, replacing `(current version)` with the latest version:

```
addSbtPlugin("nl.codestar" % "sbt-findsecbugs" % "(current version)")`
```

(You can find the current version [here](https://github.com/code-star/sbt-findsecbugs/releases).)

Optionally, override the default SpotBugs and FindSecBugs versions in your `build.sbt`:

```
findSecBugsSpotBugsVersion := "4.9.4"
findSecBugsSpotBugsPluginVersion := "1.14.0"
```

You can now run: 
```
sbt findSecBugs
```

## Ignoring sub-projects
The plugin is an autoplugin, which means it is activated for all projects by default.

If you want to ignore a sub-project, you can add the following setting to that sub-project:

```sbt
lazy val subProject = project.in(file("sub-folder"))
  .disablePlugins(FindSecBugs)
```

# Configuration

sbt-findsecbugs has several settings:

| Setting                            | Default                                             | Meaning                                                                                                                                                           |
|------------------------------------|-----------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `findSecBugsExcludeFile`           | `None`                                              | Optionally provide a SpotBugs [exclusion file](https://spotbugs.readthedocs.io/en/latest/filter.html)                                                             |
| `findSecBugsFailOnMissingClass`    | `true`                                              | Consider a `missing class` message as failure or not. Set this to `false` in case you expect them, and want to ignore them during the check                       |
| `findSecBugsParallel`              | `true`                                              | In a multimodule build, whether to run the security check for all submodules in parallel. If you run into memory issues, it might help to set this to `false`     |
| `findSecBugsPriorityThreshold`     | `Priority.Low`                                      | Set the priority threshold. Bug instances must be at least as important as this priority to be reported. Possible values: `High`, `Normal`, `Low`, `Experimental` |
| `findSecBugsSpotBugsVersion`       | 4.9.4                                               | The version of SpotBugs to use                                                                                                                                    |  
| `findSecBugsSpotBugsPluginVersion` | 1.14.0                                              | The version of FindSecBugs SpotBugs plugin to use                                                                                                                 |
| `findSecBugs / artifactPath`       | `crossTarget.value / "findsecbugs" / "report.html"` | Output path for the resulting report                                                                                                                              |
| `findSecBugs / forkOptions`        | derived from other settings                         | Configuration for the forked JVM. Uses the corresponding settings (`findSecBugs / javaOptions`)                                                                   |

# For developers of sbt-findsecbugs

## Tests
The plugin can be tested manually by running `sbt findSecBugs` in the `test-project` folder (this uses the local version of the plugin).

The plugin has automated tests which can be run by this command `sbt scripted`

## Release
To release a new version, make sure you have:
* proper access to the `nl.codestar` namespace on Sonatype.
* GnuPG (`gpg`) installed and a signing key configured.
  * We use `sbt-pgp` plugin to sign, which relies on the `gpg` command line tool
* create a `.env` file in the project root with the following variables:
  ```
  PGP_KEYID=<id of the signing key>
  PGP_PASSPHRASE=<your PGP passphrase>
  SONATYPE_USER=<user id or token id>
  SONATYPE_PASSWORD=<password or token>

  ```

Note: The `.env` file needs to be kept out of the git repository (it is `.gitignore`d).

See [Using Sonatype](https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html) in the SBT documentation.

Steps to release (preferred):
1. Update the version in `build.sbt` to a non-SNAPSHOT version.
2. `sbt publishSigned`
3. `sbt sonaUpload`
4. Go to https://central.sonatype.com/publishing/deployments and publish the deployment.
   * or run `sbt sonaRelease` to publish the deployment automatically

Steps to release via manual zip upload:
1. Update the version in `build.sbt` to a non-SNAPSHOT version.
2. `sbt publishLocalSigned`
3. In the project root
   * `./make_bundle.sh`
4. Upload the `./target/result.zip` as a new deployment to the Sonatype `nl.codestar` namespace
   * `https://central.sonatype.com/publishing`
5. If the zip is validated, you can publish by clicking the `Publish` button, or `drop` to abandon the deployment


### Previous releases
Up to version 0.16, the plugin was released via BinTray / JFrog. Old versions can be found at https://scala.jfrog.io/ui/native/sbt-plugin-releases/nl.codestar/sbt-findsecbugs/
