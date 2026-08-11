Part 1 - Student Job Application maintainability, test-ability and extend-ability exercise

## This document is made for Obsidian MD with the To-Do 


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