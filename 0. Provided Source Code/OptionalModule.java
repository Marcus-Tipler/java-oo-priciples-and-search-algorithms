import java.util.ArrayList;
import java.util.List;

public class OptionalModule {
    private String code;
    private String name;
    private int year;
    private String subjectArea;

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
}

