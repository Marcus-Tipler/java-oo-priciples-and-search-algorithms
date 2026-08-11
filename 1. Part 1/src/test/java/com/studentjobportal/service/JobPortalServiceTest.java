package com.studentjobportal.service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Collections;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;

// test saved job and application
public final class JobPortalServiceTest {
    private static final JobID JOB_ID = JobID.from("d61b9fa8-c23c-43af-b60c-3903512c8d01");
    private static final JobID MISSING_JOB_ID = JobID.from("87b3effd-61da-4d18-ae1e-dd186ea283f7");
    private static final Instant SUBMISSION_TIME = Instant.parse("2026-08-11T10:15:30Z");

    public static void main(String[] args) {
        savingSameJobTwiceDoesNotCreateDuplicate();
        applyingTwiceIsRejected();
        missingJobsCannotBeSavedOrAppliedFor();
        applicationContainsStatusAndSubmissionTime();
        removedJobsAreHandledPredictably();

        System.out.println("All JobPortalService tests passed.");
    }

    private static void savingSameJobTwiceDoesNotCreateDuplicate() {
        TestContext context = createContext();

        assertTrue(context.service.saveJob(JOB_ID));
        assertFalse(context.service.saveJob(JOB_ID));
        assertEquals(1, context.service.getSavedJobs().size());
    }

    private static void applyingTwiceIsRejected() {
        TestContext context = createContext();
        context.service.applyForJob(JOB_ID);

        assertThrows(DuplicateApplicationException.class, () -> context.service.applyForJob(JOB_ID));
        assertEquals(1, context.service.getApplications().size());
    }

    private static void missingJobsCannotBeSavedOrAppliedFor() {
        TestContext context = createContext();

        assertThrows(JobNotFoundException.class, () -> context.service.saveJob(MISSING_JOB_ID));
        assertThrows(JobNotFoundException.class, () -> context.service.applyForJob(MISSING_JOB_ID));
    }

    private static void applicationContainsStatusAndSubmissionTime() {
        TestContext context = createContext();
        Application application = context.service.applyForJob(JOB_ID);

        assertEquals(JOB_ID, application.getJobID());
        assertEquals(ApplicationStatus.SUBMITTED, application.getStatus());
        assertEquals(SUBMISSION_TIME, application.getSubmittedAt());
    }

    private static void removedJobsAreHandledPredictably() {
        TestContext context = createContext();

        context.service.saveJob(JOB_ID);
        context.service.applyForJob(JOB_ID);
        context.jobRepository.remove(JOB_ID);

        assertEquals(0, context.service.getSavedJobs().size());
        assertEquals(1, context.service.getApplications().size());
        assertThrows(JobNotFoundException.class, () -> context.service.saveJob(JOB_ID));
        assertThrows(JobNotFoundException.class, () -> context.service.applyForJob(JOB_ID));
    }

    private static TestContext createContext() {
        Job job = Job.builder()
        .id(JOB_ID)
        .title("Java Developer")
        .company("Tech Solutions Ltd")
        .jobType("Graduate")
        .location("Cheltenham")
        .build();

        JobRepository jobRepository = new JobRepository(Collections.singletonList(job));
        JobPortalService service = new JobPortalService(jobRepository, new SavedJobRepository(), new ApplicationRepository(), Clock.fixed(SUBMISSION_TIME, ZoneOffset.UTC));

        return new TestContext(jobRepository, service);
    }

    private static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true but was false");
        }
    }

    private static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false but was true");
        }
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void assertEquals(int expected, int actual) {
        if (expected != actual) {
            throw new AssertionError("Expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void assertThrows(Class<? extends Throwable> expectedType, Runnable action) {
        try {
            action.run();
        } catch (Throwable actualError) {
            if (expectedType.isInstance(actualError)) {
                return;
            }
            throw new AssertionError("Expected " + expectedType.getSimpleName() + " but caught " + actualError.getClass().getSimpleName());
        }

        throw new AssertionError("Expected " + expectedType.getSimpleName() + " but no exception was thrown"
        );
    }

    private static final class TestContext {
        private final JobRepository jobRepository;
        private final JobPortalService service;
        private TestContext(JobRepository jobRepository, JobPortalService service) {
            this.jobRepository = jobRepository;
            this.service = service;
        }
    }
}