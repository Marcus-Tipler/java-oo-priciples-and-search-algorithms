package com.studentjobportal.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationID;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.JobRepository;


// Allows for applying to one job without duplicates, application stays if job gets removed.
public final class ApplicationService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final Clock clock;

    public ApplicationService(JobRepository jobRepository, ApplicationRepository applicationRepository, Clock clock) {
        this.jobRepository = Objects.requireNonNull(jobRepository, "Job repository cannot be null");
        this.applicationRepository = Objects.requireNonNull(applicationRepository, "Application repository cannot be null");
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
    }

    public Application applyForJob(JobID jobID) {
        requireExistingJob(jobID);

        if (applicationRepository.findByJobId(jobID).isPresent()) {
            throw new DuplicateApplicationException(jobID);
        }

        Application application = Application.builder()
            .id(ApplicationID.generate())
            .JobID(jobID)
            .status(ApplicationStatus.SUBMITTED)
            .submittedAt(Instant.now(clock))
            .build();

        if (!applicationRepository.save(application)) {
            throw new DuplicateApplicationException(jobID);
        }

        return application;
    }

    public List<Application> getApplications() {
        return applicationRepository.findAll();
    }

    private void requireExistingJob(JobID jobID) {
        Objects.requireNonNull(jobID, "Job ID cannot be null");

        if (!jobRepository.findById(jobID).isPresent()) {
            throw new JobNotFoundException(jobID);
        }
    }
}