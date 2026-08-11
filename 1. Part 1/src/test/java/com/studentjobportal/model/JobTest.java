package com.studentjobportal.model;


// Tests for job
public final class JobTest {

    private static final String FIRST_ID = "d61b9fa8-c23c-43af-b60c-3903512c8d01";

    private static final String SECOND_ID = "87b3effd-61da-4d18-ae1e-dd186ea283f7";

    private int testsRun;
    private int testsPassed;

    public static void main(String[] args) {
        new JobTest().runAllTests();
    }

    private void runAllTests() {
        runTest(
            "Jobs with the same ID are equal",
            this::jobsWithSameIdAreEqual
        );

        runTest(
            "Jobs with different IDs are not equal",
            this::jobsWithDifferentIdsAreNotEqual
        );

        runTest(
            "A job is equal to itself",
            this::jobIsEqualToItself
        );

        runTest(
            "A job is not equal to null or another type",
            this::jobIsNotEqualToNullOrAnotherType
        );

        runTest(
            "toString contains every job field",
            this::toStringContainsAllJobFields
        );

        System.out.println();
        System.out.println(testsPassed + " of " + testsRun + " tests passed.");

        if (testsPassed != testsRun) {throw new AssertionError("One or more tests failed.");}
    }

    private void jobsWithSameIdAreEqual() {
        Job firstJob = new Job(
            JobID.from(FIRST_ID),
            "Java Developer",
            "Tech Solutions Ltd",
            "Graduate",
            "Cheltenham"
        );

        Job secondJob = new Job(
            JobID.from(FIRST_ID),
            "Different Title",
            "Different Company",
            "Placement",
            "Bristol"
        );

        assertEquals(
            firstJob,
            secondJob,
            "Jobs with the same ID should be equal"
        );

        assertEquals(
            firstJob.hashCode(),
            secondJob.hashCode(),
            "Equal jobs should have the same hash code"
        );
    }

    private void jobsWithDifferentIdsAreNotEqual() {
        Job firstJob = createJob(FIRST_ID);
        Job secondJob = createJob(SECOND_ID);

        assertNotEquals(
            firstJob,
            secondJob,
            "Jobs with different IDs should not be equal"
        );
    }

    private void jobIsEqualToItself() {
        Job job = createJob(FIRST_ID);

        assertEquals(
            job,
            job,
            "A job should be equal to itself"
        );
    }

    private void jobIsNotEqualToNullOrAnotherType() {
        Job job = createJob(FIRST_ID);

        assertFalse(
            job.equals(null),
            "A job should not equal null"
        );

        assertFalse(
            job.equals("not a job"),
            "A job should not equal an object of another type"
        );
    }

    private void toStringContainsAllJobFields() {
        Job job = createJob(FIRST_ID);
        String result = job.toString();

        assertContains(result, FIRST_ID);
        assertContains(result, "Java Developer");
        assertContains(result, "Tech Solutions Ltd");
        assertContains(result, "Graduate");
        assertContains(result, "Cheltenham");
    }

    private Job createJob(String id) {
        return new Job(
            JobID.from(id),
            "Java Developer",
            "Tech Solutions Ltd",
            "Graduate",
            "Cheltenham"
        );
    }

    private void runTest(String testName, Runnable test) {
        testsRun++;

        try {
            test.run();
            testsPassed++;
            System.out.println("PASS: " + testName);
        } catch (AssertionError error) {
            System.out.println("FAIL: " + testName);
            System.out.println("      " + error.getMessage());
        }
    }

    private static void assertEquals(
        Object expected,
        Object actual,
        String message) {

        boolean equal = expected == null ? actual == null : expected.equals(actual);

        if (!equal) {throw new AssertionError(message + "; expected <" + expected + "> but was <" + actual + ">");}
    }

    private static void assertNotEquals(
        Object unexpected,
        Object actual,
        String message) {

        boolean equal = unexpected == null ? actual == null : unexpected.equals(actual);

        if (equal) {throw new AssertionError(message + "; both values were <" + actual + ">");}
    }

    private static void assertFalse(
        boolean condition,
        String message) {

        if (condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertContains(
        String text,
        String expectedText) {
        if (!text.contains(expectedText)) {throw new AssertionError("Expected <" + text + "> to contain <" + expectedText + ">");}
    }
}