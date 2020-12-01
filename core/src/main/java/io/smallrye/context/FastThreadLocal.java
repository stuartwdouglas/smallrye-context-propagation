package io.smallrye.context;

import java.util.concurrent.atomic.AtomicInteger;

public class FastThreadLocal<T> extends ThreadLocal<T> {

    private static final Object NULL_SENTINEL = new Object();

    final int key;
    static final AtomicInteger counter = new AtomicInteger();

    public FastThreadLocal() {
        key = counter.getAndIncrement();
    }

    @Override
    public T get() {
        Thread thread = Thread.currentThread();
        if (thread instanceof StorageThread) {
            return getThreadLocal(((StorageThread) thread));
        }
        return super.get();
    }

    @Override
    public void set(T value) {
        Thread thread = Thread.currentThread();
        if (thread instanceof StorageThread) {
            setThreadLocal(((StorageThread) thread), value);
            return;
        }
        super.set(value);
    }

    @Override
    public void remove() {
        Thread thread = Thread.currentThread();
        if (thread instanceof StorageThread) {
            removeThreadLocal(((StorageThread) thread));
            return;
        }
        super.remove();
    }

    private T getThreadLocal(StorageThread thread) {
        int key = this.key;
        Object[] tlstate = thread.getThreadLocalState();
        if (tlstate.length > key) {
            Object ret = tlstate[key];
            if (ret == NULL_SENTINEL) {
                return null;
            } else if (ret == null) {
                T value = initialValue();
                setThreadLocal(thread, value);
                return value;
            }
            return (T) ret;
        } else {
            T value = initialValue();
            setThreadLocal(thread, value);
            return value;
        }
    }

    private void setThreadLocal(StorageThread thread, T value) {
        int key = this.key;
        Object[] tlstate = thread.getThreadLocalState();
        if (tlstate.length > key) {
            if (value == null) {
                tlstate[key] = NULL_SENTINEL;
            } else {
                tlstate[key] = value;
            }
        } else {
            Object[] newArray = new Object[FastThreadLocal.counter.get()];
            System.arraycopy(tlstate, 0, newArray, 0, tlstate.length);
            thread.setThreadLocalState(tlstate = newArray);
            if (value == null) {
                tlstate[key] = NULL_SENTINEL;
            } else {
                tlstate[key] = value;
            }
        }
    }

    private void removeThreadLocal(StorageThread thread) {
        int key = this.key;
        Object[] fastThreadLocalState = thread.getThreadLocalState();
        if (fastThreadLocalState.length > key) {
            fastThreadLocalState[key] = null;
        }
    }

    @Override
    protected T initialValue() {
        return super.initialValue();
    }
}
