package io.github.lprakashv.matcher;

import io.github.lprakashv.match.DestructuredMatch.Field;
import io.github.lprakashv.matcher.models.NonPerson;
import io.github.lprakashv.matcher.models.Person;
import org.junit.Assert;
import org.junit.Test;

public class MatcherTest {

  Person lalit = new Person("Lalit", 28, true);
  Person nitin = new Person("Nitin", 26, false);
  Person god = new Person("God", null, true);
  Person extraPerson = new Person("Extra", -1, false, "Something extra");
  Person veryOld = new Person("Gary", 101, true);

  private Matcher<Object, String> createPatternMatchForPerson(Object person) {
    return Matcher
        .<Object, String>matchFor((Object) person)
        .matchCase( // any number of field matches
            // this is a field with predicate match with Function<Object, Boolean> passed
            Field.with("name", name -> ((String) name).toLowerCase().equals("lalit")),
            Field.with("age", age -> (Integer) age < 60),
            // this is field with value match with .withValue method
            Field.withValue("eligible", true)
        )
        // every matchCase has to be followed by an action. matchCase() returns an instance of CaseActionAppender
        .action(p -> "Young Lalit found")
        // .action of CaseActionAppender will return a new Matcher with appended action
        .matchCase(Field.withValue("age", null))
        .action(p -> "God found")
        // this is a value match
        .matchValue(new Person("Nitin", 26, false))
        .action(p -> "Uneligible Nitin with age=26 found")
        // this is field with type match
        .matchCase(
            Field.with("extra", String.class)
        )
        .action(p -> "Person with String extra found with extra value=" + ((Person) p).extra)
        // this is type match
        .matchCase(NonPerson.class)
        .action(p -> "This is not a person")
        .matchCase(p -> ((Person) p).age > 100)
        .action(p -> "Very old person");
    // we can use get() which will return Optional<R>
  }

  @Test
  public void testMatchFieldPredicate() {
    String result = createPatternMatchForPerson(lalit)
        .getOrElse("Unknown");

    Assert.assertEquals("Young Lalit found", result);
  }

  @Test
  public void testMatchFieldValue() {
    String result = createPatternMatchForPerson(god)
        .getOrElse("Unknown");

    Assert.assertEquals("God found", result);
  }

  @Test
  public void testMatchType() {
    String result = createPatternMatchForPerson(new NonPerson())
        .getOrElse("Unknown");

    Assert.assertEquals("This is not a person", result);
  }

  @Test
  public void testMatchValue() {
    String result = createPatternMatchForPerson(nitin)
        .getOrElse("Unknown");

    Assert.assertEquals("Uneligible Nitin with age=26 found", result);
  }

  @Test
  public void testMatchFieldType() {
    String result = createPatternMatchForPerson(extraPerson)
        .getOrElse("Unknown");

    Assert.assertEquals("Person with String extra found with extra value=Something extra", result);
  }

  @Test
  public void testMatchPredicate() {
    String result = createPatternMatchForPerson(veryOld)
        .getOrElse("Unknown");

    Assert.assertEquals("Very old person", result);
  }

  @Test
  public void testMatchDefault() {
    String result = createPatternMatchForPerson(new Person("", 0, true))
        .getOrElse("Unknown");

    Assert.assertEquals("Unknown", result);
  }
}
