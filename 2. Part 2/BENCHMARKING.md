# Module catalogue benchmark

The benchmark is kept separate from the application code:

```text
src/main/java/catalogue/          application classes and search algorithms
src/benchmark/java/benchmark/    loading, timing, and statistics
test/benchmark/java/benchmark/   experiment orchestration and correctness checks
samples/                         benchmark datasets
```

Select an algorithm on the command line and run it against all three datasets:

```sh
./run-benchmarks.sh linear
./run-benchmarks.sh second
./run-benchmarks.sh all
```

With no argument, the runner defaults to `linear`. Use `./run-benchmarks.sh --help`
to display the available choices.

`second` is deliberately only a placeholder. It currently delegates to the
linear search so that the selection and test pipeline can run, but its output
must not be treated as evidence of a second algorithm. Replace the marked body
in `src/main/java/catalogue/search/SecondKeywordSearchPlaceholder.java` before
comparing algorithm performance.

The default is 3 untimed warm-up searches followed by 10 measured searches per
dataset. Override these values when collecting the final results, for example:

```sh
BENCHMARK_WARM_UP_ITERATIONS=10 BENCHMARK_ITERATIONS=100 ./run-benchmarks.sh
```

CSV loading, parsing, and correctness checks happen outside the timed section.
The test reports minimum, maximum, mean, and median search time. It does not use
a fixed performance assertion because results depend on the machine and its
current workload.
