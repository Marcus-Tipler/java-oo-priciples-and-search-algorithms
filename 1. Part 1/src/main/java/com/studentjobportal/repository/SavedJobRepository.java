package com.studentjobportal.repository;

import java.util.Set;

import com.studentjobportal.model.JobID;


// Saves jobs by ID and prevents same job being saved twice
public interface SavedJobRepository {
    boolean save(JobID jobId);
    boolean remove(JobID jobId);
    boolean contains(JobID jobId);
    Set<JobID> findAll();
}