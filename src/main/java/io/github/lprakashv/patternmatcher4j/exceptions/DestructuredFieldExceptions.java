package io.github.lprakashv.patternmatcher4j.exceptions;

import io.github.lprakashv.patternmatcher4j.match.Match;

import java.util.List;

public class DestructuredFieldExceptions extends MatchException {

  private final List<FieldException> fieldExceptions;

  public DestructuredFieldExceptions(
      int index, Object matchedObject, Match match, List<FieldException> fieldExceptions) {
    super(index, matchedObject, match, null);
    this.fieldExceptions = fieldExceptions;
  }

  public List<FieldException> getFieldExceptions() {
    return fieldExceptions;
  }
}
