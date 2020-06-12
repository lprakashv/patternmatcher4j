package io.github.lprakashv.patternmatcher4j.match;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;
import io.github.lprakashv.patternmatcher4j.exceptions.MatchException;
import java.util.Optional;
import java.util.function.Function;

public class PredicateMatch implements Match {

  private final Function<Object, Boolean> predicate;

  private PredicateMatch(Function<Object, Boolean> predicate) {
    this.predicate = predicate;
  }

  public static PredicateMatch of(Function<Object, Boolean> predicate) {
    return new PredicateMatch(predicate);
  }

  protected Function<Object, Boolean> getPredicate() {
    return predicate;
  }

  @Override
  public MatchType getMatchType() {
    return MatchType.PREDICATE;
  }

  @Override
  public boolean matches(int index, Object object) throws MatchException {
    try {
      return this.getPredicate()
          .andThen(Optional::ofNullable)
          .apply(object)
          .orElse(false);
    } catch (Exception e) {
      throw new MatchException(index, object, this, e);
    }
  }
}
