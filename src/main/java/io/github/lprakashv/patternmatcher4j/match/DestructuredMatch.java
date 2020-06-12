package io.github.lprakashv.patternmatcher4j.match;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;
import io.github.lprakashv.patternmatcher4j.exceptions.DestructuredFieldExceptions;
import io.github.lprakashv.patternmatcher4j.exceptions.FieldException;
import io.github.lprakashv.patternmatcher4j.exceptions.MatchException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DestructuredMatch implements Match {

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
  public MatchType getMatchType() {
    return MatchType.DESTRUCTURED;
  }

  @Override
  public boolean matches(int index, Object object) throws MatchException {
    if (object == null) {
      return false;
    }

    java.lang.reflect.Field[] declaredFields = object.getClass().getDeclaredFields();
    Map<String, java.lang.reflect.Field> allFieldsMap =
        Arrays.stream(declaredFields)
            .collect(Collectors.toMap(java.lang.reflect.Field::getName, df -> df));
    if (fieldMatches.keySet().stream()
        .anyMatch(k -> !allFieldsMap.containsKey(k))) {
      return false;
    }

    List<FieldException> matchExceptions = new ArrayList<>();
    boolean matched = true;

    for (java.lang.reflect.Field field : declaredFields) {
      field.setAccessible(true);
      if (fieldMatches.containsKey(field.getName())) {
        try {
          matched &= fieldMatches.get(field.getName()).getMatch().matches(field.get(object));
        } catch (IllegalAccessException | MatchException e) {
          matchExceptions
              .add(new FieldException(field.getName(), object, fieldMatches.get(field.getName()).getMatch(), e));
        }
      }
    }

    if (matchExceptions.isEmpty()) {
      return matched;
    } else {
      throw new DestructuredFieldExceptions(index, object, this, matchExceptions);
    }
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
