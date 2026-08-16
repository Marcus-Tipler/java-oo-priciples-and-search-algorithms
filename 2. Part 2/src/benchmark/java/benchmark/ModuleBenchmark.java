package benchmark;

import catalogue.ModuleCatalogue;
import catalogue.OptionalModule;

import java.util.List;
import java.util.Objects;

public final class ModuleBenchmark {
    private static volatile int resultSizeBlackHole;

    public BenchmarkResult benchmarkKeywordSearch(
            ModuleCatalogue catalogue,
            String dataset,
            String keyword,
            int warmUpIterations,
            int measuredIterations) {
        Objects.requireNonNull(catalogue, "catalogue");
        Objects.requireNonNull(dataset, "dataset");
        Objects.requireNonNull(keyword, "keyword");
        if (warmUpIterations < 0) {
            throw new IllegalArgumentException("Warm-up iterations cannot be negative");
        }
        if (measuredIterations < 1) {
            throw new IllegalArgumentException("At least one measured iteration is required");
        }

        List<OptionalModule> expectedResults = null;
        for (int iteration = 0; iteration < warmUpIterations; iteration++) {
            expectedResults = catalogue.searchByKeyword(keyword);
            resultSizeBlackHole = expectedResults.size();
        }
        if (expectedResults == null) {
            expectedResults = catalogue.searchByKeyword(keyword);
        }

        long[] measurements = new long[measuredIterations];
        for (int iteration = 0; iteration < measuredIterations; iteration++) {
            long start = System.nanoTime();
            List<OptionalModule> actualResults = catalogue.searchByKeyword(keyword);
            long end = System.nanoTime();

            measurements[iteration] = end - start;
            resultSizeBlackHole = actualResults.size();

            // Correctness is checked outside the timed section.
            if (!actualResults.equals(expectedResults)) {
                throw new AssertionError("Keyword search returned inconsistent results");
            }
        }

        return new BenchmarkResult(dataset, catalogue.size(), keyword, measurements);
    }
}
