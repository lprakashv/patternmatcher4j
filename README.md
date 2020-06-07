# java-pattern-matcher
An FP style pattern matcher library for Java

### Example
```java
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
  )).then(p -> "Human with name = Lalit")
  .caseMatch(DestructuredMatch.of(
    FieldMatch.with("age", ValueMatch.with(null))
  )).then(p -> "God!")
  .evaluate("Unknown");
```
