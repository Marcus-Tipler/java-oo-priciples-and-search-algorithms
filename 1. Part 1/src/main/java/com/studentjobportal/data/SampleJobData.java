package com.studentjobportal.data;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import java.util.ArrayList;
import java.util.List;


// Init demo job data
public final class SampleJobData {

    private SampleJobData() {
    }

    public static List<Job> createJobs() {
        List<Job> jobs = new ArrayList<>();

        jobs.add(new Job(
                JobID.generate(),
                "Java Developer",
                "Tech Solutions Ltd",
                "Graduate",
                "Cheltenham"
        ));

        jobs.add(new Job(
                JobID.generate(),
                "Software Tester",
                "SecureApps UK",
                "Placement",
                "Bristol"
        ));

        return jobs;
    }
}