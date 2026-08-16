package catalogue;

public class OptionalModule {
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

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public String getSubjectArea() {
        return subjectArea;
    }
}
