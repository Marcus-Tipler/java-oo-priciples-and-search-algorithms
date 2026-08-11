package com.studentjobportal.search;

import com.studentjobportal.model.Job;

public final class JobTypeSearchStrategy implements JobSearchStrategy {

    @Override
    public boolean matches(Job job, String searchTerm) {
        return SearchSupport.contains(Job::getJobType, job, searchTerm);
    }
}