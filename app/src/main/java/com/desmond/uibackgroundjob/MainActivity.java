package com.desmond.uibackgroundjob;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.desmond.uibackgroundjobmanager.AsyncManager;
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

        AsyncManager.runBackgroundTask(new TaskRunnable<String, Void>() {

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
                for (int i = 0; i < 10000000; i++) {
                }
                textview.setText(result);
            }
        });
    }

    public void startBackgroundJobWithHandler(View view) {
        final TextView textview = (TextView) findViewById(R.id.result);

        AsyncManager.runBackgroundTask(new TaskRunnable<String, MainActivity>(this) {

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
                for (int i = 0; i < 10000000; i++) {
                }
                textview.setText("result without handler");
                Log.d(TAG, "no handler result");
            }

            @Override
            public void callback(MainActivity handler, String result) {
                handler.setText("result with handler");
                Log.d(TAG, "handler result");
            }
        });
    }

    public void startPersistedTask(View view) {
        AsyncManager.runBackgroundTask(new PersistedTaskRunnable() {
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
                for (int i = 0; i < 10000000; i++) {
                }
            }
        });
    }

    public void setText(String text) {
        final TextView textview = (TextView) findViewById(R.id.result);
        textview.setText(text);
    }

    @Override
    protected void onDestroy() {
        AsyncManager.cancelAllNonPersistedTasks();
        super.onDestroy();
    }
}
