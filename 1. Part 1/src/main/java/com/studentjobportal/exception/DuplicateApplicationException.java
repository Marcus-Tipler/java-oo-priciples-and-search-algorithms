package com.studentjobportal.exception;

import com.studentjobportal.model.JobID;

// if application already exists, throw this exception
public final class DuplicateApplicationException
        extends RuntimeException {

    public DuplicateApplicationException(JobID JobID) {
        super("An application already exists for job " + JobID);
    }
}