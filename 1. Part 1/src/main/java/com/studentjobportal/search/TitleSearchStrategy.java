package com.studentjobportal.search;

import com.studentjobportal.model.Job;

public final class TitleSearchStrategy implements JobSearchStrategy {

    @Override
    public boolean matches(Job job, String searchTerm) {
        return SearchSupport.contains(Job::getTitle, job, searchTerm);
    }
}