package com.studentjobportal.model;

import java.util.Objects;

// Immutable descriptions for jobs where 
// equality is based on the immutable job ID.
public final class Job {

    // private final fields instead of public static.
    private final JobID id;
    private final String title;
    private final String company;
    private final String jobType;
    private final String location;

    // single list for job access
    public Job(
            JobID id,
            String title,
            String company,
            String jobType,
            String location) {

        this.id = Objects.requireNonNull(id, "Job ID cannot be null");
        this.title = requireText(title, "Job title");
        this.company = requireText(company, "Company");
        this.jobType = requireText(jobType, "Job type");
        this.location = requireText(location, "Location");
    }

    // accessor methods
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

    private static String requireText(String value, String fieldName) {
        Objects.requireNonNull(value, fieldName + " cannot be null");

        String trimmedValue = value.trim();
        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }

        return trimmedValue;
    }
}