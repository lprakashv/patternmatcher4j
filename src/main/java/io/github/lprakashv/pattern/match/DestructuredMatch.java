package io.github.lprakashv.pattern.match;

import io.github.lprakashv.pattern.Pattern;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;

public class DestructuredMatch implements Pattern {

  private Map<String, FieldMatch> fieldMatches = new HashMap<>();

  public DestructuredMatch(FieldMatch... fieldMatches) {
    for (FieldMatch fieldMatch : fieldMatches) {
      this.fieldMatches.put(fieldMatch.getFieldName(), fieldMatch);
    }
  }

  public static DestructuredMatch of(FieldMatch... fieldMatches) {
    return new DestructuredMatch(fieldMatches);
  }

  //@SneakyThrows
  @Override
  public boolean matches(Object object) {
    if (object == null) return false;

    return Arrays.stream(object.getClass().getDeclaredFields())
        .allMatch(field -> {
          field.setAccessible(true);
          if (this.fieldMatches.containsKey(field.getName())) {
            try {
              return this.fieldMatches
                  .get(field.getName())
                  .getMatch()
                  .matches(field.get(object));
            } catch (IllegalAccessException e) {
              e.printStackTrace();
              return false;
            }
          } else {
            return true;
          }
        });
  }
}
