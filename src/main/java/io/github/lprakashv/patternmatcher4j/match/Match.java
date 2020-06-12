package io.github.lprakashv.patternmatcher4j.match;

import io.github.lprakashv.patternmatcher4j.constants.MatchType;
import io.github.lprakashv.patternmatcher4j.exceptions.MatchException;
import java.io.Serializable;

public interface Match extends Serializable {

  MatchType getMatchType();

  boolean matches(int index, Object object) throws MatchException;

  default boolean matches(Object object) throws MatchException {
    return matches(-1, object);
  }
}
