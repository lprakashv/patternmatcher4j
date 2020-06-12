[![GitHub](https://img.shields.io/github/license/lprakashv/patternmatcher4j?style=flat-square)](LICENSE)
[![Travis (.com) branch](https://img.shields.io/travis/com/lprakashv/patternmatcher4j/master?style=flat-square)](https://travis-ci.com/lprakashv/patternmatcher4j)
[![Coveralls github branch](https://img.shields.io/coveralls/github/lprakashv/patternmatcher4j/master?style=flat-square)](https://coveralls.io/github/lprakashv/patternmatcher4j?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.lprakashv/patternmatcher4j?style=flat-square)](https://search.maven.org/search?q=g:%22io.github.lprakashv%22%20AND%20a:%22patternmatcher4j%22)

# patternmatcher4j "Switch-Case on Steroids"
An FP style pattern matcher library for Java. Pattern matching is one of the the single most popular concept in functional programming. This is inpired from [Scala's pattern matching](https://docs.scala-lang.org/tour/pattern-matching.html).

[See Javadoc](docs/javadoc/index.html)

### Dependency (replace `{version}` with the latest version)

Maven
```xml
<dependency>
  <groupId>io.github.lprakashv</groupId>
  <artifactId>patternmatcher4j</artifactId>
  <version>{version}</version>
</dependency>
```

Gradle
```groovy
implementation 'io.github.lprakashv:patternmatcher4j:{version}'
```

## Concept
This is just like switch-case block, but you can do much more than just matching primitive values and enums. 

Here, we create a *"Matcher"* for an object (a Java POJO), for which we define *"match-cases"* which can be of following type:
1. **Predicate** - a function which takes an object (of the same type of matched object) as input and returns boolean to check if the object matches or not.
2. **Value** - exact value match using Java's equals() method.
3. **Type** - to match specific type of the matched object.
4. **Destructured** - This is a more advanced match, here we can match each field with a match of their own (recursive nested matching possible!) of the matched object and can define criteria on each field to qualify as a match.

For each **"match-case"**, we define an **"action"** to perform on original matched object.

All the **match-case** and their corresponding **action** are stored in the matcher's state. There will be no computation (matching and evaluation) until we evaluate the matcher. 
Evaluation operations are: 
* **Safe evaluations**:
    * `.findFirstMatch()` - returns object of type `Option<MatcherBreakResult<R>>`, present in case if any first case matched or exception occurred.
    * `.findAllMatches()` - returns object of type `MatcherAggregatedResult<R>`, all the matched cases' action results.
* **Unsafe evaluations** (throws `MatcherException` on error):
    * `.get()` - return `Option<R>`, present in case if any first case matched.
    * `.getOrElse(R defaultValue)`, returns the matched action value or the default value if none of the case matches.

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
    * Destructured object match (or object fields match).
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

// returns Optional<MatcherBreakResult<OutputType>> with value present on either any first match or exception occurred.
matcher.getFirstMatched();

// returns value of type MatcherAggregatedResult<OutputType>.
matcher.getAllMatched();

// returns Optional<OutputType> with value present on any match.
matcher.get();

// returns strictly value of type OutputType with defaultValue when input object does not match any case.
matcher.getOrElse(OutputType defaultValue); 
```

* *MatcherBreakResult<R>* holds the state/result of the case either matched or throws an exception, has methods:
    * `int getIndex()` - returns the match-case's index in the matcher block (start from 0).
    * `MatchType getMatchType()` - returns match-case's type.
    * `R getValue()` - returns the value after applying action function, will be `null` on exception.
    * `MatcherException getException()` - returns the matcher exception if encountered, will be `null` on successful match action.

* *MatcherAggregatedResult<R>* holds the states/results of all the cases matched in the matcher, has methods:
    * `List<MatcherBreakResult<R>> getResults()` - returns all the matched/failed match-cases' results.

##### NOTES
* The matcher computation is lazy and will not start until `.get()` or `.getOrElse()` invoked on it.

Notable features it's lacking in comparison with true pattern matching (e.g. Scala's or any other functional language OOTB pattern matching):
* Inability to destructure lists (lists should be persistent data structures to keep performance in mind).
* Inability to provide compile time type checking.

#### TODOS
- [x] Add custom exceptions.
- [x] Result caching for a matcher.
- [ ] Standalone generic matcher construction (without having to write a `Function<T, Matcher<T, R>>`).
- [ ] More type safety around predicate-matcher and type-matcher.
