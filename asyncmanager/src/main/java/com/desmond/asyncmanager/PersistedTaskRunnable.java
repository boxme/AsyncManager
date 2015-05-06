package com.desmond.asyncmanager;

/**
 * A persisted TaskRunnable that runs until it's finished with it's long background operation.
 */
public abstract class PersistedTaskRunnable<Result, ResultHandler> extends TaskRunnable<Result, ResultHandler> {

    public PersistedTaskRunnable() {
        super();
        mShouldPersist = true;
    }
}
