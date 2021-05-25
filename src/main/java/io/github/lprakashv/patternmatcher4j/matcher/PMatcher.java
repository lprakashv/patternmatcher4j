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
import java.util.function.Supplier;

public class PMatcher<T, R> {

  private final T matchedObject;
  private final List<CaseAction<T, R>> caseActions;
  private final AtomicReference<Optional<PMatcherResult<R>>> resultRef =
      new AtomicReference<>(null);

  public PMatcher(T matchedObject) {
    this.matchedObject = matchedObject;
    this.caseActions = new ArrayList<>();
  }

  public PMatcher(T matchedObject, List<CaseAction<T, R>> caseActions) {
    this.matchedObject = matchedObject;
    this.caseActions = caseActions;
  }

  // ---- match cases:

  public CaseActionAppender matchCase(Class<?> clazz) {
    return new CaseActionAppender(ClassMatch.of(clazz));
  }

  public CaseActionAppender matchCase(DestructuredMatch.MField... fields) {
    return new CaseActionAppender(DestructuredMatch.of(fields));
  }

  public CaseActionAppender matchCase(Predicate<Object> predicate) {
    return new CaseActionAppender(PredicateMatch.of(predicate));
  }

  public CaseActionAppender matchValue(Object value) {
    return new CaseActionAppender(EqualityMatch.of(value));
  }

  public CaseActionAppender matchRef(Object value) {
    return new CaseActionAppender(EqualityMatch.ofRef(value));
  }

  // ---- safe match results:

  public Optional<PMatcherResult<R>> getFirstMatch() {
    if (resultRef.get() != null) {
      return resultRef.get();
    }

    PMatcherResult<R> firstMatchResult = null;

    int index = 0;
    for (CaseAction<T, R> caseAction : this.caseActions) {
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
      index++;
    }
    if (firstMatchResult == null) {
      resultRef.set(Optional.empty());
    } else {
      resultRef.set(Optional.of(firstMatchResult));
    }
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

  // ---- internal classes below:

  class CaseActionAppender {

    private final Match match;

    private CaseActionAppender(Match match) {
      this.match = match;
    }

    public PMatcher<T, R> thenTransform(Function<T, R> fn) {
      caseActions.add(new CaseAction<>(match, fn));
      return new PMatcher<>(matchedObject, caseActions);
    }

    public PMatcher<T, R> thenSupply(Supplier<R> sup) {
      caseActions.add(new CaseAction<>(match, t -> sup.get()));
      return new PMatcher<>(matchedObject, caseActions);
    }

    public PMatcher<T, R> thenReturn(R retVal) {
      caseActions.add(new CaseAction<>(match, t -> retVal));
      return new PMatcher<>(matchedObject, caseActions);
    }
  }
}
