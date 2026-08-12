# Source‑code dump

## aiSummarizer.py

| Property | Value |
|---|---|
| Type | Python |
| Size (bytes) | 6514 |
| Lines | 195 |
| First line | !/usr/bin/env python3 |

```python
#!/usr/bin/env python3
"""
Collect source files (Python, HTML, JavaScript, CSS) from the current
directory tree and write them – together with a tiny summary – to a
single Markdown document.

Each file appears as

    ## relative/path/to/file.ext

    | Property      | Value                         |
    |---------------|------------------------------|
    | Type          | Python / HTML / …            |
    | Size (bytes)  | 1234                         |
    | Lines         | 56                           |
    | First line    | <first non‑blank line>       |

    ```<lang>
    <file contents>
    ```
"""

from __future__ import annotations

import argparse
import pathlib
import sys
from typing import Dict, List, Tuple

# ----------------------------------------------------------------------
# Configuration – you can tweak these constants if you want
# ----------------------------------------------------------------------
# Extensions we care about (lower‑case, no leading dot)
EXTENSIONS = {".py", ".html", ".htm", ".js", ".css", ".java"}

# Mapping from extension → fence language for Markdown
FENCE_LANG: Dict[str, str] = {
    ".py": "python",
    ".html": "html",
    ".htm": "html",
    ".js": "javascript",
    ".css": "css",
    ".java": "java"
}

# Directory name fragments that should be ignored.  The check is
# case‑insensitive and matches *any* part that contains the fragment.
IGNORE_DIRS = {"venv", ".venv", "node_modules", "__pycache__"}


# ----------------------------------------------------------------------
# Helper functions
# ----------------------------------------------------------------------
def is_ignored(p: pathlib.Path) -> bool:
    """
    Return ``True`` if *any* component of ``p`` contains (case‑insensitively)
    one of the strings listed in ``IGNORE_DIRS``.
    """
    lowered_parts = (part.lower() for part in p.parts)
    for part in lowered_parts:
        for ignored in IGNORE_DIRS:
            if ignored in part:        # substring match → ignore
                return True
    return False


def find_files(root: pathlib.Path) -> List[pathlib.Path]:
    """Return a sorted list of files under ``root`` that match ``EXTENSIONS``."""
    files = [
        p
        for p in root.rglob("*")
        if p.is_file()
        and p.suffix.lower() in EXTENSIONS
        and not is_ignored(p)
    ]
    return sorted(files)


def first_meaningful_line(text: str) -> str:
    """
    Return the first non‑blank line that isn’t just a comment delimiter.
    If the file is empty return ``<empty>``.
    """
    for line in text.splitlines():
        stripped = line.strip()
        if not stripped:
            continue
        # Trim common comment prefixes so the table looks cleaner
        for prefix in ("#", "//", "<!--", "-->"):
            if stripped.startswith(prefix):
                stripped = stripped[len(prefix) :].strip()
        return stripped or "<blank>"
    return "<empty>"


def summarize(p: pathlib.Path) -> Tuple[str, List[Tuple[str, str]], str]:
    """
    Build a small summary for ``p``.
    Returns a tuple ``(type_name, rows, content)`` where *rows* is
    a list of ``(header, value)`` pairs ready to be rendered as a
    Markdown table, and *content* is the file’s raw text.
    """
    suffix = p.suffix.lower()
    type_name = {
        ".py": "Python",
        ".html": "HTML",
        ".htm": "HTML",
        ".js": "JavaScript",
        ".css": "CSS",
    }.get(suffix, "Unknown")

    # Read the whole file once – we need it for the fence anyway.
    # Errors fall back to an empty string so the script never crashes.
    try:
        content = p.read_text(encoding="utf-8")
    except Exception as exc:  # pragma: no cover
        content = ""
        sys.stderr.write(f"⚠️  Could not read {p}: {exc}\n")

    size = p.stat().st_size
    lines = content.count("\n") + (0 if content.endswith("\n") else 1)

    rows = [
        ("Type", type_name),
        ("Size (bytes)", str(size)),
        ("Lines", str(lines)),
        ("First line", first_meaningful_line(content).replace("|", r"\|")),
    ]
    return type_name, rows, content


def markdown_table(rows: List[Tuple[str, str]]) -> str:
    """Render ``rows`` as a simple GitHub‑flavoured Markdown table."""
    lines = ["| Property | Value |", "|---|---|"]
    for key, value in rows:
        lines.append(f"| {key} | {value} |")
    return "\n".join(lines) + "\n"


# ----------------------------------------------------------------------
# Core routine
# ----------------------------------------------------------------------
def build_markdown(
    out_path: pathlib.Path = pathlib.Path("ai_sum.md"),
    root: pathlib.Path = pathlib.Path.cwd(),
) -> None:
    """Collect source files and write the markdown document."""
    out_path.parent.mkdir(parents=True, exist_ok=True)

    files = find_files(root)

    with out_path.open("w", encoding="utf-8") as md:
        md.write("# Source‑code dump\n\n")
        for p in files:
            rel_path = p.relative_to(root)

            # ---- heading -------------------------------------------------
            md.write(f"## {rel_path}\n\n")

            # ---- summary -------------------------------------------------
            _, summary_rows, content = summarize(p)
            md.write(markdown_table(summary_rows))
            md.write("\n")

            # ---- fenced source -------------------------------------------
            fence = FENCE_LANG.get(p.suffix.lower(), "")
            md.write(f"```{fence}\n")
            md.write(content.rstrip("\n"))  # keep original line endings
            md.write("\n```\n\n")


# ----------------------------------------------------------------------
# CLI entry point
# ----------------------------------------------------------------------
def parse_cli() -> pathlib.Path:
    parser = argparse.ArgumentParser(
        description=(
            "Collect *.py, *.html, *.js and *.css files into a single markdown file. "
            "Directories whose name contains 'venv' (e.g. venv, my-venv) are ignored."
        ),
        formatter_class=argparse.ArgumentDefaultsHelpFormatter,
    )
    parser.add_argument(
        "output",
        nargs="?",
        default="ai_sum.md",
        help="Path of the markdown file to create (will be overwritten).",
    )
    args = parser.parse_args()
    return pathlib.Path(args.output)


if __name__ == "__main__":
    output_path = parse_cli()
    build_markdown(output_path)
```

## src/main/java/com/studentjobportal/StudentJobPortal.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 1904 |
| Lines | 43 |
| First line | package com.studentjobportal; |

```java
package com.studentjobportal;

import java.time.Clock;
import java.util.Scanner;

import com.studentjobportal.cli.JobPortalCli;
import com.studentjobportal.data.SampleJobDataSeeder;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.InMemoryApplicationRepository;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.InMemorySavedJobRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;
import com.studentjobportal.search.CombinedKeywordSearchStrategy;
import com.studentjobportal.search.JobSearchStrategy;
import com.studentjobportal.service.ApplicationService;
import com.studentjobportal.service.JobService;
import com.studentjobportal.service.SavedJobService;

// Starting point with little to no responsibility
public final class StudentJobPortal {

    private StudentJobPortal() {
    }

    public static void main(String[] args) { 
        JobRepository jobRepository = new InMemoryJobRepository();
        SavedJobRepository savedJobRepository = new InMemorySavedJobRepository();
        ApplicationRepository applicationRepository = new InMemoryApplicationRepository();

        SampleJobDataSeeder.seed(jobRepository);

        JobSearchStrategy searchStrategy = new CombinedKeywordSearchStrategy();
        JobService jobService = new JobService(jobRepository, searchStrategy);
        SavedJobService savedJobService = new SavedJobService(jobRepository, savedJobRepository);
        ApplicationService applicationService = new ApplicationService(jobRepository, applicationRepository, Clock.systemUTC());

        try (Scanner scanner = new Scanner(System.in)) {
            JobPortalCli cli = new JobPortalCli(scanner, System.out, jobService, savedJobService, applicationService);
            cli.run();
        }
    }
}
```

## src/main/java/com/studentjobportal/cli/JobPortalCli.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 9239 |
| Lines | 297 |
| First line | package com.studentjobportal.cli; |

```java
package com.studentjobportal.cli;

import java.io.PrintStream;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.DuplicateSavedJobException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.Job;
import com.studentjobportal.service.ApplicationService;
import com.studentjobportal.service.JobService;
import com.studentjobportal.service.SavedJobService;


// CLI user interaction
public final class JobPortalCli {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM uuuu HH:mm 'UTC'", Locale.UK).withZone(ZoneOffset.UTC);

    private final Scanner input;
    private final PrintStream output;
    private final JobService jobService;
    private final SavedJobService savedJobService;
    private final ApplicationService applicationService;

    public JobPortalCli(Scanner input, PrintStream output, JobService jobService, SavedJobService savedJobService, ApplicationService applicationService) {
        this.input = Objects.requireNonNull(input, "Input cannot be null");
        this.output = Objects.requireNonNull(output, "Output cannot be null");
        this.jobService = Objects.requireNonNull(jobService, "Job service cannot be null");
        this.savedJobService = Objects.requireNonNull(savedJobService, "Saved-job service cannot be null");
        this.applicationService = Objects.requireNonNull(applicationService, "Application service cannot be null");
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();

            String inputLine = readLine();

            if (inputLine == null) {
                return;
            }

            Integer choice = parseNumber(inputLine);

            if (choice == null) {
                output.println("Please enter a valid whole number.");
                continue;
            }

            if (choice < 1 || choice > 7) {
                output.println("Please select an option between 1 and 7.");
                continue;
            }

            switch (choice) {
                case 1:
                    handleViewAllJobs();
                    break;
                case 2:
                    handleSearchJobs();
                    break;
                case 3:
                    handleSaveJob();
                    break;
                case 4:
                    handleViewSavedJobs();
                    break;
                case 5:
                    handleApplyForJob();
                    break;
                case 6:
                    handleViewApplications();
                    break;
                case 7:
                    running = false;
                    break;
                default:
                    throw new IllegalStateException("Unexpected menu option: " + choice);
            }
        }
    }

    private void printMenu() {
        output.println();
        output.println("1. View all jobs");
        output.println("2. Search jobs");
        output.println("3. Save a job");
        output.println("4. View saved jobs");
        output.println("5. Apply for a job");
        output.println("6. View applications");
        output.println("7. Exit");
        output.print("Select option: ");
    }

    private void handleViewAllJobs() {
        List<Job> jobs = jobService.getAllJobs();

        if (jobs.isEmpty()) {
            output.println("No jobs are currently available.");
            return;
        }

        output.println();
        output.println("Available jobs:");
        printJobs(jobs);
    }

    private void handleSearchJobs() {
        output.print("Enter keyword: ");

        String searchTerm = readLine();

        if (searchTerm == null) {
            return;
        }

        if (searchTerm.trim().isEmpty()) {
            output.println("Search keyword cannot be blank.");
            return;
        }

        List<Job> matches = jobService.searchJobs(searchTerm);

        if (matches.isEmpty()) {
            output.println("No matching jobs found.");
            return;
        }

        output.println();
        output.println("Search results:");
        printJobs(matches);
    }

    private void handleSaveJob() {
        Job job = selectJob("Enter job number to save: ");

        if (job == null) {
            return;
        }

        try {
            savedJobService.saveJob(job.getId());
            output.println("Job saved: " + job.getTitle());
        } catch (DuplicateSavedJobException exception) {
            output.println("That job is already saved.");
        } catch (JobNotFoundException exception) {
            output.println(exception.getMessage());
        }
    }

    private void handleViewSavedJobs() {
        try {
            List<Job> savedJobs = savedJobService.getSavedJobs();

            if (savedJobs.isEmpty()) {
                output.println("You have no saved jobs.");
                return;
            }

            output.println();
            output.println("Saved jobs:");
            printJobs(savedJobs);
        } catch (JobNotFoundException exception) {
            output.println(exception.getMessage());
        }
    }

    private void handleApplyForJob() {
        Job job = selectJob("Enter job number to apply for: ");

        if (job == null) {
            return;
        }

        try {
            applicationService.applyForJob(job.getId());
            output.println("Application submitted for " + job.getTitle() + ".");
        } catch (DuplicateApplicationException exception) {
            output.println("You have already applied for that job.");
        } catch (JobNotFoundException exception) {
            output.println(exception.getMessage());
        }
    }

    private void handleViewApplications() {
        List<Application> applications = applicationService.getApplications();

        if (applications.isEmpty()) {
            output.println("You have no applications.");
            return;
        }

        output.println();
        output.println("Applications:");

        for (int index = 0; index < applications.size(); index++) {
            printApplication(index + 1, applications.get(index));
        }
    }

    private Job selectJob(String prompt) {
        List<Job> jobs = jobService.getAllJobs();

        if (jobs.isEmpty()) {
            output.println("No jobs are currently available.");
            return null;
        }

        output.print(prompt);
        String inputLine = readLine();

        if (inputLine == null) {
            return null;
        }

        Integer jobNumber = parseNumber(inputLine);

        if (jobNumber == null) {
            output.println("Please enter a valid whole number.");
            return null;
        }

        if (jobNumber < 1 || jobNumber > jobs.size()) {
            output.println("Job number must be between 1 and " + jobs.size() + ".");
            return null;
        }

        return jobs.get(jobNumber - 1);
    }

    private void printJobs(List<Job> jobs) {
        for (int index = 0; index < jobs.size(); index++) {
            printJob(index + 1, jobs.get(index));
        }
    }

    private void printJob(int number, Job job) {
        output.println(number + ". " + job.getTitle());
        output.println("   Company: " + job.getCompany());
        output.println("   Type: " + job.getJobType());
        output.println("   Location: " + job.getLocation());

        if (savedJobService.isSaved(job.getId())) {
            output.println("   Saved: Yes");
        }

        output.println();
    }

    private void printApplication(int number, Application application) {
        output.println(number + ". Application");

        try {
            Job job = jobService.getJobById(application.getJobID());
            output.println("   Job: " + job.getTitle());
            output.println("   Company: " + job.getCompany());
        } catch (JobNotFoundException exception) {
            output.println("   Job: No longer available");
        }

        output.println("   Job ID: " + application.getJobID());
        output.println("   Status: " + formatStatus(application.getStatus()));
        output.println("   Submitted: " + DATE_FORMAT.format(application.getSubmittedAt()));
        output.println();
    }

    private String formatStatus(ApplicationStatus status) {
        String lowerCaseStatus = status.name().toLowerCase(Locale.ROOT);
        return Character.toUpperCase(lowerCaseStatus.charAt(0) ) + lowerCaseStatus.substring(1);
    }

    private Integer parseNumber(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException exception) {
            return null;
        }
    }

    private String readLine() {
        if (!input.hasNextLine()) {
            return null;
        }

        return input.nextLine();
    }
}
```

## src/main/java/com/studentjobportal/cli/package-info.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 58 |
| Lines | 2 |
| First line | CLI user interaction |

```java
// CLI user interaction
package com.studentjobportal.cli;
```

## src/main/java/com/studentjobportal/data/SampleJobDataSeeder.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 1033 |
| Lines | 36 |
| First line | package com.studentjobportal.data; |

```java
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
```

## src/main/java/com/studentjobportal/data/package-info.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 67 |
| Lines | 2 |
| First line | Init data load and demo data |

```java
// Init data load and demo data
package com.studentjobportal.data;
```

## src/main/java/com/studentjobportal/exception/DuplicateApplicationException.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 351 |
| Lines | 12 |
| First line | package com.studentjobportal.exception; |

```java
package com.studentjobportal.exception;

import com.studentjobportal.model.JobID;

// if application already exists, throw this exception
public final class DuplicateApplicationException
        extends RuntimeException {

    public DuplicateApplicationException(JobID JobID) {
        super("An application already exists for job " + JobID);
    }
}
```

## src/main/java/com/studentjobportal/exception/DuplicateSavedJobException.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 323 |
| Lines | 11 |
| First line | package com.studentjobportal.exception; |

```java
package com.studentjobportal.exception;

import com.studentjobportal.model.JobID;

// Duplicate saved jobs throws this exception.
public final class DuplicateSavedJobException extends RuntimeException {

    public DuplicateSavedJobException(JobID jobID) {
        super("Job " + jobID + " has already been saved");
    }
}
```

## src/main/java/com/studentjobportal/exception/JobNotFoundException.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 303 |
| Lines | 11 |
| First line | package com.studentjobportal.exception; |

```java
package com.studentjobportal.exception;

import com.studentjobportal.model.JobID;

// if op does not find job, throw this exception
public final class JobNotFoundException extends RuntimeException {

    public JobNotFoundException(JobID JobID) {
        super("No job exists with ID " + JobID);
    }
}
```

## src/main/java/com/studentjobportal/exception/JobValidationException.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 392 |
| Lines | 16 |
| First line | package com.studentjobportal.exception; |

```java
package com.studentjobportal.exception;

// invalid job data or search input throws this exception
public final class JobValidationException
        extends IllegalArgumentException {

    public JobValidationException(String message) {
        super(message);
    }

    public JobValidationException(
        String message,
        Throwable cause) {
        super(message, cause);
    }
}
```

## src/main/java/com/studentjobportal/exception/package-info.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 58 |
| Lines | 2 |
| First line | error handling |

```java
// error handling
package com.studentjobportal.exception;
```

## src/main/java/com/studentjobportal/model/Application.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 2708 |
| Lines | 111 |
| First line | package com.studentjobportal.model; |

```java
package com.studentjobportal.model;

import java.time.Instant;
import java.util.Objects;

// Immutable application record where 
// equality is based on the immutable application ID. 
// (maintain-ability)
public final class Application {

    private final ApplicationID id;
    private final JobID JobID;
    private final ApplicationStatus status;
    private final Instant submittedAt;

    private Application(
        ApplicationID id,
        JobID JobID,
        ApplicationStatus status,
        Instant submittedAt) {

        this.id = id;
        this.JobID = JobID;
        this.status = status;
        this.submittedAt = submittedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ApplicationID getId() {
        return id;
    }

    public JobID getJobID() {
        return JobID;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    @Override
    public String toString() {
        return "Application{" + "id=" + id + ", JobID=" + JobID + ", status=" + status + ", submittedAt=" + submittedAt + '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Application)) {
            return false;
        }

        Application otherApplication = (Application) other;
        return id.equals(otherApplication.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static final class Builder {

        private ApplicationID id;
        private JobID JobID;
        private ApplicationStatus status;
        private Instant submittedAt;

        private Builder() {
        }

        public Builder id(ApplicationID id) {
            this.id = id;
            return this;
        }

        public Builder JobID(JobID JobID) {
            this.JobID = JobID;
            return this;
        }

        public Builder status(ApplicationStatus status) {
            this.status = status;
            return this;
        }

        public Builder submittedAt(Instant submittedAt) {
            this.submittedAt = submittedAt;
            return this;
        }

        public Application build() {
            return new Application(
                Objects.requireNonNull(id, "Application ID cannot be null"),
                Objects.requireNonNull(JobID, "Job ID cannot be null"),
                Objects.requireNonNull(status, "Application status cannot be null"),
                Objects.requireNonNull(submittedAt, "Submission time cannot be null")
            );
        }
    }
}
```

## src/main/java/com/studentjobportal/model/ApplicationID.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 1182 |
| Lines | 51 |
| First line | package com.studentjobportal.model; |

```java
package com.studentjobportal.model;

import java.util.Objects;
import java.util.UUID;

// Immutable id for a job application (maintain-ability)
public final class ApplicationID {

    private final UUID value;

    private ApplicationID(UUID value) {
        this.value = Objects.requireNonNull(value, "Application ID cannot be null");
    }

    public static ApplicationID generate() {
        return new ApplicationID(UUID.randomUUID());
    }

    public static ApplicationID from(String value) {
        Objects.requireNonNull(value, "Application ID value cannot be null");
        return new ApplicationID(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof ApplicationID)) {
            return false;
        }

        ApplicationID otherId = (ApplicationID) other;
        return value.equals(otherId.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
```

## src/main/java/com/studentjobportal/model/ApplicationStatus.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 180 |
| Lines | 9 |
| First line | package com.studentjobportal.model; |

```java
package com.studentjobportal.model;

// different job application states (extend-ability)
public enum ApplicationStatus {
    SUBMITTED,
    WITHDRAWN,
    ACCEPTED,
    REJECTED
}
```

## src/main/java/com/studentjobportal/model/Job.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 3553 |
| Lines | 153 |
| First line | package com.studentjobportal.model; |

```java
package com.studentjobportal.model;

import com.studentjobportal.exception.JobValidationException;

// Immutable descriptions for jobs where 
// equality is based on the immutable job ID. 
// Jobs now created through builder (maintain-ability)
public final class Job {

    // private final fields instead of public static.
    private final JobID id;
    private final String title;
    private final String company;
    private final String jobType;
    private final String location;

    // single list for job access
    private Job(
        JobID id,
        String title,
        String company,
        String jobType,
        String location) {

        this.id = id;
        this.title = title;
        this.company = company;
        this.jobType = jobType;
        this.location = location;
    }

    // accessor methods
    public static Builder builder() {
        return new Builder();
    }

    public JobID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCompany() {
        return company;
    }

    public String getJobType() {
        return jobType;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Job{" + "id=" + id +
        ", title='" + title + '\'' +
        ", company='" + company + '\'' +
        ", jobType='" + jobType + '\'' +
        ", location='" + location + '\'' +
        '}';
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Job)) {
            return false;
        }

        Job otherJob = (Job) other;
        return id.equals(otherJob.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    // Build validated job object.
    public static final class Builder {

        private JobID id;
        private String title;
        private String company;
        private String jobType;
        private String location;

        private Builder() {
        }

        public Builder id(JobID id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder company(String company) {
            this.company = company;
            return this;
        }

        public Builder jobType(String jobType) {
            this.jobType = jobType;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Job build() {
            if (id == null) {
                throw new JobValidationException("Job ID cannot be null");
            }

            return new Job(
                id,
                requireText(title, "Job title"),
                requireText(company, "Company"),
                requireText(jobType, "Job type"),
                requireText(location, "Location")
            );
        }

        private static String requireText(String value, String fieldName) {

            if (value == null) {
                throw new JobValidationException(fieldName + " cannot be null");
            }

            String trimmedValue = value.trim();

            if (trimmedValue.isEmpty()) {
                throw new JobValidationException(fieldName + " cannot be blank");
            }

            return trimmedValue;
        }
    }
}
```

## src/main/java/com/studentjobportal/model/JobID.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 1301 |
| Lines | 61 |
| First line | package com.studentjobportal.model; |

```java
package com.studentjobportal.model;

import java.util.Objects;
import java.util.UUID;

import com.studentjobportal.exception.JobValidationException;


// Job identifier
public final class JobID {

    private final UUID value;

    private JobID(UUID value) {
        this.value = Objects.requireNonNull(value);
    }

    public static JobID generate() {
        return new JobID(UUID.randomUUID());
    }

    public static JobID from(String value) {
        if (value == null) {
            throw new JobValidationException("Job ID value cannot be null");
        }

        try {
            return new JobID(UUID.fromString(value));
        } catch (IllegalArgumentException cause) {
            throw new JobValidationException("Invalid job ID: " + value, cause);
        }
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof JobID)) {
            return false;
        }

        JobID otherID = (JobID) other;
        return value.equals(otherID.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
```

## src/main/java/com/studentjobportal/model/package-info.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 74 |
| Lines | 2 |
| First line | models and immutable value objects |

```java
// models and immutable value objects
package com.studentjobportal.model;
```

## src/main/java/com/studentjobportal/repository/ApplicationRepository.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 393 |
| Lines | 15 |
| First line | package com.studentjobportal.repository; |

```java
package com.studentjobportal.repository;

import java.util.List;
import java.util.Optional;

import com.studentjobportal.model.Application;
import com.studentjobportal.model.JobID;


// store a max of 1 job per application
public interface ApplicationRepository {
    boolean save(Application application);
    Optional<Application> findByJobId(JobID jobId);
    List<Application> findAll();
}
```

## src/main/java/com/studentjobportal/repository/InMemoryApplicationRepository.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 1253 |
| Lines | 42 |
| First line | package com.studentjobportal.repository; |

```java
package com.studentjobportal.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import com.studentjobportal.model.Application;
import com.studentjobportal.model.JobID;

// Stores 1 application per job in memory
public final class InMemoryApplicationRepository implements ApplicationRepository {

    private final Map<JobID, Application> applicationsByJobId = new LinkedHashMap<>();

    @Override
    public boolean save(Application application) {Objects.requireNonNull(application, "Application cannot be null");

        JobID jobId = application.getJobID();

        if (applicationsByJobId.containsKey(jobId)) {
            return false;
        }

        applicationsByJobId.put(jobId, application);
        return true;
    }

    @Override
    public Optional<Application> findByJobId(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");
        return Optional.ofNullable(applicationsByJobId.get(jobId));
    }

    @Override
    public List<Application> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(applicationsByJobId.values()));
    }
}
```

## src/main/java/com/studentjobportal/repository/InMemoryJobRepository.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 1340 |
| Lines | 51 |
| First line | package com.studentjobportal.repository; |

```java
package com.studentjobportal.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;

// Store jobs in memory permanently
public final class InMemoryJobRepository implements JobRepository {

    private final List<Job> jobs = new ArrayList<>();

    @Override
    public List<Job> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(jobs));
    }

    @Override
    public Optional<Job> findById(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");

        for (Job job : jobs) {
            if (job.getId().equals(jobId)) {
                return Optional.of(job);
            }
        }

        return Optional.empty();
    }

    @Override
    public void save(Job job) {
        Objects.requireNonNull(job, "Job cannot be null");

        if (findById(job.getId()).isPresent()) {
            throw new IllegalArgumentException("A job with ID " + job.getId() + " already exists");
        }

        jobs.add(job);
    }

    @Override
    public boolean deleteById(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");
        return jobs.removeIf(job -> job.getId().equals(jobId));
    }
}
```

## src/main/java/com/studentjobportal/repository/InMemorySavedJobRepository.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 1017 |
| Lines | 38 |
| First line | package com.studentjobportal.repository; |

```java
package com.studentjobportal.repository;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import com.studentjobportal.model.JobID;

// Store saved job id in memory
public final class InMemorySavedJobRepository
        implements SavedJobRepository {

    private final Set<JobID> savedJobIds = new LinkedHashSet<>();

    @Override
    public boolean save(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");
        return savedJobIds.add(jobId);
    }

    @Override
    public boolean remove(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");
        return savedJobIds.remove(jobId);
    }

    @Override
    public boolean contains(JobID jobId) {
        Objects.requireNonNull(jobId, "Job ID cannot be null");
        return savedJobIds.contains(jobId);
    }

    @Override
    public Set<JobID> findAll() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(savedJobIds));
    }
}
```

## src/main/java/com/studentjobportal/repository/JobRepository.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 365 |
| Lines | 16 |
| First line | package com.studentjobportal.repository; |

```java
package com.studentjobportal.repository;

import java.util.List;
import java.util.Optional;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;


// Memory for available jobs
public interface JobRepository {
    List<Job> findAll();
    Optional<Job> findById(JobID jobId);
    void save(Job job);
    boolean deleteById(JobID jobId);
}
```

## src/main/java/com/studentjobportal/repository/SavedJobRepository.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 332 |
| Lines | 14 |
| First line | package com.studentjobportal.repository; |

```java
package com.studentjobportal.repository;

import java.util.Set;

import com.studentjobportal.model.JobID;


// Saves jobs by ID and prevents same job being saved twice
public interface SavedJobRepository {
    boolean save(JobID jobId);
    boolean remove(JobID jobId);
    boolean contains(JobID jobId);
    Set<JobID> findAll();
}
```

## src/main/java/com/studentjobportal/repository/package-info.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 99 |
| Lines | 2 |
| First line | storage and retrieval abstractions and implementations |

```java
// storage and retrieval abstractions and implementations
package com.studentjobportal.repository;
```

## src/main/java/com/studentjobportal/search/CombinedKeywordSearchStrategy.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 751 |
| Lines | 29 |
| First line | package com.studentjobportal.search; |

```java
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
```

## src/main/java/com/studentjobportal/search/CompanySearchStrategy.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 301 |
| Lines | 10 |
| First line | package com.studentjobportal.search; |

```java
package com.studentjobportal.search;

import com.studentjobportal.model.Job;

public final class CompanySearchStrategy implements JobSearchStrategy {
    @Override
    public boolean matches(Job job, String searchTerm) {
        return SearchSupport.contains(Job::getCompany, job, searchTerm);
    }
}
```

## src/main/java/com/studentjobportal/search/JobSearchStrategy.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 225 |
| Lines | 10 |
| First line | package com.studentjobportal.search; |

```java
package com.studentjobportal.search;

import com.studentjobportal.model.Job;

// Replace-able job matching algorithm
// Blank = nothing.

public interface JobSearchStrategy {
    boolean matches(Job job, String searchTerm);
}
```

## src/main/java/com/studentjobportal/search/JobTypeSearchStrategy.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 302 |
| Lines | 11 |
| First line | package com.studentjobportal.search; |

```java
package com.studentjobportal.search;

import com.studentjobportal.model.Job;

public final class JobTypeSearchStrategy implements JobSearchStrategy {

    @Override
    public boolean matches(Job job, String searchTerm) {
        return SearchSupport.contains(Job::getJobType, job, searchTerm);
    }
}
```

## src/main/java/com/studentjobportal/search/LocationSearchStrategy.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 304 |
| Lines | 11 |
| First line | package com.studentjobportal.search; |

```java
package com.studentjobportal.search;

import com.studentjobportal.model.Job;

public final class LocationSearchStrategy implements JobSearchStrategy {

    @Override
    public boolean matches(Job job, String searchTerm) {
        return SearchSupport.contains(Job::getLocation, job, searchTerm);
    }
}
```

## src/main/java/com/studentjobportal/search/SearchSupport.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 873 |
| Lines | 34 |
| First line | package com.studentjobportal.search; |

```java
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
```

## src/main/java/com/studentjobportal/search/TitleSearchStrategy.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 298 |
| Lines | 11 |
| First line | package com.studentjobportal.search; |

```java
package com.studentjobportal.search;

import com.studentjobportal.model.Job;

public final class TitleSearchStrategy implements JobSearchStrategy {

    @Override
    public boolean matches(Job job, String searchTerm) {
        return SearchSupport.contains(Job::getTitle, job, searchTerm);
    }
}
```

## src/main/java/com/studentjobportal/search/package-info.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 71 |
| Lines | 2 |
| First line | matching and search strategies |

```java
// matching and search strategies
package com.studentjobportal.search;
```

## src/main/java/com/studentjobportal/service/ApplicationService.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 2254 |
| Lines | 63 |
| First line | package com.studentjobportal.service; |

```java
package com.studentjobportal.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationID;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.JobRepository;


// Allows for applying to one job without duplicates, application stays if job gets removed.
public final class ApplicationService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final Clock clock;

    public ApplicationService(JobRepository jobRepository, ApplicationRepository applicationRepository, Clock clock) {
        this.jobRepository = Objects.requireNonNull(jobRepository, "Job repository cannot be null");
        this.applicationRepository = Objects.requireNonNull(applicationRepository, "Application repository cannot be null");
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
    }

    public Application applyForJob(JobID jobID) {
        requireExistingJob(jobID);

        if (applicationRepository.findByJobId(jobID).isPresent()) {
            throw new DuplicateApplicationException(jobID);
        }

        Application application = Application.builder()
            .id(ApplicationID.generate())
            .JobID(jobID)
            .status(ApplicationStatus.SUBMITTED)
            .submittedAt(Instant.now(clock))
            .build();

        if (!applicationRepository.save(application)) {
            throw new DuplicateApplicationException(jobID);
        }

        return application;
    }

    public List<Application> getApplications() {
        return applicationRepository.findAll();
    }

    private void requireExistingJob(JobID jobID) {
        Objects.requireNonNull(jobID, "Job ID cannot be null");

        if (!jobRepository.findById(jobID).isPresent()) {
            throw new JobNotFoundException(jobID);
        }
    }
}
```

## src/main/java/com/studentjobportal/service/JobPortalService.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 3187 |
| Lines | 85 |
| First line | package com.studentjobportal.service; |

```java
package com.studentjobportal.service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationID;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;


// Allows for saving or applying to job without duplicates, reject new actions on non-existent job, and 
// preserves existing applications as historical records when jobs are removed.
public final class JobPortalService {

    private final JobRepository jobRepository;
    private final SavedJobRepository savedJobRepository;
    private final ApplicationRepository applicationRepository;
    private final Clock clock;

    public JobPortalService(JobRepository jobRepository, SavedJobRepository savedJobRepository, ApplicationRepository applicationRepository, Clock clock) {
        this.jobRepository = Objects.requireNonNull(jobRepository, "Job repository cannot be null");
        this.savedJobRepository = Objects.requireNonNull(savedJobRepository, "Saved-job repository cannot be null");
        this.applicationRepository = Objects.requireNonNull(applicationRepository, "Application repository cannot be null");
        this.clock = Objects.requireNonNull(clock, "Clock cannot be null");
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public boolean saveJob(JobID JobID) {
        requireExistingJob(JobID);
        return savedJobRepository.save(JobID);
    }

    public boolean isSaved(JobID JobID) {
        return savedJobRepository.contains(JobID);
    }

    public List<Job> getSavedJobs() {
        List<Job> savedJobs = new ArrayList<>();

        for (JobID JobID : savedJobRepository.findAll()) {
            jobRepository.findById(JobID).ifPresent(savedJobs::add);
        }

        return savedJobs;
    }

    public Application applyForJob(JobID JobID) {
        requireExistingJob(JobID);

        if (applicationRepository.findByJobId(JobID).isPresent()) {throw new DuplicateApplicationException(JobID);}

        Application application = Application.builder()
            .id(ApplicationID.generate())
            .JobID(JobID)
            .status(ApplicationStatus.SUBMITTED)
            .submittedAt(Instant.now(clock))
            .build();

        if (!applicationRepository.save(application)) {throw new DuplicateApplicationException(JobID);}

        return application;
    }

    public List<Application> getApplications() {
        return applicationRepository.findAll();
    }

    private Job requireExistingJob(JobID JobID) {
        Objects.requireNonNull(JobID, "Job ID cannot be null");
        return jobRepository.findById(JobID).orElseThrow(() -> new JobNotFoundException(JobID));
    }
}
```

## src/main/java/com/studentjobportal/service/JobService.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 1478 |
| Lines | 46 |
| First line | package com.studentjobportal.service; |

```java
package com.studentjobportal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.search.JobSearchStrategy;


// Job view and search ops
public final class JobService {

    private final JobRepository jobRepository;
    private final JobSearchStrategy searchStrategy;

    public JobService(JobRepository jobRepository, JobSearchStrategy searchStrategy) {
        this.jobRepository = Objects.requireNonNull(jobRepository, "Job repository cannot be null");
        this.searchStrategy = Objects.requireNonNull(searchStrategy, "Search strategy cannot be null");
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job getJobById(JobID jobID) {
        Objects.requireNonNull(jobID, "Job ID cannot be null");
        return jobRepository.findById(jobID).orElseThrow(() -> new JobNotFoundException(jobID));
    }

    public List<Job> searchJobs(String searchTerm) {
        List<Job> matches = new ArrayList<>();

        for (Job job : jobRepository.findAll()) {
            if (searchStrategy.matches(job, searchTerm)) {
                matches.add(job);
            }
        }

        return Collections.unmodifiableList(matches);
    }
}
```

## src/main/java/com/studentjobportal/service/SavedJobService.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 1990 |
| Lines | 55 |
| First line | package com.studentjobportal.service; |

```java
package com.studentjobportal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.studentjobportal.exception.DuplicateSavedJobException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;


// allows for saving job without duplicates, reject new actions on non-existent job
public final class SavedJobService {

    private final JobRepository jobRepository;
    private final SavedJobRepository savedJobRepository;

    public SavedJobService(JobRepository jobRepository, SavedJobRepository savedJobRepository) {
        this.jobRepository = Objects.requireNonNull(jobRepository, "Job repository cannot be null");
        this.savedJobRepository = Objects.requireNonNull(savedJobRepository, "Saved-job repository cannot be null");
    }

    public void saveJob(JobID jobID) {
        requireExistingJob(jobID);

        if (!savedJobRepository.save(jobID)) {
            throw new DuplicateSavedJobException(jobID); // FIXME: please create file for this
        }
    }

    public boolean isSaved(JobID jobID) {
        Objects.requireNonNull(jobID, "Job ID cannot be null");
        return savedJobRepository.contains(jobID);
    }

    public List<Job> getSavedJobs() {
        List<Job> savedJobs = new ArrayList<>();

        for (JobID jobID : savedJobRepository.findAll()) {
            Job job = jobRepository.findById(jobID).orElseThrow(() -> new JobNotFoundException(jobID));
            savedJobs.add(job);
        }

        return Collections.unmodifiableList(savedJobs);
    }

    private Job requireExistingJob(JobID jobID) {
        Objects.requireNonNull(jobID, "Job ID cannot be null");
        return jobRepository.findById(jobID).orElseThrow(() -> new JobNotFoundException(jobID));
    }
}
```

## src/main/java/com/studentjobportal/service/package-info.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 68 |
| Lines | 2 |
| First line | use cases and business ops |

```java
// use cases and business ops
package com.studentjobportal.service;
```

## src/test/java/com/studentjobportal/cli/JobPortalCliIntegrationTest.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 5747 |
| Lines | 163 |
| First line | package com.studentjobportal.cli; |

```java
package com.studentjobportal.cli;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.ApplicationRepository;
import com.studentjobportal.repository.InMemoryApplicationRepository;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.InMemorySavedJobRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.repository.SavedJobRepository;
import com.studentjobportal.search.CombinedKeywordSearchStrategy;
import com.studentjobportal.service.ApplicationService;
import com.studentjobportal.service.JobService;
import com.studentjobportal.service.SavedJobService;

/**
 * Exercises the command-line interface with simulated input and captured output.
 */
final class JobPortalCliIntegrationTest {

    private static final Instant SUBMISSION_TIME =
            Instant.parse("2026-08-11T10:15:30Z");

    @Test
    void exercisesAllSevenMenuOptions() {
        String input = "1\n"
                + "2\n"
                + "java\n"
                + "3\n"
                + "1\n"
                + "4\n"
                + "5\n"
                + "1\n"
                + "6\n"
                + "7\n";

        String output = runCli(input, true);

        assertContains(output, "1. View all jobs");
        assertContains(output, "1. Java Developer");
        assertContains(output, "Company: Tech Solutions Ltd");
        assertContains(output, "Search results:");
        assertContains(output, "Job saved: Java Developer");
        assertContains(output, "Saved jobs:");
        assertContains(output, "Application submitted for Java Developer.");
        assertContains(output, "Applications:");
        assertContains(output, "Status: Submitted");
        assertContains(output, "Submitted: 11 Aug 2026 10:15 UTC");

        // The CLI should render user-friendly fields, not domain debug strings.
        assertFalse(output.contains("Job{"));
        assertFalse(output.contains("Application{"));
    }

    @Test
    void handlesInvalidInputWithoutTerminating() {
        String input = "not-a-number\n"
                + "9\n"
                + "2\n"
                + "   \n"
                + "3\n"
                + "not-a-number\n"
                + "3\n"
                + "99\n"
                + "7\n";

        String output = runCli(input, true);

        assertContains(output, "Please enter a valid whole number.");
        assertContains(output, "Please select an option between 1 and 7.");
        assertContains(output, "Search keyword cannot be blank.");
        assertContains(output, "Job number must be between 1 and 1.");
        assertContains(output, "7. Exit");
    }

    @Test
    void displaysUsefulEmptyCollectionMessages() {
        String output = runCli("1\n4\n6\n7\n", false);

        assertContains(output, "No jobs are currently available.");
        assertContains(output, "You have no saved jobs.");
        assertContains(output, "You have no applications.");
    }

    private static String runCli(String simulatedInput, boolean includeJob) {
        JobRepository jobRepository = new InMemoryJobRepository();
        SavedJobRepository savedJobRepository =
                new InMemorySavedJobRepository();
        ApplicationRepository applicationRepository =
                new InMemoryApplicationRepository();

        if (includeJob) {
            jobRepository.save(createJob());
        }

        JobService jobService = new JobService(
                jobRepository,
                new CombinedKeywordSearchStrategy()
        );
        SavedJobService savedJobService = new SavedJobService(
                jobRepository,
                savedJobRepository
        );
        ApplicationService applicationService = new ApplicationService(
                jobRepository,
                applicationRepository,
                Clock.fixed(SUBMISSION_TIME, ZoneOffset.UTC)
        );

        ByteArrayInputStream inputBytes = new ByteArrayInputStream(
                simulatedInput.getBytes(StandardCharsets.UTF_8)
        );
        ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();

        try (Scanner scanner = new Scanner(inputBytes);
                PrintStream output = new PrintStream(
                        outputBytes,
                        true,
                        StandardCharsets.UTF_8)) {
            JobPortalCli cli = new JobPortalCli(
                    scanner,
                    output,
                    jobService,
                    savedJobService,
                    applicationService
            );
            cli.run();
        }

        return outputBytes.toString(StandardCharsets.UTF_8);
    }

    private static void assertContains(String text, String expectedText) {
        assertTrue(
                text.contains(expectedText),
                () -> "Expected CLI output to contain <" + expectedText + ">"
        );
    }

    private static Job createJob() {
        return Job.builder()
                .id(JobID.from("d61b9fa8-c23c-43af-b60c-3903512c8d01"))
                .title("Java Developer")
                .company("Tech Solutions Ltd")
                .jobType("Graduate")
                .location("Cheltenham")
                .build();
    }
}
```

## src/test/java/com/studentjobportal/model/ApplicationTest.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 3556 |
| Lines | 97 |
| First line | package com.studentjobportal.model; |

```java
package com.studentjobportal.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

/**
 * Verifies the immutable state, validation and identity rules of
 * {@link Application}.
 */
final class ApplicationTest {

    private static final String APPLICATION_ID =
            "fcfc1ba8-8788-4377-b397-994e0afe202b";
    private static final String SECOND_APPLICATION_ID =
            "0a011f56-cc19-43ce-a677-98cb63982f78";
    private static final String JOB_ID =
            "d61b9fa8-c23c-43af-b60c-3903512c8d01";
    private static final Instant SUBMITTED_AT =
            Instant.parse("2026-08-11T10:15:30Z");

    @Test
    void builderStoresRequiredInformation() {
        Application application = createApplication(APPLICATION_ID, SUBMITTED_AT);

        assertEquals(ApplicationID.from(APPLICATION_ID), application.getId());
        assertEquals(JobID.from(JOB_ID), application.getJobID());
        assertEquals(ApplicationStatus.SUBMITTED, application.getStatus());
        assertEquals(SUBMITTED_AT, application.getSubmittedAt());
    }

    @Test
    void builderRejectsMissingRequiredInformation() {
        assertThrows(NullPointerException.class,
                () -> validBuilder().id(null).build());
        assertThrows(NullPointerException.class,
                () -> validBuilder().JobID(null).build());
        assertThrows(NullPointerException.class,
                () -> validBuilder().status(null).build());
        assertThrows(NullPointerException.class,
                () -> validBuilder().submittedAt(null).build());
    }

    @Test
    void equalityUsesApplicationId() {
        Application first = createApplication(APPLICATION_ID, SUBMITTED_AT);
        Application sameId = createApplication(
                APPLICATION_ID,
                SUBMITTED_AT.plusSeconds(60)
        );
        Application differentId = createApplication(
                SECOND_APPLICATION_ID,
                SUBMITTED_AT
        );

        // Non-identity fields do not affect the application's identity.
        assertEquals(first, sameId);
        assertEquals(first.hashCode(), sameId.hashCode());
        assertNotEquals(first, differentId);
        assertNotEquals(null, first);
        assertNotEquals("not an application", first);
    }

    @Test
    void toStringContainsAllFields() {
        String result = createApplication(APPLICATION_ID, SUBMITTED_AT).toString();

        assertTrue(result.contains(APPLICATION_ID));
        assertTrue(result.contains(JOB_ID));
        assertTrue(result.contains("SUBMITTED"));
        assertTrue(result.contains(SUBMITTED_AT.toString()));
    }

    private static Application.Builder validBuilder() {
        return Application.builder()
                .id(ApplicationID.from(APPLICATION_ID))
                .JobID(JobID.from(JOB_ID))
                .status(ApplicationStatus.SUBMITTED)
                .submittedAt(SUBMITTED_AT);
    }

    private static Application createApplication(
            String applicationId,
            Instant submittedAt) {
        return Application.builder()
                .id(ApplicationID.from(applicationId))
                .JobID(JobID.from(JOB_ID))
                .status(ApplicationStatus.SUBMITTED)
                .submittedAt(submittedAt)
                .build();
    }
}
```

## src/test/java/com/studentjobportal/model/JobTest.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 3815 |
| Lines | 109 |
| First line | package com.studentjobportal.model; |

```java
package com.studentjobportal.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.studentjobportal.exception.JobValidationException;

/**
 * Verifies job construction, validation and ID-based equality.
 */
final class JobTest {

    private static final String FIRST_ID =
            "d61b9fa8-c23c-43af-b60c-3903512c8d01";
    private static final String SECOND_ID =
            "87b3effd-61da-4d18-ae1e-dd186ea283f7";

    @Test
    void builderCreatesAValidTrimmedJob() {
        JobID id = JobID.from(FIRST_ID);

        Job job = Job.builder()
                .id(id)
                .title("  Java Developer  ")
                .company("  Tech Solutions Ltd  ")
                .jobType("  Graduate  ")
                .location("  Cheltenham  ")
                .build();

        assertEquals(id, job.getId());
        assertEquals("Java Developer", job.getTitle());
        assertEquals("Tech Solutions Ltd", job.getCompany());
        assertEquals("Graduate", job.getJobType());
        assertEquals("Cheltenham", job.getLocation());
    }

    @Test
    void builderRejectsMissingId() {
        assertThrows(
                JobValidationException.class,
                () -> validBuilder().id(null).build()
        );
    }

    @Test
    void builderRejectsInvalidTextFields() {
        assertInvalidText(() -> validBuilder().title(null).build());
        assertInvalidText(() -> validBuilder().title("   ").build());
        assertInvalidText(() -> validBuilder().company(null).build());
        assertInvalidText(() -> validBuilder().company("   ").build());
        assertInvalidText(() -> validBuilder().jobType(null).build());
        assertInvalidText(() -> validBuilder().jobType("   ").build());
        assertInvalidText(() -> validBuilder().location(null).build());
        assertInvalidText(() -> validBuilder().location("   ").build());
    }

    @Test
    void equalityUsesJobId() {
        Job firstJob = createJob(FIRST_ID);
        Job sameId = Job.builder()
                .id(JobID.from(FIRST_ID))
                .title("Different Title")
                .company("Different Company")
                .jobType("Placement")
                .location("Bristol")
                .build();
        Job differentId = createJob(SECOND_ID);

        // A job's immutable ID, rather than its descriptive fields, defines identity.
        assertEquals(firstJob, sameId);
        assertEquals(firstJob.hashCode(), sameId.hashCode());
        assertNotEquals(firstJob, differentId);
        assertEquals(firstJob, firstJob);
        assertNotEquals(null, firstJob);
        assertNotEquals("not a job", firstJob);
    }

    @Test
    void toStringContainsAllFields() {
        String result = createJob(FIRST_ID).toString();

        assertTrue(result.contains(FIRST_ID));
        assertTrue(result.contains("Java Developer"));
        assertTrue(result.contains("Tech Solutions Ltd"));
        assertTrue(result.contains("Graduate"));
        assertTrue(result.contains("Cheltenham"));
    }

    private static void assertInvalidText(Runnable buildAction) {
        assertThrows(JobValidationException.class, buildAction::run);
    }

    private static Job.Builder validBuilder() {
        return Job.builder()
                .id(JobID.from(FIRST_ID))
                .title("Java Developer")
                .company("Tech Solutions Ltd")
                .jobType("Graduate")
                .location("Cheltenham");
    }

    private static Job createJob(String id) {
        return validBuilder().id(JobID.from(id)).build();
    }
}
```

## src/test/java/com/studentjobportal/repository/InMemoryApplicationRepositoryTest.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 2314 |
| Lines | 73 |
| First line | package com.studentjobportal.repository; |

```java
package com.studentjobportal.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationID;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.JobID;

/**
 * Verifies application storage and the one-application-per-job constraint.
 */
final class InMemoryApplicationRepositoryTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    private ApplicationRepository repository;

    @BeforeEach
    void setUp() {
        // A fresh repository keeps every test independent of execution order.
        repository = new InMemoryApplicationRepository();
    }

    @Test
    void savesAndFindsAnApplication() {
        Application application = createApplication();

        assertTrue(repository.save(application));
        assertEquals(application, repository.findByJobId(JOB_ID).orElseThrow());
    }

    @Test
    void returnsEmptyForUnknownJob() {
        assertTrue(repository.findByJobId(JOB_ID).isEmpty());
    }

    @Test
    void preventsTwoApplicationsForTheSameJob() {
        assertTrue(repository.save(createApplication()));
        assertFalse(repository.save(createApplication()));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void returnsAnUnmodifiableSnapshot() {
        repository.save(createApplication());
        List<Application> returnedApplications = repository.findAll();

        assertThrows(UnsupportedOperationException.class, returnedApplications::clear);
        assertEquals(1, repository.findAll().size());
    }

    private static Application createApplication() {
        return Application.builder()
                .id(ApplicationID.generate())
                .JobID(JOB_ID)
                .status(ApplicationStatus.SUBMITTED)
                .submittedAt(Instant.parse("2026-08-11T10:15:30Z"))
                .build();
    }
}
```

## src/test/java/com/studentjobportal/repository/InMemoryJobRepositoryTest.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 2231 |
| Lines | 83 |
| First line | package com.studentjobportal.repository; |

```java
package com.studentjobportal.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;

/**
 * Verifies the CRUD operations and defensive collection handling of the
 * in-memory job repository.
 */
final class InMemoryJobRepositoryTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    private JobRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryJobRepository();
    }

    @Test
    void savesAndFindsAJob() {
        Job job = createJob();

        repository.save(job);

        assertEquals(job, repository.findById(JOB_ID).orElseThrow());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void returnsEmptyForUnknownJob() {
        assertTrue(repository.findById(JOB_ID).isEmpty());
    }

    @Test
    void rejectsDuplicateJobIds() {
        repository.save(createJob());

        assertThrows(IllegalArgumentException.class,
                () -> repository.save(createJob()));
    }

    @Test
    void returnsAnUnmodifiableSnapshot() {
        repository.save(createJob());
        List<Job> returnedJobs = repository.findAll();

        assertThrows(UnsupportedOperationException.class, returnedJobs::clear);
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void deletesAJob() {
        repository.save(createJob());

        assertTrue(repository.deleteById(JOB_ID));
        assertTrue(repository.findById(JOB_ID).isEmpty());
        assertFalse(repository.deleteById(JOB_ID));
    }

    private static Job createJob() {
        return Job.builder()
                .id(JOB_ID)
                .title("Java Developer")
                .company("Tech Solutions Ltd")
                .jobType("Graduate")
                .location("Cheltenham")
                .build();
    }
}
```

## src/test/java/com/studentjobportal/repository/InMemorySavedJobRepositoryTest.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 1721 |
| Lines | 63 |
| First line | package com.studentjobportal.repository; |

```java
package com.studentjobportal.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.studentjobportal.model.JobID;

/**
 * Verifies saved-job membership, duplicate prevention and safe collection
 * exposure.
 */
final class InMemorySavedJobRepositoryTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );

    private SavedJobRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemorySavedJobRepository();
    }

    @Test
    void savesAJobId() {
        assertTrue(repository.save(JOB_ID));
        assertTrue(repository.contains(JOB_ID));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void preventsDuplicateSavedJobs() {
        assertTrue(repository.save(JOB_ID));
        assertFalse(repository.save(JOB_ID));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void removesASavedJob() {
        repository.save(JOB_ID);

        assertTrue(repository.remove(JOB_ID));
        assertFalse(repository.contains(JOB_ID));
        assertFalse(repository.remove(JOB_ID));
    }

    @Test
    void returnsAnUnmodifiableSnapshot() {
        repository.save(JOB_ID);
        Set<JobID> returnedIds = repository.findAll();

        assertThrows(UnsupportedOperationException.class, returnedIds::clear);
        assertEquals(1, repository.findAll().size());
    }
}
```

## src/test/java/com/studentjobportal/search/JobSearchStrategyTest.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 2994 |
| Lines | 94 |
| First line | package com.studentjobportal.search; |

```java
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
```

## src/test/java/com/studentjobportal/service/ApplicationServiceTest.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 3270 |
| Lines | 96 |
| First line | package com.studentjobportal.service; |

```java
package com.studentjobportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.InMemoryApplicationRepository;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.JobRepository;

/**
 * Verifies successful application submission and the service's error paths.
 */
final class ApplicationServiceTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );
    private static final JobID MISSING_JOB_ID = JobID.from(
            "87b3effd-61da-4d18-ae1e-dd186ea283f7"
    );
    private static final Instant SUBMISSION_TIME =
            Instant.parse("2026-08-11T10:15:30Z");

    @Test
    void submitsAnApplication() {
        ApplicationService service = createService();

        Application application = service.applyForJob(JOB_ID);

        assertEquals(JOB_ID, application.getJobID());
        assertEquals(ApplicationStatus.SUBMITTED, application.getStatus());
        assertEquals(SUBMISSION_TIME, application.getSubmittedAt());
        assertEquals(1, service.getApplications().size());
    }

    @Test
    void rejectsDuplicateApplication() {
        ApplicationService service = createService();
        service.applyForJob(JOB_ID);

        DuplicateApplicationException exception = assertThrows(
                DuplicateApplicationException.class,
                () -> service.applyForJob(JOB_ID)
        );

        assertTrue(exception.getMessage().contains(JOB_ID.toString()));
        assertEquals(1, service.getApplications().size());
    }

    @Test
    void rejectsMissingJob() {
        ApplicationService service = createService();

        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                () -> service.applyForJob(MISSING_JOB_ID)
        );

        assertTrue(exception.getMessage().contains(MISSING_JOB_ID.toString()));
    }

    private static ApplicationService createService() {
        JobRepository jobRepository = new InMemoryJobRepository();
        jobRepository.save(createJob());

        // The fixed clock makes the generated submission timestamp deterministic.
        return new ApplicationService(
                jobRepository,
                new InMemoryApplicationRepository(),
                Clock.fixed(SUBMISSION_TIME, ZoneOffset.UTC)
        );
    }

    private static Job createJob() {
        return Job.builder()
                .id(JOB_ID)
                .title("Java Developer")
                .company("Tech Solutions Ltd")
                .jobType("Graduate")
                .location("Cheltenham")
                .build();
    }
}
```

## src/test/java/com/studentjobportal/service/JobPortalServiceTest.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 4546 |
| Lines | 128 |
| First line | package com.studentjobportal.service; |

```java
package com.studentjobportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Test;

import com.studentjobportal.exception.DuplicateApplicationException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Application;
import com.studentjobportal.model.ApplicationStatus;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.InMemoryApplicationRepository;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.InMemorySavedJobRepository;
import com.studentjobportal.repository.JobRepository;

/**
 * Integration tests for the combined portal service and its repositories.
 */
final class JobPortalServiceTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );
    private static final JobID MISSING_JOB_ID = JobID.from(
            "87b3effd-61da-4d18-ae1e-dd186ea283f7"
    );
    private static final Instant SUBMISSION_TIME =
            Instant.parse("2026-08-11T10:15:30Z");

    @Test
    void savingSameJobTwiceDoesNotCreateDuplicate() {
        TestContext context = createContext();

        assertTrue(context.service.saveJob(JOB_ID));
        assertFalse(context.service.saveJob(JOB_ID));
        assertEquals(1, context.service.getSavedJobs().size());
    }

    @Test
    void applyingTwiceIsRejected() {
        TestContext context = createContext();
        context.service.applyForJob(JOB_ID);

        assertThrows(DuplicateApplicationException.class,
                () -> context.service.applyForJob(JOB_ID));
        assertEquals(1, context.service.getApplications().size());
    }

    @Test
    void missingJobsCannotBeSavedOrAppliedFor() {
        TestContext context = createContext();

        assertThrows(JobNotFoundException.class,
                () -> context.service.saveJob(MISSING_JOB_ID));
        assertThrows(JobNotFoundException.class,
                () -> context.service.applyForJob(MISSING_JOB_ID));
    }

    @Test
    void applicationContainsStatusAndSubmissionTime() {
        Application application = createContext().service.applyForJob(JOB_ID);

        assertEquals(JOB_ID, application.getJobID());
        assertEquals(ApplicationStatus.SUBMITTED, application.getStatus());
        assertEquals(SUBMISSION_TIME, application.getSubmittedAt());
    }

    @Test
    void removedJobsAreHandledPredictably() {
        TestContext context = createContext();
        context.service.saveJob(JOB_ID);
        context.service.applyForJob(JOB_ID);

        // Applications are historical records, while saved jobs require a live job.
        context.jobRepository.deleteById(JOB_ID);

        assertTrue(context.service.getSavedJobs().isEmpty());
        assertEquals(1, context.service.getApplications().size());
        assertThrows(JobNotFoundException.class,
                () -> context.service.saveJob(JOB_ID));
        assertThrows(JobNotFoundException.class,
                () -> context.service.applyForJob(JOB_ID));
    }

    private static TestContext createContext() {
        JobRepository jobRepository = new InMemoryJobRepository();
        jobRepository.save(createJob());

        JobPortalService service = new JobPortalService(
                jobRepository,
                new InMemorySavedJobRepository(),
                new InMemoryApplicationRepository(),
                Clock.fixed(SUBMISSION_TIME, ZoneOffset.UTC)
        );
        return new TestContext(jobRepository, service);
    }

    private static Job createJob() {
        return Job.builder()
                .id(JOB_ID)
                .title("Java Developer")
                .company("Tech Solutions Ltd")
                .jobType("Graduate")
                .location("Cheltenham")
                .build();
    }

    private static final class TestContext {
        private final JobRepository jobRepository;
        private final JobPortalService service;

        private TestContext(
                JobRepository jobRepository,
                JobPortalService service) {
            this.jobRepository = jobRepository;
            this.service = service;
        }
    }
}
```

## src/test/java/com/studentjobportal/service/JobServiceTest.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 2478 |
| Lines | 80 |
| First line | package com.studentjobportal.service; |

```java
package com.studentjobportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.JobRepository;
import com.studentjobportal.search.TitleSearchStrategy;

/**
 * Verifies job retrieval and delegation to an injected search strategy.
 */
final class JobServiceTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );
    private static final JobID MISSING_JOB_ID = JobID.from(
            "87b3effd-61da-4d18-ae1e-dd186ea283f7"
    );

    @Test
    void returnsAllJobs() {
        assertEquals(1, createService().getAllJobs().size());
    }

    @Test
    void returnsJobById() {
        Job job = createService().getJobById(JOB_ID);

        assertEquals(JOB_ID, job.getId());
        assertEquals("Java Developer", job.getTitle());
    }

    @Test
    void searchesUsingInjectedStrategy() {
        JobService service = createService();

        assertEquals(1, service.searchJobs("JAVA").size());
        // The injected title strategy must not match a company-only term.
        assertTrue(service.searchJobs("Tech Solutions").isEmpty());
    }

    @Test
    void returnsNoResultsForBlankSearch() {
        assertTrue(createService().searchJobs("   ").isEmpty());
    }

    @Test
    void throwsForMissingJob() {
        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                () -> createService().getJobById(MISSING_JOB_ID)
        );

        assertTrue(exception.getMessage().contains(MISSING_JOB_ID.toString()));
    }

    private static JobService createService() {
        JobRepository repository = new InMemoryJobRepository();
        repository.save(createJob());
        return new JobService(repository, new TitleSearchStrategy());
    }

    private static Job createJob() {
        return Job.builder()
                .id(JOB_ID)
                .title("Java Developer")
                .company("Tech Solutions Ltd")
                .jobType("Graduate")
                .location("Cheltenham")
                .build();
    }
}
```

## src/test/java/com/studentjobportal/service/SavedJobServiceTest.java

| Property | Value |
|---|---|
| Type | Unknown |
| Size (bytes) | 3588 |
| Lines | 111 |
| First line | package com.studentjobportal.service; |

```java
package com.studentjobportal.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.studentjobportal.exception.DuplicateSavedJobException;
import com.studentjobportal.exception.JobNotFoundException;
import com.studentjobportal.model.Job;
import com.studentjobportal.model.JobID;
import com.studentjobportal.repository.InMemoryJobRepository;
import com.studentjobportal.repository.InMemorySavedJobRepository;
import com.studentjobportal.repository.JobRepository;

/**
 * Verifies saved-job retrieval, duplicate prevention and invalid references.
 */
final class SavedJobServiceTest {

    private static final JobID JOB_ID = JobID.from(
            "d61b9fa8-c23c-43af-b60c-3903512c8d01"
    );
    private static final JobID MISSING_JOB_ID = JobID.from(
            "87b3effd-61da-4d18-ae1e-dd186ea283f7"
    );

    @Test
    void savesAndReturnsAJob() {
        TestContext context = createContext();

        context.service.saveJob(JOB_ID);

        assertTrue(context.service.isSaved(JOB_ID));
        assertEquals(1, context.service.getSavedJobs().size());
        assertEquals(JOB_ID, context.service.getSavedJobs().get(0).getId());
    }

    @Test
    void rejectsDuplicateSavedJob() {
        TestContext context = createContext();
        context.service.saveJob(JOB_ID);

        DuplicateSavedJobException exception = assertThrows(
                DuplicateSavedJobException.class,
                () -> context.service.saveJob(JOB_ID)
        );

        assertTrue(exception.getMessage().contains(JOB_ID.toString()));
    }

    @Test
    void rejectsMissingJob() {
        TestContext context = createContext()

        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                () -> context.service.saveJob(MISSING_JOB_ID)
        );

        assertTrue(exception.getMessage().contains(MISSING_JOB_ID.toString()));
    }

    @Test
    void reportsBrokenSavedJobReference() {
        TestContext context = createContext();
        context.service.saveJob(JOB_ID);

        // Simulate a job being removed after the user saved it.
        context.jobRepository.deleteById(JOB_ID);

        JobNotFoundException exception = assertThrows(
                JobNotFoundException.class,
                context.service::getSavedJobs
        );
        assertTrue(exception.getMessage().contains(JOB_ID.toString()));
    }

    private static TestContext createContext() {
        JobRepository jobRepository = new InMemoryJobRepository();
        jobRepository.save(createJob());
        SavedJobService service = new SavedJobService(
                jobRepository,
                new InMemorySavedJobRepository()
        );
        return new TestContext(jobRepository, service);
    }

    private static Job createJob() {
        return Job.builder()
                .id(JOB_ID)
                .title("Java Developer")
                .company("Tech Solutions Ltd")
                .jobType("Graduate")
                .location("Cheltenham")
                .build();
    }

    private static final class TestContext {
        private final JobRepository jobRepository;
        private final SavedJobService service;

        private TestContext(
                JobRepository jobRepository,
                SavedJobService service) {
            this.jobRepository = jobRepository;
            this.service = service;
        }
    }
}
```

