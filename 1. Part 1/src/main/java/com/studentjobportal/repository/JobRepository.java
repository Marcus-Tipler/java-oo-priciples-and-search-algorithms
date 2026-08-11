package com.studentjobportal.repository;

import java.util.List;
import java.util.Optional;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;


// Memory for available jobs
public interface JobRepository {
    List<Job> findAll();
    Optional<Job> findById(JobID jobId);
    void save(Job job);
    boolean deleteById(JobID jobId);
}