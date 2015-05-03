package com.desmond.uibackgroundjobmanager;

/**
 * Created by desmond on 3/5/15.
 */
public class AsyncStatus {

    public static final int STATE_ACTION_CANCEL = -1;
    public static final int STATE_ACTION_WAITING = 0;
    public static final int STATE_ACTION_STARTED = 1;
    public static final int STATE_ACTION_COMPLETED = 2;

    private int mStatus;

    public AsyncStatus() {
        mStatus = STATE_ACTION_WAITING;
    }

    public boolean isCanceled() {
        return mStatus == STATE_ACTION_CANCEL;
    }

    public boolean isWaiting() {
        return mStatus == STATE_ACTION_WAITING;
    }

    public boolean isStarted() {
        return mStatus == STATE_ACTION_STARTED;
    }

    public boolean isCompleted() {
        return mStatus == STATE_ACTION_COMPLETED;
    }

    public void cancel() {
        mStatus = STATE_ACTION_CANCEL;
    }

    public void completed() {
        mStatus = STATE_ACTION_COMPLETED;
    }

    public void started() {
        mStatus = STATE_ACTION_STARTED;
    }

    public void waiting() {
        mStatus = STATE_ACTION_WAITING;
    }
}
