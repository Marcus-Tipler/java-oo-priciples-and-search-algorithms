package benchmark;

import catalogue.ModuleCatalogue;
import catalogue.OptionalModule;
import catalogue.search.KeywordSearchAlgorithm;

import java.util.List;
import java.util.Objects;

public final class ModuleBenchmark {
    private static volatile int resultSizeBlackHole;

    public BenchmarkResult benchmarkKeywordSearch(
            ModuleCatalogue catalogue,
            KeywordSearchAlgorithm algorithm,
            String dataset,
            String keyword,
            int warmUpIterations,
            int measuredIterations) {
        Objects.requireNonNull(catalogue, "catalogue");
        Objects.requireNonNull(algorithm, "algorithm");
        Objects.requireNonNull(dataset, "dataset");
        Objects.requireNonNull(keyword, "keyword");
        if (warmUpIterations < 0) {
            throw new IllegalArgumentException("Warm-up iterations cannot be negative");
        }
        if (measuredIterations < 1) {
            throw new IllegalArgumentException("At least one measured iteration is required");
        }

        List<OptionalModule> expectedResults = catalogue.searchByKeyword(keyword);
        for (int iteration = 0; iteration < warmUpIterations; iteration++) {
            List<OptionalModule> warmUpResults = algorithm.search(catalogue, keyword);
            resultSizeBlackHole = warmUpResults.size();
            if (!warmUpResults.equals(expectedResults)) {
                throw new AssertionError(
                        algorithm.displayName() + " returned incorrect warm-up results");
            }
        }

        long[] measurements = new long[measuredIterations];
        for (int iteration = 0; iteration < measuredIterations; iteration++) {
            long start = System.nanoTime();
            List<OptionalModule> actualResults = algorithm.search(catalogue, keyword);
            long end = System.nanoTime();

            measurements[iteration] = end - start;
            resultSizeBlackHole = actualResults.size();

            // Correctness is checked outside the timed section.
            if (!actualResults.equals(expectedResults)) {
                throw new AssertionError(
                        algorithm.displayName() + " returned incorrect results");
            }
        }

        return new BenchmarkResult(
                algorithm.displayName(), dataset, catalogue.size(), keyword, measurements);
    }
}
