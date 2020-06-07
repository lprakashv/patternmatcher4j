package io.github.lprakashv.matcher;

import io.github.lprakashv.matcher.models.Person;
import io.github.lprakashv.pattern.match.DestructuredMatch;
import io.github.lprakashv.pattern.match.FieldMatch;
import io.github.lprakashv.pattern.match.PredicateMatch;
import io.github.lprakashv.pattern.match.ValueMatch;
import org.junit.Assert;
import org.junit.Test;

public class MatcherTest {

  Person lalit = new Person("Lalit", 28, true);
  Person nitin = new Person("Nitin", 27, true);
  Person god = new Person("God", null, true);

  private Matcher<Person, String> createPatternMatchForPerson(Person person) {
    return Matcher.<Person, String>matches(person)
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
        )).then(p -> "God!");
  }

  @Test
  public void testMatchHuman() {
    String result = createPatternMatchForPerson(lalit)
        .evaluate("Unknown");

    Assert.assertEquals(result, "Human with name Lalit");
  }

  @Test
  public void testMatchGod() {
    String result = createPatternMatchForPerson(god)
        .evaluate("Unknown");

    Assert.assertEquals(result, "God!");
  }

  @Test
  public void testMatchDefault() {
    String result = createPatternMatchForPerson(nitin)
        .evaluate("Unknown");

    Assert.assertEquals(result, "Unknown");
  }
}
