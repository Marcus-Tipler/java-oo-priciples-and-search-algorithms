package benchmark;

import catalogue.ModuleCatalogue;
import catalogue.OptionalModule;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class CsvModuleLoader {
    private static final String EXPECTED_HEADER = "code,name,year,subjectArea";

    public ModuleCatalogue load(Path csvFile) throws IOException {
        ModuleCatalogue catalogue = new ModuleCatalogue();

        try (BufferedReader reader = Files.newBufferedReader(csvFile, StandardCharsets.UTF_8)) {
            String header = reader.readLine();
            if (header != null && header.startsWith("\uFEFF")) {
                header = header.substring(1);
            }
            if (!EXPECTED_HEADER.equals(header)) {
                throw new IOException("Unexpected CSV header in " + csvFile + ": " + header);
            }

            String line;
            long lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isEmpty()) {
                    continue;
                }

                List<String> fields = parseCsvLine(line);
                if (fields.size() != 4) {
                    throw new IOException(
                            "Expected 4 fields at " + csvFile + ":" + lineNumber
                                    + " but found " + fields.size());
                }

                int year;
                try {
                    year = Integer.parseInt(fields.get(2));
                } catch (NumberFormatException exception) {
                    throw new IOException(
                            "Invalid year at " + csvFile + ":" + lineNumber,
                            exception);
                }

                catalogue.addModule(new OptionalModule(
                        fields.get(0), fields.get(1), year, fields.get(3)));
            }
        }

        return catalogue;
    }

    private List<String> parseCsvLine(String line) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean quoted = false;

        for (int index = 0; index < line.length(); index++) {
            char character = line.charAt(index);
            if (character == '"') {
                if (quoted && index + 1 < line.length() && line.charAt(index + 1) == '"') {
                    field.append('"');
                    index++;
                } else {
                    quoted = !quoted;
                }
            } else if (character == ',' && !quoted) {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }

        if (quoted) {
            throw new IOException("Unclosed quoted field in CSV row");
        }
        fields.add(field.toString());
        return fields;
    }
}
