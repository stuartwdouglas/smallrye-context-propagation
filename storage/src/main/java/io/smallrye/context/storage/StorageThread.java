package io.smallrye.context.storage;

public interface StorageThread {

    void setThreadLocalState(Object[] data);

    Object[] getThreadLocalState();
}
