package com.studentjobportal.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.studentjobportal.model.Application;
import com.studentjobportal.model.JobID;

// Stores 1 application per job in memory
public final class InMemoryApplicationRepository implements ApplicationRepository {

    private final Map<JobID, Application> applicationsByJobId = new LinkedHashMap<>();

    @Override
    public boolean save(Application application) {Objects.requireNonNull(application, "Application cannot be null");

        JobID jobId = application.getJobID();

        if (applicationsByJobId.containsKey(jobId)) {
            return false;
        }

        applicationsByJobId.put(jobId, application);
        return true;
    }

    @Override
    public Optional<Application> findByJobId(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");
        return Optional.ofNullable(applicationsByJobId.get(jobId));
    }

    @Override
    public List<Application> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(applicationsByJobId.values()));
    }
}