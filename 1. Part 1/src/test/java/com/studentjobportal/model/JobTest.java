package com.studentjobportal.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.studentjobportal.exception.JobValidationException;

/**
 * Verifies job construction, validation and ID-based equality.
 */
final class JobTest {

    private static final String FIRST_ID =
            "d61b9fa8-c23c-43af-b60c-3903512c8d01";
    private static final String SECOND_ID =
            "87b3effd-61da-4d18-ae1e-dd186ea283f7";

    @Test
    void builderCreatesAValidTrimmedJob() {
        JobID id = JobID.from(FIRST_ID);

        Job job = Job.builder()
                .id(id)
                .title("  Java Developer  ")
                .company("  Tech Solutions Ltd  ")
                .jobType("  Graduate  ")
                .location("  Cheltenham  ")
                .build();

        assertEquals(id, job.getId());
        assertEquals("Java Developer", job.getTitle());
        assertEquals("Tech Solutions Ltd", job.getCompany());
        assertEquals("Graduate", job.getJobType());
        assertEquals("Cheltenham", job.getLocation());
    }

    @Test
    void builderRejectsMissingId() {
        assertThrows(
                JobValidationException.class,
                () -> validBuilder().id(null).build()
        );
    }

    @Test
    void builderRejectsInvalidTextFields() {
        assertInvalidText(() -> validBuilder().title(null).build());
        assertInvalidText(() -> validBuilder().title("   ").build());
        assertInvalidText(() -> validBuilder().company(null).build());
        assertInvalidText(() -> validBuilder().company("   ").build());
        assertInvalidText(() -> validBuilder().jobType(null).build());
        assertInvalidText(() -> validBuilder().jobType("   ").build());
        assertInvalidText(() -> validBuilder().location(null).build());
        assertInvalidText(() -> validBuilder().location("   ").build());
    }

    @Test
    void equalityUsesJobId() {
        Job firstJob = createJob(FIRST_ID);
        Job sameId = Job.builder()
                .id(JobID.from(FIRST_ID))
                .title("Different Title")
                .company("Different Company")
                .jobType("Placement")
                .location("Bristol")
                .build();
        Job differentId = createJob(SECOND_ID);

        // A job's immutable ID, rather than its descriptive fields, defines identity.
        assertEquals(firstJob, sameId);
        assertEquals(firstJob.hashCode(), sameId.hashCode());
        assertNotEquals(firstJob, differentId);
        assertEquals(firstJob, firstJob);
        assertNotEquals(null, firstJob);
        assertNotEquals("not a job", firstJob);
    }

    @Test
    void toStringContainsAllFields() {
        String result = createJob(FIRST_ID).toString();

        assertTrue(result.contains(FIRST_ID));
        assertTrue(result.contains("Java Developer"));
        assertTrue(result.contains("Tech Solutions Ltd"));
        assertTrue(result.contains("Graduate"));
        assertTrue(result.contains("Cheltenham"));
    }

    private static void assertInvalidText(Runnable buildAction) {
        assertThrows(JobValidationException.class, buildAction::run);
    }

    private static Job.Builder validBuilder() {
        return Job.builder()
                .id(JobID.from(FIRST_ID))
                .title("Java Developer")
                .company("Tech Solutions Ltd")
                .jobType("Graduate")
                .location("Cheltenham");
    }

    private static Job createJob(String id) {
        return validBuilder().id(JobID.from(id)).build();
    }
}
