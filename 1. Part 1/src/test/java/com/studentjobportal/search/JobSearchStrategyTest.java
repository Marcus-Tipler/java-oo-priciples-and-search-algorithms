package com.studentjobportal.search;

import static com.studentjobportal.TestAssertions.assertFalse;
import static com.studentjobportal.TestAssertions.assertThrows;
import static com.studentjobportal.TestAssertions.assertTrue;
import com.studentjobportal.exception.JobValidationException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;

public final class JobSearchStrategyTest {

    private static final Job JOB = Job.builder()
            .id(JobID.from(
                    "d61b9fa8-c23c-43af-b60c-3903512c8d01"
            ))
            .title("Java Developer")
            .company("Tech Solutions Ltd")
            .jobType("Graduate")
            .location("Cheltenham")
            .build();

    public static void main(String[] args) {
        titleStrategySearchesTitle();
        companyStrategySearchesCompany();
        locationStrategySearchesLocation();
        jobTypeStrategySearchesJobType();
        combinedStrategySearchesEveryField();
        searchesAreCaseInsensitive();
        partialTermsAreSupported();
        blankTermsMatchNothing();
        nullTermsAreRejected();

        System.out.println(
                "All search strategy tests passed."
        );
    }

    private static void titleStrategySearchesTitle() {
        JobSearchStrategy strategy =
                new TitleSearchStrategy();

        assertTrue(strategy.matches(JOB, "Java"));
        assertFalse(strategy.matches(JOB, "Tester"));
    }

    private static void companyStrategySearchesCompany() {
        JobSearchStrategy strategy =
                new CompanySearchStrategy();

        assertTrue(strategy.matches(JOB, "Solutions"));
        assertFalse(strategy.matches(JOB, "SecureApps"));
    }

    private static void locationStrategySearchesLocation() {
        JobSearchStrategy strategy =
                new LocationSearchStrategy();

        assertTrue(strategy.matches(JOB, "Cheltenham"));
        assertFalse(strategy.matches(JOB, "Bristol"));
    }

    private static void jobTypeStrategySearchesJobType() {
        JobSearchStrategy strategy =
                new JobTypeSearchStrategy();

        assertTrue(strategy.matches(JOB, "Graduate"));
        assertFalse(strategy.matches(JOB, "Placement"));
    }

    private static void combinedStrategySearchesEveryField() {
        JobSearchStrategy strategy =
                new CombinedKeywordSearchStrategy();

        assertTrue(strategy.matches(JOB, "Developer"));
        assertTrue(strategy.matches(JOB, "Tech Solutions"));
        assertTrue(strategy.matches(JOB, "Graduate"));
        assertTrue(strategy.matches(JOB, "Cheltenham"));
        assertFalse(strategy.matches(JOB, "Bristol"));
    }

    private static void searchesAreCaseInsensitive() {
        JobSearchStrategy strategy =
                new CombinedKeywordSearchStrategy();

        assertTrue(strategy.matches(JOB, "JAVA"));
        assertTrue(strategy.matches(JOB, "tech solutions"));
        assertTrue(strategy.matches(JOB, "CHELTENHAM"));
    }

    private static void partialTermsAreSupported() {
        JobSearchStrategy strategy =
                new CombinedKeywordSearchStrategy();

        assertTrue(strategy.matches(JOB, "jav"));
        assertTrue(strategy.matches(JOB, "chel"));
        assertTrue(strategy.matches(JOB, "solut"));
    }

    private static void blankTermsMatchNothing() {
        JobSearchStrategy strategy =
                new CombinedKeywordSearchStrategy();

        assertFalse(strategy.matches(JOB, ""));
        assertFalse(strategy.matches(JOB, "   "));
    }

    private static void nullTermsAreRejected() {
        JobSearchStrategy strategy =
                new CombinedKeywordSearchStrategy();

        assertThrows(
                JobValidationException.class,
                () -> strategy.matches(JOB, null)
        );
    }
}