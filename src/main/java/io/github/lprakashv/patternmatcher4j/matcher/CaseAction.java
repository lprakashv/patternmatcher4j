package io.github.lprakashv.patternmatcher4j.matcher;

import io.github.lprakashv.patternmatcher4j.match.Match;
import java.util.function.Function;

class CaseAction<T, R> {

  final Match matchCase;
  final Function<T, R> action;

  CaseAction(Match matchCase, Function<T, R> action) {
    this.matchCase = matchCase;
    this.action = action;
  }
}
