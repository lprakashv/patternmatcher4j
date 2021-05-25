package testpackage;

import io.github.lprakashv.patternmatcher4j.exceptions.PMatcherException;
import io.github.lprakashv.patternmatcher4j.match.DestructuredMatch.MField;
import io.github.lprakashv.patternmatcher4j.matcher.PMatcher;
import org.junit.Assert;
import org.junit.Test;

public class PMatcherTest {

  Person lalit = new Person("Lalit", 28, true);
  Person nitin = new Person("Nitin", 26, false);
  Person god = new Person("God", null, true);
  Person extraPerson = new Person("Extra", -1, false, "Something extra");
  Person veryOld = new Person("Gary", 101, true);

  private PMatcher<Object, String> createPatternMatchForPerson(Object person) {
    return new PMatcher<Object, String>(person)
        .matchRef(lalit)
        .thenReturn("Original one and only Lalit found!")
        .matchCase( // any number of field matches
            // this is a field with predicate match with Function<Object, Boolean> passed
            MField.with(
                "name", name -> name != null && ((String) name).toLowerCase().equals("lalit")),
            MField.with("age", age -> age != null && (Integer) age < 60),
            // this is field with value match with .withValue method
            MField.withValue("eligible", true))
        // every matchCase has to be followed by an action. matchCase() returns an instance of
        // CaseActionAppender
        .thenReturn("Young Lalit found")
        // .action of CaseActionAppender will return a new Matcher with appended action
        .matchCase(MField.withValue("age", null))
        .thenReturn("God found")
        // this is a value match
        .matchValue(new Person("Nitin", 26, false))
        .thenTransform(p -> "Uneligible Nitin with age=" + ((Person) p).getAge() + " found")
        // this is field with type match
        .matchCase(MField.with("extra", String.class))
        .thenTransform(
            p -> "testpackage.Person with String extra found with extra value=" + ((Person) p).getExtra())
        // this is type match
        .matchCase(NonPerson.class)
        .thenReturn("This is not a person")
        .matchCase(p -> p != null && ((Person) p).getAge() > 100)
        .thenReturn("Very old person");
    // we can use get() which will return Optional<R>
  }

  @Test
  public void testMatchFieldPredicate() throws PMatcherException, CloneNotSupportedException {
    String result = createPatternMatchForPerson(lalit.clone()).getOrElse("Unknown");

    Assert.assertEquals("Young Lalit found", result);
  }

  @Test
  public void testMatchFieldValue() throws PMatcherException {
    String result = createPatternMatchForPerson(god).getOrElse("Unknown");

    Assert.assertEquals("God found", result);
  }

  @Test
  public void testMatchType() throws PMatcherException {
    String result = createPatternMatchForPerson(new NonPerson()).getOrElse("Unknown");

    Assert.assertEquals("This is not a person", result);
  }

  @Test
  public void testMatchValue() throws PMatcherException {
    String result = createPatternMatchForPerson(nitin).getOrElse("Unknown");

    Assert.assertEquals("Uneligible Nitin with age=26 found", result);
  }

  @Test
  public void testMatchFieldType() throws PMatcherException {
    String result = createPatternMatchForPerson(extraPerson).getOrElse("Unknown");

    Assert.assertEquals("testpackage.Person with String extra found with extra value=Something extra", result);
  }

  @Test
  public void testMatchPredicate() throws PMatcherException {
    String result = createPatternMatchForPerson(veryOld).getOrElse("Unknown");

    Assert.assertEquals("Very old person", result);
  }

  @Test
  public void testMatchDefault() throws PMatcherException {
    String result = createPatternMatchForPerson(new Person("", 0, true)).getOrElse("Unknown");

    Assert.assertEquals("Unknown", result);
  }

  @Test
  public void testMatchRef() throws PMatcherException {
    String result = createPatternMatchForPerson(lalit).getOrElse("Unknown");

    Assert.assertEquals("Original one and only Lalit found!", result);
  }
}
