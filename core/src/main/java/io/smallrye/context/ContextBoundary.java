package io.smallrye.context;

import java.util.Map;

import io.netty.util.concurrent.FastThreadLocal;
import io.smallrye.context.impl.CapturedContextState;
import io.smallrye.context.impl.ThreadContextProviderPlan;

public class ContextBoundary {

    private static final FastThreadLocal<CapturedContextState> currentContextState = new FastThreadLocal<>();
    private static volatile boolean enabled = false;

    public static void setEnabled(boolean enabled) {
        ContextBoundary.enabled = enabled;
    }

    /**
     * Invoked when something changes the current context state (e.g. starting a transaction).
     *
     */
    public static void contextChanged() {
        currentContextState.remove();
    }

    /**
     * Explicitly indicates that there is currently not context to capture, used at the beginning of a
     * HTTP request before it is dispatched to prevent unnecessary captures.
     */
    public static void setNoContext() {
        currentContextState.set(CapturedContextState.EMPTY);
    }

    public static CapturedContextState currentState() {
        return currentContextState.get();
    }

    public static CapturedContextState capture(ThreadContextProviderPlan plan, Map<String, String> props) {
        if (!enabled) {
            return new CapturedContextState(plan, props);
        } else {
            CapturedContextState current = currentContextState.get();
            if (current == null) {
                current = new CapturedContextState(plan, props);
                currentContextState.set(current);
            }
            return current;
        }
    }
}
