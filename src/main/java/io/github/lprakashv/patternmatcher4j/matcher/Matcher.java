package io.github.lprakashv.patternmatcher4j.matcher;

import io.github.lprakashv.patternmatcher4j.match.DestructuredMatch;
import io.github.lprakashv.patternmatcher4j.match.Match;
import io.github.lprakashv.patternmatcher4j.match.PredicateMatch;
import io.github.lprakashv.patternmatcher4j.match.TypeMatch;
import io.github.lprakashv.patternmatcher4j.match.ValueMatch;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class Matcher<T, R> {

  private final T matchedObject;
  private final List<CaseAction<T, R>> caseActions;

  private Matcher(T matchedObject) {
    this.matchedObject = matchedObject;
    this.caseActions = new ArrayList<>();
  }

  private Matcher(T matchedObject, List<CaseAction<T, R>> caseActions) {
    this.matchedObject = matchedObject;
    this.caseActions = caseActions;
  }

  public static <T1, R1> Matcher<T1, R1> matchFor(T1 matchedObject) {
    return new Matcher<>(matchedObject);
  }

  class CaseActionAppender {

    private Match match;

    private CaseActionAppender(Match match) {
      this.match = match;
    }

    public Matcher<T, R> action(Function<T, R> fn) {
      caseActions.add(new CaseAction<>(match, fn));
      return new Matcher<>(matchedObject, caseActions);
    }
  }

  public CaseActionAppender matchCase(Class<?> type) {
    return new CaseActionAppender(TypeMatch.of(type));
  }

  public CaseActionAppender matchCase(DestructuredMatch.Field... fields) {
    return new CaseActionAppender(DestructuredMatch.of(fields));
  }

  public CaseActionAppender matchCase(Function<Object, Boolean> predicate) {
    return new CaseActionAppender(PredicateMatch.of(predicate));
  }

  public CaseActionAppender matchValue(Object value) {
    return new CaseActionAppender(ValueMatch.of(value));
  }

  public Optional<R> get() {
    for (CaseAction<T, R> caseAction : this.caseActions) {
      if (caseAction.matchCase.matches(this.matchedObject)) {
        return Optional.of(caseAction.action.apply(this.matchedObject));
      }
    }
    return Optional.empty();
  }

  public R getOrElse(R defaultValue) {
    return get().orElse(defaultValue);
  }

  private static class CaseAction<T1, R1> {

    final Match matchCase;
    final Function<T1, R1> action;

    CaseAction(Match matchCase, Function<T1, R1> action) {
      this.matchCase = matchCase;
      this.action = action;
    }
  }
}