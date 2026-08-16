package algorithm_two;

import algorithm_one.OptionalModule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BinarySearchModuleCatalogue {

    private final List<OptionalModule> modules;

    public BinarySearchModuleCatalogue(List<OptionalModule> modules) {
        // Make copy of dataset
        this.modules = new ArrayList<>(modules);
        // Sort the dataset (because Binary Search requires sorted list)
        this.modules.sort(Comparator.comparing(OptionalModule::getCode, String.CASE_INSENSITIVE_ORDER)
        );
    }

    // binary search (best case should be O(1) still, but otherwise should be O(log n))
    public OptionalModule searchByCode(String code) {

        int low = 0;
        int high = modules.size() - 1;

        while (low <= high) {
            int middle = low + (high - low) / 2;
            OptionalModule middleModule = modules.get(middle);
            int comparison = middleModule.getCode().compareToIgnoreCase(code);

            if (comparison == 0) {
                return middleModule;
            }

            if (comparison < 0) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }

        return null;
    }

    public int size() {
        return modules.size();
    }
}

// This should be better (about to test it so we will see, would be awkward if not)
// because binary search "throws away" half of the remaining data
// so for 1M records it should only take log2(1000000) which is aprox 19.93 iterations.
// The thing i'm worried about is the sorting stage, so tests will give me the answer,
// during the development of this i've used ".sort" which has it's own "weight",
// According to Baeldung, the ".sort" stage costs O(n log n) so it SHOULD be more
// efficient still, especially on a larger data-set.