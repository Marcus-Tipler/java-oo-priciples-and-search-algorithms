package catalogue.search;

import catalogue.ModuleCatalogue;
import catalogue.OptionalModule;

import java.util.List;

public final class LinearKeywordSearch implements KeywordSearchAlgorithm {
    @Override
    public String commandName() {
        return "linear";
    }

    @Override
    public String displayName() {
        return "LINEAR SEARCH";
    }

    @Override
    public boolean isPlaceholder() {
        return false;
    }

    @Override
    public List<OptionalModule> search(ModuleCatalogue catalogue, String keyword) {
        return catalogue.searchByKeyword(keyword);
    }
}
