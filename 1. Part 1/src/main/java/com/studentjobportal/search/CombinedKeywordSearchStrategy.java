package com.studentjobportal.search;

import com.studentjobportal.model.Job;

import java.util.Arrays;
import java.util.List;


// searches titles, companies, locations and job types
public final class CombinedKeywordSearchStrategy
        implements JobSearchStrategy {

    private final List<JobSearchStrategy> strategies =
            Arrays.asList(
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