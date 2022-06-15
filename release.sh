#!/bin/sh -ex

: ${1?"Usage: $0 <[pre]major|[pre]minor|[pre]patch|prerelease>"}

if [ "$(semver -h | grep -c 'A JavaScript implementation of the https://semver.org/ specification')" -eq "0" ]; then
  echo "please install: npm install -g semver"
  exit 1
fi

#./mvnw scm:check-local-modification

#current=$(git describe --abbrev=0 || echo 0.0.0)
release="4.2.0"
next=$(semver ${release} -i minor)

git checkout -b release/${release}

./mvnw versions:set -D newVersion=${release}
git commit -am "Release ${release}"
./mvnw -s ~/.m2/settings-oss.xml clean deploy scm:tag -P release -D tag=${release} -D pushChanges=false -D skipTests -D dependency-check.skip

./mvnw versions:set -D newVersion=${next}-SNAPSHOT
git commit -am "Development ${next}-SNAPSHOT"

git push --set-upstream origin release/${release}
git push --tags

git checkout main
git branch -D release/${release}
