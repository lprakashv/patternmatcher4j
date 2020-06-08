
# java-pattern-matcher
An FP style pattern matcher library for Java

### Example
```java
// Assume some Person objects as : Person(name, age, eligible, Object extra)

String stringFromObjectPatternMatching = 
    Matcher
        .<Object, String>matchFor((Object) person)
        .matchCase(
            Field.with("name", name -> ((String) name).toLowerCase().equals("lalit")),
            Field.with("age", age -> (Integer) age < 60),
            Field.withValue("eligible", true)
        )
        .action(p -> "Young Lalit found")
        .matchCase(Field.withValue("age", null))
        .action(p -> "God found")
        .matchValue(new Person("Nitin", 26, false))
        .action(p -> "Uneligible Nitin with age=26 found")
        .matchCase(
            Field.with("extra", String.class)
        )
        .action(p -> "Person with String extra found with extra value=" + ((Person) p).extra)
        .matchCase(NonPerson.class)
        .action(p -> "This is not a person")
        .matchCase(p -> ((Person) p).age > 100)
        .action(p -> "Very old person")
        .getOrElse("Unknown");
```

### Creating a matcher
```java
Matcher.<InputType, OutputType>matchFor(InputType matchedObject);
```

### Adding match cases on a matcher
* `.matchCase(Field... fieldMatches)` 
    * Destrucutred object match (or object fields match).
    * Multiple Field arguments can be given representing each field's matching.
    * Recursive/nested match on field using:
        * `Field.with("fieldName", Class<?>|Function<Object, Boolean>|Field...)`.
        * For field value matching `Field.withValue("fieldName", valueObject)`. 
    * Each field will have a field name and a match (nested/recursive matching is also possible!).
* `.matchValue(Object value)`
    * Value match.
    * To match exact values.
    * NOTE: this is different than `.matchCase()` to have Object value matching. 
* `.matchCase(Class<?> type)`
    * Type match.
    * To match class of the matched object.
* `.matchCase(Function<Object, Boolean> predicate)`
    * Predicate match.
    * Will match only if the function returns true for the matched object.

##### NOTES
* Each `.matchCase()` and `.matchValue()` will return an instance of `CaseActionAppender` class.
* `CaseActionAppender` class cannot be instantiated without a `Matcher` instance.

### Defining actions on "matched" cases 
```java
Matcher<InputType, OutputType> matcher; // some matcher

matcher
    .matchCase(...)
    .action(
        (InputType o) -> {
            // returning OutputType value
        }
    );
```

##### NOTES
* Each `.action()` will return a new `Matcher` instance.
* Each `.action()` defines the action for "only" the `.matchCase()` or `.matchValue()` immediately preceding it.

### Evaluating a matcher and getting the output
```java
Matcher<InputType, OutputType> matcher; // some matcher

// returns Optional<OutputType> with value presend on any match.
matcher.get();

// returns strictly value of type OutputType with defaultValue not matching any case.
matcher.getOrElse(OutputType defaultValue); 
```

##### NOTES
* The matcher computation is lazy and will not start until `.get()` or `.getOrElse()` invoked on it.

#### TODOS
- [ ] Add custom exceptions.
- [ ] Add exception propagation with computation yielding result or exception.
