package com.desmond.uibackgroundjobmanager;

/**
 * Created by desmond on 3/5/15.
 */
public class AsyncStatus {

    public static final int STATE_ACTION_CANCELLED = -1;
    public static final int STATE_ACTION_WAITING = 0;
    public static final int STATE_ACTION_STARTED = 1;
    public static final int STATE_ACTION_COMPLETED = 2;

    private int mStatus;

    public AsyncStatus() {
        mStatus = STATE_ACTION_WAITING;
    }

    boolean isWaiting() {
        return mStatus == STATE_ACTION_WAITING;
    }

    boolean isStarted() {
        return mStatus == STATE_ACTION_STARTED;
    }

    boolean isCompleted() {
        return mStatus == STATE_ACTION_COMPLETED;
    }

    boolean isCancelled() {
        return mStatus == STATE_ACTION_CANCELLED;
    }

    void completed() {
        mStatus = STATE_ACTION_COMPLETED;
    }

    void started() {
        mStatus = STATE_ACTION_STARTED;
    }

    void waiting() {
        mStatus = STATE_ACTION_WAITING;
    }

    void cancelled() {
        mStatus = STATE_ACTION_CANCELLED;
    }

    boolean isCleanable() {
        return isCancelled() || isCompleted();
    }

    String getMessage() {
        switch (mStatus) {
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
