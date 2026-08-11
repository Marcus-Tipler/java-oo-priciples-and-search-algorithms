package com.studentjobportal.search;

import java.util.Locale;
import java.util.Objects;

import com.studentjobportal.exception.JobValidationException;

final class SearchSupport {

    private SearchSupport() {
        
    }

    static boolean contains(JobFieldAccessor fieldAccessor, com.studentjobportal.model.Job job, String searchTerm) {

        Objects.requireNonNull(job, "Job cannot be null");

        if (searchTerm == null) {
            throw new JobValidationException("Search term cannot be null");
        }

        String normalisedTerm = searchTerm.trim().toLowerCase(Locale.ROOT);

        if (normalisedTerm.isEmpty()) {
            return false;
        }

        return fieldAccessor.get(job).toLowerCase(Locale.ROOT).contains(normalisedTerm);
    }

    interface JobFieldAccessor {
        String get(com.studentjobportal.model.Job job);
    }
}