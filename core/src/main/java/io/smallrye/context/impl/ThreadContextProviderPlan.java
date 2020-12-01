package io.smallrye.context.impl;

import java.util.Set;

import org.eclipse.microprofile.context.spi.ThreadContextProvider;

public class ThreadContextProviderPlan {

    public final ThreadContextProvider[] propagatedProviders;
    public final ThreadContextProvider[] unchangedProviders;
    public final ThreadContextProvider[] clearedProviders;

    public ThreadContextProviderPlan(Set<ThreadContextProvider> propagatedSet, Set<ThreadContextProvider> unchangedSet,
            Set<ThreadContextProvider> clearedSet) {
        this.propagatedProviders = propagatedSet.toArray(new ThreadContextProvider[0]);
        this.unchangedProviders = unchangedSet.toArray(new ThreadContextProvider[0]);
        this.clearedProviders = clearedSet.toArray(new ThreadContextProvider[0]);
    }
}
