# Part 1: Student Job Portal OO Refactor

This part refactors the supplied procedural `StudentJobPortal.java` into a layered, testable Java application. The program provides an interactive command-line portal where a student can view and search jobs, save a job, submit one application per job, and review saved jobs and applications.

The application targets Java 17 and uses only core Java libraries at runtime.

## Run the application

From this directory (`1. Part 1`):

```sh
find src/main/java -name '*.java' -print0 | xargs -0 javac --release 17 -d bin
java -cp bin com.studentjobportal.StudentJobPortal
```

Alternatively, Maven can compile the source before the same entry point is launched:

```sh
mvn compile
java -cp target/classes com.studentjobportal.StudentJobPortal
```

The application starts with two sample vacancies. Select a numbered menu option and follow the prompts:

1. View all jobs
2. Search jobs
3. Save a job
4. View saved jobs
5. Apply for a job
6. View applications
7. Exit

All repositories are in memory, so saved jobs and applications last for the current process only.

## How the application works

`StudentJobPortal.main` is the composition root. It creates the in-memory repositories, loads sample jobs, selects the combined keyword-search strategy, constructs the services, and passes those services to the CLI.

The responsibilities are separated by package:

| Package | Responsibility |
| --- | --- |
| `model` | Immutable domain objects and identifiers: `Job`, `Application`, `JobID`, `ApplicationID`, and `ApplicationStatus` |
| `repository` | Storage contracts and in-memory implementations for jobs, saved job IDs, and applications |
| `search` | Interchangeable rules for matching a job by title, company, location, job type, or all fields |
| `service` | Use-case and validation logic for browsing/searching, saving jobs, and applying |
| `cli` | Input parsing, menu navigation, and output formatting |
| `data` | Seeding the two demonstration jobs |
| `exception` | Domain-specific errors for invalid, missing, or duplicate operations |

The main runtime flow is:

1. `StudentJobPortal` creates one implementation for each repository interface.
2. `SampleJobDataSeeder` builds validated jobs and stores them through `JobRepository`.
3. The CLI translates a menu choice into a call to `JobService`, `SavedJobService`, or `ApplicationService`.
4. Services enforce the use-case rules and work through repository interfaces rather than concrete storage classes.
5. The CLI presents returned domain objects or converts expected domain exceptions into user-friendly messages.

## GoF design patterns

### Builder - creational

`Job.Builder` and `Application.Builder` construct domain objects through named steps. Construction is kept inside each model, required fields are checked before an instance is returned, and completed objects are immutable. This prevents partially initialised domain objects and avoids long, ambiguous constructors.

Example locations:

- `src/main/java/com/studentjobportal/model/Job.java`
- `src/main/java/com/studentjobportal/model/Application.java`
- `src/main/java/com/studentjobportal/data/SampleJobDataSeeder.java`

### Strategy - behavioural

`JobSearchStrategy` defines the job-matching behaviour used by `JobService`. Title, company, location, and job-type implementations can be selected or extended without changing the service. `CombinedKeywordSearchStrategy` composes the four strategies to provide the current all-field keyword search.

Example locations:

- `src/main/java/com/studentjobportal/search/JobSearchStrategy.java`
- `src/main/java/com/studentjobportal/search/CombinedKeywordSearchStrategy.java`
- `src/main/java/com/studentjobportal/service/JobService.java`

These patterns are from different GoF categories, satisfying the coursework constraint for two patterns while keeping the design proportionate to the application.

## Other object-oriented design decisions

- Repository interfaces separate business logic from storage. The current in-memory implementations can be replaced without rewriting the services.
- Constructor injection makes service dependencies explicit and allows tests to provide isolated repositories and a controlled `Clock`.
- `JobID` and `ApplicationID` are value objects rather than unvalidated strings. Equality for `Job` and `Application` is based on their immutable identifiers.
- `Job`, `Application`, and repository result collections prevent callers from directly mutating stored state.
- Services keep validation and use-case rules out of the CLI. Duplicate saves/applications and missing jobs have dedicated exception types.
- `ApplicationService` receives a `Clock`, allowing deterministic submission timestamps in automated tests.

## Behaviour and business rules

- Searches are case-insensitive and match partial text across title, company, job type, and location.
- A job can be saved only once.
- A student can submit only one application for a given job.
- An application records a generated ID, the job ID, `SUBMITTED` status, and a UTC timestamp.
- Invalid menu choices and job numbers are handled without terminating the application.

## Tests

The test suite contains 50 JUnit 5 tests in 11 test classes. It covers models, repositories, all search strategies, each service, and an end-to-end CLI interaction.

Run it from this directory with Maven:

```sh
mvn test
```

JUnit is a test-scoped Maven dependency; it is not used by the application at runtime. Test source is under `src/test/java`, and Maven writes build output and reports under `target/`.

## UML artefacts

The editable draw.io source and exported PNGs are in [1. Diagrams](<1. Diagrams>):

- [Use-case diagram](<1. Diagrams/24062219_CM2307_UC-Diagram_2026-08-12-UC Diagram.drawio.png>)
- [Class diagram](<1. Diagrams/24062219_CM2307_Class-Diagram_2026-08-12.drawio.png>)
- [Sequence diagram](<1. Diagrams/24062219_CM2307_Sequence-Diagram_2026-08-12.drawio.png>)
- [Editable draw.io source](<1. Diagrams/24062219_CM2307_All-Diagrams_2026-08-12.drawio>)

## Key files

- `src/main/java/com/studentjobportal/StudentJobPortal.java` - program entry point and dependency wiring
- `src/main/java/com/studentjobportal/cli/JobPortalCli.java` - interactive interface
- `src/main/java/com/studentjobportal/service/` - application use cases
- `src/main/java/com/studentjobportal/repository/` - persistence contracts and in-memory stores
- `src/main/java/com/studentjobportal/search/` - Strategy pattern implementation
- `src/main/java/com/studentjobportal/model/` - Builder pattern and domain model
- `src/test/java/com/studentjobportal/` - automated tests
- `pom.xml` - Java 17, JUnit, and Surefire configuration

[Return to the repository overview](../readme.md)

