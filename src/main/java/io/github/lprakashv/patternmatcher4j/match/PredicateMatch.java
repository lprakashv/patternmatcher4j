package io.github.lprakashv.patternmatcher4j.match;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;
import io.github.lprakashv.patternmatcher4j.exceptions.MatchException;

import java.util.function.Predicate;

public class PredicateMatch implements Match {

  private final Predicate<Object> predicate;

  private PredicateMatch(Predicate<Object> predicate) {
    this.predicate = predicate;
  }

  public static PredicateMatch of(Predicate<Object> predicate) {
    return new PredicateMatch(predicate);
  }

  protected Predicate<Object> getPredicate() {
    return predicate;
  }

  @Override
  public MatchType getMatchType() {
    return MatchType.PREDICATE;
  }

  @Override
  public boolean matches(int index, Object object) throws MatchException {
    try {
      return this.getPredicate() != null && this.getPredicate().test(object);
    } catch (Exception e) {
      throw new MatchException(index, object, this, e);
    }
  }
}
