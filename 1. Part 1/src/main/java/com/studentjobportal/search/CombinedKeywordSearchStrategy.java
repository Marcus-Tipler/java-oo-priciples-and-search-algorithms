package com.studentjobportal.search;

import java.util.Arrays;
import java.util.List;

import com.studentjobportal.model.Job;


// searches titles, companies, locations and job types
public final class CombinedKeywordSearchStrategy implements JobSearchStrategy {

    private final List<JobSearchStrategy> strategies = Arrays.asList(
        new TitleSearchStrategy(),
        new CompanySearchStrategy(),
        new LocationSearchStrategy(),
        new JobTypeSearchStrategy()
    );

    @Override
    public boolean matches(Job job, String searchTerm) {
        for (JobSearchStrategy strategy : strategies) {
            if (strategy.matches(job, searchTerm)) {
                return true;
            }
        }

        return false;
    }
}