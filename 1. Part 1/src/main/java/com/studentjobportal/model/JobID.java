package com.studentjobportal.model;

import java.util.Objects;
import java.util.UUID;

import com.studentjobportal.exception.JobValidationException;


// Job identifier
public final class JobID {

    private final UUID value;

    private JobID(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static JobID generate() {
        return new JobID(UUID.randomUUID());
    }

    public static JobID from(String value) {
        if (value == null) {
            throw new JobValidationException("Job ID value cannot be null");
        }

        try {
            return new JobID(UUID.fromString(value));
        } catch (IllegalArgumentException cause) {
            throw new JobValidationException("Invalid job ID: " + value, cause);
        }
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof JobID)) {
            return false;
        }

        JobID otherID = (JobID) other;
        return value.equals(otherID.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}