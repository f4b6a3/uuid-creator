#!/bin/bash

ARTIFACT_ID=uuid-creator

# find the script folder
SCRIPT_DIR=$(dirname "$0")

# go to the parent folder
cd "${SCRIPT_DIR}/.."

# compile the parent project
mvn clean install -DskipTests

# create a copy with the expected name
cp "${PWD}/target/${ARTIFACT_ID}"-*-SNAPSHOT.jar "${PWD}/target/${ARTIFACT_ID}"-0.0.1-BENCHMARK.jar

# go to the benchmark folder
cd benchmark

# compile the benchmark project
mvn validate
mvn clean install

# run the benchmark
# /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java -jar target/benchmarks.jar
java -jar target/benchmarks.jar

