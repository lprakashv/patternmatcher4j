package io.github.lprakashv.matcher;

import io.github.lprakashv.match.DestructuredMatch.Field;
import io.github.lprakashv.matcher.models.Person;
import org.junit.Assert;
import org.junit.Test;

public class MatcherTest {

  Person lalit = new Person("Lalit", 28, true);
  Person nitin = new Person("Nitin", 27, true);
  Person god = new Person("God", null, true);

  private Matcher<Person, String> createPatternMatchForPerson(Person person) {
    return Matcher.<Person, String>match(person)
        .matchCase(
            Field.with("name", name -> ((String) name).toLowerCase().equals("lalit")),
            Field.with("age", age -> (Integer) age < 60),
            Field.withValue("eligible", true)
        )
        .action(p -> "Young Lalit found")
        .matchCase(Field.withValue("age", null))
        .action(p -> "God found");
  }

  @Test
  public void testMatchHuman() {
    String result = createPatternMatchForPerson(lalit)
        .getOrElse("Unknown");

    Assert.assertEquals("Young Lalit found", result);
  }

  @Test
  public void testMatchGod() {
    String result = createPatternMatchForPerson(god)
        .getOrElse("Unknown");

    Assert.assertEquals("God found", result);
  }

  @Test
  public void testMatchDefault() {
    String result = createPatternMatchForPerson(nitin)
        .getOrElse("Unknown");

    Assert.assertEquals("Unknown", result);
  }
}
