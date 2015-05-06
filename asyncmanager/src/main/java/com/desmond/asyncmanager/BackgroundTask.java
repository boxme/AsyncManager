package com.desmond.asyncmanager;

/**
 * BackgroundTask object that will control one TaskRunnable.
 * TaskRunnable should ideally be accessed only by a BackgroundTask
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

    /**
     * @param currentThread Current thread that this task is being processed on
     */
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
        AsyncManager.getInstance().recycleBackgroundTask(this);
    }

    void recycle() {
        mCurrentThread = null;
        mTaskRunnable = null;
    }
}
