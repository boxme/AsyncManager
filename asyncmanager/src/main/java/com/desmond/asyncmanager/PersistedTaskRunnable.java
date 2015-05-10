package com.desmond.asyncmanager;

/**
 * A persisted TaskRunnable that runs until it's finished with it's long background operation.
 *
 * Usage of this case should not be an anonymous instantiation, as any reference to the calling
 * activity/fragment should be avoided. Instead, a static inner class should be made out of
 * PersistedTaskRunnable.
 *
 * Check out the code demo for an example.
 */
public abstract class PersistedTaskRunnable<Params, Result, ResultHandler>
        extends TaskRunnable<Params, Result, ResultHandler> {

    public PersistedTaskRunnable() {
        super();
        mShouldPersist = true;
    }
}
