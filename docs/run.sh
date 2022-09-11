#!/bin/bash

# find the script folder
SCRIPT_DIR=$(dirname "$0")

# go to the parent folder
cd "${SCRIPT_DIR}/.."

# clear old docs
rm -rf docs/javadoc

# generate new docs
find src/main/java/com/github/f4b6a3/uuid/ -name "*.java" | xargs javadoc -d docs/javadoc

