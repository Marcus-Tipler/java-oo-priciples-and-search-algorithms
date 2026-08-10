package com.studentjobportal.cli;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;

// CLI user interaction
public final class StudentJobPortalCli {

    private final Scanner scanner;
    private final List<Job> jobs;
    private final Set<JobID> savedJobIds;

    public StudentJobPortalCli(Scanner scanner, List<Job> jobs) {
        this.scanner = Objects.requireNonNull(scanner, "Scanner cannot be null");
        this.jobs = new ArrayList<>(
                Objects.requireNonNull(jobs, "Jobs cannot be null")
        );
        this.savedJobIds = new HashSet<>();
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewJobs();
                    break;
                case 2:
                    searchJobs();
                    break;
                case 3:
                    saveJob();
                    break;
                case 4:
                    applyForJob();
                    break;
                case 5:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. View Jobs");
        System.out.println("2. Search Jobs");
        System.out.println("3. Save Job");
        System.out.println("4. Apply for Job");
        System.out.println("5. Exit");
        System.out.print("Select option: ");
    }

    private void viewJobs() {
        for (int index = 0; index < jobs.size(); index++) {
            printJob(index, jobs.get(index));
        }
    }

    private void searchJobs() {
        System.out.print("Enter keyword: ");
        String keyword = scanner.nextLine()
                .trim()
                .toLowerCase(Locale.ROOT);

        boolean matchFound = false;

        for (int index = 0; index < jobs.size(); index++) {
            Job job = jobs.get(index);

            if (matches(job, keyword)) {
                printJob(index, job);
                matchFound = true;
            }
        }

        if (!matchFound) {
            System.out.println("No matching jobs found.");
        }
    }

    private boolean matches(Job job, String keyword) {
        return job.getTitle().toLowerCase(Locale.ROOT).contains(keyword)
                || job.getCompany().toLowerCase(Locale.ROOT).contains(keyword)
                || job.getJobType().toLowerCase(Locale.ROOT).contains(keyword)
                || job.getLocation().toLowerCase(Locale.ROOT).contains(keyword);
    }

    private void saveJob() {
        System.out.print("Enter job number to save: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        Job job = findJobByIndex(index);
        if (job == null) {
            System.out.println("Invalid job number.");
            return;
        }

        if (savedJobIds.add(job.getId())) {
            System.out.println("Job saved.");
        } else {
            System.out.println("Job is already saved.");
        }
    }

    private void applyForJob() {
        System.out.print("Enter job number to apply for: ");
        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        Job job = findJobByIndex(index);
        if (job == null) {
            System.out.println("Invalid job number.");
            return;
        }

        System.out.println(
                "Application submitted for " + job.getTitle()
        );
    }

    private Job findJobByIndex(int index) {
        if (index < 0 || index >= jobs.size()) {
            return null;
        }

        return jobs.get(index);
    }

    private void printJob(int index, Job job) {
        String savedMarker = savedJobIds.contains(job.getId())
                ? " | Saved"
                : "";

        System.out.println(
                (index + 1) + ". "
                        + job.getTitle() + " | "
                        + job.getCompany() + " | "
                        + job.getJobType() + " | "
                        + job.getLocation()
                        + savedMarker
        );
    }
}