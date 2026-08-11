package com.studentjobportal.repository;

import java.util.List;

import static com.studentjobportal.TestAssertions.assertEquals;
import static com.studentjobportal.TestAssertions.assertFalse;
import static com.studentjobportal.TestAssertions.assertThrows;
import static com.studentjobportal.TestAssertions.assertTrue;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;

public final class InMemoryJobRepositoryTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    public static void main(String[] args) {
        savesAndFindsAJob();
        returnsEmptyForUnknownJob();
        rejectsDuplicateJobIds();
        returnsSafeCollections();
        deletesAJob();

        System.out.println(
                "All InMemoryJobRepository tests passed."
        );
    }

    private static void savesAndFindsAJob() {
        JobRepository repository =
                new InMemoryJobRepository();

        Job job = createJob();
        repository.save(job);

        Job found = repository.findById(JOB_ID)
                .orElseThrow(() ->
                        new AssertionError("Job was not found")
                );

        assertEquals(job, found);
        assertEquals(1, repository.findAll().size());
    }

    private static void returnsEmptyForUnknownJob() {
        JobRepository repository =
                new InMemoryJobRepository();

        assertFalse(repository.findById(JOB_ID).isPresent());
    }

    private static void rejectsDuplicateJobIds() {
        JobRepository repository =
                new InMemoryJobRepository();

        repository.save(createJob());

        assertThrows(
                IllegalArgumentException.class,
                () -> repository.save(createJob())
        );
    }

    private static void returnsSafeCollections() {
        JobRepository repository =
                new InMemoryJobRepository();

        repository.save(createJob());

        List<Job> returnedJobs = repository.findAll();

        assertThrows(
                UnsupportedOperationException.class,
                returnedJobs::clear
        );

        assertEquals(1, repository.findAll().size());
    }

    private static void deletesAJob() {
        JobRepository repository =
                new InMemoryJobRepository();

        repository.save(createJob());

        assertTrue(repository.deleteById(JOB_ID));
        assertFalse(repository.findById(JOB_ID).isPresent());
        assertFalse(repository.deleteById(JOB_ID));
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