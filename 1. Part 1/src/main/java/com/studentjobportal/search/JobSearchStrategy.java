package com.studentjobportal.search;

import com.studentjobportal.model.Job;

// Replace-able job matching algorithm
// Blank = nothing.

public interface JobSearchStrategy {
    boolean matches(Job job, String searchTerm);
}