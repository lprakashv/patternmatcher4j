package io.github.lprakashv.patternmatcher4j.matcher;

import io.github.lprakashv.patternmatcher4j.match.Match;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class CaseActionAppender<T, R> {

    private final T matchedObject;
    private final List<CaseAction<T, R>> caseActions;
    private final Match match;

    CaseActionAppender(T matchedObject, List<CaseAction<T, R>> caseActions, Match match) {
        this.matchedObject = matchedObject;
        this.caseActions = caseActions;
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