package io.github.lprakashv.patternmatcher4j.matcher;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;
import io.github.lprakashv.patternmatcher4j.exceptions.MatcherException;

public class MatcherBreakResult<R> {

  private final int index;
  private final MatchType matchType;
  private final R value;
  private final MatcherException exception;

  MatcherBreakResult(int index,
      MatchType matchType, R value,
      MatcherException exception) {
    this.index = index;
    this.matchType = matchType;
    this.value = value;
    this.exception = exception;
  }

  public int getIndex() {
    return index;
  }

  public MatchType getMatchType() {
    return matchType;
  }

  public R getValue() {
    return value;
  }

  public MatcherException getException() {
    return exception;
  }
}
