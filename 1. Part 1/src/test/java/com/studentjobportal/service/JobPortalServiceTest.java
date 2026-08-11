package com.studentjobportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import com.studentjobportal.repository.InMemorySavedJobRepository;
import com.studentjobportal.repository.JobRepository;

/**
 * Integration tests for the combined portal service and its repositories.
 */
final class JobPortalServiceTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );
    private static final JobID MISSING_JOB_ID = JobID.from(
            "87b3effd-61da-4d18-ae1e-dd186ea283f7"
    );
    private static final Instant SUBMISSION_TIME =
            Instant.parse("2026-08-11T10:15:30Z");

    @Test
    void savingSameJobTwiceDoesNotCreateDuplicate() {
        TestContext context = createContext();

        assertTrue(context.service.saveJob(JOB_ID));
        assertFalse(context.service.saveJob(JOB_ID));
        assertEquals(1, context.service.getSavedJobs().size());
    }

    @Test
    void applyingTwiceIsRejected() {
        TestContext context = createContext();
        context.service.applyForJob(JOB_ID);

        assertThrows(DuplicateApplicationException.class,
                () -> context.service.applyForJob(JOB_ID));
        assertEquals(1, context.service.getApplications().size());
    }

    @Test
    void missingJobsCannotBeSavedOrAppliedFor() {
        TestContext context = createContext();

        assertThrows(JobNotFoundException.class,
                () -> context.service.saveJob(MISSING_JOB_ID));
        assertThrows(JobNotFoundException.class,
                () -> context.service.applyForJob(MISSING_JOB_ID));
    }

    @Test
    void applicationContainsStatusAndSubmissionTime() {
        Application application = createContext().service.applyForJob(JOB_ID);

        assertEquals(JOB_ID, application.getJobID());
        assertEquals(ApplicationStatus.SUBMITTED, application.getStatus());
        assertEquals(SUBMISSION_TIME, application.getSubmittedAt());
    }

    @Test
    void removedJobsAreHandledPredictably() {
        TestContext context = createContext();
        context.service.saveJob(JOB_ID);
        context.service.applyForJob(JOB_ID);

        // Applications are historical records, while saved jobs require a live job.
        context.jobRepository.deleteById(JOB_ID);

        assertTrue(context.service.getSavedJobs().isEmpty());
        assertEquals(1, context.service.getApplications().size());
        assertThrows(JobNotFoundException.class,
                () -> context.service.saveJob(JOB_ID));
        assertThrows(JobNotFoundException.class,
                () -> context.service.applyForJob(JOB_ID));
    }

    private static TestContext createContext() {
        JobRepository jobRepository = new InMemoryJobRepository();
        jobRepository.save(createJob());

        JobPortalService service = new JobPortalService(
                jobRepository,
                new InMemorySavedJobRepository(),
                new InMemoryApplicationRepository(),
                Clock.fixed(SUBMISSION_TIME, ZoneOffset.UTC)
        );
        return new TestContext(jobRepository, service);
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

    private static final class TestContext {
        private final JobRepository jobRepository;
        private final JobPortalService service;

        private TestContext(
                JobRepository jobRepository,
                JobPortalService service) {
            this.jobRepository = jobRepository;
            this.service = service;
        }
    }
}
