package com.studentjobportal;

import java.time.Clock;
import java.util.Scanner;

import com.studentjobportal.cli.StudentJobPortalCli;
import com.studentjobportal.data.SampleJobDataSeeder;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.InMemoryApplicationRepository;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.InMemorySavedJobRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;
import com.studentjobportal.service.JobPortalService;

// Starting point with little to no responsibility
public final class StudentJobPortal {

    private StudentJobPortal() {
    }

    public static void main(String[] args) {
        JobRepository jobRepository =
                new InMemoryJobRepository();

        SavedJobRepository savedJobRepository =
                new InMemorySavedJobRepository();

        ApplicationRepository applicationRepository =
                new InMemoryApplicationRepository();

        SampleJobDataSeeder.seed(jobRepository);

        JobPortalService service = new JobPortalService(
                jobRepository,
                savedJobRepository,
                applicationRepository,
                Clock.systemUTC()
        );

        try (Scanner scanner = new Scanner(System.in)) {
            StudentJobPortalCli cli =
                    new StudentJobPortalCli(scanner, service);

            cli.run();
        }
    }
}