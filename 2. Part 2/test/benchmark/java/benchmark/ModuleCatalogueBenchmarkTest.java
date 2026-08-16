package benchmark;

import catalogue.ModuleCatalogue;
import catalogue.OptionalModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Standalone benchmark test. It deliberately avoids machine-dependent timing assertions.
 */
public final class ModuleCatalogueBenchmarkTest {
    private static final String KEYWORD = "Cyber";
    private static final int DEFAULT_WARM_UP_ITERATIONS = 3;
    private static final int DEFAULT_MEASURED_ITERATIONS = 10;

    private final Path dataDirectory;
    private final CsvModuleLoader loader = new CsvModuleLoader();
    private final ModuleBenchmark benchmark = new ModuleBenchmark();

    private ModuleCatalogueBenchmarkTest(Path dataDirectory) {
        this.dataDirectory = dataDirectory;
    }

    public static void main(String[] args) throws IOException {
        Path dataDirectory = Paths.get(
                System.getProperty("benchmark.dataDirectory", "samples"));
        ModuleCatalogueBenchmarkTest test = new ModuleCatalogueBenchmarkTest(dataDirectory);

        test.datasetsContainIdenticalModules();
        System.out.println("Dataset content check: PASS");
        System.out.println("========================================");
        System.out.println("MODULE SEARCH BENCHMARK");
        System.out.println("========================================");

        test.benchmarkOrderedDataset();
        test.benchmarkRandomDataset();
        test.benchmarkReverseDataset();
    }

    void benchmarkOrderedDataset() throws IOException {
        runBenchmark("ORDERED", "modules_1m_ordered.csv");
    }

    void benchmarkRandomDataset() throws IOException {
        runBenchmark("RANDOM", "modules_1m_random.csv");
    }

    void benchmarkReverseDataset() throws IOException {
        runBenchmark("REVERSE", "modules_1m_reverse.csv");
    }

    void datasetsContainIdenticalModules() throws IOException {
        DatasetFingerprint ordered = fingerprint(dataDirectory.resolve("modules_1m_ordered.csv"));
        DatasetFingerprint random = fingerprint(dataDirectory.resolve("modules_1m_random.csv"));
        DatasetFingerprint reverse = fingerprint(dataDirectory.resolve("modules_1m_reverse.csv"));

        require(ordered.equals(random), "Ordered and random datasets differ");
        require(ordered.equals(reverse), "Ordered and reverse datasets differ");
    }

    private void runBenchmark(String dataset, String fileName) throws IOException {
        // Loading and parsing finish before ModuleBenchmark starts its timer.
        ModuleCatalogue catalogue = loader.load(dataDirectory.resolve(fileName));
        verifyKeywordSearch(catalogue);

        BenchmarkResult result = benchmark.benchmarkKeywordSearch(
                catalogue,
                dataset,
                KEYWORD,
                integerProperty("benchmark.warmUpIterations", DEFAULT_WARM_UP_ITERATIONS),
                integerProperty("benchmark.iterations", DEFAULT_MEASURED_ITERATIONS));

        System.out.println();
        System.out.print(result.formatReport());
        System.out.println("----------------------------------------");
    }

    private void verifyKeywordSearch(ModuleCatalogue catalogue) {
        List<OptionalModule> results = catalogue.searchByKeyword(KEYWORD);
        require(!results.isEmpty(), "Expected the dataset to contain Cyber modules");

        for (OptionalModule module : results) {
            String searchableText = module.getCode() + " " + module.getName()
                    + " " + module.getSubjectArea();
            require(
                    searchableText.toLowerCase().contains(KEYWORD.toLowerCase()),
                    "Search returned a module that does not contain the keyword");
        }
    }

    private DatasetFingerprint fingerprint(Path csvFile) throws IOException {
        long count = 0;
        long sum = 0;
        long xor = 0;

        try (BufferedReader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8)) {
            if (reader.readLine() == null) {
                throw new IOException("Dataset is empty: " + csvFile);
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isEmpty()) {
                    long hash = fnv1a64(line);
                    count++;
                    sum += hash;
                    xor ^= Long.rotateLeft(hash, (int) (hash & 63));
                }
            }
        }
        return new DatasetFingerprint(count, sum, xor);
    }

    private long fnv1a64(String value) {
        long hash = 0xcbf29ce484222325L;
        for (int index = 0; index < value.length(); index++) {
            hash ^= value.charAt(index);
            hash *= 0x100000001b3L;
        }
        return hash;
    }

    private int integerProperty(String name, int defaultValue) {
        return Integer.parseInt(System.getProperty(name, Integer.toString(defaultValue)));
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static final class DatasetFingerprint {
        private final long count;
        private final long sum;
        private final long xor;

        private DatasetFingerprint(long count, long sum, long xor) {
            this.count = count;
            this.sum = sum;
            this.xor = xor;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof DatasetFingerprint)) {
                return false;
            }
            DatasetFingerprint that = (DatasetFingerprint) other;
            return count == that.count && sum == that.sum && xor == that.xor;
        }

        @Override
        public int hashCode() {
            int result = Long.hashCode(count);
            result = 31 * result + Long.hashCode(sum);
            return 31 * result + Long.hashCode(xor);
        }
    }
}
