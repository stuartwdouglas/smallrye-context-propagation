package io.smallrye.context.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.context.spi.ThreadContextProvider;
import org.eclipse.microprofile.context.spi.ThreadContextSnapshot;

import io.smallrye.context.ContextBoundary;

public class CapturedContextState {

    public static CapturedContextState EMPTY = new CapturedContextState();

    private final List<ThreadContextSnapshot> threadContext = new LinkedList<>();

    public CapturedContextState(ThreadContextProviderPlan plan,
            Map<String, String> props) {
        for (ThreadContextProvider provider : plan.propagatedProviders) {
            ThreadContextSnapshot snapshot = provider.currentContext(props);
            if (snapshot != null) {
                threadContext.add(snapshot);
            }
        }
        for (ThreadContextProvider provider : plan.clearedProviders) {
            ThreadContextSnapshot snapshot = provider.clearedContext(props);
            if (snapshot != null) {
                threadContext.add(snapshot);
            }
        }
    }

    private CapturedContextState() {
    }

    public ActiveContextState begin() {
        if (this == ContextBoundary.currentState()) {
            //current context is already the same, this is a NOOP
            return EMPTY.begin();
        } else {
            return new ActiveContextState(threadContext);
        }
    }
}
