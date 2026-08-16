package catalogue.search;

import catalogue.ModuleCatalogue;
import catalogue.OptionalModule;

import java.util.List;

/**
 * Replace the body of {@link #search(ModuleCatalogue, String)} with the second
 * algorithm when it is ready.
 */
public final class SecondKeywordSearchPlaceholder implements KeywordSearchAlgorithm {
    @Override
    public String commandName() {
        return "second";
    }

    @Override
    public String displayName() {
        return "SECOND ALGORITHM (PLACEHOLDER)";
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public List<OptionalModule> search(ModuleCatalogue catalogue, String keyword) {
        // TODO: Replace this delegation with the second search algorithm.
        return catalogue.searchByKeyword(keyword);
    }
}
