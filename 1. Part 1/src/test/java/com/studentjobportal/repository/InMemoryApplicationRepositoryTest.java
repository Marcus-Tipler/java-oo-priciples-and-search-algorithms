package com.studentjobportal.repository;

import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationID;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.JobID;

import java.time.Instant;
import java.util.List;

import static com.studentjobportal.TestAssertions.assertEquals;
import static com.studentjobportal.TestAssertions.assertFalse;
import static com.studentjobportal.TestAssertions.assertThrows;
import static com.studentjobportal.TestAssertions.assertTrue;

public final class InMemoryApplicationRepositoryTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    public static void main(String[] args) {
        savesAndFindsAnApplication();
        returnsEmptyForUnknownJob();
        preventsTwoApplicationsForTheSameJob();
        returnsSafeCollections();

        System.out.println(
                "All InMemoryApplicationRepository tests passed."
        );
    }

    private static void savesAndFindsAnApplication() {
        ApplicationRepository repository =
                new InMemoryApplicationRepository();

        Application application = createApplication();

        assertTrue(repository.save(application));

        Application found = repository.findByJobId(JOB_ID)
                .orElseThrow(() ->
                        new AssertionError(
                                "Application was not found"
                        )
                );

        assertEquals(application, found);
    }

    private static void returnsEmptyForUnknownJob() {
        ApplicationRepository repository =
                new InMemoryApplicationRepository();

        assertFalse(
                repository.findByJobId(JOB_ID).isPresent()
        );
    }

    private static void preventsTwoApplicationsForTheSameJob() {
        ApplicationRepository repository =
                new InMemoryApplicationRepository();

        assertTrue(repository.save(createApplication()));
        assertFalse(repository.save(createApplication()));
        assertEquals(1, repository.findAll().size());
    }

    private static void returnsSafeCollections() {
        ApplicationRepository repository =
                new InMemoryApplicationRepository();

        repository.save(createApplication());

        List<Application> returnedApplications =
                repository.findAll();

        assertThrows(
                UnsupportedOperationException.class,
                returnedApplications::clear
        );

        assertEquals(1, repository.findAll().size());
    }

    private static Application createApplication() {
        return Application.builder()
                .id(ApplicationID.generate())
                .jobId(JOB_ID)
                .status(ApplicationStatus.SUBMITTED)
                .submittedAt(
                        Instant.parse("2026-08-11T10:15:30Z")
                )
                .build();
    }
}