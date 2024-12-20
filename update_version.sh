#!/bin/bash

if [[ -z "$UPDATE_TYPE" ]]; then
    echo "Error: No update type provided." >&2
    exit 1
fi

current_version=$(cat VERSION)
IFS='.' read -r -a version_parts <<<"$current_version"

case $UPDATE_TYPE in
major)
    version_parts[0]=$((version_parts[0] + 1))
    version_parts[1]=0
    version_parts[2]=0
    ;;
minor)
    version_parts[1]=$((version_parts[1] + 1))
    version_parts[2]=0
    ;;
patch)
    version_parts[2]=$((version_parts[2] + 1))
    ;;
*)
    echo "Error: Unknown update type '$UPDATE_TYPE'." >&2
    exit 1
    ;;
esac

NEW_VERSION="${version_parts[0]}.${version_parts[1]}.${version_parts[2]}"

# Update the version in the VERSION file and package.json, the gradle should pick the version from the NEW_VERSION env var
echo "$NEW_VERSION" >VERSION
jq ".version = \"$new_version\"" ./node/package.json >tmp.json && mv tmp.json ./node/package.json

echo "version=$NEW_VERSION" >>$GITHUB_OUTPUT
echo "Updated version to $NEW_VERSION"
