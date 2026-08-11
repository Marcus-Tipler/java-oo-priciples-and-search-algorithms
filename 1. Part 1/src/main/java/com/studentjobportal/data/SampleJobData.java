package com.studentjobportal.data;

import java.util.ArrayList;
import java.util.List;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;

// Init demo job data
public final class SampleJobData {

    private SampleJobData() {
    }

    public static List<Job> createJobs() {
        List<Job> jobs = new ArrayList<>();

        jobs.add(
            Job.builder()
            .id(JobID.generate())
            .title("Java Developer")
            .company("Tech Solutions Ltd")
            .jobType("Graduate")
            .location("Cheltenham")
            .build()
        );

        jobs.add(
            Job.builder()
            .id(JobID.generate())
            .title("Software Tester")
            .company("SecureApps UK")
            .jobType("Placement")
            .location("Bristol")
            .build()
        );

        return jobs;
    }
}