package com.studentjobportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.search.TitleSearchStrategy;

/**
 * Verifies job retrieval and delegation to an injected search strategy.
 */
final class JobServiceTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );
    private static final JobID MISSING_JOB_ID = JobID.from(
            "87b3effd-61da-4d18-ae1e-dd186ea283f7"
    );

    @Test
    void returnsAllJobs() {
        assertEquals(1, createService().getAllJobs().size());
    }

    @Test
    void returnsJobById() {
        Job job = createService().getJobById(JOB_ID);

        assertEquals(JOB_ID, job.getId());
        assertEquals("Java Developer", job.getTitle());
    }

    @Test
    void searchesUsingInjectedStrategy() {
        JobService service = createService();

        assertEquals(1, service.searchJobs("JAVA").size());
        // The injected title strategy must not match a company-only term.
        assertTrue(service.searchJobs("Tech Solutions").isEmpty());
    }

    @Test
    void returnsNoResultsForBlankSearch() {
        assertTrue(createService().searchJobs("   ").isEmpty());
    }

    @Test
    void throwsForMissingJob() {
        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                () -> createService().getJobById(MISSING_JOB_ID)
        );

        assertTrue(exception.getMessage().contains(MISSING_JOB_ID.toString()));
    }

    private static JobService createService() {
        JobRepository repository = new InMemoryJobRepository();
        repository.save(createJob());
        return new JobService(repository, new TitleSearchStrategy());
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
