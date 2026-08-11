package com.studentjobportal.cli;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.InMemoryApplicationRepository;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.InMemorySavedJobRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;
import com.studentjobportal.search.CombinedKeywordSearchStrategy;
import com.studentjobportal.service.ApplicationService;
import com.studentjobportal.service.JobService;
import com.studentjobportal.service.SavedJobService;

/**
 * Exercises the command-line interface with simulated input and captured output.
 */
final class JobPortalCliIntegrationTest {

    private static final Instant SUBMISSION_TIME =
            Instant.parse("2026-08-11T10:15:30Z");

    @Test
    void exercisesAllSevenMenuOptions() {
        String input = "1\n"
                + "2\n"
                + "java\n"
                + "3\n"
                + "1\n"
                + "4\n"
                + "5\n"
                + "1\n"
                + "6\n"
                + "7\n";

        String output = runCli(input, true);

        assertContains(output, "1. View all jobs");
        assertContains(output, "1. Java Developer");
        assertContains(output, "Company: Tech Solutions Ltd");
        assertContains(output, "Search results:");
        assertContains(output, "Job saved: Java Developer");
        assertContains(output, "Saved jobs:");
        assertContains(output, "Application submitted for Java Developer.");
        assertContains(output, "Applications:");
        assertContains(output, "Status: Submitted");
        assertContains(output, "Submitted: 11 Aug 2026 10:15 UTC");

        // The CLI should render user-friendly fields, not domain debug strings.
        assertFalse(output.contains("Job{"));
        assertFalse(output.contains("Application{"));
    }

    @Test
    void handlesInvalidInputWithoutTerminating() {
        String input = "not-a-number\n"
                + "9\n"
                + "2\n"
                + "   \n"
                + "3\n"
                + "not-a-number\n"
                + "3\n"
                + "99\n"
                + "7\n";

        String output = runCli(input, true);

        assertContains(output, "Please enter a valid whole number.");
        assertContains(output, "Please select an option between 1 and 7.");
        assertContains(output, "Search keyword cannot be blank.");
        assertContains(output, "Job number must be between 1 and 1.");
        assertContains(output, "7. Exit");
    }

    @Test
    void displaysUsefulEmptyCollectionMessages() {
        String output = runCli("1\n4\n6\n7\n", false);

        assertContains(output, "No jobs are currently available.");
        assertContains(output, "You have no saved jobs.");
        assertContains(output, "You have no applications.");
    }

    private static String runCli(String simulatedInput, boolean includeJob) {
        JobRepository jobRepository = new InMemoryJobRepository();
        SavedJobRepository savedJobRepository =
                new InMemorySavedJobRepository();
        ApplicationRepository applicationRepository =
                new InMemoryApplicationRepository();

        if (includeJob) {
            jobRepository.save(createJob());
        }

        JobService jobService = new JobService(
                jobRepository,
                new CombinedKeywordSearchStrategy()
        );
        SavedJobService savedJobService = new SavedJobService(
                jobRepository,
                savedJobRepository
        );
        ApplicationService applicationService = new ApplicationService(
                jobRepository,
                applicationRepository,
                Clock.fixed(SUBMISSION_TIME, ZoneOffset.UTC)
        );

        ByteArrayInputStream inputBytes = new ByteArrayInputStream(
                simulatedInput.getBytes(StandardCharsets.UTF_8)
        );
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

        try (Scanner scanner = new Scanner(inputBytes);
                PrintStream output = new PrintStream(
                        outputBytes,
                        true,
                        StandardCharsets.UTF_8)) {
            JobPortalCli cli = new JobPortalCli(
                    scanner,
                    output,
                    jobService,
                    savedJobService,
                    applicationService
            );
            cli.run();
        }

        return outputBytes.toString(StandardCharsets.UTF_8);
    }

    private static void assertContains(String text, String expectedText) {
        assertTrue(
                text.contains(expectedText),
                () -> "Expected CLI output to contain <" + expectedText + ">"
        );
    }

    private static Job createJob() {
        return Job.builder()
                .id(JobID.from("d61b9fa8-c23c-43af-b60c-3903512c8d01"))
                .title("Java Developer")
                .company("Tech Solutions Ltd")
                .jobType("Graduate")
                .location("Cheltenham")
                .build();
    }
}
