package com.studentjobportal.model;

import java.time.Instant;

// test for application
public final class ApplicationTest {

    private static final String APPLICATION_ID = "fcfc1ba8-8788-4377-b397-994e0afe202b";
    private static final String SECOND_APPLICATION_ID = "0a011f56-cc19-43ce-a677-98cb63982f78";
    private static final String JOB_ID = "d61b9fa8-c23c-43af-b60c-3903512c8d01";

    public static void main(String[] args) {
        applicationStoresRequiredInformation();
        equalityUsesApplicationID();
        toStringContainsAllFields();
        System.out.println("All Application tests passed.");
    }

    private static void applicationStoresRequiredInformation() {
        Instant submittedAt = Instant.parse("2026-08-11T10:15:30Z");
        Application application = createApplication(APPLICATION_ID, submittedAt);

        assertEquals(ApplicationID.from(APPLICATION_ID), application.getId());
        assertEquals(JobID.from(JOB_ID), application.getJobID());
        assertEquals(ApplicationStatus.SUBMITTED, application.getStatus());
        assertEquals(submittedAt, application.getSubmittedAt());
    }

    private static void equalityUsesApplicationID() {
        Instant firstTime = Instant.parse("2026-08-11T10:15:30Z");
        Instant secondTime = Instant.parse("2026-08-12T10:15:30Z");
        Application first = createApplication(APPLICATION_ID, firstTime);
        Application sameId = createApplication(APPLICATION_ID, secondTime);
        Application differentId = createApplication(SECOND_APPLICATION_ID, firstTime);

        assertEquals(first, sameId);
        assertEquals(first.hashCode(), sameId.hashCode());
        assertNotEquals(first, differentId);
    }

    private static void toStringContainsAllFields() {
        Application application = createApplication(APPLICATION_ID, Instant.parse("2026-08-11T10:15:30Z"));
        String result = application.toString();

        assertContains(result, APPLICATION_ID);
        assertContains(result, JOB_ID);
        assertContains(result, "SUBMITTED");
        assertContains(result, "2026-08-11T10:15:30Z");
    }

    private static Application createApplication(String APPLICATION_ID, Instant submittedAt) {
        return Application.builder()
        .id(ApplicationID.from(APPLICATION_ID))
        .JobID(JobID.from(JOB_ID))
        .status(ApplicationStatus.SUBMITTED)
        .submittedAt(submittedAt)
        .build();
    }

    private static void assertEquals(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected <" + expected + "> but was <" + actual + ">");
        }
    }

    private static void assertNotEquals(Object unexpected, Object actual) {
        if (unexpected.equals(actual)) {
            throw new AssertionError("Values should not be equal: " + actual);
        }
    }

    private static void assertContains(String text, String expectedText) {
        if (!text.contains(expectedText)) {
            throw new AssertionError("Expected <" + text + "> to contain <" + expectedText + ">");
        }
    }
}