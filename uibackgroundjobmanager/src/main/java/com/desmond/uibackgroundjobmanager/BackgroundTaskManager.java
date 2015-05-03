package com.desmond.uibackgroundjobmanager;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * Created by desmond on 30/4/15.
 */
public class BackgroundTaskManager {

    // Sets the amount of time an idle thread will wait for a task before terminating
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * NOTE: This is the number of total available cores. On current versions of
     * Android, with devices that use plug-and-play cores, this will return less
     * than the total number of cores. The total number of cores is not
     * available in current Android implementations.
     */
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAXIMUM_POOL_SIZE = 4;

    private final Queue<BackgroundTask> mBackgroundTaskWorkQueue;
    private final Queue<BackgroundTask> mExecutingTaskWorkQueue;

    private final BlockingQueue<Runnable> mBackgroundWorkQueue;
    private final ThreadPoolExecutor mTaskThreadPool;

    private static final BackgroundTaskManager sInstance;

    static  {
        sInstance = new BackgroundTaskManager();
    }

    private BackgroundTaskManager() {
        mBackgroundTaskWorkQueue = new LinkedBlockingQueue<>();
        mExecutingTaskWorkQueue = new LinkedBlockingQueue<>();

        // List queue that blocks when the queue is empty
        mBackgroundWorkQueue = new LinkedBlockingQueue<>();

        mTaskThreadPool = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mBackgroundWorkQueue);
    }

    public static BackgroundTaskManager getInstance() {
        return sInstance;
    }

    public static BackgroundTask runBackgroundTask(TaskRunnable run) {
        BackgroundTask backgroundTask = sInstance.mBackgroundTaskWorkQueue.poll();

        if (backgroundTask == null) {
            backgroundTask = new BackgroundTask();
        }

        backgroundTask.initializeTask(getInstance(), run);

        sInstance.mExecutingTaskWorkQueue.offer(backgroundTask);

        sInstance.mTaskThreadPool.execute(run);

        return backgroundTask;
    }

    public static void removeBackgroundTask(BackgroundTask backgroundTask) {
        synchronized (sInstance) {
            Thread thread = backgroundTask.getCurrentThread();

            if (thread != null) {
                thread.interrupt();
            }
        }
        getInstance().mTaskThreadPool.remove(backgroundTask.getTaskRunnable());
    }

    /**
     * Cancels all the Threads in the ThreadPool
     */
    public static void cancelAll() {
        Runnable[] taskArray =  new TaskRunnable[getInstance().mBackgroundWorkQueue.size()];
        getInstance().mBackgroundWorkQueue.toArray(taskArray);

        BackgroundTask[] executingTaskArray = new BackgroundTask[getInstance().mExecutingTaskWorkQueue.size()];
        getInstance().mExecutingTaskWorkQueue.toArray(executingTaskArray);

        int taskArrayLen = taskArray.length;
        int currentTaskArrayLen = executingTaskArray.length;

        synchronized (sInstance) {
            for (int i = 0; i < taskArrayLen; i++) {
                TaskRunnable runnable = (TaskRunnable) taskArray[i];

                if (!runnable.mShouldPersist) {
                    getInstance().mTaskThreadPool.remove(runnable);
                }
            }

            for (int i = 0; i < currentTaskArrayLen; i++) {
                Thread thread = executingTaskArray[i].getCurrentThread();

                if (!executingTaskArray[i].getTaskRunnable().mShouldPersist) {
                    if (thread != null) {
                        thread.interrupt();
                    }
                    getInstance().mExecutingTaskWorkQueue.remove(executingTaskArray[i]);
                }
            }
        }
    }

    void recycleBackgroundTask(BackgroundTask task) {
        task.recycle();
        mExecutingTaskWorkQueue.remove(task);
        mBackgroundTaskWorkQueue.offer(task);
    }

    public static void cleanUp() {
        getInstance().mTaskThreadPool.shutdown();
    }

    public static void setThreadPoolSize(int size) {
        getInstance().mTaskThreadPool.setCorePoolSize(size);
        getInstance().mTaskThreadPool.setMaximumPoolSize(size);
    }
}
