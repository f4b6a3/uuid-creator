#!/bin/bash

ARTIFACT_ID=uuid-creator

# find the script folder
SCRIPT_DIR=$(dirname "$0")

# go to the parent folder
cd "${SCRIPT_DIR}/.."

# compile the parent project
./mvnw --batch-mode --quiet --fail-fast clean install -DskipTests

# create a copy with the expected name
cp "${PWD}/target/${ARTIFACT_ID}"-*-SNAPSHOT.jar "${PWD}/target/${ARTIFACT_ID}"-0.0.1-BENCHMARK.jar

# go to the benchmark folder
cd benchmark

# compile the benchmark project
../mvnw --batch-mode --quiet --fail-fast validate
../mvnw --batch-mode --quiet --fail-fast clean install

# run the benchmark
java -jar target/benchmarks.jar

