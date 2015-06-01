package com.desmond.asyncmanager;

import android.support.annotation.IntDef;

/**
 * State of a TaskRunnable during its entire lifecycle in the thread.
 */
public class AsyncStatus {

    @IntDef({STATE_ACTION_CANCELLED, STATE_ACTION_WAITING, STATE_ACTION_STARTED, STATE_ACTION_COMPLETED})
    public @interface AsyncStatusState {}
    public static final int STATE_ACTION_CANCELLED = -1;
    public static final int STATE_ACTION_WAITING = 0;
    public static final int STATE_ACTION_STARTED = 1;
    public static final int STATE_ACTION_COMPLETED = 2;

    private int mState;

    public AsyncStatus() {
        setStatus(STATE_ACTION_WAITING);
    }

    boolean isWaiting() {
        return getAsyncStatusState() == STATE_ACTION_WAITING;
    }

    boolean isStarted() {
        return getAsyncStatusState() == STATE_ACTION_STARTED;
    }

    boolean isCompleted() {
        return getAsyncStatusState() == STATE_ACTION_COMPLETED;
    }

    boolean isCancelled() {
        return getAsyncStatusState() == STATE_ACTION_CANCELLED;
    }

    void completed() {
        setStatus(STATE_ACTION_COMPLETED);
    }

    void started() {
        setStatus(STATE_ACTION_STARTED);
    }

    void waiting() {
        setStatus(STATE_ACTION_WAITING);
    }

    void cancelled() {
        setStatus(STATE_ACTION_CANCELLED);
    }

    protected void setStatus(@AsyncStatusState int state) {
        mState = state;
    }

    @AsyncStatusState
    protected int getAsyncStatusState() {
        return mState;
    }

    boolean isCleanable() {
        return isCancelled() || isCompleted();
    }

    String getMessage() {
        switch (mState) {
            case STATE_ACTION_CANCELLED:
                return "Cancelled";

            case STATE_ACTION_WAITING:
                return "Waiting to be processed";

            case STATE_ACTION_STARTED:
                return "Processing results";

            case STATE_ACTION_COMPLETED:
                return "Processing completed";

            default:
                throw new IllegalArgumentException("Wrong status");
        }
    }
}
