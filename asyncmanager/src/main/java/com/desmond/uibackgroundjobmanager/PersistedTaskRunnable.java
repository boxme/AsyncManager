package com.desmond.uibackgroundjobmanager;

/**
 * Created by desmond on 3/5/15.
 */
public abstract class PersistedTaskRunnable extends TaskRunnable<Void, Void> {

    public PersistedTaskRunnable() {
        super();
        mShouldPersist = true;
    }
}
