// again, added package
package algorithm_one;

import java.util.ArrayList;
import java.util.List;

// keeping searches but adding module code lookup
public class ModuleCatalogue {
    // again, added final here, not likely to change
    private final List<OptionalModule> modules;

    public ModuleCatalogue() {
        this.modules = new ArrayList<>();
    }

    public ModuleCatalogue(List<OptionalModule> modules) {
        this.modules = new ArrayList<>(modules);
    }

    public void addModule(OptionalModule module) {
        modules.add(module);
    }

    // linear search O(n)
    public OptionalModule searchByCode(String code) {
        for (OptionalModule module : modules) {
            if (module.getCode().equalsIgnoreCase(code)) {
                return module;
            }
        }
        return null;
    }

    public List<OptionalModule> searchByYearAndSubject(int year, String subjectArea) {
        List<OptionalModule> results = new ArrayList<>();
        for (OptionalModule module : modules) {
            if (module.getYear() == year && module.getSubjectArea().equalsIgnoreCase(subjectArea)) {
                results.add(module);
            }
        }
        return results;
    }

    public List<OptionalModule> searchByKeyword(String keyword) {
        List<OptionalModule> results = new ArrayList<>();
        for (OptionalModule module : modules) {
            if (module.getCode().toLowerCase().contains(keyword.toLowerCase()) ||
                module.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                module.getSubjectArea().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(module);
            }
        }
        return results;
    }

    public int size() {
        return modules.size();
    }
}
