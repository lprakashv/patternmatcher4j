package io.github.lprakashv.patternmatcher4j.match;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;

public class TypeMatch implements Match {

  private final Class<?> type;

  public TypeMatch(Class<?> type) {
    this.type = type;
  }

  public static TypeMatch of(Class<?> type) {
    return new TypeMatch(type);
  }

  protected Class<?> getTypeClass() {
    return type;
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
