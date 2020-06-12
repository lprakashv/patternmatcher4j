package io.github.lprakashv.patternmatcher4j.matcher;

import io.github.lprakashv.patternmatcher4j.exceptions.ActionEvaluationException;
import io.github.lprakashv.patternmatcher4j.exceptions.MatchException;
import io.github.lprakashv.patternmatcher4j.exceptions.MatcherException;
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

  public static <T1, R1> Matcher<T1, R1> matchFor(T1 matchedObject) {
    return new Matcher<>(matchedObject);
  }

  // ---- match cases:

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

  // ---- safe match results:

  public MatcherAggregatedResult<R> getAllMatches() {
    List<MatcherBreakResult<R>> breakResults = new ArrayList<>();

    int index = 0;
    for (CaseAction<T, R> caseAction : this.caseActions) {
      try {
        if (caseAction.matchCase.matches(index, this.matchedObject)) {
          onMatchAdd(breakResults, index, caseAction.matchCase, caseAction.action);
        }
      } catch (MatchException e) {
        breakResults.add(new MatcherBreakResult<>(
            index,
            caseAction.matchCase.getMatchType(),
            null,
            e
        ));
      }
      index++;
    }

    return new MatcherAggregatedResult<>(breakResults);
  }

  public Optional<MatcherBreakResult<R>> getFirstMatch() {
    int index = 0;
    for (CaseAction<T, R> caseAction : this.caseActions) {
      try {
        if (caseAction.matchCase.matches(index, this.matchedObject)) {
          return onMatchReturn(index, caseAction.matchCase, caseAction.action);
        }
      } catch (MatchException e) {
        return Optional.of(new MatcherBreakResult<>(
            index,
            caseAction.matchCase.getMatchType(),
            null,
            e
        ));
      }
      index++;
    }
    return Optional.empty();
  }

  // ---- unsafe match results (throws exceptions):

  public Optional<R> get() throws MatcherException {
    Optional<MatcherBreakResult<R>> matcherBreakResult = getFirstMatch();
    if (matcherBreakResult.isPresent()) {
      if (matcherBreakResult.get().getException() != null) {
        throw matcherBreakResult.get().getException();
      }
      return Optional.of(matcherBreakResult.get().getValue());
    }

    return Optional.empty();
  }

  public R getOrElse(R defaultValue) throws MatcherException {
    return get().orElse(defaultValue);
  }

  // ---- private methods below:

  private Matcher(T matchedObject) {
    this.matchedObject = matchedObject;
    this.caseActions = new ArrayList<>();
  }

  private Matcher(T matchedObject, List<CaseAction<T, R>> caseActions) {
    this.matchedObject = matchedObject;
    this.caseActions = caseActions;
  }

  private void onMatchAdd(List<MatcherBreakResult<R>> breakResults, int index, Match match,
      Function<T, R> action) {
    try {
      breakResults.add(new MatcherBreakResult<>(
          index,
          match.getMatchType(),
          action.apply(this.matchedObject),
          null
      ));
    } catch (Exception e) {
      breakResults.add(new MatcherBreakResult<>(
          index,
          match.getMatchType(),
          null,
          new ActionEvaluationException(index, matchedObject,
              "Failed to evaluate action at index: " + index, e)
      ));
    }
  }

  private Optional<MatcherBreakResult<R>> onMatchReturn(int index, Match match,
      Function<T, R> action) {
    try {
      return Optional.of(new MatcherBreakResult<>(
          index,
          match.getMatchType(),
          action.apply(this.matchedObject),
          null
      ));
    } catch (Exception e) {
      return Optional.of(new MatcherBreakResult<>(
          index,
          match.getMatchType(),
          null,
          new ActionEvaluationException(index, matchedObject,
              "Failed to evaluate action at index: " + index, e)
      ));
    }
  }

  // ---- internal classes below:

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
}