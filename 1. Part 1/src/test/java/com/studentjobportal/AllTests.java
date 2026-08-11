package com.studentjobportal;

import com.studentjobportal.cli.JobPortalCliIntegrationTest;
import com.studentjobportal.model.ApplicationTest;
import com.studentjobportal.model.JobTest;
import com.studentjobportal.repository.InMemoryApplicationRepositoryTest;
import com.studentjobportal.repository.InMemoryJobRepositoryTest;
import com.studentjobportal.repository.InMemorySavedJobRepositoryTest;
import com.studentjobportal.search.JobSearchStrategyTest;
import com.studentjobportal.service.ApplicationServiceTest;
import com.studentjobportal.service.JobServiceTest;
import com.studentjobportal.service.SavedJobServiceTest;

public final class AllTests {

    private AllTests() {
    }

    public static void main(String[] args) {
        JobTest.main(new String[0]);
        ApplicationTest.main(new String[0]);

        InMemoryJobRepositoryTest.main(new String[0]);
        InMemorySavedJobRepositoryTest.main(new String[0]);
        InMemoryApplicationRepositoryTest.main(new String[0]);

        JobSearchStrategyTest.main(new String[0]);

        JobServiceTest.main(new String[0]);
        SavedJobServiceTest.main(new String[0]);
        ApplicationServiceTest.main(new String[0]);

        JobPortalCliIntegrationTest.main(new String[0]);

        System.out.println();
        System.out.println("==============================");
        System.out.println("ALL TEST SUITES PASSED");
        System.out.println("==============================");
    }
}