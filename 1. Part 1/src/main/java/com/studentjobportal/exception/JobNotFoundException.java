package com.studentjobportal.exception;

import com.studentjobportal.model.JobID;

// if op does not find job, throw this exception
public final class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(JobID JobID) {
        super("No job exists with ID " + JobID);
    }
}