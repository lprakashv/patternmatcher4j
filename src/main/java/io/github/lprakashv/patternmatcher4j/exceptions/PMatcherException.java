package io.github.lprakashv.patternmatcher4j.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class PMatcherException extends Exception {

  private final int index;
  private final transient Object matchedObject;

  public PMatcherException(int index, Object matchedObject) {
    this.index = index;
    this.matchedObject = matchedObject;
  }

  public PMatcherException(int index, Object matchedObject, String message) {
    super(message);
    this.index = index;
    this.matchedObject = matchedObject;
  }

  public PMatcherException(int index, Object matchedObject, String message, Throwable cause) {
    super(message, cause);
    this.index = index;
    this.matchedObject = matchedObject;
  }

  public PMatcherException(int index, Object matchedObject, Throwable cause) {
    super(cause);
    this.index = index;
    this.matchedObject = matchedObject;
  }

  public PMatcherException(
      int index,
      Object matchedObject,
      String message,
      Throwable cause,
      boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.index = index;
    this.matchedObject = matchedObject;
  }

  @Override
  public String getMessage() {
    StringBuilder sb = new StringBuilder();
    Map<String, String> errorDetails = new LinkedHashMap<>();

    errorDetails.put("matched_object", Objects.toString(matchedObject));

    if (index > -1) {
      errorDetails.put("matcher_index", String.valueOf(index));
    }

    if (this instanceof MatchException) {
      errorDetails.put(
          "match_type", "\"" + ((MatchException) this).getMatch().getMatchType().name() + "\"");

      if (this instanceof DestructuredFieldExceptions) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldException fieldException :
            ((DestructuredFieldExceptions) this).getFieldExceptions()) {
          fieldErrors.put(fieldException.getFieldName(), "\"" + fieldException.getMessage() + "\"");
        }
        errorDetails.put("field_errors", fieldErrors.toString());
      }
    } else {
      sb.append("Error in Action. ");
      errorDetails.put("action_exception", this.getCause().getMessage());
    }

    sb.append("error_details: ").append(errorDetails.toString());

    return sb.toString();
  }
}
