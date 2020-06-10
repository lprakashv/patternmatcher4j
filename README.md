![GitHub](https://img.shields.io/github/license/lprakashv/patternmatcher4j?style=flat-square)
![Travis (.com) branch](https://img.shields.io/travis/com/lprakashv/patternmatcher4j/master?style=flat-square)
![Coveralls github branch](https://img.shields.io/coveralls/github/lprakashv/patternmatcher4j/master?style=flat-square)
![Maven Central](https://img.shields.io/maven-central/v/io.github.lprakashv/patternmatcher4j?style=flat-square)

# patternmatcher4j "Switch-Case on Steriods"
An FP style pattern matcher library for Java. Pattern matching is one of the the single most popular concept. This is inpired from [Scala's pattern matching](https://docs.scala-lang.org/tour/pattern-matching.html).

## Concept
This is just like switch-case block, but you can do much more than just matching primitive values and enums. 

Here, we create a *"Matcher"* for an object (a Java POJO), for which we define *"match-cases"* which can be of following type:
1. *Predicate* - a function which takes an object (of the same type of matched object) as input and returns boolean to check if the object matches or not.
2. *Value* - exact value match using Java's equals() method.
3. *Type* - to match specific type of the matched object.
4. *Destrucured* - This is a more advanced match, here we can match each field of the matched object and can define criteria on each field to qualify as a match.

For each *"match-case"*, we define an action to perform on original matched object.

All the *match-case* and their corresponding *action* are stored in the matcher's state. There will be no computation (matching and evaluation) until we trigger matcher's *"get"*.

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
