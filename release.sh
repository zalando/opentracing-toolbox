#!/bin/sh -ex

: ${1?"Usage: $0 <new version>"}

./mvnw scm:check-local-modification

release="$1"

./mvnw versions:set -D newVersion="${release}"
git commit -am "Release ${release}"
./mvnw -s ~/.m2/settings-oss.xml clean deploy scm:tag -P release -D tag="${release}" -D pushChanges=false -D skipTests -D dependency-check.skip

git push --tags
