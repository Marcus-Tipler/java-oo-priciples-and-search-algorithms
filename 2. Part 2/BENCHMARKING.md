# Module catalogue benchmark

The benchmark is kept separate from the application code:

```text
src/main/java/catalogue/          application classes
src/benchmark/java/benchmark/    loading, timing, and statistics
test/benchmark/java/benchmark/   experiment orchestration and correctness checks
samples/                         benchmark datasets
```

Run all three datasets from the project root:

```sh
./run-benchmarks.sh
```

The default is 3 untimed warm-up searches followed by 10 measured searches per
dataset. Override these values when collecting the final results, for example:

```sh
BENCHMARK_WARM_UP_ITERATIONS=10 BENCHMARK_ITERATIONS=100 ./run-benchmarks.sh
```

CSV loading, parsing, and correctness checks happen outside the timed section.
The test reports minimum, maximum, mean, and median search time. It does not use
a fixed performance assertion because results depend on the machine and its
current workload.
