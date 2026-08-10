package com.studentjobportal;

import com.studentjobportal.cli.StudentJobPortalCli;
import com.studentjobportal.data.SampleJobData;
import com.studentjobportal.model.Job;
import java.util.List;
import java.util.Scanner;

// Starting point with little to no responsibility
public final class StudentJobPortal {

    private StudentJobPortal() {
    }

    public static void main(String[] args) {
        List<Job> jobs = SampleJobData.createJobs();

        try (Scanner scanner = new Scanner(System.in)) {
            StudentJobPortalCli application =
                    new StudentJobPortalCli(scanner, jobs);

            application.run();
        }
    }
}