package io.github.lprakashv.patternmatcher4j.exceptions;

import io.github.lprakashv.patternmatcher4j.match.Match;

public class FieldException extends MatchException {

  private final String fieldName;

  public FieldException(String fieldName, Object matchedObject, Match match, Exception underlying) {
    super(-1, matchedObject, match, underlying);
    this.fieldName = fieldName;
  }

  public String getFieldName() {
    return fieldName;
  }
}
