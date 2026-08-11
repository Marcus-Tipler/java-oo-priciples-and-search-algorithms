package com.studentjobportal.service;

import static com.studentjobportal.TestAssertions.assertContains;
import static com.studentjobportal.TestAssertions.assertEquals;
import static com.studentjobportal.TestAssertions.assertThrows;
import static com.studentjobportal.TestAssertions.assertTrue;
import com.studentjobportal.exception.DuplicateSavedJobException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.InMemorySavedJobRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;

public final class SavedJobServiceTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    public static void main(String[] args) {
        savesAndReturnsAJob();
        rejectsDuplicateSavedJob();
        rejectsMissingJob();
        reportsBrokenSavedJobReference();

        System.out.println(
                "All SavedJobService tests passed."
        );
    }

    private static void savesAndReturnsAJob() {
        TestContext context = createContext();

        context.service.saveJob(JOB_ID);

        assertTrue(context.service.isSaved(JOB_ID));
        assertEquals(1, context.service.getSavedJobs().size());

        Job savedJob = context.service
                .getSavedJobs()
                .get(0);

        assertEquals(JOB_ID, savedJob.getId());
    }

    private static void rejectsDuplicateSavedJob() {
        TestContext context = createContext();

        context.service.saveJob(JOB_ID);

        DuplicateSavedJobException exception = assertThrows(
                DuplicateSavedJobException.class,
                () -> context.service.saveJob(JOB_ID)
        );

        assertContains(
                exception.getMessage(),
                JOB_ID.toString()
        );
    }

    private static void rejectsMissingJob() {
        TestContext context = createContext();

        JobID missingID = JobID.from(
                "87b3effd-61da-4d18-ae1e-dd186ea283f7"
        );

        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                () -> context.service.saveJob(missingID)
        );

        assertContains(
                exception.getMessage(),
                missingID.toString()
        );
    }

    private static void reportsBrokenSavedJobReference() {
        TestContext context = createContext();

        context.service.saveJob(JOB_ID);
        context.jobRepository.deleteById(JOB_ID);

        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                context.service::getSavedJobs
        );

        assertContains(
                exception.getMessage(),
                JOB_ID.toString()
        );
    }

    private static TestContext createContext() {
        JobRepository jobRepository =
                new InMemoryJobRepository();

        SavedJobRepository savedJobRepository =
                new InMemorySavedJobRepository();

        jobRepository.save(
                Job.builder()
                        .id(JOB_ID)
                        .title("Java Developer")
                        .company("Tech Solutions Ltd")
                        .jobType("Graduate")
                        .location("Cheltenham")
                        .build()
        );

        SavedJobService service = new SavedJobService(
                jobRepository,
                savedJobRepository
        );

        return new TestContext(jobRepository, service);
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