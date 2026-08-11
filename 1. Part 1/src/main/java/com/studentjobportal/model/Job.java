package com.studentjobportal.model;

import com.studentjobportal.exception.JobValidationException;

// Immutable descriptions for jobs where 
// equality is based on the immutable job ID. 
// Jobs now created through builder (maintain-ability)
public final class Job {

    // private final fields instead of public static.
    private final JobID id;
    private final String title;
    private final String company;
    private final String jobType;
    private final String location;

    // single list for job access
    private Job(
            JobID id,
            String title,
            String company,
            String jobType,
            String location) {

        this.id = id;
        this.title = title;
        this.company = company;
        this.jobType = jobType;
        this.location = location;
    }

    // accessor methods
    public static Builder builder() {
        return new Builder();
    }

    public JobID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getJobType() {
        return jobType;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", company='" + company + '\'' +
                ", jobType='" + jobType + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Job)) {
            return false;
        }

        Job otherJob = (Job) other;
        return id.equals(otherJob.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    // Build validated job object.
    public static final class Builder {

        private JobID id;
        private String title;
        private String company;
        private String jobType;
        private String location;

        private Builder() {
        }

        public Builder id(JobID id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder jobType(String jobType) {
            this.jobType = jobType;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Job build() {
            if (id == null) {
                throw new JobValidationException("Job ID cannot be null");
            }

            return new Job(
                id,
                requireText(title, "Job title"),
                requireText(company, "Company"),
                requireText(jobType, "Job type"),
                requireText(location, "Location")
            );
        }

        private static String requireText(String value, String fieldName) {

            if (value == null) {
                throw new JobValidationException(fieldName + " cannot be null");
            }

            String trimmedValue = value.trim();

            if (trimmedValue.isEmpty()) {
                throw new JobValidationException(fieldName + " cannot be blank");
            }

            return trimmedValue;
        }
    }
}