package io.github.lprakashv.patternmatcher4j.matcher;

import java.util.Collections;
import java.util.List;

public class MatcherAggregatedResult<R> {
  private final List<MatcherBreakResult<R>> results;

  public MatcherAggregatedResult(
      List<MatcherBreakResult<R>> results) {
    this.results = results;
  }

  public List<MatcherBreakResult<R>> getResults() {
    return Collections.unmodifiableList(results);
  }
}
