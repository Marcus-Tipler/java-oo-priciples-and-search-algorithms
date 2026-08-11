package com.studentjobportal;

public final class TestAssertions {

    private TestAssertions() {
    }

    public static void assertEquals(
            Object expected,
            Object actual) {

        boolean equal = expected == null
                ? actual == null
                : expected.equals(actual);

        if (!equal) {
            throw new AssertionError(
                    "Expected <" + expected
                            + "> but was <" + actual + ">"
            );
        }
    }

    public static void assertTrue(boolean condition) {
        if (!condition) {
            throw new AssertionError(
                    "Expected true but was false"
            );
        }
    }

    public static void assertFalse(boolean condition) {
        if (condition) {
            throw new AssertionError(
                    "Expected false but was true"
            );
        }
    }

    public static void assertContains(
            String text,
            String expectedText) {

        if (!text.contains(expectedText)) {
            throw new AssertionError(
                    "Expected <" + text
                            + "> to contain <"
                            + expectedText + ">"
            );
        }
    }

    public static <T extends Throwable> T assertThrows(
            Class<T> expectedType,
            Runnable action) {

        try {
            action.run();
        } catch (Throwable actualError) {
            if (expectedType.isInstance(actualError)) {
                return expectedType.cast(actualError);
            }

            AssertionError assertionError = new AssertionError(
                    "Expected " + expectedType.getSimpleName()
                            + " but caught "
                            + actualError.getClass().getSimpleName()
            );

            assertionError.initCause(actualError);
            throw assertionError;
        }

        throw new AssertionError(
                "Expected " + expectedType.getSimpleName()
                        + " but no exception was thrown"
        );
    }
}