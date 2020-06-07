# java-pattern-matcher
An FP style pattern matcher library for Java

### Example
```java
// Assume some Person objects as : Person(name, age, eligible)

String evaluatedActionAfterProperMatch = 
  Matcher.<Person, String>matches(person)
    //destructuring of the object
    .caseMatch(DestructuredMatch.of(
      FieldMatch.with(
        "name", PredicateMatch.with(
          String.class,
          n -> ((String) n).toLowerCase().equals("lalit")
        )
      ),
      FieldMatch.with(
        "age",
        PredicateMatch.with(Integer.class, a -> (Integer) a < 60)
      ),
      FieldMatch.with("eligible", ValueMatch.with(true))
  ))
  .then(p -> "Human with name = Lalit")
  .caseMatch(DestructuredMatch.of(
    FieldMatch.with("age", ValueMatch.with(null))
  ))
  .then(p -> "God!")
  .evaluate("Unknown");
```

We can use following matches:
* `DestructuredMatch.of(FieldMatch... fieldMatches)` with each fields name and a pattern (nested matching possible!) defined.
* `ValueMatch.with(Object value)`.
* `TypeMatch.with(Class<?> type)`.
* `PredicateMatch.with(Class<?> type, Function<Object, Boolean> predicate)` this extends TypeMatch, which makes sense as we will have to cast the argument in the predicate function.
