package com.studentjobportal.repository;

import java.util.List;
import java.util.Optional;

import com.studentjobportal.model.Application;
import com.studentjobportal.model.JobID;


// store a max of 1 job per application
public interface ApplicationRepository {
    boolean save(Application application);
    Optional<Application> findByJobId(JobID jobId);
    List<Application> findAll();
}