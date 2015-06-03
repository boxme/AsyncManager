package com.desmond.asyncmanager;

import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import java.lang.ref.WeakReference;

/**
 * Task that runs its long operation in a background thread, and posts its result
 * to be executed on the main UI thread.
 *
 * @param <Result> Expected result type from the #doLongOperation()
 * @param <ResultHandler> Handler that might be required to handle the result on the UI thread
 */
public abstract class TaskRunnable<Params, Result, ResultHandler> implements Runnable {

    private static final String TAG = TaskRunnable.class.getSimpleName();

    private BackgroundTask mTask;
    private AsyncStatus mStatus;

    private Params mParams;

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
        mParams = null;
        mResultHandler = null;
    }

//    public TaskRunnable(Params params, ResultHandler resultHandler) {
//        mStatus = new AsyncStatus();
//        mParams = params;
//        if (resultHandler != null) {
//            mResultHandler = new WeakReference<>(resultHandler);
//        }
//    }

    public TaskRunnable setParams(@NonNull Params params) {
        mParams = params;
        return this;
    }

    public TaskRunnable setResultHandler(@NonNull ResultHandler resultHandler) {
        mResultHandler = new WeakReference<>(resultHandler);
        return this;
    }

    @Override
    public void run() {
        try {
            checkForThreadInterruption();
            mTask.setCurrentThread(Thread.currentThread());

            if (mStatus.isWaiting()) {
                mStatus.started();
                mResult = doLongOperation(mParams);

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

    @WorkerThread
    public abstract Result doLongOperation(Params params) throws InterruptedException;

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

    @MainThread
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
                mParams = null;
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
