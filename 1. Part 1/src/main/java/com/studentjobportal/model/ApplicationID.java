package com.studentjobportal.model;

import java.util.Objects;
import java.util.UUID;

// Immutable id for a job application (maintain-ability)
public final class ApplicationID {

    private final UUID value;

    private ApplicationID(UUID value) {
        this.value = Objects.requireNonNull(value, "Application ID cannot be null");
    }

    public static ApplicationID generate() {
        return new ApplicationID(UUID.randomUUID());
    }

    public static ApplicationID from(String value) {
        Objects.requireNonNull(value, "Application ID value cannot be null");
        return new ApplicationID(UUID.fromString(value));
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

        if (!(other instanceof ApplicationID)) {
            return false;
        }

        ApplicationID otherId = (ApplicationID) other;
        return value.equals(otherId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}