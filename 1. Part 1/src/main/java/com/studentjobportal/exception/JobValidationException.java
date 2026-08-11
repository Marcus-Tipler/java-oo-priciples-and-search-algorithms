package com.studentjobportal.exception;

// invalid job data or search input throws this exception
public final class JobValidationException
        extends IllegalArgumentException {

    public JobValidationException(String message) {
        super(message);
    }

    public JobValidationException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}