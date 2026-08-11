package com.studentjobportal.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.studentjobportal.model.JobID;

/**
 * Verifies saved-job membership, duplicate prevention and safe collection
 * exposure.
 */
final class InMemorySavedJobRepositoryTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    private SavedJobRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemorySavedJobRepository();
    }

    @Test
    void savesAJobId() {
        assertTrue(repository.save(JOB_ID));
        assertTrue(repository.contains(JOB_ID));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void preventsDuplicateSavedJobs() {
        assertTrue(repository.save(JOB_ID));
        assertFalse(repository.save(JOB_ID));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void removesASavedJob() {
        repository.save(JOB_ID);

        assertTrue(repository.remove(JOB_ID));
        assertFalse(repository.contains(JOB_ID));
        assertFalse(repository.remove(JOB_ID));
    }

    @Test
    void returnsAnUnmodifiableSnapshot() {
        repository.save(JOB_ID);
        Set<JobID> returnedIds = repository.findAll();

        assertThrows(UnsupportedOperationException.class, returnedIds::clear);
        assertEquals(1, repository.findAll().size());
    }
}
