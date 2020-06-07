package io.github.lprakashv.pattern.match;

import io.github.lprakashv.pattern.Pattern;

public class FieldMatch {

  private String fieldName;
  private Pattern match;

  public FieldMatch(String fieldName, Pattern match) {
    this.fieldName = fieldName;
    this.match = match;
  }

  public static FieldMatch with(String fieldName, Pattern match) {
    return new FieldMatch(fieldName, match);
  }

  public String getFieldName() {
    return fieldName;
  }

  public Pattern getMatch() {
    return match;
  }
}
