package com.desmond.uibackgroundjobmanager;

/**
 * Created by desmond on 30/4/15.
 */
public abstract class BackgroundJob<T> implements Runnable {

    @Override
    public void run() {
        // Moves the current Thread into the background
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);

        try {
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
        } catch (InterruptedException e) {

        } finally {

            // Clears the Thread's interrupt flag
            Thread.interrupted();
        }
    }

    abstract T operation();
}
