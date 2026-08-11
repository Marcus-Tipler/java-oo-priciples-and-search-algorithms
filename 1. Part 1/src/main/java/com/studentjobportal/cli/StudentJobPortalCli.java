package com.studentjobportal.cli;

import java.util.List;
import java.util.Objects;
import java.util.Scanner;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.DuplicateSavedJobException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.exception.JobValidationException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.Job;
import com.studentjobportal.service.ApplicationService;
import com.studentjobportal.service.JobService;
import com.studentjobportal.service.SavedJobService;

// CLI user interaction
public final class StudentJobPortalCli {

    private final Scanner scanner;
    private final JobService jobService;
    private final SavedJobService savedJobService;
    private final ApplicationService applicationService;

    public StudentJobPortalCli(
    Scanner scanner,
    JobService jobService,
    SavedJobService savedJobService,
    ApplicationService applicationService) {
        this.scanner = Objects.requireNonNull(scanner);
        this.jobService = Objects.requireNonNull(jobService);
        this.savedJobService = Objects.requireNonNull(savedJobService);
        this.applicationService = Objects.requireNonNull(applicationService);
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            Integer choice = readNumber();

            if (choice == null) {
                continue;
            }

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
        printJobs(jobService.getAllJobs());
    }

    private void searchJobs() {
        System.out.print("Enter keyword: ");
        String searchTerm = scanner.nextLine();

        try {
            List<Job> matches = jobService.searchJobs(searchTerm);

            if (matches.isEmpty()) {
                System.out.println("No matching jobs found.");
                return;
            }

            printJobs(matches);

        } catch (JobValidationException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void saveJob() {
        Job job = selectJob("Enter job number to save: ");

        if (job == null) {
            return;
        }

        try {
            savedJobService.saveJob(job.getId());
            System.out.println("Job saved.");
        } catch (DuplicateSavedJobException exception) {
            System.out.println("Job is already saved.");
        } catch (JobNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void viewSavedJobs() {
        try {
            List<Job> savedJobs = savedJobService.getSavedJobs();

            if (savedJobs.isEmpty()) {
                System.out.println("No saved jobs.");
                return;
            }

            printJobs(savedJobs);
        } catch (JobNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void applyForJob() {
        Job job = selectJob("Enter job number to apply for: ");

        if (job == null) {
            return;
        }

        try {
            applicationService.applyForJob(job.getId());
            System.out.println("Application submitted for " + job.getTitle());
        } catch (DuplicateApplicationException exception) {
            System.out.println("You have already applied for this job.");
        } catch (JobNotFoundException exception) {
            System.out.println(exception.getMessage());
        }
    }

    private void viewApplications() {
        List<Application> applications = applicationService.getApplications();

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
        Integer jobNumber = readNumber();

        if (jobNumber == null) {
            return null;
        }

        List<Job> jobs = jobService.getAllJobs();
        int index = jobNumber - 1;

        if (index < 0 || index >= jobs.size()) {
            System.out.println("Invalid job number.");
            return null;
        }

        return jobs.get(index);
    }

    private Integer readNumber() {
        String input = scanner.nextLine().trim();

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException exception) {
            System.out.println("Please enter a valid whole number.");
            return null;
        }
    }

    private void printJobs(List<Job> jobs) {
        for (int index = 0; index < jobs.size(); index++) {
            Job job = jobs.get(index);

            String savedMarker = savedJobService.isSaved(job.getId()) ? " | Saved" : "";

            System.out.println((index + 1) + ". " + job.getTitle() + " | " + job.getCompany() + " | " + job.getJobType() + " | " + job.getLocation() + savedMarker);
        }
    }
}