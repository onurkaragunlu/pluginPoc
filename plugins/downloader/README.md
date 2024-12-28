Gradle Phases Overview
Gradle builds operate in three distinct phases:

## Initialization Phase

Gradle identifies projects to configure (root and subprojects).
`settings.gradle.kts` is executed.
## Configuration Phase

Gradle evaluates all `build.gradle` files.
Tasks are created and configured, but not executed.
tasks.register blocks runs during this phase.
## Execution Phase

Gradle executes only the tasks explicitly requested (e.g., ./gradlew build).
`doLast` and `doFirst` blocks run at this stage.


| Criteria  | Gradle Sync |                       Gradle Run                       |
| ------------- |:-------------:|:------------------------------------------------------:|
| Initialization Phase	      | Runs    |                          Runs                          |
| Configuration Phase     | Runs (inside tasks registered)     |             Runs (inside tasks registered)             |
| Execution Phase	     | Does not run   |                          Runs                          |
| Purpose	     |Evaluate build scripts	    |                     Execute tasks                      |
| Impact	     | Structure validation, dependency check     |Runs the build and executes `doLast`and `doFirst` blocks|


## Practical Gradle Sync Process
* Dependencies are downloaded if missing.
* Tasks are registered but not executed.
* Error detection in build scripts (e.g., syntax, missing dependencies).
* IDE integration: Android Studio/IntelliJ processes updated project structure.

## Key Takeaways for Gradle Task Execution
* Use tasks.register for efficient, lazy task creation.
* Use doLast for logic that should run during task execution.
* Use afterEvaluate for post-configuration adjustments.

## **What is a Provider?**
- A `Provider` is a **lazy-evaluated container** in Gradle.
- It supplies values or objects **only when needed**, improving performance and flexibility.
- Commonly used for:
    - Linking task outputs to other tasks.
    - Deferring value computation until execution time.

---

## **Key Provider Methods**

| **Method**        | **What it Does**                                              | **Example**                                     |
|-------------------|--------------------------------------------------------------|------------------------------------------------|
| **`get()`**       | Retrieves the value at execution time.                        | `val value = provider.get()`                   |
| **`orElse`**      | Supplies a default value if none is defined.                  | `val version = provider.orElse("1.0")`         |
| **`map`**         | Transforms the value within the provider.                     | `val upper = provider.map { it.uppercase() }`  |
| **`flatMap`**     | Links one provider to another, creating a dependency.         | `val nested = provider.flatMap { it.dir("nested") }` |
| **`isPresent`**   | Checks if the value is defined.                               | `if (provider.isPresent) println("Exists")`    |

