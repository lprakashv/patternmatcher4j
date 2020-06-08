
# java-pattern-matcher
An FP style pattern matcher library for Java

### Example
```java
// Assume some Person objects as : Person(name, age, eligible, Object extra)

String evaluatedActionAfterProperMatch = 
    Matcher.<Person, String>match((Object) person)
        .matchCase( // any number of field matches
            // this is a preficate match with Function<Object, Boolean> passed
            Field.with("name", name -> ((String) name).toLowerCase().equals("lalit")),
            Field.with("age", age -> (Integer) age < 60),
            // this is value match with .withValue method
            Field.withValue("eligible", true)
        )
        // every matchCase has to be followed by an action. matchCase() returns an instance of CaseActionAppender
        .action(p -> "Young Lalit found")
        // .action of CaseActionAppender will return a new Matcher with appended action
        .matchCase(Field.withValue("age", null))
        .action(p -> "God found")
        // this is a value matcher
        .matchValue(new Person("Nitin", 26, false))
        .action(p -> "Uneligible Nitin with age=26 found")
        // this is field a type matcher
        .matchCase(
            Field.with("extra", String.class)
        )
        .action(p -> "Person with String extra found with extra value=" + p.extra)
        // this is type matcher
        .matchCase(NonPerson.class)
        .action(p -> "This is not a person")
        // we can use get() which will return Optional<R>
        .getOrElse("Unknown");
```

We can use following matches:
* `DestructuredMatch.of(FieldMatch... fieldMatches)` with each fields name and a pattern (nested matching possible!) defined.
* `ValueMatch.with(Object value)`.
* `TypeMatch.with(Class<?> type)`.
* `PredicateMatch.with(Class<?> type, Function<Object, Boolean> predicate)` this extends TypeMatch, which makes sense as we will have to cast the argument in the predicate function.
