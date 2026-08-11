Part 1 - Student Job Application maintainability, test-ability and extend-ability exercise

## This document is made for Obsidian MD with the To-Do 

## Phase 5 — Introduce repository abstractions

Define what the program needs before deciding how it is stored.

- [ ] Create a `JobRepository` interface.
- [ ] Add operations such as:

```text
findAll
findById
save
```

- [ ] Create `SavedJobRepository`.
- [ ] Create `ApplicationRepository`.
- [ ] Implement accessible in-memory versions, for example:

```text
InMemoryJobRepository
InMemorySavedJobRepository
InMemoryApplicationRepository
```

- [ ] Store collections as `private final` fields inside repository implementations.
- [ ] Return safe views or copies rather than exposing mutable internal collections.
- [ ] Add repository tests.

Avoid placing search business rules in the repository unless the repository contract explicitly owns querying.

## Phase 6 — Move sample data into the data layer

- [ ] Remove the hard-coded sample jobs from `main()`.
- [ ] Create a data seeder or fixture class.
- [ ] Construct the two initial jobs through the same validated creation mechanism used elsewhere.
- [ ] Insert them through `JobRepository`.

**Checkpoint:** `main()` no longer knows the details of the sample jobs.

## Phase 7 — Add the search Strategy pattern

- [ ] Create a `JobSearchStrategy` interface.
- [ ] Implement individual strategies where useful:

```text
TitleSearchStrategy
CompanySearchStrategy
LocationSearchStrategy
JobTypeSearchStrategy
CombinedKeywordSearchStrategy
```

- [ ] Make searches case-insensitive.
- [ ] Decide how blank search terms should behave.
- [ ] Keep strategy implementations independent of console input/output.
- [ ] Add focused tests for matches, non-matches, casing and blank input.

The current code already searches title, company and location, despite the note saying it searches only titles. The real improvement is making that behaviour explicit, replaceable and independently testable.

## Phase 8 — Add the service layer

- [ ] Create a `JobService`.
- [ ] Create a `SavedJobService`.
- [ ] Create an `ApplicationService`.
- [ ] Inject repositories and search strategies through constructors.
- [ ] Move business operations into services:

```text
view all jobs
search for jobs
save a job
view saved jobs
apply for a job
view applications
```

- [ ] Keep services independent of `Scanner` and `System.out`.
- [ ] Add service tests using in-memory repositories or test doubles.

A service should return results or throw a meaningful exception; it should not display menu messages.

## Phase 9 — Make errors traceable

- [ ] Create specific exceptions where they improve clarity, such as:

```text
JobNotFoundException
JobValidationException
DuplicateSavedJobException
DuplicateApplicationException
```

- [ ] Include useful context in messages, especially the relevant job ID.
- [ ] Preserve original causes when translating lower-level exceptions.
- [ ] Avoid broad `catch (Exception)` blocks.
- [ ] Handle expected user mistakes near the CLI boundary.
- [ ] Allow unexpected programming faults to remain visible during development.
- [ ] Add tests for service failure paths.

“Traceable” does not mean catching everything. It means failures retain enough context to locate their source.

## Phase 10 — Extract the command-line interface

- [ ] Create a dedicated menu or CLI class, such as `JobPortalCli`.
- [ ] Inject the services and input/output dependencies through its constructor.
- [ ] Move the main loop into that class.
- [ ] Replace the long `if/else` chain with a `switch`.
- [ ] Display the complete menu:

```text
1. View all jobs
2. Search jobs
3. Save a job
4. View saved jobs
5. Apply for a job
6. View applications
7. Exit
```

- [ ] Give each menu option a small, named handler method.
- [ ] Ensure the CLI only coordinates input, service calls and output.

After this phase, `main()` should roughly do only three things:

1. Construct repositories and services.
2. Seed initial data.
3. Start the CLI.

## Phase 11 — Validate all user input

- [ ] Read menu input as text and parse it safely.
- [ ] Catch `NumberFormatException` for numeric selections.
- [ ] Reject menu options outside `1–7`.
- [ ] Validate job numbers before accessing a collection.
- [ ] Handle blank search keywords.
- [ ] Print useful messages when jobs, saved jobs or applications are absent.
- [ ] Ensure invalid input returns to the menu rather than terminating the program.
- [ ] Test input validation where practical.

Reading a line and parsing it is generally simpler than mixing `nextInt()` and `nextLine()`, which commonly produces skipped-input problems.

## Phase 12 — Complete verification and cleanup

- [ ] Test every domain object.
- [ ] Test builder validation.
- [ ] Test every repository implementation.
- [ ] Test each search strategy.
- [ ] Test service success and error paths.
- [ ] Add a small CLI integration test if time permits.
- [ ] Remove obsolete static lists and commented-out legacy logic.
- [ ] Check that mutable state is private and appropriately protected.
- [ ] Check that every dependency is supplied through a constructor.
- [ ] Confirm that `main()` contains no business logic.
- [ ] Run the full test suite and manually exercise all seven menu options.

A sensible implementation order is therefore:

```text
Project structure
→ Job model
→ Builder and validation
→ Saved-job/Application models
→ Repository interfaces
→ In-memory repositories
→ Seed data
→ Search strategies
→ Services and exceptions
→ CLI and menus
→ Input validation
→ Final integration testing
```

This order gives you a compilable checkpoint after every stage and lets you explain each design decision independently in your coursework.