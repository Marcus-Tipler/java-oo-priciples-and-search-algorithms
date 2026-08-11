package com.studentjobportal.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;

// Store jobs in memory permanently
public final class InMemoryJobRepository implements JobRepository {

    private final List<Job> jobs = new ArrayList<>();

    @Override
    public List<Job> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(jobs));
    }

    @Override
    public Optional<Job> findById(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");

        for (Job job : jobs) {
            if (job.getId().equals(jobId)) {
                return Optional.of(job);
            }
        }

        return Optional.empty();
    }

    @Override
    public void save(Job job) {
        Objects.requireNonNull(job, "Job cannot be null");

        if (findById(job.getId()).isPresent()) {
            throw new IllegalArgumentException("A job with ID " + job.getId() + " already exists");
        }

        jobs.add(job);
    }

    @Override
    public boolean deleteById(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");
        return jobs.removeIf(job -> job.getId().equals(jobId));
    }
}