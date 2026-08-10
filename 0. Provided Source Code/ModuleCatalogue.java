import java.util.ArrayList;
import java.util.List;

public class ModuleCatalogue {
    private List<OptionalModule> modules = new ArrayList<>();

    public void addModule(OptionalModule module) {
        modules.add(module);
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
}
