package io.github.lprakashv.patternmatcher4j.match;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;

import java.util.Objects;

public class EqualityMatch implements Match {

  private final Object value;
  private final boolean refMatch;

  private EqualityMatch(Object value, boolean refMatch) {
    this.value = value;
    this.refMatch = refMatch;
  }

  public static EqualityMatch of(Object value) {
    return new EqualityMatch(value, false);
  }

  public static EqualityMatch ofRef(Object value) {
    return new EqualityMatch(value, true);
  }

  protected Object getValue() {
    return this.value;
  }

  @Override
  public MatchType getMatchType() {
    return (this.refMatch) ? MatchType.REF : MatchType.VALUE;
  }

  @Override
  public boolean matches(int index, Object object) {
    if (this.refMatch) {
      return this.value == object;
    }
    return Objects.equals(this.getValue(), object);
  }
}
