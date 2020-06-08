package io.github.lprakashv.patternmatcher4j.matcher.models;

import java.util.Objects;

public class Person {

  public String name;
  public Integer age;
  public Boolean eligible;
  public Object extra;

  public Person(String name, Integer age, Boolean eligible) {
    this.name = name;
    this.age = age;
    this.eligible = eligible;
    this.extra = null;
  }

  public Person(String name, Integer age, Boolean eligible, Object extra) {
    this.name = name;
    this.age = age;
    this.eligible = eligible;
    this.extra = extra;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Person)) {
      return false;
    }
    Person person = (Person) o;
    return Objects.equals(name, person.name) &&
        Objects.equals(age, person.age) &&
        Objects.equals(eligible, person.eligible) &&
        Objects.equals(extra, person.extra);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, age, eligible, extra);
  }
}