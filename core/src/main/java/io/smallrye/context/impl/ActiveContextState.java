package io.smallrye.context.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.microprofile.context.spi.ThreadContextController;
import org.eclipse.microprofile.context.spi.ThreadContextSnapshot;

import io.smallrye.context.ContextBoundary;

public class ActiveContextState {

    private List<ThreadContextController> activeContext;

    public ActiveContextState(List<ThreadContextSnapshot> threadContext) {
        if (threadContext.isEmpty()) {
            activeContext = Collections.emptyList();
        } else {
            ContextBoundary.contextChanged();
            activeContext = threadContext.isEmpty() ? Collections.emptyList() : new ArrayList<>(threadContext.size());
            for (ThreadContextSnapshot threadContextSnapshot : threadContext) {
                activeContext.add(threadContextSnapshot.begin());
            }
        }
    }

    public void endContext() {
        if (!activeContext.isEmpty()) {
            ContextBoundary.contextChanged();
            // restore in reverse order
            for (int i = activeContext.size() - 1; i >= 0; i--) {
                activeContext.get(i).endContext();
            }
        }
    }
}
