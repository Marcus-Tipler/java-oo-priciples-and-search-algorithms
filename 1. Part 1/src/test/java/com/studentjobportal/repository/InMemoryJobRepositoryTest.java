package com.studentjobportal.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;

/**
 * Verifies the CRUD operations and defensive collection handling of the
 * in-memory job repository.
 */
final class InMemoryJobRepositoryTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    private JobRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryJobRepository();
    }

    @Test
    void savesAndFindsAJob() {
        Job job = createJob();

        repository.save(job);

        assertEquals(job, repository.findById(JOB_ID).orElseThrow());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void returnsEmptyForUnknownJob() {
        assertTrue(repository.findById(JOB_ID).isEmpty());
    }

    @Test
    void rejectsDuplicateJobIds() {
        repository.save(createJob());

        assertThrows(IllegalArgumentException.class,
                () -> repository.save(createJob()));
    }

    @Test
    void returnsAnUnmodifiableSnapshot() {
        repository.save(createJob());
        List<Job> returnedJobs = repository.findAll();

        assertThrows(UnsupportedOperationException.class, returnedJobs::clear);
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void deletesAJob() {
        repository.save(createJob());

        assertTrue(repository.deleteById(JOB_ID));
        assertTrue(repository.findById(JOB_ID).isEmpty());
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
