package io.github.lprakashv.pattern.match;

import io.github.lprakashv.pattern.Pattern;
import java.util.Objects;

public class ValueMatch implements Pattern {

  private Object value;

  public ValueMatch(Object value) {
    this.value = value;
  }

  public static ValueMatch with(Object value) {
    return new ValueMatch(value);
  }

  @Override
  public boolean matches(Object object) {
    return Objects.equals(value, object);
  }
}
