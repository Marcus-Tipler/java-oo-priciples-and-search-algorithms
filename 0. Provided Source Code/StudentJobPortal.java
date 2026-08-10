import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StudentJobPortal {

    static List<String> jobTitles = new ArrayList<>();
    static List<String> companies = new ArrayList<>();
    static List<String> jobTypes = new ArrayList<>();
    static List<String> locations = new ArrayList<>();
    static List<Boolean> savedJobs = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

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

        while (running) {
            System.out.println("\n1. View Jobs");
            System.out.println("2. Search Jobs");
            System.out.println("3. Save Job");
            System.out.println("4. Apply for Job");
            System.out.println("5. Exit");
            System.out.print("Select option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            if (choice == 1) {
                for (int i = 0; i < jobTitles.size(); i++) {
                    System.out.println((i + 1) + ". " + jobTitles.get(i) + " | " +
                            companies.get(i) + " | " + jobTypes.get(i) + " | " + locations.get(i));
                }
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
