package com.studentjobportal.model;

// updated test for job
public final class JobTest {

    private static final String FIRST_ID = "d61b9fa8-c23c-43af-b60c-3903512c8d01";
    private static final String SECOND_ID = "87b3effd-61da-4d18-ae1e-dd186ea283f7";
    private int testsRun;
    private int testsPassed;

    public static void main(String[] args) {new JobTest().runAllTests();}

    private void runAllTests() {
        runTest("Builder creates a valid job", this::builderCreatesValidJob);
        runTest("Builder rejects a missing ID", this::builderRejectsMissingId);
        runTest("Builder rejects an invalid title", this::builderRejectsInvalidTitle);
        runTest("Builder rejects an invalid company", this::builderRejectsInvalidCompany);
        runTest("Builder rejects an invalid job type", this::builderRejectsInvalidJobType);
        runTest("Builder rejects an invalid location", this::builderRejectsInvalidLocation);
        runTest("Jobs with the same ID are equal", this::jobsWithSameIdAreEqual);
        runTest("Jobs with different IDs are not equal", this::jobsWithDifferentIdsAreNotEqual);
        runTest("A job is equal to itself", this::jobIsEqualToItself);
        runTest("A job is not equal to null or another type", this::jobIsNotEqualToNullOrAnotherType);
        runTest("toString contains every job field", this::toStringContainsAllJobFields);

        System.out.println();
        System.out.println(testsPassed + " of " + testsRun + " tests passed.");

        if (testsPassed != testsRun) {throw new AssertionError("One or more tests failed.");}
    }

    private void builderCreatesValidJob() {
        JobID id = JobID.from(FIRST_ID);

        Job job = Job.builder()
            .id(id)
            .title("  Java Developer  ")
            .company("  Tech Solutions Ltd  ")
            .jobType("  Graduate  ")
            .location("  Cheltenham  ")
            .build();

        assertEquals(id, job.getId(), "ID was not stored");
        assertEquals("Java Developer", job.getTitle(), "Title was not stored correctly");
        assertEquals("Tech Solutions Ltd", job.getCompany(), "Company was not stored correctly");
        assertEquals("Graduate", job.getJobType(), "Job type was not stored correctly");
        assertEquals("Cheltenham", job.getLocation(), "Location was not stored correctly");
    }

    private void builderRejectsMissingId() {
        assertThrows(NullPointerException.class, () -> validBuilder().id(null).build(), "A null job ID should be rejected");
    }

    private void builderRejectsInvalidTitle() {
        assertThrows(NullPointerException.class, () -> validBuilder().title(null).build(), "A null title should be rejected");
        assertThrows(IllegalArgumentException.class, () -> validBuilder().title("   ").build(), "A blank title should be rejected");
    }

    private void builderRejectsInvalidCompany() {
        assertThrows(NullPointerException.class, () -> validBuilder().company(null).build(), "A null company should be rejected");
        assertThrows(IllegalArgumentException.class, () -> validBuilder().company("   ").build(), "A blank company should be rejected");
    }

    private void builderRejectsInvalidJobType() {
        assertThrows(NullPointerException.class, () -> validBuilder().jobType(null).build(), "A null job type should be rejected");
        assertThrows(IllegalArgumentException.class, () -> validBuilder().jobType("   ").build(), "A blank job type should be rejected");
    }

    private void builderRejectsInvalidLocation() {
        assertThrows(NullPointerException.class, () -> validBuilder().location(null).build(), "A null location should be rejected");
        assertThrows(IllegalArgumentException.class,() -> validBuilder().location("   ").build(), "A blank location should be rejected");
    }

    private void jobsWithSameIdAreEqual() {
        Job firstJob = createJob(FIRST_ID);

        Job secondJob = Job.builder()
            .id(JobID.from(FIRST_ID))
            .title("Different Title")
            .company("Different Company")
            .jobType("Placement")
            .location("Bristol")
            .build();

        assertEquals(firstJob, secondJob, "Jobs with the same ID should be equal");
        assertEquals(firstJob.hashCode(), secondJob.hashCode(), "Equal jobs should have the same hash code");
    }

    private void jobsWithDifferentIdsAreNotEqual() {
        Job firstJob = createJob(FIRST_ID);
        Job secondJob = createJob(SECOND_ID);

        assertNotEquals(firstJob, secondJob, "Jobs with different IDs should not be equal");
    }

    private void jobIsEqualToItself() {
        Job job = createJob(FIRST_ID);
        assertEquals(job, job, "A job should be equal to itself");
    }

    private void jobIsNotEqualToNullOrAnotherType() {
        Job job = createJob(FIRST_ID);

        assertFalse(
            job.equals(null),
            "A job should not equal null"
        );

        assertFalse(
            job.equals("not a job"),
            "A job should not equal another type"
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

    private Job.Builder validBuilder() {
        return Job.builder()
        .id(JobID.from(FIRST_ID))
        .title("Java Developer")
        .company("Tech Solutions Ltd")
        .jobType("Graduate")
        .location("Cheltenham");
    }

    private Job createJob(String id) {
        return Job.builder()
        .id(JobID.from(id))
        .title("Java Developer")
        .company("Tech Solutions Ltd")
        .jobType("Graduate")
        .location("Cheltenham")
        .build();
    }

    private void runTest(String testName, Runnable test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.println("PASS: " + testName);
        } catch (Throwable error) {
            System.out.println("FAIL: " + testName);
            System.out.println("      " + error.getMessage());
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        boolean equal = expected == null ? actual == null : expected.equals(actual);
        if (!equal) {throw new AssertionError(message + "; expected <" + expected + "> but was <" + actual + ">");}
    }

    private static void assertNotEquals(Object unexpected, Object actual, String message) {
        boolean equal = unexpected == null ? actual == null : unexpected.equals(actual);
        if (equal) {throw new AssertionError(message + "; both values were <" + actual + ">");}
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {throw new AssertionError(message);}
    }

    private static void assertContains(String text, String expectedText) {
        if (!text.contains(expectedText)) {throw new AssertionError("Expected <" + text + "> to contain <" + expectedText + ">");}
    }

    private static void assertThrows(Class<? extends Throwable> expectedType, Runnable action, String message) {
        try {action.run();} 
        catch (Throwable actualError) {
            if (expectedType.isInstance(actualError)) {
                return;
            }
            throw new AssertionError(message + "; expected " + expectedType.getSimpleName() + " but caught " + actualError.getClass().getSimpleName());
        }
        throw new AssertionError(message + "; expected " + expectedType.getSimpleName() + " but no exception was thrown");
    }
}