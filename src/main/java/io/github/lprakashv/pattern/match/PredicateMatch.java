package io.github.lprakashv.pattern.match;

import java.util.function.Function;

public class PredicateMatch extends TypeMatch {

  private Function<Object, Boolean> predicate;

  public PredicateMatch(Class<?> type, Function<Object, Boolean> predicate) {
    super(type);
    this.predicate = predicate;
  }

  public static PredicateMatch with(Class<?> type, Function<Object, Boolean> predicate) {
    return new PredicateMatch(type, predicate);
  }

  @Override
  public boolean matches(Object object) {
    return super.matches(object) && predicate.apply(object);
  }
}
