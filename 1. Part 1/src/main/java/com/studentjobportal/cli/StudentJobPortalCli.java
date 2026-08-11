package com.studentjobportal.cli;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.Job;
import com.studentjobportal.service.JobPortalService;

// CLI user interaction
public final class StudentJobPortalCli {

    private final Scanner scanner;
    private final JobPortalService service;

    public StudentJobPortalCli(Scanner scanner, JobPortalService service) {
        this.scanner = Objects.requireNonNull(scanner, "Scanner cannot be null");
        this.service = Objects.requireNonNull(service, "Service cannot be null");
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
                    viewSavedJobs();
                    break;
                case 5:
                    applyForJob();
                    break;
                case 6:
                    viewApplications();
                    break;
                case 7:
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
        System.out.println("4. View Saved Jobs");
        System.out.println("5. Apply for Job");
        System.out.println("6. View Applications");
        System.out.println("7. Exit");
        System.out.print("Select option: ");
    }

    private void viewJobs() {
        List<Job> jobs = service.getAllJobs();

        for (int index = 0; index < jobs.size(); index++) {
            printJob(index, jobs.get(index));
        }
    }

    private void searchJobs() {
        System.out.print("Enter keyword: ");

        String keyword = scanner.nextLine().trim().toLowerCase(Locale.ROOT);

        List<Job> jobs = service.getAllJobs();
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
        Job job = selectJob("Enter job number to save: ");

        if (job == null) {
            return;
        }

        if (service.saveJob(job.getId())) {
            System.out.println("Job saved.");
        } else {
            System.out.println("Job is already saved.");
        }
    }

    private void viewSavedJobs() {
        List<Job> savedJobs = service.getSavedJobs();

        if (savedJobs.isEmpty()) {
            System.out.println("No saved jobs.");
            return;
        }

        for (Job job : savedJobs) {
            System.out.println(job);
        }
    }

    private void applyForJob() {
        Job job = selectJob("Enter job number to apply for: ");

        if (job == null) {
            return;
        }

        try {
            service.applyForJob(job.getId());
            System.out.println("Application submitted for " + job.getTitle());
        } catch (DuplicateApplicationException exception) {
            System.out.println("You have already applied for this job.");
        }
    }

    private void viewApplications() {
        List<Application> applications = service.getApplications();

        if (applications.isEmpty()) {
            System.out.println("No applications.");
            return;
        }

        for (Application application : applications) {
            System.out.println(application);
        }
    }

    private Job selectJob(String prompt) {
        System.out.print(prompt);

        int index = scanner.nextInt() - 1;
        scanner.nextLine();

        List<Job> jobs = service.getAllJobs();

        if (index < 0 || index >= jobs.size()) {
            System.out.println("Invalid job number.");
            return null;
        }

        return jobs.get(index);
    }

    private void printJob(int index, Job job) {
        String savedMarker = service.isSaved(job.getId()) ? " | Saved" : "";
        System.out.println((index + 1) + ". " + job.getTitle() + " | " + job.getCompany() + " | " + job.getJobType() + " | " + job.getLocation() + savedMarker);
    }
}