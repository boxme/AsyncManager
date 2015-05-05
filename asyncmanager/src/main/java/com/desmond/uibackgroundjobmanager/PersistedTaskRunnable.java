package com.desmond.uibackgroundjobmanager;

/**
 * Persisted Task that runs until it's finished with it's long background doLongOperation.
 */
public abstract class PersistedTaskRunnable<Result, ResultHandler> extends TaskRunnable<Result, ResultHandler> {

    public PersistedTaskRunnable() {
        super();
        mShouldPersist = true;
    }
}
