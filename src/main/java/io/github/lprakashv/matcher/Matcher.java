package io.github.lprakashv.matcher;

import io.github.lprakashv.pattern.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Matcher<T, R> {

  private final T baseObject;
  private List<PatternAction<T, R>> patternActions = new ArrayList<>();

  public Matcher(T baseObject) {
    this.baseObject = baseObject;
  }

  public static <T1, R1> Matcher<T1, R1> matches(T1 baseObject) {
    return new Matcher<>(baseObject);
  }

  public MatchActionAppender<T, R> caseMatch(Pattern pattern) {
    MatchActionAppender<T, R> matchActionAppender = new MatchActionAppender<>(this);
    this.patternActions.add(new PatternAction<>(pattern, matchActionAppender));
    return matchActionAppender;
  }

  public R evaluate(R defaultValue) {
    for (PatternAction<T, R> patternAction : this.patternActions) {
      if (patternAction.pattern.matches(this.baseObject)) {
        return patternAction.actionAppender.action.apply(this.baseObject);
      }
    }
    return defaultValue;
  }

  public R evaluate() {
    return evaluate(null);
  }

  static class MatchActionAppender<T1, R1> {

    private Matcher<T1, R1> matcher;
    private Function<T1, R1> action;

    MatchActionAppender(Matcher<T1, R1> matcher) {
      this.matcher = matcher;
    }

    Matcher<T1, R1> then(Function<T1, R1> action) {
      this.action = action;
      return this.matcher;
    }
  }

  private static class PatternAction<T1, R1> {

    Pattern pattern;
    MatchActionAppender<T1, R1> actionAppender;

    public PatternAction(Pattern pattern, MatchActionAppender<T1, R1> actionAppender) {
      this.pattern = pattern;
      this.actionAppender = actionAppender;
    }
  }
}