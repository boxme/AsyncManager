package com.desmond.uibackgroundjobmanager;

/**
 * Created by desmond on 3/5/15.
 */
public class BackgroundTask implements TaskRunnable.TaskRunnableMethods {

    private TaskRunnable mTaskRunnable;
    private static BackgroundTaskManager mTaskManager;
    private Thread mCurrentThread;

    void initializeTask(BackgroundTaskManager jobManager, TaskRunnable taskRunnable) {
        mTaskRunnable = taskRunnable;
        mTaskManager = jobManager;
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
        mTaskManager.recycleBackgroundTask(this);
    }

    void recycle() {
        mCurrentThread = null;
        mTaskRunnable = null;
    }
}
