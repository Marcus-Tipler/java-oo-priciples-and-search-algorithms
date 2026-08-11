package com.studentjobportal.repository;

import java.util.Set;

import static com.studentjobportal.TestAssertions.assertEquals;
import static com.studentjobportal.TestAssertions.assertFalse;
import static com.studentjobportal.TestAssertions.assertThrows;
import static com.studentjobportal.TestAssertions.assertTrue;
import com.studentjobportal.model.JobID;

public final class InMemorySavedJobRepositoryTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    public static void main(String[] args) {
        savesAJobId();
        preventsDuplicateSavedJobs();
        removesASavedJob();
        returnsSafeCollections();

        System.out.println(
                "All InMemorySavedJobRepository tests passed."
        );
    }

    private static void savesAJobId() {
        SavedJobRepository repository =
                new InMemorySavedJobRepository();

        assertTrue(repository.save(JOB_ID));
        assertTrue(repository.contains(JOB_ID));
        assertEquals(1, repository.findAll().size());
    }

    private static void preventsDuplicateSavedJobs() {
        SavedJobRepository repository =
                new InMemorySavedJobRepository();

        assertTrue(repository.save(JOB_ID));
        assertFalse(repository.save(JOB_ID));
        assertEquals(1, repository.findAll().size());
    }

    private static void removesASavedJob() {
        SavedJobRepository repository =
                new InMemorySavedJobRepository();

        repository.save(JOB_ID);

        assertTrue(repository.remove(JOB_ID));
        assertFalse(repository.contains(JOB_ID));
        assertFalse(repository.remove(JOB_ID));
    }

    private static void returnsSafeCollections() {
        SavedJobRepository repository =
                new InMemorySavedJobRepository();

        repository.save(JOB_ID);

        Set<JobID> returnedIds = repository.findAll();

        assertThrows(
                UnsupportedOperationException.class,
                returnedIds::clear
        );

        assertEquals(1, repository.findAll().size());
    }
}