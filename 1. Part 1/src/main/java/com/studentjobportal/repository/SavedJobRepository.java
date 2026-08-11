package com.studentjobportal.repository;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import com.studentjobportal.model.JobID;


// Saves jobs by ID and prevents same job being saved twice
public final class SavedJobRepository {

    private final Set<JobID> savedJobIDs = new LinkedHashSet<>();

    public boolean save(JobID JobID) {
        Objects.requireNonNull(JobID, "Job ID cannot be null");
        return savedJobIDs.add(JobID);
    }

    public boolean remove(JobID JobID) {
        Objects.requireNonNull(JobID, "Job ID cannot be null");
        return savedJobIDs.remove(JobID);
    }

    public boolean contains(JobID JobID) {
        Objects.requireNonNull(JobID, "Job ID cannot be null");
        return savedJobIDs.contains(JobID);
    }

    public Set<JobID> findAll() {
        return Collections.unmodifiableSet(
            new LinkedHashSet<>(savedJobIDs)
        );
    }
}