#!/bin/sh
set -eu

SCRIPT_DIRECTORY=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
BENCHMARK_BUILD_DIRECTORY=$(mktemp -d "${TMPDIR:-/tmp}/module-benchmark.XXXXXX")
trap 'rm -rf "$BENCHMARK_BUILD_DIRECTORY"' EXIT HUP INT TERM

javac -d "$BENCHMARK_BUILD_DIRECTORY" \
    "$SCRIPT_DIRECTORY/src/main/java/catalogue/ModuleCatalogue.java" \
    "$SCRIPT_DIRECTORY/src/main/java/catalogue/OptionalModule.java" \
    "$SCRIPT_DIRECTORY/src/main/java/catalogue/search/KeywordSearchAlgorithm.java" \
    "$SCRIPT_DIRECTORY/src/main/java/catalogue/search/LinearKeywordSearch.java" \
    "$SCRIPT_DIRECTORY/src/main/java/catalogue/search/SecondKeywordSearchPlaceholder.java" \
    "$SCRIPT_DIRECTORY/src/benchmark/java/benchmark/BenchmarkResult.java" \
    "$SCRIPT_DIRECTORY/src/benchmark/java/benchmark/CsvModuleLoader.java" \
    "$SCRIPT_DIRECTORY/src/benchmark/java/benchmark/ModuleBenchmark.java" \
    "$SCRIPT_DIRECTORY/test/benchmark/java/benchmark/ModuleCatalogueBenchmarkTest.java"

java \
    -Dbenchmark.dataDirectory="$SCRIPT_DIRECTORY/samples" \
    -Dbenchmark.warmUpIterations="${BENCHMARK_WARM_UP_ITERATIONS:-3}" \
    -Dbenchmark.iterations="${BENCHMARK_ITERATIONS:-10}" \
    -cp "$BENCHMARK_BUILD_DIRECTORY" \
    benchmark.ModuleCatalogueBenchmarkTest "$@"
