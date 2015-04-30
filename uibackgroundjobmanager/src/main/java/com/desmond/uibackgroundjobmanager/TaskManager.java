package com.desmond.uibackgroundjobmanager;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by desmond on 30/4/15.
 */
public class TaskManager {

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
    private static final int CORE_POOL_SIZE = 8;
    private static final int MAXIMUM_POOL_SIZE = 8;

    // A queue of Runnables for the background job pool
    private final BlockingQueue<Runnable> mBackgroundWorkQueue;
    private final ThreadPoolExecutor mTaskThreadPool;

    private static TaskManager sInstance = null;

    static  {
        sInstance = new TaskManager();
    }

    private TaskManager() {

        // List queue that blocks when the queue is empty
        mBackgroundWorkQueue = new LinkedBlockingQueue<>();

        mTaskThreadPool = new ThreadPoolExecutor(
                CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mBackgroundWorkQueue);


    }

    public static TaskManager getInstance() {
        return sInstance;
    }
}
