package io.smallrye.context;

public interface StorageThread {

    void setThreadLocalState(Object[] data);

    Object[] getThreadLocalState();
}
