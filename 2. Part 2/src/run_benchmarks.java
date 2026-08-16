import java.nio.file.Path;
import java.nio.file.Paths;

import benchmark.BenchmarkRunner;

public class run_benchmarks {

    public static void main(String[] args) {

        Path samplesDirectory = Paths.get("samples");

        String[] datasets = {"coursework_modules_1m_ascending.csv", "coursework_modules_1m_descending.csv", "coursework_modules_1m_shuffled_seed2307.csv"};

        System.out.println();
        System.out.println("MODULE CATALOGUE SEARCH BENCHMARK");
        System.out.println("Algorithm 1: Linear Search  - O(n)");
        System.out.println("Algorithm 2: Binary Search  - O(log n)");

        for (String dataset : datasets) {
            Path datasetPath = samplesDirectory.resolve(dataset);

            try {
                BenchmarkRunner.run(datasetPath);
            } catch (Exception exception) {
                System.err.println("Unable to benchmark " + dataset + ": " + exception.getMessage());
                exception.printStackTrace();
            }
        }
        System.out.println();
        System.out.println("All benchmarks complete.");
    }
}