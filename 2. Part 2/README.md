# Part 2: Scalable Module-Code Search

This part compares the supplied list-based module catalogue with a refactored catalogue designed for repeated module-code lookups. The baseline performs a linear scan; the alternative sorts a private copy of the modules once and then performs iterative binary search.

The project targets Java 17, uses only the Java standard library, and includes a benchmark over three one-million-record datasets.

## Compile and run

Run these commands from this directory (`2. Part 2`), because the benchmark resolves its data from the local `samples/` folder:

```sh
find src -name '*.java' -print0 | xargs -0 javac --release 17 -d out
java -cp out run_benchmarks
```

The benchmark processes the datasets sequentially and prints results to the terminal. Existing `out/` bytecode can be replaced safely by recompiling from `src/`.

## Implementations

### Algorithm 1: linear search

`algorithm_one.ModuleCatalogue` stores modules in an `ArrayList`. `searchByCode` examines entries in list order until it finds an equal code or reaches the end.

- Best-case lookup: `O(1)` when the target is first
- Average- and worst-case lookup: `O(n)`
- Missing-code lookup: `O(n)`
- Catalogue construction from an existing list: `O(n)` time and `O(n)` space for the defensive copy

The baseline also retains year/subject and keyword filters. Both necessarily scan the list and return a result list, so they remain `O(n)` time (plus space for the matches). The binary-search refactor is specifically for exact module-code lookup.

### Algorithm 2: binary search

`algorithm_two.BinarySearchModuleCatalogue` copies the input list and sorts it by module code using a case-insensitive comparator. `searchByCode` repeatedly halves the remaining range until the code is found or the range is empty.

- Catalogue copy and sort: `O(n log n)` time
- Stored catalogue: `O(n)` space
- Best-case lookup: `O(1)` when the first midpoint is the target
- Average-, worst-, and missing-code lookup: `O(log n)`
- Additional per-search space: `O(1)` because the search is iterative

Sorting is an upfront cost. For a single lookup, a linear scan may be cheaper, especially if the target is near the front. The refactor pays off when the same catalogue receives repeated exact-code searches, because the one-time sort is amortised across those queries.

## Benchmark design

`run_benchmarks` invokes `BenchmarkRunner` for:

| Dataset | Records | Purpose |
| --- | ---: | --- |
| `coursework_modules_1m_ascending.csv` | 1,000,000 | Places the maximum code last, demonstrating a worst-position successful linear lookup |
| `coursework_modules_1m_descending.csv` | 1,000,000 | Places the maximum code first, demonstrating the best linear lookup position |
| `coursework_modules_1m_shuffled_seed2307.csv` | 1,000,000 | Uses a deterministic shuffle to represent an unordered catalogue |

For every dataset, the runner:

1. Parses the CSV into immutable-field `OptionalModule` objects.
2. constructs the linear catalogue;
3. measures the binary catalogue's copy-and-sort cost separately;
4. chooses the maximum existing code so its position changes with input order;
5. creates a guaranteed-absent code for unsuccessful-search measurements;
6. warms up both search methods to reduce first-invocation JIT effects;
7. averages 10 linear searches and 100,000 binary searches; and
8. stores results in a volatile field so the JVM cannot discard the calls as unused.

Each report includes CSV loading time, binary build/sort time, average nanoseconds per lookup, milliseconds per lookup, and the measured ratio between the two search times.

## Interpreting results

Timing values depend on hardware, JVM version, memory pressure, and other processes, so a particular nanosecond value is not itself the complexity claim. The important observations are:

- Ascending input makes the chosen successful lookup linear-search worst-position, while descending input makes it best-position.
- A missing code always requires the linear implementation to inspect all one million records.
- Input order affects linear lookup position but not the number of binary-search range halvings after the catalogue is sorted.
- A best-position linear lookup can be faster than binary search for that individual request.
- Repeated lookups and unsuccessful lookups show why the `O(log n)` catalogue can justify its upfront `O(n log n)` preparation cost.

The benchmark is a demonstration harness rather than a microbenchmarking framework. Re-run it several times and use representative averages if the raw timings are quoted as evidence.

## CSV loading

`benchmark.CsvModuleLoader` reads UTF-8 with `BufferedReader`, skips blank lines, recognises a header through the non-numeric year field, strips an initial UTF-8 byte-order mark, handles quoted commas and escaped double quotes, and reports malformed rows with their line number.

The supplied sample files each contain a header plus one million records and are approximately 66 MB. They are intentionally large enough to make the scaling difference visible.

## Key files

- `src/algorithm_one/OptionalModule.java` - module data object
- `src/algorithm_one/ModuleCatalogue.java` - baseline `O(n)` code search and list filters
- `src/algorithm_two/BinarySearchModuleCatalogue.java` - sorted catalogue and iterative `O(log n)` code search
- `src/benchmark/CsvModuleLoader.java` - standard-library CSV loading
- `src/benchmark/BenchmarkRunner.java` - setup, warm-up, measurement, and reporting
- `src/run_benchmarks.java` - executable entry point and dataset list
- `samples/` - ascending, descending, and seed-2307 shuffled datasets

[Return to the repository overview](../readme.md)
