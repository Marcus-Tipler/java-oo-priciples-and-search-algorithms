package com.studentjobportal.service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationID;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;


// allows for saving or applying to job without duplicates, reject new actions on non-existent job, and 
// preserves existing applications as historical records when jobs are removed.
public final class JobPortalService {

    private final JobRepository jobRepository;
    private final SavedJobRepository savedJobRepository;
    private final ApplicationRepository applicationRepository;
    private final Clock clock;

    public JobPortalService(JobRepository jobRepository, SavedJobRepository savedJobRepository, ApplicationRepository applicationRepository, Clock clock) {
        this.jobRepository = Objects.requireNonNull(jobRepository, "Job repository cannot be null");
        this.savedJobRepository = Objects.requireNonNull(savedJobRepository, "Saved-job repository cannot be null");
        this.applicationRepository = Objects.requireNonNull(applicationRepository, "Application repository cannot be null");
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public boolean saveJob(JobID JobID) {
        requireExistingJob(JobID);
        return savedJobRepository.save(JobID);
    }

    public boolean isSaved(JobID JobID) {
        return savedJobRepository.contains(JobID);
    }

    public List<Job> getSavedJobs() {
        List<Job> savedJobs = new ArrayList<>();

        for (JobID JobID : savedJobRepository.findAll()) {
            jobRepository.findById(JobID).ifPresent(savedJobs::add);
        }

        return savedJobs;
    }

    public Application applyForJob(JobID JobID) {
        requireExistingJob(JobID);

        if (applicationRepository.findByJobID(JobID).isPresent()) {throw new DuplicateApplicationException(JobID);}

        Application application = Application.builder()
                .id(ApplicationID.generate())
                .JobID(JobID)
                .status(ApplicationStatus.SUBMITTED)
                .submittedAt(Instant.now(clock))
                .build();

        if (!applicationRepository.save(application)) {throw new DuplicateApplicationException(JobID);}

        return application;
    }

    public List<Application> getApplications() {
        return applicationRepository.findAll();
    }

    private Job requireExistingJob(JobID JobID) {
        Objects.requireNonNull(JobID, "Job ID cannot be null");

        return jobRepository.findById(JobID)
                .orElseThrow(() -> new JobNotFoundException(JobID));
    }
}