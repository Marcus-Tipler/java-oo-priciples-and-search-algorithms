package com.studentjobportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.studentjobportal.exception.DuplicateSavedJobException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.InMemorySavedJobRepository;
import com.studentjobportal.repository.JobRepository;

/**
 * Verifies saved-job retrieval, duplicate prevention and invalid references.
 */
final class SavedJobServiceTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );
    private static final JobID MISSING_JOB_ID = JobID.from(
            "87b3effd-61da-4d18-ae1e-dd186ea283f7"
    );

    @Test
    void savesAndReturnsAJob() {
        TestContext context = createContext();

        context.service.saveJob(JOB_ID);

        assertTrue(context.service.isSaved(JOB_ID));
        assertEquals(1, context.service.getSavedJobs().size());
        assertEquals(JOB_ID, context.service.getSavedJobs().get(0).getId());
    }

    @Test
    void rejectsDuplicateSavedJob() {
        TestContext context = createContext();
        context.service.saveJob(JOB_ID);

        DuplicateSavedJobException exception = assertThrows(
                DuplicateSavedJobException.class,
                () -> context.service.saveJob(JOB_ID)
        );

        assertTrue(exception.getMessage().contains(JOB_ID.toString()));
    }

    @Test
    void rejectsMissingJob() {
        TestContext context = createContext();

        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                () -> context.service.saveJob(MISSING_JOB_ID)
        );

        assertTrue(exception.getMessage().contains(MISSING_JOB_ID.toString()));
    }

    @Test
    void reportsBrokenSavedJobReference() {
        TestContext context = createContext();
        context.service.saveJob(JOB_ID);

        // Simulate a job being removed after the user saved it.
        context.jobRepository.deleteById(JOB_ID);

        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                context.service::getSavedJobs
        );
        assertTrue(exception.getMessage().contains(JOB_ID.toString()));
    }

    private static TestContext createContext() {
        JobRepository jobRepository = new InMemoryJobRepository();
        jobRepository.save(createJob());
        SavedJobService service = new SavedJobService(
                jobRepository,
                new InMemorySavedJobRepository()
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
        private final SavedJobService service;

        private TestContext(
                JobRepository jobRepository,
                SavedJobService service) {
            this.jobRepository = jobRepository;
            this.service = service;
        }
    }
}
