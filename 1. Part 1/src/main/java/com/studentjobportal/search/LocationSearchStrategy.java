package com.studentjobportal.search;

import com.studentjobportal.model.Job;

public final class LocationSearchStrategy implements JobSearchStrategy {

    @Override
    public boolean matches(Job job, String searchTerm) {
        return SearchSupport.contains(Job::getLocation, job, searchTerm);
    }
}