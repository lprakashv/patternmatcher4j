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
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DestructuredMatch implements Match {

  private final Map<String, MField> fieldMatches = new HashMap<>();

  private DestructuredMatch(MField... fields) {
    for (MField field : fields) {
      this.fieldMatches.put(field.getFieldName(), field);
    }
  }

  public static DestructuredMatch of(MField... fields) {
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
    if (fieldMatches.keySet().stream().anyMatch(k -> !allFieldsMap.containsKey(k))) {
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
          matchExceptions.add(
              new FieldException(
                  field.getName(), object, fieldMatches.get(field.getName()).getMatch(), e));
        }
      }
    }

    if (matchExceptions.isEmpty()) {
      return matched;
    } else {
      throw new DestructuredFieldExceptions(index, object, this, matchExceptions);
    }
  }

  public static class MField {

    private final String fieldName;
    private final Match match;

    private MField(String fieldName, Match match) {
      this.fieldName = fieldName;
      this.match = match;
    }

    public static MField with(String fieldName, Class<?> type) {
      return new MField(fieldName, ClassMatch.of(type));
    }

    public static MField with(String fieldName, MField... fields) {
      return new MField(fieldName, DestructuredMatch.of(fields));
    }

    public static MField with(String fieldName, Predicate<Object> predicate) {
      return new MField(fieldName, PredicateMatch.of(predicate));
    }

    public static MField withValue(String fieldName, Object value) {
      return new MField(fieldName, EqualityMatch.of(value));
    }

    public String getFieldName() {
      return fieldName;
    }

    public Match getMatch() {
      return match;
    }
  }
}
