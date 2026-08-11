package com.studentjobportal.data;

import java.util.Objects;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.JobRepository;

// Demo job data moved here
public final class SampleJobDataSeeder {

    private SampleJobDataSeeder() {
    }

    public static void seed(JobRepository jobRepository) {
        Objects.requireNonNull(jobRepository, "Job repository cannot be null");

        jobRepository.save(Job.builder()
            .id(JobID.from("f46a50d4-0e92-43e8-b57d-a6463cbd5cc4"))
            .title("Java Developer")
            .company("Tech Solutions Ltd")
            .jobType("Graduate")
            .location("Cheltenham")
            .build()
        );

        jobRepository.save(Job.builder()
            .id(JobID.from("186dbb7e-7f5e-4fd9-bdce-6b018bd345c8"))
            .title("Software Tester")
            .company("SecureApps UK")
            .jobType("Placement")
            .location("Bristol")
            .build()
        );
    }
}