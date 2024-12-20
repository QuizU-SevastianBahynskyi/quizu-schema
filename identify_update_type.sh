#!/bin/bash

path_to_spec="./openapi/openapi.yaml"

if git diff --quiet HEAD -- "$path_to_spec"; then
    SHOULD_PUBLISH=false
    exit 0
fi

SHOULD_PUBLISH=true

# Calculate the volume of changes
diff_volume=$(git diff HEAD -- "$path_to_spec" | tr -d ' \t\n\r' | wc -c)
old_spec_volume=$(git show HEAD:"$path_to_spec" | tr -d ' \t\n\r' | wc -c)

# Avoid division by zero
if [ "$old_spec_volume" -eq 0 ]; then
    echo "Error: Old specification is empty or not found." >&2
    exit 1
fi

# Calculate the percentage of changes
percentage_of_changes=$(echo "scale=2; $diff_volume / $old_spec_volume" | bc)

# Determine the update type
if (($(echo "$percentage_of_changes > 0.7" | bc -l))); then
    UPDATE_TYPE="major"
elif (($(echo "$percentage_of_changes > 0.45" | bc -l))); then
    UPDATE_TYPE="minor"
else
    UPDATE_TYPE="patch"
fi
