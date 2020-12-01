package io.smallrye.context.test.classloading;

import java.util.List;
import java.util.function.BiConsumer;

import org.eclipse.microprofile.context.spi.ContextManagerExtension;
import org.eclipse.microprofile.context.spi.ContextManagerProvider;
import org.junit.Assert;

import io.smallrye.context.SmallRyeContextManager;
import io.smallrye.context.SmallRyeContextManagerProvider;
import io.smallrye.context.impl.ThreadContextProviderPlan;

public class ATest implements BiConsumer<ClassLoader, ClassLoader> {

    @Override
    public void accept(ClassLoader thisClassLoader, ClassLoader parentClassLoader) {
        Assert.assertEquals(thisClassLoader, ATest.class.getClassLoader());
        ContextManagerProvider contextProvider = ContextManagerProvider.instance();
        System.err.println("A CP: " + contextProvider);
        System.err.println("A CM: " + contextProvider.getContextManager());
        Assert.assertEquals(parentClassLoader, contextProvider.getClass().getClassLoader());

        SmallRyeContextManager contextManager = (SmallRyeContextManager) contextProvider.getContextManager();
        ThreadContextProviderPlan plan = contextManager.getProviderPlan();
        Assert.assertEquals(1, plan.propagatedProviders.length);
        Assert.assertEquals("A", plan.propagatedProviders[0].getThreadContextType());
        Assert.assertTrue(plan.unchangedProviders.length == 0);
        Assert.assertTrue(plan.clearedProviders.length == 0);

        List<ContextManagerExtension> propagators = SmallRyeContextManagerProvider.getManager().getExtensions();
        Assert.assertEquals(1, propagators.size());
        Assert.assertTrue(propagators.get(0).getClass() == MultiClassloadingTest.AThreadContextPropagator.class);
    }
}
