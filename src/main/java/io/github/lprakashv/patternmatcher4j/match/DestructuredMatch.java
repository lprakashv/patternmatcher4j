package io.github.lprakashv.patternmatcher4j.match;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class DestructuredMatch extends Match {

  private final Map<String, Field> fieldMatches = new HashMap<>();

  private DestructuredMatch(Field... fields) {
    for (Field field : fields) {
      this.fieldMatches.put(field.getFieldName(), field);
    }
  }

  public static DestructuredMatch of(Field... fields) {
    return new DestructuredMatch(fields);
  }

  @Override
  protected MatchType getMatchType() {
    return MatchType.DESTRUCTURED;
  }

  @Override
  protected Map<String, Field> getMatch() {
    return this.fieldMatches;
  }

  public static class Field {

    private final String fieldName;
    private final Match match;

    private Field(String fieldName, Match match) {
      this.fieldName = fieldName;
      this.match = match;
    }

    public static Field with(String fieldName, Class<?> type) {
      return new Field(fieldName, TypeMatch.of(type));
    }

    public static Field with(String fieldName, Field... fields) {
      return new Field(fieldName, DestructuredMatch.of(fields));
    }

    public static Field with(String fieldName, Function<Object, Boolean> predicate) {
      return new Field(fieldName, PredicateMatch.of(predicate));
    }

    public static Field withValue(String fieldName, Object value) {
      return new Field(fieldName, ValueMatch.of(value));
    }

    public String getFieldName() {
      return fieldName;
    }

    public Match getMatch() {
      return match;
    }
  }
}
