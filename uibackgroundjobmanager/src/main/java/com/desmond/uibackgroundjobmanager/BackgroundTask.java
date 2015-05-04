package com.desmond.uibackgroundjobmanager;

import android.util.Log;

/**
 * Created by desmond on 3/5/15.
 */
public class BackgroundTask implements TaskRunnable.TaskRunnableMethods {

    private TaskRunnable mTaskRunnable;
    private Thread mCurrentThread;

    void initializeTask(TaskRunnable taskRunnable) {
        mTaskRunnable = taskRunnable;
        taskRunnable.setTask(this);
    }

    @Override
    public Thread getCurrentThread() {
        synchronized (this) {
            return mCurrentThread;
        }
    }

    @Override
    public void setCurrentThread(Thread currentThread) {
        synchronized (this) {
            mCurrentThread = currentThread;
        }
    }

    @Override
    public TaskRunnable getTaskRunnable() {
        return mTaskRunnable;
    }

    @Override
    public void completedJob() {
        BackgroundTaskManager.getInstance().recycleBackgroundTask(this);
    }

    void recycle() {
        mCurrentThread = null;
        mTaskRunnable = null;
    }
}
