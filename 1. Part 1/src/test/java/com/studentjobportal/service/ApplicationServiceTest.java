package com.studentjobportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.InMemoryApplicationRepository;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.JobRepository;

/**
 * Verifies successful application submission and the service's error paths.
 */
final class ApplicationServiceTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );
    private static final JobID MISSING_JOB_ID = JobID.from(
            "87b3effd-61da-4d18-ae1e-dd186ea283f7"
    );
    private static final Instant SUBMISSION_TIME =
            Instant.parse("2026-08-11T10:15:30Z");

    @Test
    void submitsAnApplication() {
        ApplicationService service = createService();

        Application application = service.applyForJob(JOB_ID);

        assertEquals(JOB_ID, application.getJobID());
        assertEquals(ApplicationStatus.SUBMITTED, application.getStatus());
        assertEquals(SUBMISSION_TIME, application.getSubmittedAt());
        assertEquals(1, service.getApplications().size());
    }

    @Test
    void rejectsDuplicateApplication() {
        ApplicationService service = createService();
        service.applyForJob(JOB_ID);

        DuplicateApplicationException exception = assertThrows(
                DuplicateApplicationException.class,
                () -> service.applyForJob(JOB_ID)
        );

        assertTrue(exception.getMessage().contains(JOB_ID.toString()));
        assertEquals(1, service.getApplications().size());
    }

    @Test
    void rejectsMissingJob() {
        ApplicationService service = createService();

        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                () -> service.applyForJob(MISSING_JOB_ID)
        );

        assertTrue(exception.getMessage().contains(MISSING_JOB_ID.toString()));
    }

    private static ApplicationService createService() {
        JobRepository jobRepository = new InMemoryJobRepository();
        jobRepository.save(createJob());

        // The fixed clock makes the generated submission timestamp deterministic.
        return new ApplicationService(
                jobRepository,
                new InMemoryApplicationRepository(),
                Clock.fixed(SUBMISSION_TIME, ZoneOffset.UTC)
        );
    }

    private static Job createJob() {
        return Job.builder()
                .id(JOB_ID)
                .title("Java Developer")
                .company("Tech Solutions Ltd")
                .jobType("Graduate")
                .location("Cheltenham")
                .build();
    }
}
