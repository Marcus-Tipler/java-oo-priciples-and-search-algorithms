package com.studentjobportal.service;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.InMemoryApplicationRepository;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.JobRepository;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static com.studentjobportal.TestAssertions.assertContains;
import static com.studentjobportal.TestAssertions.assertEquals;
import static com.studentjobportal.TestAssertions.assertThrows;

public final class ApplicationServiceTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    private static final Instant SUBMISSION_TIME =
            Instant.parse("2026-08-11T10:15:30Z");

    public static void main(String[] args) {
        submitsAnApplication();
        rejectsDuplicateApplication();
        rejectsMissingJob();
        returnsApplications();

        System.out.println(
                "All ApplicationService tests passed."
        );
    }

    private static void submitsAnApplication() {
        ApplicationService service = createService();

        Application application =
                service.applyForJob(JOB_ID);

        assertEquals(JOB_ID, application.getJobId());

        assertEquals(
                ApplicationStatus.SUBMITTED,
                application.getStatus()
        );

        assertEquals(
                SUBMISSION_TIME,
                application.getSubmittedAt()
        );
    }

    private static void rejectsDuplicateApplication() {
        ApplicationService service = createService();

        service.applyForJob(JOB_ID);

        DuplicateApplicationException exception = assertThrows(
                DuplicateApplicationException.class,
                () -> service.applyForJob(JOB_ID)
        );

        assertContains(
                exception.getMessage(),
                JOB_ID.toString()
        );
    }

    private static void rejectsMissingJob() {
        ApplicationService service = createService();

        JobID missingID = JobID.from(
                "87b3effd-61da-4d18-ae1e-dd186ea283f7"
        );

        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                () -> service.applyForJob(missingID)
        );

        assertContains(
                exception.getMessage(),
                missingID.toString()
        );
    }

    private static void returnsApplications() {
        ApplicationService service = createService();

        service.applyForJob(JOB_ID);

        assertEquals(
                1,
                service.getApplications().size()
        );
    }

    private static ApplicationService createService() {
        JobRepository jobRepository =
                new InMemoryJobRepository();

        ApplicationRepository applicationRepository =
                new InMemoryApplicationRepository();

        jobRepository.save(
                Job.builder()
                        .id(JOB_ID)
                        .title("Java Developer")
                        .company("Tech Solutions Ltd")
                        .jobType("Graduate")
                        .location("Cheltenham")
                        .build()
        );

        return new ApplicationService(
                jobRepository,
                applicationRepository,
                Clock.fixed(
                        SUBMISSION_TIME,
                        ZoneOffset.UTC
                )
        );
    }
}