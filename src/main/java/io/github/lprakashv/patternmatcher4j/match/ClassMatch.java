package io.github.lprakashv.patternmatcher4j.match;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;

public class ClassMatch implements Match {

  private final Class<?> clazz;

  public ClassMatch(Class<?> clazz) {
    this.clazz = clazz;
  }

  public static ClassMatch of(Class<?> type) {
    return new ClassMatch(type);
  }

  protected Class<?> getTypeClass() {
    return clazz;
  }

  @Override
  public MatchType getMatchType() {
    return MatchType.TYPE;
  }

  @Override
  public boolean matches(int index, Object object) {
    return object != null && this.getTypeClass() == object.getClass();
  }
}
