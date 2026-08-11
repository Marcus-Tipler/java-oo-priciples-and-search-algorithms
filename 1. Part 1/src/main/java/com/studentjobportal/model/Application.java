package com.studentjobportal.model;

import java.time.Instant;
import java.util.Objects;

// Immutable application record where 
// equality is based on the immutable application ID. 
// (maintain-ability)
public final class Application {

    private final ApplicationID id;
    private final JobID JobID;
    private final ApplicationStatus status;
    private final Instant submittedAt;

    private Application(
        ApplicationID id,
        JobID JobID,
        ApplicationStatus status,
        Instant submittedAt) {

        this.id = id;
        this.JobID = JobID;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ApplicationID getId() {
        return id;
    }

    public JobID getJobID() {
        return JobID;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    @Override
    public String toString() {
        return "Application{" + "id=" + id + ", JobID=" + JobID + ", status=" + status + ", submittedAt=" + submittedAt + '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Application)) {
            return false;
        }

        Application otherApplication = (Application) other;
        return id.equals(otherApplication.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static final class Builder {

        private ApplicationID id;
        private JobID JobID;
        private ApplicationStatus status;
        private Instant submittedAt;

        private Builder() {
        }

        public Builder id(ApplicationID id) {
            this.id = id;
            return this;
        }

        public Builder JobID(JobID JobID) {
            this.JobID = JobID;
            return this;
        }

        public Builder status(ApplicationStatus status) {
            this.status = status;
            return this;
        }

        public Builder submittedAt(Instant submittedAt) {
            this.submittedAt = submittedAt;
            return this;
        }

        public Application build() {
            return new Application(
                Objects.requireNonNull(id, "Application ID cannot be null"),
                Objects.requireNonNull(JobID, "Job ID cannot be null"),
                Objects.requireNonNull(status, "Application status cannot be null"),
                Objects.requireNonNull(submittedAt, "Submission time cannot be null")
            );
        }
    }
}