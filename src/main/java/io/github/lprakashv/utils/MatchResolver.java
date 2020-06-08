package io.github.lprakashv.utils;

import io.github.lprakashv.match.DestructuredMatch;
import io.github.lprakashv.match.DestructuredMatch.Field;
import io.github.lprakashv.match.Match;
import io.github.lprakashv.match.PredicateMatch;
import io.github.lprakashv.match.TypeMatch;
import io.github.lprakashv.match.ValueMatch;
import java.util.function.Function;

public class MatchResolver {

  public static Match resolve(Object object) {
    if (object == null) {
      return ValueMatch.of(null);
    } else if (object instanceof Class) {
      return TypeMatch.of((Class<?>) object);
    } else if (object instanceof Field[]) {
      return DestructuredMatch.of((Field[]) object);
    } else if (object instanceof Function) {
      try {
        return PredicateMatch.of((Function<Object, Boolean>) object);
      } catch (ClassCastException cce) {
        //TODO
        return PredicateMatch.of(o -> false);
      }
    } else {
      return ValueMatch.of(object);
    }

  }
}
