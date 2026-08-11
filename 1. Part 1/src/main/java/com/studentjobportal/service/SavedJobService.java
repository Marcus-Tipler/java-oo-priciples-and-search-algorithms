package com.studentjobportal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.studentjobportal.exception.DuplicateSavedJobException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;


// allows for saving job without duplicates, reject new actions on non-existent job
public final class SavedJobService {

    private final JobRepository jobRepository;
    private final SavedJobRepository savedJobRepository;

    public SavedJobService(JobRepository jobRepository, SavedJobRepository savedJobRepository) {
        this.jobRepository = Objects.requireNonNull(jobRepository, "Job repository cannot be null");
        this.savedJobRepository = Objects.requireNonNull(savedJobRepository, "Saved-job repository cannot be null");
    }

    public void saveJob(JobID jobID) {
        requireExistingJob(jobID);

        if (!savedJobRepository.save(jobID)) {
            throw new DuplicateSavedJobException(jobID); // FIXME: please create file for this
        }
    }

    public boolean isSaved(JobID jobID) {
        Objects.requireNonNull(jobID, "Job ID cannot be null");
        return savedJobRepository.contains(jobID);
    }

    public List<Job> getSavedJobs() {
        List<Job> savedJobs = new ArrayList<>();

        for (JobID jobID : savedJobRepository.findAll()) {
            Job job = jobRepository.findById(jobID).orElseThrow(() -> new JobNotFoundException(jobID));
            savedJobs.add(job);
        }

        return Collections.unmodifiableList(savedJobs);
    }

    private Job requireExistingJob(JobID jobID) {
        Objects.requireNonNull(jobID, "Job ID cannot be null");
        return jobRepository.findById(jobID).orElseThrow(() -> new JobNotFoundException(jobID));
    }
}