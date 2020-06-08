package io.github.lprakashv.match;

import io.github.lprakashv.constants.MatchType;
import java.util.function.Function;

public class PredicateMatch extends Match {

  private final Function<Object, Boolean> predicate;

  private PredicateMatch(Function<Object, Boolean> predicate) {
    this.predicate = predicate;
  }

  public static PredicateMatch of(Function<Object, Boolean> predicate) {
    return new PredicateMatch(predicate);
  }

  @Override
  protected MatchType getMatchType() {
    return MatchType.PREDICATE;
  }

  @Override
  protected Function<Object, Boolean> getMatch() {
    return predicate;
  }
}
