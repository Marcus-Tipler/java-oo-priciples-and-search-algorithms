Part 1 - Student Job Application maintainability, test-ability and extend-ability exercise

## This document is made for Obsidian MD with the To-Do list plugin.
- [ ] Replace list items for domain objects.
- [ ] Instead of Public Statics, i'd like to replace them for Private Finals (if necessary), it's generally better practice. 
- [ ] Have constructors for each method.
- [ ] Move everything OUT of MAIN
    - Add service layer with trace-able error management
    - Display menus else-where in new class

I think the Builder pattern suits this job better because a job can have several properties that work together, and more properties might need adding later (extend-ability), however currently with static lists, that can easily cause corruption. (if i added a wage to the list, i'd have to go through and add wages to all previous jobs or else the program might crash.). Builder avoids long constructors and makes creation readable.
- [ ] Validate required fields inside build function so that invalid jobs cannot be created.

- [ ] Add repository abstractions (create interfaces for each repo such as jobs, and implement accessible public classes)

- [ ] Searching currently only looks at the title, maybe implement a Strategy Pattern to Searching Implementation (based on AI declaration doc 1)?

- [ ] Fix menu to display missing "Saved Jobs" because there's no point of having that variable otherwise, i'm assuming this is what the coursework intends me to do? If not, just use it as proof of extend-ability? Have these:
    1. View all jobs
    2. Search jobs
    3. Save a job
    4. View saved jobs
    5. Apply for a job
    6. View applications
    7. Exit

- Input really should have validation
    try catch
- errors should all be trace-able
For domain objects, implement toString(), equals() & hashCode()


- Create well needed test files and structure the program like an actual java program instead of having a single java file. (something like "cli", "models", "repos", "services", "data" and "search")