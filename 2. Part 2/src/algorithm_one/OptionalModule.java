// changes to make this part of a package
package algorithm_one;

public class OptionalModule {
    // i've changed this to be final, because no changes will be needed once loaded.
    private final String code;
    private final String name;
    private final int year;
    private final String subjectArea;

    public OptionalModule(String code, String name, int year, String subjectArea) {
        this.code = code;
        this.name = name;
        this.year = year;
        this.subjectArea = subjectArea;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public int getYear() { return year; }
    public String getSubjectArea() { return subjectArea; }

    @Override
    public String toString() {
        return code + " - " + name + " (Year " + year + ", " + subjectArea + ")";
    }
}

