package io.github.lprakashv.patternmatcher4j.exceptions;

public class ActionEvaluationException extends MatcherException {

  public ActionEvaluationException(int index, Object matchedObject, String message,
      Throwable cause) {
    super(index, matchedObject, message, cause);
  }
}
