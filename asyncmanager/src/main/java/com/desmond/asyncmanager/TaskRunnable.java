package com.desmond.asyncmanager;

import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

/**
 * Task that runs its long operation in a background thread, and posts its result
 * to be executed on the main UI thread.
 *
 * @param <Result> Expected result type from the #doLongOperation()
 * @param <ResultHandler> Handler that might be required to handle the result on the UI thread
 */
public abstract class TaskRunnable<Result, ResultHandler> implements Runnable {

    private static final String TAG = TaskRunnable.class.getSimpleName();

    private BackgroundTask mTask;
    private AsyncStatus mStatus;

    /**
     * Handler that will process the result on the UI thread.
     */
    private WeakReference<ResultHandler> mResultHandler;
    /**
     * Result returned after the long background operation.
     */
    private Result mResult;

    boolean mShouldPersist = false;

    /**
     * Methods that will be able to control this runnable
     */
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

    public TaskRunnable(@NonNull ResultHandler resultHandler) {
        mStatus = new AsyncStatus();
        mResultHandler = new WeakReference<>(resultHandler);
    }

    @Override
    public void run() {
        try {
            checkForThreadInterruption();
            mTask.setCurrentThread(Thread.currentThread());

            if (mStatus.isWaiting()) {
                mStatus.started();
                mResult = doLongOperation();

                checkForThreadInterruption();
                sendToUIThread();
            } else if (mStatus.isStarted()) {
                callbackOnUIThread();
                mStatus.completed();
            }
        } catch (InterruptedException e) {
            mStatus.cancelled();
        } finally {
            cleanUp();
            Thread.interrupted();
        }
    }

    public abstract Result doLongOperation() throws InterruptedException;

    public void callback(Result result) {}
    public void callback(ResultHandler handler, Result result) {}

    private void sendToUIThread() {
        UIThreadUtility.post(this);
    }

    protected void checkForThreadInterruption() throws InterruptedException {
        if (Thread.interrupted()) {
            throw new InterruptedException();
        }
    }

    private void callbackOnUIThread() {
        synchronized (this) {
            if (isActive()) {
                if (mResultHandler == null) {
                    callback(mResult);
                } else {
                    callback(mResultHandler.get(), mResult);
                }
            }
        }
    }

    /**
     * @return true if this runnable is still valid to be processed
     */
    private boolean isActive() {
        if (mTask == null)          return false;
        if (mResultHandler == null) return true;

        ResultHandler handler = mResultHandler.get();
        return handler != null;
    }

    private void cleanUp() {
        synchronized (this) {
            if (mTask != null && mStatus.isCleanable()) {
                mTask.completedJob();
                mStatus.isWaiting();
                mResult = null;
                mResultHandler = null;
                mTask = null;
            }
        }
    }

    /**
     * Set the BackgroundTask in control of this runnable
     * @param task
     */
    void setTask(BackgroundTask task) {
        mTask = task;
    }

    BackgroundTask getTask() {
        return mTask;
    }
}
