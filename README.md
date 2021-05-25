[![GitHub](https://img.shields.io/github/license/lprakashv/patternmatcher4j?style=flat-square)](LICENSE)
[![Travis (.com) branch](https://img.shields.io/travis/com/lprakashv/patternmatcher4j/master?style=flat-square)](https://travis-ci.com/lprakashv/patternmatcher4j)
[![Coveralls github branch](https://img.shields.io/coveralls/github/lprakashv/patternmatcher4j/master?style=flat-square)](https://coveralls.io/github/lprakashv/patternmatcher4j?branch=master)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.lprakashv/patternmatcher4j?style=flat-square)](https://search.maven.org/search?q=g:%22io.github.lprakashv%22%20AND%20a:%22patternmatcher4j%22)

# patternmatcher4j "Switch-Case on Steroids"

An FP style pattern matcher library for Java. Pattern matching is one of the the single most popular concept in
functional programming. This is inpired
from [Scala's pattern matching](https://docs.scala-lang.org/tour/pattern-matching.html).

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

**[See Javadoc](https://lprakashv.github.io/patternmatcher4j/)** \[ __Outdated (need to fix!)__ \]

## Concept

This is just like switch-case block, but you can do much more than just matching primitive values and enums.

Here, we create a *"Matcher"* for an object (a Java POJO), for which we define *"Match-cases"* which can be of following
type:

1. **Predicate** - a function which takes an object (of the same type of matched object) as input and returns boolean to
   check if the object matches or not.
2. **Equality** - 2 types: (1) exact value Match using Java's `equals()` method and (2) reference match using `==`.
3. **Class** - to Match specific class `Class<?>` of the matched object.
4. **Destructured** - This is a more advanced Match, here we can Match each field with a Match of their own (recursive
   nested matching possible!) of the matched object and can define criteria on each field to qualify as a Match.

For each **"Match-case"**, we define an **"action"** to perform on original matched object.

All the **Match-case** and their corresponding **action** are stored in the matcher's state. There will be no
computation (matching and evaluation) until we evaluate the matcher. Evaluation operations are:

* **Safe evaluations**:
    * `.findFirstMatch()` - returns object of type `Option<MatcherResult<R>>`, present in case if any first case matched
      or exception occurred.
* **Unsafe evaluations** (throws `MatcherException` on error):
    * `.get()` - return `Option<R>`, present in case if any first case matched.
    * `.getOrElse(R defaultValue)`, returns the matched action value or the default value if none of the case matches.

### Example

```java
// Assume some Person objects as : Person(name, age, eligible, Object extra)

String stringFromObjectPatternMatching = 
        new PMatcher<Object, String>(person)
        .matchRef(lalit)
        .thenReturn("Original one and only Lalit found!")
        
        .matchCase(
            MField.with("name", name -> name != null && ((String)name).toLowerCase().equals("lalit")),
            MField.with("age", age -> age != null && (Integer) age< 60),
            MField.withValue("eligible", true))
        .thenReturn("Young Lalit found")
        
        .matchCase(MField.withValue("age", null))
        .thenReturn("God found")
        
        .matchValue(new Person("Nitin", 26, false))
        .thenTransform(p -> "Uneligible Nitin with age=" + ((Person)p).getAge() + " found")
        
        .matchCase(MField.with("extra", String.class))
        .thenTransform(p -> "Person with String extra found with extra value=" + ((Person)p).getExtra())
        
        .matchCase(NonPerson.class)
        .thenReturn("This is not a person")
        
        .matchCase(p -> p != null && ((Person)p).getAge() > 100)
        .thenReturn("Very old person")
        
        .getOrElse("Unknown");
```

### Creating a matcher

```java
new PMatcher(InputType matchedObject);
```

### Adding Match cases on a matcher

* `.matchCase(MField... fieldMatches)`
    * Destructured object Match (or object fields Match).
    * Multiple Field arguments can be given representing each field's matching.
    * Recursive/nested Match on field using:
        * `Field.with("fieldName", Class<?>|Predicate<Object>|MField...)`.
        * For field value matching `Field.withValue("fieldName", valueObject)`.
    * Each field will have a field name and a Match (nested/recursive matching is also possible!).
* `.matchValue(Object value)`
    * Value Match.
    * To match value equality.
    * NOTE: this is different than `.matchCase()` to have Object value matching.
* `.matchRef(Object value)`
    * Reference Match.
    * To match reference equality.
    * NOTE: this is different than `.matchCase()` to have Object reference matching.
* `.matchCase(Class<?> type)`
    * Class Match.
    * To Match class of the matched object.
* `.matchCase(Predicate<Object> predicate)`
    * Predicate Match.
    * Will Match only if the function returns true for the matched object.

##### NOTES

* Each `.matchCase()`, `.matchValue()` and `.matchRef()` will return an instance of `CaseActionAppender` class.
* `CaseActionAppender` class cannot be instantiated without a `PMatcher` instance.

### Defining actions on "matched" cases

```java
PMatcher<InputType, OutputType> matcher; // some matcher

        matcher
        .matchCase(...)
        .thenTransform((InputType o)-> sometransormation(o) )
        .matchCase(...)
        .thenSupply(() -> {
            R calculatedValue = ...;
            return calculcatedValue;
        })
        .matchCase(...)
        .thenReturn( returnValue );
```

##### NOTES

* Each action (`thenTransform()`, `thenSupply()` and `thenReturn()`) will return a new `Matcher` instance.
* Each action defines the action for "only" the `.matchCase()`, `.matchValue()` or `.matchRef()` immediately preceding it.

### Evaluating a matcher and getting the output

```java
PMatcher<InputType, OutputType> matcher; // some matcher

// returns Optional<MatcherResult<OutputType>> with value present on either any first Match or exception occurred.
        matcher.getFirstMatched();

// returns Optional<OutputType> with value present on any Match.
        matcher.get();

// returns strictly value of type OutputType with defaultValue when input object does not Match any case.
        matcher.getOrElse(OutputType defaultValue); 
```

* *MatcherResult<R>* holds the state/result of the case either matched or throws an exception, has methods:
    * `int getIndex()` - returns the Match-case's index in the matcher block (start from 0).
    * `MatchType getMatchType()` - returns Match-case's type.
    * `R getValue()` - returns the value after applying action function, will be `null` on exception.
    * `MatcherException getException()` - returns the matcher exception if encountered, will be `null` on successful
      Match action.

##### NOTES

* The matcher computation is lazy and will not start until `.get()` or `.getOrElse()` invoked on it.

Notable features it's lacking in comparison with true pattern matching (e.g. Scala's or any other functional language
OOTB pattern matching):

* Inability to destructure lists (lists should be persistent data structures to keep performance in mind).
* Inability to provide compile time type checking.
