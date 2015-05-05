package com.desmond.uibackgroundjobmanager;

/**
 * Persisted Task that runs until it's finished with it's long background doLongOperation.
 * ResultHandler is set to be Void because it's usually not expected
 */
public abstract class PersistedTaskRunnable<Result, ResultHandler> extends TaskRunnable<Result, ResultHandler> {

    public PersistedTaskRunnable() {
        super();
        mShouldPersist = true;
    }
}
