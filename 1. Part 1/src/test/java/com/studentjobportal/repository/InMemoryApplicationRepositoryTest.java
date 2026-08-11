package com.studentjobportal.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationID;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.JobID;

/**
 * Verifies application storage and the one-application-per-job constraint.
 */
final class InMemoryApplicationRepositoryTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    private ApplicationRepository repository;

    @BeforeEach
    void setUp() {
        // A fresh repository keeps every test independent of execution order.
        repository = new InMemoryApplicationRepository();
    }

    @Test
    void savesAndFindsAnApplication() {
        Application application = createApplication();

        assertTrue(repository.save(application));
        assertEquals(application, repository.findByJobId(JOB_ID).orElseThrow());
    }

    @Test
    void returnsEmptyForUnknownJob() {
        assertTrue(repository.findByJobId(JOB_ID).isEmpty());
    }

    @Test
    void preventsTwoApplicationsForTheSameJob() {
        assertTrue(repository.save(createApplication()));
        assertFalse(repository.save(createApplication()));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void returnsAnUnmodifiableSnapshot() {
        repository.save(createApplication());
        List<Application> returnedApplications = repository.findAll();

        assertThrows(UnsupportedOperationException.class, returnedApplications::clear);
        assertEquals(1, repository.findAll().size());
    }

    private static Application createApplication() {
        return Application.builder()
                .id(ApplicationID.generate())
                .JobID(JOB_ID)
                .status(ApplicationStatus.SUBMITTED)
                .submittedAt(Instant.parse("2026-08-11T10:15:30Z"))
                .build();
    }
}
