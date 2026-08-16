package benchmark;

import java.util.Arrays;
import java.util.Locale;

public final class BenchmarkResult {
    private static final double NANOSECONDS_PER_MILLISECOND = 1_000_000.0;

    private final String dataset;
    private final int recordCount;
    private final String keyword;
    private final long[] elapsedNanoseconds;

    public BenchmarkResult(
            String dataset,
            int recordCount,
            String keyword,
            long[] elapsedNanoseconds) {
        if (elapsedNanoseconds.length == 0) {
            throw new IllegalArgumentException("At least one measurement is required");
        }
        this.dataset = dataset;
        this.recordCount = recordCount;
        this.keyword = keyword;
        this.elapsedNanoseconds = elapsedNanoseconds.clone();
    }

    public double minimumMilliseconds() {
        long minimum = elapsedNanoseconds[0];
        for (long elapsed : elapsedNanoseconds) {
            minimum = Math.min(minimum, elapsed);
        }
        return minimum / NANOSECONDS_PER_MILLISECOND;
    }

    public double maximumMilliseconds() {
        long maximum = elapsedNanoseconds[0];
        for (long elapsed : elapsedNanoseconds) {
            maximum = Math.max(maximum, elapsed);
        }
        return maximum / NANOSECONDS_PER_MILLISECOND;
    }

    public double meanMilliseconds() {
        double total = 0.0;
        for (long elapsed : elapsedNanoseconds) {
            total += elapsed;
        }
        return (total / elapsedNanoseconds.length) / NANOSECONDS_PER_MILLISECOND;
    }

    public double medianMilliseconds() {
        long[] sorted = elapsedNanoseconds.clone();
        Arrays.sort(sorted);
        int middle = sorted.length / 2;

        if (sorted.length % 2 == 1) {
            return sorted[middle] / NANOSECONDS_PER_MILLISECOND;
        }
        return ((sorted[middle - 1] + sorted[middle]) / 2.0)
                / NANOSECONDS_PER_MILLISECOND;
    }

    public String formatReport() {
        return String.format(
                Locale.ROOT,
                "Dataset: %s%n"
                        + "Records: %,d%n"
                        + "Iterations: %d%n%n"
                        + "Keyword: %s%n%n"
                        + "Minimum:  %.3f ms%n"
                        + "Maximum:  %.3f ms%n"
                        + "Mean:     %.3f ms%n"
                        + "Median:   %.3f ms%n",
                dataset,
                recordCount,
                elapsedNanoseconds.length,
                keyword,
                minimumMilliseconds(),
                maximumMilliseconds(),
                meanMilliseconds(),
                medianMilliseconds());
    }
}
