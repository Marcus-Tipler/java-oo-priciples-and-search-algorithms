package catalogue.search;

import catalogue.ModuleCatalogue;
import catalogue.OptionalModule;

import java.util.List;

/** A selectable keyword-search implementation. */
public interface KeywordSearchAlgorithm {
    String commandName();

    String displayName();

    boolean isPlaceholder();

    List<OptionalModule> search(ModuleCatalogue catalogue, String keyword);
}
