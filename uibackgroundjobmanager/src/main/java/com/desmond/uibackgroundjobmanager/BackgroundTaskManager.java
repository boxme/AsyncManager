package com.desmond.uibackgroundjobmanager;

import android.util.Log;

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
    private static final int CORE_POOL_SIZE = 2;
    private static final int MAXIMUM_POOL_SIZE = 2;

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

        backgroundTask.initializeTask(run);

        sInstance.mExecutingTaskWorkQueue.offer(backgroundTask);

        sInstance.mTaskThreadPool.execute(run);

        return backgroundTask;
    }

    public static void cancelNonPersistedTask(BackgroundTask backgroundTask) {
        synchronized (sInstance) {
            Thread thread = backgroundTask.getCurrentThread();
            TaskRunnable runnable = backgroundTask.getTaskRunnable();

            if (!runnable.mShouldPersist) {
                if (thread != null) {
                    thread.interrupt();
                }

                getInstance().mTaskThreadPool.remove(runnable);
                getInstance().mExecutingTaskWorkQueue.remove(backgroundTask);
            }
        }
    }

    /**
     * Cancels all the Threads for non persisted task in the ThreadPool
     */
    public static void cancelAllNonPersistedTasks() {
        BackgroundTask[] taskArray = new BackgroundTask[getInstance().mExecutingTaskWorkQueue.size()];
        getInstance().mExecutingTaskWorkQueue.toArray(taskArray);

        int taskArrayLen = taskArray.length;

        synchronized (sInstance) {
            Thread thread;
            TaskRunnable runnable;
            BackgroundTask task;
            for (int i = 0; i < taskArrayLen; i++) {
                task = taskArray[i];
                thread = task.getCurrentThread();
                runnable = task.getTaskRunnable();

                if (!runnable.mShouldPersist) {
                    if (thread != null) {
                        thread.interrupt();
                    }
                    getInstance().mTaskThreadPool.remove(runnable);
                    getInstance().mExecutingTaskWorkQueue.remove(task);
                }
            }
        }
    }

    void recycleBackgroundTask(BackgroundTask task) {
        task.recycle();
        mExecutingTaskWorkQueue.remove(task);
        mBackgroundTaskWorkQueue.offer(task);
        Log.d("Manager", "executing queue size " + mExecutingTaskWorkQueue.size());
        Log.d("Manager", "work queue size " + mBackgroundTaskWorkQueue.size());
    }

    public static void cleanUp() {
        getInstance().mTaskThreadPool.shutdown();
    }

    public static void setThreadPoolSize(int size) {
        getInstance().mTaskThreadPool.setCorePoolSize(size);
        getInstance().mTaskThreadPool.setMaximumPoolSize(size);
    }
}
