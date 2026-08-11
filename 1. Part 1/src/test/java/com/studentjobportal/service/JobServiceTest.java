package com.studentjobportal.service;

import static com.studentjobportal.TestAssertions.assertContains;
import static com.studentjobportal.TestAssertions.assertEquals;
import static com.studentjobportal.TestAssertions.assertThrows;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.search.TitleSearchStrategy;

public final class JobServiceTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    public static void main(String[] args) {
        returnsAllJobs();
        returnsJobById();
        searchesUsingInjectedStrategy();
        returnsNoResultsForBlankSearch();
        throwsForMissingJob();

        System.out.println("All JobService tests passed.");
    }

    private static void returnsAllJobs() {
        JobService service = createService();

        assertEquals(1, service.getAllJobs().size());
    }

    private static void returnsJobById() {
        JobService service = createService();

        Job job = service.getJobById(JOB_ID);

        assertEquals(JOB_ID, job.getId());
        assertEquals("Java Developer", job.getTitle());
    }

    private static void searchesUsingInjectedStrategy() {
        JobService service = createService();

        assertEquals(
                1,
                service.searchJobs("JAVA").size()
        );

        /*
         * The injected strategy searches titles only.
         */
        assertEquals(
                0,
                service.searchJobs("Tech Solutions").size()
        );
    }

    private static void returnsNoResultsForBlankSearch() {
        JobService service = createService();

        assertEquals(0, service.searchJobs("   ").size());
    }

    private static void throwsForMissingJob() {
        JobService service = createService();

        JobID missingID = JobID.from(
                "87b3effd-61da-4d18-ae1e-dd186ea283f7"
        );

        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                () -> service.getJobById(missingID)
        );

        assertContains(
                exception.getMessage(),
                missingID.toString()
        );
    }

    private static JobService createService() {
        JobRepository repository =
                new InMemoryJobRepository();

        repository.save(createJob());

        return new JobService(
                repository,
                new TitleSearchStrategy()
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