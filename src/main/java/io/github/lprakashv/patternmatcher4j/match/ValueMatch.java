package io.github.lprakashv.patternmatcher4j.match;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;
import java.util.Objects;

public class ValueMatch implements Match {

  private final Object value;

  private ValueMatch(Object value) {
    this.value = value;
  }

  public static ValueMatch of(Object value) {
    return new ValueMatch(value);
  }

  protected Object getValue() {
    return this.value;
  }

  @Override
  public MatchType getMatchType() {
    return MatchType.VALUE;
  }

  @Override
  public boolean matches(int index, Object object) {
    return Objects.equals(this.getValue(), object);
  }
}
