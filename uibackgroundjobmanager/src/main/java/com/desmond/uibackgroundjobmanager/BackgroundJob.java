package com.desmond.uibackgroundjobmanager;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * Created by desmond on 30/4/15.
 */
public abstract class BackgroundJob<T, K> implements Runnable {

    private AsyncStatus mStatus;
    private WeakReference<K> mResultHandler;
    private T mResult;

    public BackgroundJob() {
        mStatus = new AsyncStatus();
        mResultHandler = null;
    }

    public BackgroundJob(@NonNull K resultHandler) {
        mStatus = new AsyncStatus();
        mResultHandler = new WeakReference<K>(resultHandler);
    }

    @Override
    public void run() {
        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }

            if (mStatus.isCanceled()) {
                cleanUp();
                return;
            }

            if (mStatus.isWaiting()) {
                // Moves the current Thread into the background
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                mStatus.isStarted();
                mResult = operation();
                mStatus.isCompleted();

                UIThreadUtility.post(this);
            } else if (mStatus.isCompleted()) {
                callback();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            cleanUp();
            // Clears the Thread's interrupt flag
            Thread.interrupted();
        }
    }

    abstract T operation();


    private void callback() {
        if (isActive()) {
            if (mResultHandler == null) {
                callback(mResult, mStatus);
            } else {
                callback(mResultHandler.get(), mResult, mStatus);
            }
        }
        cleanUp();
    }

    public void callback(T result, AsyncStatus status) {}
    public void callback(K handler, T result, AsyncStatus status) {}

    private boolean isActive() {
        if (mResultHandler == null) return true;

        K handler = mResultHandler.get();
        if (handler == null) {
            return false;
        }

        return true;
    }

    private void cleanUp() {
        mStatus.waiting();
        mResult = null;
        mResultHandler = null;
    }
}
