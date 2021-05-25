package io.github.lprakashv.patternmatcher4j.exceptions;

public class ActionEvaluationException extends PMatcherException {

  public ActionEvaluationException(
      int index, Object matchedObject, String message, Throwable cause) {
    super(index, matchedObject, message, cause);
  }
}
