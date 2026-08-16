package benchmark;

import algorithm_one.OptionalModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvModuleLoader {

    private CsvModuleLoader() {
        // don't touch
    }

    public static List<OptionalModule> load(Path path) throws IOException {

        List<OptionalModule> modules = new ArrayList<>(1_000_000);

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean firstRecord = true;
            long lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.isBlank()) { continue; }

                List<String> fields = parseCsvLine(line);

                if (fields.size() < 4) {
                    throw new IOException("Invalid CSV row at line " + lineNumber + ": expected at least 4 columns.");
                }

                String code = fields.get(0).trim();

                // remove UTF-8 BOM if CSV has one
                if (firstRecord && code.startsWith("\uFEFF")) {
                    code = code.substring(1);
                }

                String name = fields.get(1).trim();
                String yearText = fields.get(2).trim();
                String subjectArea = fields.get(3).trim();
                int year;

                try {
                    year = Integer.parseInt(yearText);
                } 
                catch (NumberFormatException exception) {

                    // assumes first row is header
                    if (firstRecord) {
                        firstRecord = false;
                        continue;
                    }

                    throw new IOException("Invalid year at CSV line " + lineNumber + ": " + yearText, exception);
                }

                modules.add(new OptionalModule(code, name, year, subjectArea));

                firstRecord = false;
            }
        }

        return modules;
    }

    // Small CSV parser which also handles quoted values:
    private static List<String> parseCsvLine(String line) {

        List<String> fields = new ArrayList<>();

        StringBuilder current = new StringBuilder();

        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char character = line.charAt(i);

            if (character == '"') {

                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }

            } else if (character == ',' && !insideQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(character);
            }
        }

        fields.add(current.toString());

        return fields;
    }
}