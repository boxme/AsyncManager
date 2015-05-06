# AsyncManager
## Description
AsyncManager hopes to keep management of background threads as simple as possible, 
by keeping the creation and termination of background tasks to just one API call respectively.
Callback, which will be processed on the UI thread, can be overridden to 
process the result returned from the background operation.

## SDK Support
SDK Version 11 & above

## Features
* Create & maintain background threads for you.
* Contain callbacks to handle the result from a background job.
* Prevent memory leaks from the usual pitfalls of background threads & AsyncTask.
* Runs all operations asynhcronously over 4 threads. (More can be set through the API)

## Usage 
### Instantiation
AsyncManager will be instantiated automatically as a Singleton.

### Start Background Task
Start a background task. Assign the type of result expected, if any. 
You can use the API checkForThreadInterruption() to check for early termination
```
// A BackgroundTask object will be returned from this method. Reference it if require.
AsyncManager.runBackgroundTask(new TaskRunnable<Result, Void>() {
    @Override
    public Result doLongOperation() throws InterruptedException {
        // checkForThreadInterruption();
        // Your long operation
        return result;
    }
    
    // Override this callback if you need to handle the result on the UI thread
    @Override
    public void callback(Result result) {
        // Handle the result from doLongOperation()
    }
});
```

Start a background task that will be persisted and allowed to run till its completion
```
AsyncManager.runBackgroundTask(new PersistedTaskRunnable<Result, Void>() {
    @Override
    public Result doLongOperation() throws InterruptedException {
        // Your long operation
        return result;
    }
    
    // Override this callback if you need to handle the result on the UI thread
    @Override
    public void callback(Result result) {
        // Handle the result from doLongOperation()
    }
});
```

Start a background task that will assign a handler to handle the result
```
AsyncManager.runBackgroundTask(new TaskRunnable<Result, MainActivity>(handler) {
    @Override
    public ResultType doLongOperation() throws InterruptedException {
        // checkForThreadInterruption();
        // Your long operation
        return result;
    }
    
    // handler is weakly referenced, if it has been GC, this callback 
    // will not be triggered
    @Override
    public void callback(MainActivity handler, Result result) {
        // handler to handle the result
    }
});
```

### Stop Task
It is good to clear the task if it's no longer required, so that BackgroundTask 
can be used for future work as soon as possible. 
Example: clear them in onDestroyView() or onDestroy()
```
// Cancel one task
AsyncManager.cancelOneTask(BackgroundTask, boolean shouldClearPersistedTask);

// To clear all non persisted tasks
AsyncManager.cancelAllNonPersistedTasks();

// To clear all tasks, including persisted ones
AsyncManager.cancelAllTasks();
```
#### Check out the code examples within the app module for more.

## FAQ
Qn: Does this take care of configuration changes?<br />
Ans: Yes, it does.

Qn: Why not just use the AsyncTask?<br />
Ans: Execution differences on different platform version can be a hassle to manage. 

API level | execute | executeOnExecutor
--- | --- | ---
11 - 12 | Concurrent | Sequential/concurrent (customizable)
13+ | Sequential | Sequential/concurrent (customizable)
Furthermore, all AsyncTask instances also share an application-wide, global execution property. This means that if two different threads launch two different instances at the same time, they will still be executed sequentially.<br>

If all you need is one single thread to process your background thread sequentially, you can consider using IntentService.
