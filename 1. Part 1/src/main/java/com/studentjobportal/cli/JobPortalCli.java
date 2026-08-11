package com.studentjobportal.cli;

import java.io.PrintStream;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.DuplicateSavedJobException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.Job;
import com.studentjobportal.service.ApplicationService;
import com.studentjobportal.service.JobService;
import com.studentjobportal.service.SavedJobService;


// CLI user interaction
public final class JobPortalCli {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM uuuu HH:mm 'UTC'", Locale.UK).withZone(ZoneOffset.UTC);

    private final Scanner input;
    private final PrintStream output;
    private final JobService jobService;
    private final SavedJobService savedJobService;
    private final ApplicationService applicationService;

    public JobPortalCli(Scanner input, PrintStream output, JobService jobService, SavedJobService savedJobService, ApplicationService applicationService) {
        this.input = Objects.requireNonNull(input, "Input cannot be null");
        this.output = Objects.requireNonNull(output, "Output cannot be null");
        this.jobService = Objects.requireNonNull(jobService, "Job service cannot be null");
        this.savedJobService = Objects.requireNonNull(savedJobService, "Saved-job service cannot be null");
        this.applicationService = Objects.requireNonNull(applicationService, "Application service cannot be null");
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();

            String inputLine = readLine();

            if (inputLine == null) {
                return;
            }

            Integer choice = parseNumber(inputLine);

            if (choice == null) {
                output.println("Please enter a valid whole number.");
                continue;
            }

            if (choice < 1 || choice > 7) {
                output.println("Please select an option between 1 and 7.");
                continue;
            }

            switch (choice) {
                case 1:
                    handleViewAllJobs();
                    break;
                case 2:
                    handleSearchJobs();
                    break;
                case 3:
                    handleSaveJob();
                    break;
                case 4:
                    handleViewSavedJobs();
                    break;
                case 5:
                    handleApplyForJob();
                    break;
                case 6:
                    handleViewApplications();
                    break;
                case 7:
                    running = false;
                    break;
                default:
                    throw new IllegalStateException("Unexpected menu option: " + choice);
            }
        }
    }

    private void printMenu() {
        output.println();
        output.println("1. View all jobs");
        output.println("2. Search jobs");
        output.println("3. Save a job");
        output.println("4. View saved jobs");
        output.println("5. Apply for a job");
        output.println("6. View applications");
        output.println("7. Exit");
        output.print("Select option: ");
    }

    private void handleViewAllJobs() {
        List<Job> jobs = jobService.getAllJobs();

        if (jobs.isEmpty()) {
            output.println("No jobs are currently available.");
            return;
        }

        output.println();
        output.println("Available jobs:");
        printJobs(jobs);
    }

    private void handleSearchJobs() {
        output.print("Enter keyword: ");

        String searchTerm = readLine();

        if (searchTerm == null) {
            return;
        }

        if (searchTerm.trim().isEmpty()) {
            output.println("Search keyword cannot be blank.");
            return;
        }

        List<Job> matches = jobService.searchJobs(searchTerm);

        if (matches.isEmpty()) {
            output.println("No matching jobs found.");
            return;
        }

        output.println();
        output.println("Search results:");
        printJobs(matches);
    }

    private void handleSaveJob() {
        Job job = selectJob("Enter job number to save: ");

        if (job == null) {
            return;
        }

        try {
            savedJobService.saveJob(job.getId());
            output.println("Job saved: " + job.getTitle());
        } catch (DuplicateSavedJobException exception) {
            output.println("That job is already saved.");
        } catch (JobNotFoundException exception) {
            output.println(exception.getMessage());
        }
    }

    private void handleViewSavedJobs() {
        try {
            List<Job> savedJobs = savedJobService.getSavedJobs();

            if (savedJobs.isEmpty()) {
                output.println("You have no saved jobs.");
                return;
            }

            output.println();
            output.println("Saved jobs:");
            printJobs(savedJobs);
        } catch (JobNotFoundException exception) {
            output.println(exception.getMessage());
        }
    }

    private void handleApplyForJob() {
        Job job = selectJob("Enter job number to apply for: ");

        if (job == null) {
            return;
        }

        try {
            applicationService.applyForJob(job.getId());
            output.println("Application submitted for " + job.getTitle() + ".");
        } catch (DuplicateApplicationException exception) {
            output.println("You have already applied for that job.");
        } catch (JobNotFoundException exception) {
            output.println(exception.getMessage());
        }
    }

    private void handleViewApplications() {
        List<Application> applications = applicationService.getApplications();

        if (applications.isEmpty()) {
            output.println("You have no applications.");
            return;
        }

        output.println();
        output.println("Applications:");

        for (int index = 0; index < applications.size(); index++) {
            printApplication(index + 1, applications.get(index));
        }
    }

    private Job selectJob(String prompt) {
        List<Job> jobs = jobService.getAllJobs();

        if (jobs.isEmpty()) {
            output.println("No jobs are currently available.");
            return null;
        }

        output.print(prompt);
        String inputLine = readLine();

        if (inputLine == null) {
            return null;
        }

        Integer jobNumber = parseNumber(inputLine);

        if (jobNumber == null) {
            output.println("Please enter a valid whole number.");
            return null;
        }

        if (jobNumber < 1 || jobNumber > jobs.size()) {
            output.println("Job number must be between 1 and " + jobs.size() + ".");
            return null;
        }

        return jobs.get(jobNumber - 1);
    }

    private void printJobs(List<Job> jobs) {
        for (int index = 0; index < jobs.size(); index++) {
            printJob(index + 1, jobs.get(index));
        }
    }

    private void printJob(int number, Job job) {
        output.println(number + ". " + job.getTitle());
        output.println("   Company: " + job.getCompany());
        output.println("   Type: " + job.getJobType());
        output.println("   Location: " + job.getLocation());

        if (savedJobService.isSaved(job.getId())) {
            output.println("   Saved: Yes");
        }

        output.println();
    }

    private void printApplication(int number, Application application) {
        output.println(number + ". Application");

        try {
            Job job = jobService.getJobById(application.getJobID());
            output.println("   Job: " + job.getTitle());
            output.println("   Company: " + job.getCompany());
        } catch (JobNotFoundException exception) {
            output.println("   Job: No longer available");
        }

        output.println("   Job ID: " + application.getJobID());
        output.println("   Status: " + formatStatus(application.getStatus()));
        output.println("   Submitted: " + DATE_FORMAT.format(application.getSubmittedAt()));
        output.println();
    }

    private String formatStatus(ApplicationStatus status) {
        String lowerCaseStatus = status.name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lowerCaseStatus.charAt(0) ) + lowerCaseStatus.substring(1);
    }

    private Integer parseNumber(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String readLine() {
        if (!input.hasNextLine()) {
            return null;
        }

        return input.nextLine();
    }
}