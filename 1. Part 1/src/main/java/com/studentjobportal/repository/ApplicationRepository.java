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

// store a max of 1 job per application
public final class ApplicationRepository {

    private final Map<JobID, Application> applicationsByJobID = new LinkedHashMap<>();

    public boolean save(Application application) {
        Objects.requireNonNull(application, "Application cannot be null");

        JobID JobID = application.getJobID();

        if (applicationsByJobID.containsKey(JobID)) {
            return false;
        }

        applicationsByJobID.put(JobID, application);
        return true;
    }

    public Optional<Application> findByJobID(JobID JobID) {
        Objects.requireNonNull(JobID, "Job ID cannot be null");

        return Optional.ofNullable(
            applicationsByJobID.get(JobID)
        );
    }

    public List<Application> findAll() {
        return Collections.unmodifiableList(
            new ArrayList<>(applicationsByJobID.values())
        );
    }
}