package com.desmond.uibackgroundjobmanager;

import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by desmond on 30/4/15.
 */
public abstract class TaskRunnable<T, K> implements Runnable {

    private static final String TAG = TaskRunnable.class.getSimpleName();

    private BackgroundTask mTask;
    private AsyncStatus mStatus;
    private WeakReference<K> mResultHandler;
    private T mResult;

    boolean mShouldPersist = false;

    interface TaskRunnableMethods {
        void setCurrentThread(Thread currentThread);
        Thread getCurrentThread();
        TaskRunnable getTaskRunnable();
        void completedJob();
    }

    public TaskRunnable() {
        mStatus = new AsyncStatus();
        mResultHandler = null;
    }

    public TaskRunnable(@NonNull K resultHandler) {
        mStatus = new AsyncStatus();
        mResultHandler = new WeakReference<>(resultHandler);
    }

    @Override
    public void run() {
        try {
            checkForThreadInterruption();
//            if (mTask == null) return;
            mTask.setCurrentThread(Thread.currentThread());

            if (mStatus.isWaiting()) {
                // Moves the current Thread into the background
                android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

                mStatus.started();
                mResult = operation();
                mStatus.completed();

                checkForThreadInterruption();
                UIThreadUtility.post(this);
            } else if (mStatus.isCompleted()) {
                callback();
            }
        } catch (InterruptedException e) {
            Log.d(TAG, "thread is interrupted");
        } finally {
            Log.d(TAG, "clean up");
            cleanUp();
            // Clears the Thread's interrupt flag
            Thread.interrupted();
        }
    }

    public abstract T operation();

    public void callback(T result) {}
    public void callback(K handler, T result) {}

    private void checkForThreadInterruption() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    private void callback() {
        if (isActive()) {
            if (mResultHandler == null) {
                callback(mResult);
            } else {
                callback(mResultHandler.get(), mResult);
            }
        }
    }

    private boolean isActive() {
        if (mResultHandler == null) return true;

        K handler = mResultHandler.get();
        return handler != null;
    }

    private void cleanUp() {
        if (mTask != null) mTask.completedJob();

        mStatus.waiting();
        mResult = null;
        mResultHandler = null;
        mTask = null;
    }

    void setTask(BackgroundTask task) {
        mTask = task;
    }

    BackgroundTask getTask() {
        return mTask;
    }
}
