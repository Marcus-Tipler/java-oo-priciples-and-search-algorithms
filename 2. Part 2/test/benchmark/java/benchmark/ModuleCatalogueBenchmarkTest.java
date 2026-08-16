package benchmark;

import catalogue.ModuleCatalogue;
import catalogue.OptionalModule;
import catalogue.search.KeywordSearchAlgorithm;
import catalogue.search.LinearKeywordSearch;
import catalogue.search.SecondKeywordSearchPlaceholder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Standalone benchmark test. It deliberately avoids machine-dependent timing assertions.
 */
public final class ModuleCatalogueBenchmarkTest {
    private static final String KEYWORD = "Cyber";
    private static final int DEFAULT_WARM_UP_ITERATIONS = 3;
    private static final int DEFAULT_MEASURED_ITERATIONS = 10;

    private final Path dataDirectory;
    private final List<KeywordSearchAlgorithm> algorithms;
    private final CsvModuleLoader loader = new CsvModuleLoader();
    private final ModuleBenchmark benchmark = new ModuleBenchmark();

    private ModuleCatalogueBenchmarkTest(
            Path dataDirectory,
            List<KeywordSearchAlgorithm> algorithms) {
        this.dataDirectory = dataDirectory;
        this.algorithms = algorithms;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 1 && ("--help".equals(args[0]) || "-h".equals(args[0]))) {
            printUsage();
            return;
        }

        List<KeywordSearchAlgorithm> algorithms;
        try {
            algorithms = selectAlgorithms(args);
        } catch (IllegalArgumentException exception) {
            System.err.println("Error: " + exception.getMessage());
            printUsage();
            System.exit(2);
            return;
        }

        Path dataDirectory = Paths.get(
                System.getProperty("benchmark.dataDirectory", "samples"));
        ModuleCatalogueBenchmarkTest test = new ModuleCatalogueBenchmarkTest(
                dataDirectory, algorithms);

        test.datasetsContainIdenticalModules();
        System.out.println("Dataset content check: PASS");
        System.out.println("========================================");
        System.out.println("MODULE SEARCH BENCHMARK");
        System.out.println("========================================");
        System.out.println("Selected: " + selectedNames(algorithms));

        for (KeywordSearchAlgorithm algorithm : algorithms) {
            if (algorithm.isPlaceholder()) {
                System.out.println(
                        "WARNING: 'second' is a placeholder and currently delegates to linear search.");
            }
        }

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

        for (KeywordSearchAlgorithm algorithm : algorithms) {
            BenchmarkResult result = benchmark.benchmarkKeywordSearch(
                    catalogue,
                    algorithm,
                    dataset,
                    KEYWORD,
                    integerProperty("benchmark.warmUpIterations", DEFAULT_WARM_UP_ITERATIONS),
                    integerProperty("benchmark.iterations", DEFAULT_MEASURED_ITERATIONS));

            System.out.println();
            System.out.print(result.formatReport());
            System.out.println("----------------------------------------");
        }
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

    private static List<KeywordSearchAlgorithm> selectAlgorithms(String[] args) {
        if (args.length > 1) {
            throw new IllegalArgumentException("Expected one algorithm name");
        }

        String selection = args.length == 0
                ? "linear"
                : args[0].toLowerCase(Locale.ROOT);
        KeywordSearchAlgorithm linear = new LinearKeywordSearch();
        KeywordSearchAlgorithm second = new SecondKeywordSearchPlaceholder();

        switch (selection) {
            case "linear":
                return Arrays.asList(linear);
            case "second":
                return Arrays.asList(second);
            case "all":
                return Arrays.asList(linear, second);
            default:
                throw new IllegalArgumentException("Unknown algorithm: " + selection);
        }
    }

    private static String selectedNames(List<KeywordSearchAlgorithm> algorithms) {
        List<String> names = new ArrayList<>();
        for (KeywordSearchAlgorithm algorithm : algorithms) {
            names.add(algorithm.commandName());
        }
        return String.join(", ", names);
    }

    private static void printUsage() {
        System.out.println("Usage: ./run-benchmarks.sh [linear|second|all]");
        System.out.println("  linear  Existing linear keyword search (default)");
        System.out.println("  second  Placeholder for the future second algorithm");
        System.out.println("  all     Run both selectable implementations");
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
