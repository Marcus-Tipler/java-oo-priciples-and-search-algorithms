package com.studentjobportal.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

/**
 * Verifies the immutable state, validation and identity rules of
 * {@link Application}.
 */
final class ApplicationTest {

    private static final String APPLICATION_ID =
            "fcfc1ba8-8788-4377-b397-994e0afe202b";
    private static final String SECOND_APPLICATION_ID =
            "0a011f56-cc19-43ce-a677-98cb63982f78";
    private static final String JOB_ID =
            "d61b9fa8-c23c-43af-b60c-3903512c8d01";
    private static final Instant SUBMITTED_AT =
            Instant.parse("2026-08-11T10:15:30Z");

    @Test
    void builderStoresRequiredInformation() {
        Application application = createApplication(APPLICATION_ID, SUBMITTED_AT);

        assertEquals(ApplicationID.from(APPLICATION_ID), application.getId());
        assertEquals(JobID.from(JOB_ID), application.getJobID());
        assertEquals(ApplicationStatus.SUBMITTED, application.getStatus());
        assertEquals(SUBMITTED_AT, application.getSubmittedAt());
    }

    @Test
    void builderRejectsMissingRequiredInformation() {
        assertThrows(NullPointerException.class,
                () -> validBuilder().id(null).build());
        assertThrows(NullPointerException.class,
                () -> validBuilder().JobID(null).build());
        assertThrows(NullPointerException.class,
                () -> validBuilder().status(null).build());
        assertThrows(NullPointerException.class,
                () -> validBuilder().submittedAt(null).build());
    }

    @Test
    void equalityUsesApplicationId() {
        Application first = createApplication(APPLICATION_ID, SUBMITTED_AT);
        Application sameId = createApplication(
                APPLICATION_ID,
                SUBMITTED_AT.plusSeconds(60)
        );
        Application differentId = createApplication(
                SECOND_APPLICATION_ID,
                SUBMITTED_AT
        );

        // Non-identity fields do not affect the application's identity.
        assertEquals(first, sameId);
        assertEquals(first.hashCode(), sameId.hashCode());
        assertNotEquals(first, differentId);
        assertNotEquals(null, first);
        assertNotEquals("not an application", first);
    }

    @Test
    void toStringContainsAllFields() {
        String result = createApplication(APPLICATION_ID, SUBMITTED_AT).toString();

        assertTrue(result.contains(APPLICATION_ID));
        assertTrue(result.contains(JOB_ID));
        assertTrue(result.contains("SUBMITTED"));
        assertTrue(result.contains(SUBMITTED_AT.toString()));
    }

    private static Application.Builder validBuilder() {
        return Application.builder()
                .id(ApplicationID.from(APPLICATION_ID))
                .JobID(JobID.from(JOB_ID))
                .status(ApplicationStatus.SUBMITTED)
                .submittedAt(SUBMITTED_AT);
    }

    private static Application createApplication(
            String applicationId,
            Instant submittedAt) {
        return Application.builder()
                .id(ApplicationID.from(applicationId))
                .JobID(JobID.from(JOB_ID))
                .status(ApplicationStatus.SUBMITTED)
                .submittedAt(submittedAt)
                .build();
    }
}
