# Part 1: Extend-ability, Maintainability, Test-ability.

### Compile and Run program
```zsh
find src/main/java -name '*.java' -print0 | xargs -0 javac -d bin
java -cp bin com.studentjobportal.StudentJobPortal
```


### Compile and Run program with TESTS (test-ability)
```zsh
find src/main/java src/test/java -name '*.java' -print0 | xargs -0 javac -d bin
java -cp bin com.studentjobportal.model.JobTest
```

