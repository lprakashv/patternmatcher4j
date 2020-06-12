package io.github.lprakashv.patternmatcher4j.exceptions;

import io.github.lprakashv.patternmatcher4j.match.Match;

public class MatchException extends MatcherException {

  private final Match match;

  public MatchException(int index, Object matchedObject, Match match, Exception underlying) {
    super(index, matchedObject, underlying);
    this.match = match;
  }

  public Match getMatch() {
    return match;
  }
}
