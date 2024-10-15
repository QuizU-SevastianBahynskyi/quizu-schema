#!/bin/bash

usage() {
    echo "Usage: $0 [major|minor|patch]"
    exit 1
}

if [ -z "$1" ]; then
    usage
fi

# Read the current version from the version file
VERSION_FILE="VERSION"
if [ ! -f "$VERSION_FILE" ]; then
    echo "0.0.0" > "$VERSION_FILE"
fi
CURRENT_VERSION=$(cat "$VERSION_FILE")

# Split the version into major, minor, and patch
IFS='.' read -r -a VERSION_PARTS <<< "$CURRENT_VERSION"
MAJOR=${VERSION_PARTS[0]}
MINOR=${VERSION_PARTS[1]}
PATCH=${VERSION_PARTS[2]}

# Increment the appropriate part of the version
case "$1" in
    major)
        MAJOR=$((MAJOR + 1))
        MINOR=0
        PATCH=0
        ;;
    minor)
        MINOR=$((MINOR + 1))
        PATCH=0
        ;;
    patch)
        PATCH=$((PATCH + 1))
        if [ "$PATCH" -ge 20 ]; then
            PATCH=0
            MINOR=$((MINOR + 1))
            if [ "$MINOR" -ge 20 ]; then
                MINOR=0
                MAJOR=$((MAJOR + 1))
            fi
        fi
        ;;
    *)
        usage
        ;;
esac

# Return the new version
NEW_VERSION="$MAJOR.$MINOR.$PATCH"
echo "$NEW_VERSION"