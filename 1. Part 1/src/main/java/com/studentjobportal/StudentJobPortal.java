package com.studentjobportal;

import java.time.Clock;
import java.util.Scanner;

import com.studentjobportal.cli.JobPortalCli;
import com.studentjobportal.data.SampleJobDataSeeder;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.InMemoryApplicationRepository;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.InMemorySavedJobRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;
import com.studentjobportal.search.CombinedKeywordSearchStrategy;
import com.studentjobportal.search.JobSearchStrategy;
import com.studentjobportal.service.ApplicationService;
import com.studentjobportal.service.JobService;
import com.studentjobportal.service.SavedJobService;

// Starting point with little to no responsibility
public final class StudentJobPortal {

    private StudentJobPortal() {
    }

    public static void main(String[] args) { 
        JobRepository jobRepository = new InMemoryJobRepository();
        SavedJobRepository savedJobRepository = new InMemorySavedJobRepository();
        ApplicationRepository applicationRepository = new InMemoryApplicationRepository();

        SampleJobDataSeeder.seed(jobRepository);

        JobSearchStrategy searchStrategy = new CombinedKeywordSearchStrategy();
        JobService jobService = new JobService(jobRepository, searchStrategy);
        SavedJobService savedJobService = new SavedJobService(jobRepository, savedJobRepository);
        ApplicationService applicationService = new ApplicationService(jobRepository, applicationRepository, Clock.systemUTC());

        try (Scanner scanner = new Scanner(System.in)) {
            JobPortalCli cli = new JobPortalCli(scanner, System.out, jobService, savedJobService, applicationService);
            cli.run();
        }
    }
}