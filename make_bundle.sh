#!/usr/bin/env bash
ARTIFACT_VERSION=0.18
BUNDLE_FOLDER=target/nl/codestar/sbt-findsecbugs_2.12_1.0/$ARTIFACT_VERSION
mkdir -p $BUNDLE_FOLDER
cp -r target/scala-2.12/sbt-1.0/sbt-findsecbugs_2.12_1.0-${ARTIFACT_VERSION}* $BUNDLE_FOLDER
find $BUNDLE_FOLDER -type f \( -name "*.jar" -o -name "*.pom" \) -exec sh -c 'md5sum "$1" | awk "{print \$1}" > "$1.md5";sha1sum "$1" | awk "{print \$1}" > "$1.sha1"' _ {} \;
rm -f target/result.zip
(cd target; zip -r result.zip nl)
