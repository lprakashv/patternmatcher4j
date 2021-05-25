package io.github.lprakashv.patternmatcher4j.matcher;

import io.github.lprakashv.patternmatcher4j.exceptions.ActionEvaluationException;
import io.github.lprakashv.patternmatcher4j.exceptions.MatchException;
import io.github.lprakashv.patternmatcher4j.exceptions.PMatcherException;
import io.github.lprakashv.patternmatcher4j.match.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

public class PMatcher<T, R> {

  private final T matchedObject;
  private final List<CaseAction<T, R>> caseActions;
  private final AtomicReference<Optional<PMatcherResult<R>>> resultRef =
      new AtomicReference<>(Optional.empty());

  public PMatcher(T matchedObject) {
    this.matchedObject = matchedObject;
    this.caseActions = new ArrayList<>();
  }

  public PMatcher(T matchedObject, List<CaseAction<T, R>> caseActions) {
    this.matchedObject = matchedObject;
    this.caseActions = caseActions;
  }

  // ---- match cases:

  public CaseActionAppender<T, R> matchCase(Class<?> clazz) {
    return new CaseActionAppender<>(matchedObject, caseActions, ClassMatch.of(clazz));
  }

  public CaseActionAppender<T, R> matchCase(DestructuredMatch.MField... fields) {
    return new CaseActionAppender<>(matchedObject, caseActions, DestructuredMatch.of(fields));
  }

  public CaseActionAppender<T, R> matchCase(Predicate<Object> predicate) {
    return new CaseActionAppender<>(matchedObject, caseActions, PredicateMatch.of(predicate));
  }

  public CaseActionAppender<T, R> matchValue(Object value) {
    return new CaseActionAppender<>(matchedObject, caseActions, EqualityMatch.of(value));
  }

  public CaseActionAppender<T, R> matchRef(Object value) {
    return new CaseActionAppender<>(matchedObject, caseActions, EqualityMatch.ofRef(value));
  }

  // ---- safe match results:

  public Optional<PMatcherResult<R>> getFirstMatch() {
    if (resultRef.get().isPresent()) {
      return resultRef.get();
    }

    PMatcherResult<R> firstMatchResult = null;

    for (int index = 0; index < this.caseActions.size(); index++) {
      CaseAction<T, R> caseAction = this.caseActions.get(index);
      try {
        if (caseAction.matchCase.matches(index, this.matchedObject)) {
          firstMatchResult = onMatchReturn(index, caseAction.matchCase, caseAction.action);
          break;
        }
      } catch (MatchException e) {
        firstMatchResult =
            new PMatcherResult<>(index, caseAction.matchCase.getMatchType(), null, e);
        break;
      }
    }
    resultRef.set(Optional.ofNullable(firstMatchResult));
    return resultRef.get();
  }

  // ---- unsafe match results (throws exceptions):

  public Optional<R> get() throws PMatcherException {
    Optional<PMatcherResult<R>> matcherResult = getFirstMatch();
    if (matcherResult.isPresent()) {
      if (matcherResult.get().getException() != null) {
        throw matcherResult.get().getException();
      }
      return Optional.of(matcherResult.get().getValue());
    }

    return Optional.empty();
  }

  public R getOrElse(R defaultValue) throws PMatcherException {
    return get().orElse(defaultValue);
  }

  // ---- private methods below:

  private PMatcherResult<R> onMatchReturn(int index, Match match, Function<T, R> action) {
    try {
      return new PMatcherResult<>(index, match.getMatchType(), action.apply(matchedObject), null);
    } catch (Exception e) {
      return new PMatcherResult<>(
          index,
          match.getMatchType(),
          null,
          new ActionEvaluationException(
              index, matchedObject, "Failed to evaluate action at index: " + index, e));
    }
  }
}
