package com.studentjobportal.exception;

import com.studentjobportal.model.JobID;

// Duplicate saved jobs throws this exception.
public final class DuplicateSavedJobException extends RuntimeException {

    public DuplicateSavedJobException(JobID jobID) {
        super("Job " + jobID + " has already been saved");
    }
}