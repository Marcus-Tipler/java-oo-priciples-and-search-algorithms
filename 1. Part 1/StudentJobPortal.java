// ------------------------
// Imports
// ------------------------
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StudentJobPortal {

    // ------------------------
    // List declarations
    // ------------------------
    static List<String> jobTitles = new ArrayList<>();
    static List<String> companies = new ArrayList<>();
    static List<String> jobTypes = new ArrayList<>();
    static List<String> locations = new ArrayList<>();
    static List<Boolean> savedJobs = new ArrayList<>();
    // Would change static list to something more robust depending on what i decide on doing
    // Static isn't maintainable, test-able or extend-able, good for demo but not for production env.

    // ------------------------
    // Main program loop here
    // ------------------------
    // Currently contains all logic, needs logic moved to separate classes (ideally) based on responsibility
    // and needs private rather than static access to list data for security, maintainability, upgrade-ability and test-ability.
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        // adds basic "dev" list, 2 jobs.
        jobTitles.add("Java Developer");
        companies.add("Tech Solutions Ltd");
        jobTypes.add("Graduate");
        locations.add("Cheltenham");
        savedJobs.add(false);

        jobTitles.add("Software Tester");
        companies.add("SecureApps UK");
        jobTypes.add("Placement");
        locations.add("Bristol");
        savedJobs.add(false);
        // System works on simple "Print", "Read" & "Execute instruction" structure in while loop, so returns to menu after.
        // Currently takes static data set in variables for the StudentJobPortal class, not ideal for the given reqs.
        while (running) {
            System.out.println("\n1. View Jobs");
            System.out.println("2. Search Jobs");
            System.out.println("3. Save Job"); // This doesn't do anything? You can save but not view?
            System.out.println("4. Apply for Job");
            System.out.println("5. Exit");
            System.out.print("Select option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            // No error handling? Not sure if that's required but surely comes under "test-ability"?
            // Also maybe replace IF statements with SWITCHES?
            if (choice == 1) {
                for (int i = 0; i < jobTitles.size(); i++) {
                    System.out.println((i + 1) + ". " + jobTitles.get(i) + " | " +
                            companies.get(i) + " | " + jobTypes.get(i) + " | " + locations.get(i));
                }
                // All of these can be made a lot simpler by making dedicated calls.
            } else if (choice == 2) {
                System.out.print("Enter keyword: ");
                String keyword = scanner.nextLine().toLowerCase();
                for (int i = 0; i < jobTitles.size(); i++) {
                    if (jobTitles.get(i).toLowerCase().contains(keyword) ||
                            companies.get(i).toLowerCase().contains(keyword) ||
                            locations.get(i).toLowerCase().contains(keyword)) {
                        System.out.println("Match: " + jobTitles.get(i));
                    }
                }
            } else if (choice == 3) {
                System.out.print("Enter job number to save: ");
                int index = scanner.nextInt() - 1;
                scanner.nextLine();
                if (index >= 0 && index < savedJobs.size()) {
                    savedJobs.set(index, true);
                    System.out.println("Job saved.");
                    // ??? Saved? Where? Surely this feature needs adding if the variables are there?
                }
            } else if (choice == 4) {
                System.out.print("Enter job number to apply for: ");
                int index = scanner.nextInt() - 1;
                scanner.nextLine();
                if (index >= 0 && index < jobTitles.size()) {
                    System.out.println("Application submitted for " + jobTitles.get(index));
                }
            } else if (choice == 5) {
                running = false;
            }
        }

        scanner.close();
    }
}
