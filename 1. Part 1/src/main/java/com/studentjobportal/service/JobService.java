package com.studentjobportal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.search.JobSearchStrategy;


// Job view and search ops
public final class JobService {

    private final JobRepository jobRepository;
    private final JobSearchStrategy searchStrategy;

    public JobService(JobRepository jobRepository, JobSearchStrategy searchStrategy) {
        this.jobRepository = Objects.requireNonNull(jobRepository, "Job repository cannot be null");
        this.searchStrategy = Objects.requireNonNull(searchStrategy, "Search strategy cannot be null");
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(JobID jobID) {
        Objects.requireNonNull(jobID, "Job ID cannot be null");
        return jobRepository.findById(jobID).orElseThrow(() -> new JobNotFoundException(jobID));
    }

    public List<Job> searchJobs(String searchTerm) {
        List<Job> matches = new ArrayList<>();

        for (Job job : jobRepository.findAll()) {
            if (searchStrategy.matches(job, searchTerm)) {
                matches.add(job);
            }
        }

        return Collections.unmodifiableList(matches);
    }
}