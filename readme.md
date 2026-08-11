# Part 1: Extend-ability, Maintainability, Test-ability.

### Compile and Run program
```zsh
find src/main/java -name '*.java' -print0 | xargs -0 javac -d bin
java -cp bin com.studentjobportal.StudentJobPortal
```


### Run all tests (test-ability)
```zsh
cd "1. Part 1"
mvn test
```
