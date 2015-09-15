#!/bin/bash
set -e

. "$(git --exec-path)/git-sh-setup"

## Make sure no changes are outstanding.
require_clean_work_tree "perform a release"

## Make sure we are at the top of the repository
cd_to_toplevel

function getCurrentPomVersion {
  mvn -q -Dexec.executable="echo" -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.4.0:exec 2> /dev/null
}

function incrementPomVersion {
  
  read currentPomVersion
  
	declare -a version=($(echo ${currentPomVersion} | tr - . | tr . ' '))
	
	if [ "$2" == "major" ]; then
		## [THIS].0.1-SNAPSHOT
		version[0]=$[${version[0]} + 1]
	elif [ "$2" == "minor" ]; then
		## 0.[THIS].1-SNAPSHOT
		version[1]=$[${version[1]} + 1]
	else
		## 0.0.[THIS]-SNAPSHOT
		version[2]=$[${version[2]} + 1]
	fi
	
	if [ -n ${version[3]} ]; then
		version[3]=-${version[3]}
	fi
	
	echo ${version[0]}.${version[1]}.${version[2]}${version[3]}
}

CURRENT_SNAP_VERSION=$(getCurrentPomVersion)
RELEASE_VERSION=${CURRENT_SNAP_VERSION%-SNAPSHOT}
NEXT_SNAP_VERSION=$(echo ${CURRENT_SNAP_VERSION} | incrementPomVersion)

echo "Current Version: ${CURRENT_SNAP_VERSION}"
echo "Release Version: ${RELEASE_VERSION}"
echo "Next Version:    ${NEXT_SNAP_VERSION}"

## Remove SNAPSHOT
## Compile and Install
## Commit Change and Tag
## Increment, add SNAPSHOT and Commit Change
mvn --batch-mode release:prepare -Dtag="${RELEASE_VERSION}" -DreleaseVersion="${RELEASE_VERSION}" -DdevelopmentVersion="${NEXT_SNAP_VERSION}" -DautoVersionSubmodules=true 

## Clean Up Local Repo
find -type f -name '*.releaseBackup' | xargs -I {} rm -v {}
rm -v release.properties
