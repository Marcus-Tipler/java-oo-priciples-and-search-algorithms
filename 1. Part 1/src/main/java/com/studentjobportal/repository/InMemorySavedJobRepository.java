package com.studentjobportal.repository;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import com.studentjobportal.model.JobID;

// Store saved job id in memory
public final class InMemorySavedJobRepository
        implements SavedJobRepository {

    private final Set<JobID> savedJobIds = new LinkedHashSet<>();

    @Override
    public boolean save(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");
        return savedJobIds.add(jobId);
    }

    @Override
    public boolean remove(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");
        return savedJobIds.remove(jobId);
    }

    @Override
    public boolean contains(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");
        return savedJobIds.contains(jobId);
    }

    @Override
    public Set<JobID> findAll() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(savedJobIds));
    }
}