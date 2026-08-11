package com.studentjobportal;

import java.time.Clock;
import java.util.Scanner;

import com.studentjobportal.cli.StudentJobPortalCli;
import com.studentjobportal.data.SampleJobData;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;
import com.studentjobportal.service.JobPortalService;

// Starting point with little to no responsibility
public final class StudentJobPortal {

    private StudentJobPortal() {
    }

    public static void main(String[] args) {
        JobRepository jobRepository = new JobRepository(SampleJobData.createJobs());
        SavedJobRepository savedJobRepository = new SavedJobRepository();
        ApplicationRepository applicationRepository = new ApplicationRepository();
        JobPortalService service = new JobPortalService(jobRepository, savedJobRepository, applicationRepository, Clock.systemUTC());

        try (Scanner scanner = new Scanner(System.in)) {
            new StudentJobPortalCli(scanner, service).run();
        }
    }
}