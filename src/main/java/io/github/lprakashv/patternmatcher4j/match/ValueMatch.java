package io.github.lprakashv.patternmatcher4j.match;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;

public class ValueMatch extends Match {

  private final Object value;

  private ValueMatch(Object value) {
    this.value = value;
  }

  public static ValueMatch of(Object value) {
    return new ValueMatch(value);
  }

  @Override
  protected MatchType getMatchType() {
    return MatchType.VALUE;
  }

  @Override
  protected Object getMatch() {
    return this.value;
  }
}
