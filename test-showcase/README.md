# test-showcase
A multi-project sbt example that uses the `sbt-findsecbugs` plugin to run SpotBugs with FindSecurityBugs.

There are 3 sub-projects in this showcase:
- `case_ignored_with_issues`: a subproject that has security issues, but is ignored by the plugin
- `case_no_issues`: a subproject that has no security issues
- `case_with_issues`: a subproject that has security issues and is not ignored


## Usage
```
cd test-showcase
sbt findSecBugs
```

Output of the scans is written to the `[sub-project]/target/scala-2.12/findsecbugs/report.html` file for each included subproject.
