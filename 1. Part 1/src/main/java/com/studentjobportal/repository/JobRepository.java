package com.studentjobportal.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;

// Memory for available jobs
public final class JobRepository {

    private final List<Job> jobs = new ArrayList<>();

    public JobRepository(Collection<Job> initialJobs) {
        Objects.requireNonNull(
            initialJobs,
            "Initial jobs cannot be null"
        );

        for (Job job : initialJobs) {
            add(job);
        }
    }

    public void add(Job job) {
        Objects.requireNonNull(job, "Job cannot be null");

        if (findById(job.getId()).isPresent()) {
            throw new IllegalArgumentException(
                "A job with ID " + job.getId() + " already exists"
            );
        }

        jobs.add(job);
    }

    public Optional<Job> findById(JobID id) {
        Objects.requireNonNull(id, "Job ID cannot be null");

        for (Job job : jobs) {
            if (job.getId().equals(id)) {
                return Optional.of(job);
            }
        }

        return Optional.empty();
    }

    public List<Job> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(jobs));
    }

    public boolean remove(JobID id) {
        Objects.requireNonNull(id, "Job ID cannot be null");
        return jobs.removeIf(job -> job.getId().equals(id));
    }

}