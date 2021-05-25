package io.github.lprakashv.patternmatcher4j.matcher.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Person implements Cloneable {

  private String name;
  private Integer age;
  private Boolean eligible;
  private Object extra;

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
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}