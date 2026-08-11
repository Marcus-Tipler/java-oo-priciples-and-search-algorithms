package com.studentjobportal.search;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.studentjobportal.exception.JobValidationException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;

/**
 * Verifies each search strategy's field selection and their shared matching
 * rules.
 */
final class JobSearchStrategyTest {

    private static final Job JOB = Job.builder()
            .id(JobID.from("d61b9fa8-c23c-43af-b60c-3903512c8d01"))
            .title("Java Developer")
            .company("Tech Solutions Ltd")
            .jobType("Graduate")
            .location("Cheltenham")
            .build();

    @Test
    void titleStrategySearchesOnlyTitle() {
        JobSearchStrategy strategy = new TitleSearchStrategy();

        assertTrue(strategy.matches(JOB, "Java"));
        assertFalse(strategy.matches(JOB, "Tech Solutions"));
    }

    @Test
    void companyStrategySearchesOnlyCompany() {
        JobSearchStrategy strategy = new CompanySearchStrategy();

        assertTrue(strategy.matches(JOB, "Solutions"));
        assertFalse(strategy.matches(JOB, "Cheltenham"));
    }

    @Test
    void locationStrategySearchesOnlyLocation() {
        JobSearchStrategy strategy = new LocationSearchStrategy();

        assertTrue(strategy.matches(JOB, "Cheltenham"));
        assertFalse(strategy.matches(JOB, "Graduate"));
    }

    @Test
    void jobTypeStrategySearchesOnlyJobType() {
        JobSearchStrategy strategy = new JobTypeSearchStrategy();

        assertTrue(strategy.matches(JOB, "Graduate"));
        assertFalse(strategy.matches(JOB, "Developer"));
    }

    @Test
    void combinedStrategySearchesEveryField() {
        JobSearchStrategy strategy = new CombinedKeywordSearchStrategy();

        assertTrue(strategy.matches(JOB, "Developer"));
        assertTrue(strategy.matches(JOB, "Tech Solutions"));
        assertTrue(strategy.matches(JOB, "Graduate"));
        assertTrue(strategy.matches(JOB, "Cheltenham"));
        assertFalse(strategy.matches(JOB, "Bristol"));
    }

    @Test
    void matchingIsCaseInsensitiveAndSupportsPartialTerms() {
        JobSearchStrategy strategy = new CombinedKeywordSearchStrategy();

        assertTrue(strategy.matches(JOB, "JAVA"));
        assertTrue(strategy.matches(JOB, "tech solutions"));
        assertTrue(strategy.matches(JOB, "chel"));
    }

    @Test
    void blankTermsMatchNothing() {
        JobSearchStrategy strategy = new CombinedKeywordSearchStrategy();

        assertFalse(strategy.matches(JOB, ""));
        assertFalse(strategy.matches(JOB, "   "));
    }

    @Test
    void nullTermsAreRejected() {
        JobSearchStrategy strategy = new CombinedKeywordSearchStrategy();

        assertThrows(JobValidationException.class,
                () -> strategy.matches(JOB, null));
    }
}
