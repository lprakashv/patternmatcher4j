package io.github.lprakashv.matcher.models;

public class Person {

  public String name;
  public Integer age;
  public Boolean eligible;

  public Person(String name, Integer age, Boolean eligible) {
    this.name = name;
    this.age = age;
    this.eligible = eligible;
  }
}