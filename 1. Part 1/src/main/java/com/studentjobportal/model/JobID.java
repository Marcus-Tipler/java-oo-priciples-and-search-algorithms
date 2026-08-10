package com.studentjobportal.model;

import java.util.Objects;
import java.util.UUID;


// Job identifier
public final class JobID {

    private final UUID value;

    private JobID(UUID value) {
        this.value = Objects.requireNonNull(value, "Job ID cannot be null");
    }

    // Creates a new Job ID
    public static JobID generate() {
        return new JobID(UUID.randomUUID());
    }

    // Properly create Job ID from string
    public static JobID from(String value) {
        Objects.requireNonNull(value, "Job ID value cannot be null");
        return new JobID(UUID.fromString(value));
    }

    public UUID value() {
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

        JobID otherJobID = (JobID) other;
        return value.equals(otherJobID.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}