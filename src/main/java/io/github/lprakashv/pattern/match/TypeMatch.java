package io.github.lprakashv.pattern.match;

import io.github.lprakashv.pattern.Pattern;

public class TypeMatch implements Pattern {

  private Class<?> type;

  public TypeMatch(Class<?> type) {
    this.type = type;
  }

  public static TypeMatch with(Class<?> type) {
    return new TypeMatch(type);
  }

  @Override
  public boolean matches(Object object) {
    return (object != null && object.getClass() == type);
  }
}
