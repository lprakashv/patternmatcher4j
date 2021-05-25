package io.github.lprakashv.patternmatcher4j.matcher;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;
import io.github.lprakashv.patternmatcher4j.exceptions.PMatcherException;

public class PMatcherResult<R> {

  private final int index;
  private final MatchType matchType;
  private final R value;
  private final PMatcherException exception;

  PMatcherResult(int index, MatchType matchType, R value, PMatcherException exception) {
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

  public PMatcherException getException() {
    return exception;
  }
}
