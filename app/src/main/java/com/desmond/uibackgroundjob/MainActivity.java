package com.desmond.uibackgroundjob;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.desmond.uibackgroundjobmanager.BackgroundTaskManager;
import com.desmond.uibackgroundjobmanager.TaskRunnable;
import com.desmond.uibackgroundjobmanager.PersistedTaskRunnable;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startBackgroundJob(View view) {
        final TextView textview = (TextView) findViewById(R.id.result);

        BackgroundTaskManager.runBackgroundTask(new TaskRunnable<String, Void>() {

            @Override
            public String operation() {
                int number = 0;
                for (int i = 0; i < 1000000000; i++) {
                    number++;
                }
                return String.valueOf(number);
            }

            @Override
            public void callback(String result) {
                for (int i = 0; i < 10000000; i++) {}
                textview.setText(result);
            }

        });
    }

    public void startPersistedTask(View view) {
        BackgroundTaskManager.runBackgroundTask(new PersistedTaskRunnable() {
            @Override
            public Void operation() {
                int number = 0;
                for (int i = 0; i < 900000000; i++) {
                    number++;
                }
                Log.d(TAG, "number is " + number);
                return null;
            }

            @Override
            public void callback(Void result) {
                for (int i = 0; i < 10000000; i++) {}
            }
        });
    }

    @Override
    protected void onDestroy() {
        BackgroundTaskManager.cancelAllNonPersistedTasks();
        super.onDestroy();
    }
}
