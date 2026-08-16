package benchmark;

import algorithm_one.ModuleCatalogue;
import algorithm_one.OptionalModule;
import algorithm_two.BinarySearchModuleCatalogue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public final class BenchmarkRunner {

    private static final int LINEAR_REPETITIONS = 10;
    private static final int BINARY_REPETITIONS = 100000;
    // stop jvm from seeing return as unused 
    private static volatile OptionalModule blackHole;

    private BenchmarkRunner() {
    }

    public static void run(Path dataset) throws IOException {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Dataset: " + dataset.getFileName());
        System.out.println("==================================================");

        // load CSV
        long loadStart = System.nanoTime();
        List<OptionalModule> modules = CsvModuleLoader.load(dataset);
        long loadEnd = System.nanoTime();

        System.out.printf("Records loaded: %,d%n", modules.size());
        System.out.printf("CSV loading time: %.3f ms%n", nanosToMillis(loadEnd - loadStart));

        // Linear Algorithm (should be O(n))
        ModuleCatalogue linearCatalogue = new ModuleCatalogue(modules);

        // highest module code: ascending test should be last, descending should be first, shuffled should be unpredictable
        String existingCode = findMaximumCode(modules);

        // create a value that does not occur in File
        String missingCode = createMissingCode(modules);

        // Binary Algortihm (Should be O(log n) not including the sort algorithm involved)
        long buildStart = System.nanoTime();

        BinarySearchModuleCatalogue binaryCatalogue = new BinarySearchModuleCatalogue(modules);

        long buildEnd = System.nanoTime();

        System.out.printf("Binary catalogue build/sort: %.3f ms%n", nanosToMillis(buildEnd - buildStart));
        System.out.println();
        System.out.println( "Existing code being searched: " + existingCode);

        benchmarkSearch("Existing module", existingCode, linearCatalogue, binaryCatalogue);

        System.out.println();
        System.out.println("Missing code being searched: " + missingCode);

        benchmarkSearch("Missing module", missingCode, linearCatalogue, binaryCatalogue);
    }

    private static void benchmarkSearch(String description, String searchCode, ModuleCatalogue linearCatalogue, BinarySearchModuleCatalogue binaryCatalogue) {

        /*
         * Small warm-up before measurements.
         *
         * Java uses a Just-In-Time compiler, so timing the
         * very first invocation can give misleading results.
         */
        for (int i = 0; i < 3; i++) {
            blackHole = linearCatalogue.searchByCode(searchCode);
        }

        for (int i = 0; i < 10_000; i++) {
            blackHole = binaryCatalogue.searchByCode(searchCode);
        }

        /*
         * O(n) measurement.
         */
        long linearStart = System.nanoTime();

        for (int i = 0; i < LINEAR_REPETITIONS; i++) {
            blackHole = linearCatalogue.searchByCode(searchCode);
        }

        long linearEnd = System.nanoTime();

        /*
         * O(log n) measurement.
         */
        long binaryStart = System.nanoTime();

        for (int i = 0; i < BINARY_REPETITIONS; i++) {
            blackHole = binaryCatalogue.searchByCode(searchCode);
        }

        long binaryEnd = System.nanoTime();

        double linearAverageNs = (double) (linearEnd - linearStart) / LINEAR_REPETITIONS;

        double binaryAverageNs = (double) (binaryEnd - binaryStart) / BINARY_REPETITIONS;

        double speedUp = linearAverageNs / binaryAverageNs;

        System.out.println("--- " + description + " ---");
        System.out.printf("Algorithm 1 - Linear O(n):      %,.0f ns/search (%.6f ms)%n", linearAverageNs, linearAverageNs / 1_000_000.0);
        System.out.printf("Algorithm 2 - Binary O(log n): %,.0f ns/search (%.6f ms)%n", binaryAverageNs, binaryAverageNs / 1_000_000.0);

        System.out.printf("Search speed-up:                %.2fx%n", speedUp);
    }

    private static String findMaximumCode(
            List<OptionalModule> modules) {

        if (modules.isEmpty()) {
            throw new IllegalArgumentException("Cannot benchmark an empty dataset.");
        }

        String maximum = modules.get(0).getCode();

        for (OptionalModule module : modules) {
            if (module.getCode().compareToIgnoreCase(maximum) > 0) {
                maximum = module.getCode();
            }
        }

        return maximum;
    }

    private static String createMissingCode(List<OptionalModule> modules) {
        String candidate = "__MODULE_CODE_DOES_NOT_EXIST__";
        while (containsCode(modules, candidate)) {
            candidate += "_X";
        }
        return candidate;
    }

    private static boolean containsCode(List<OptionalModule> modules, String code) {
        for (OptionalModule module : modules) {
            if (module.getCode().equalsIgnoreCase(code)) {
                return true;
            }
        }
        return false;
    }

    private static double nanosToMillis(long nanos) {
        return nanos / 1000000.0;
    }
}