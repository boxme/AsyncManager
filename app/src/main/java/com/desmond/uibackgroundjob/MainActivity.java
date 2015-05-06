package com.desmond.uibackgroundjob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.PersistedTaskRunnable;
import com.desmond.asyncmanager.TaskRunnable;


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
            public String doLongOperation() throws InterruptedException {
                int number = 0;
                checkForThreadInterruption();
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

        AsyncManager.runBackgroundTask(new TaskRunnable<Void, Void>() {
            @Override
            public Void doLongOperation() throws InterruptedException {
                return null;
            }
        });
    }

    public void startBackgroundJobWithHandler(View view) {
        final TextView textview = (TextView) findViewById(R.id.result);

        AsyncManager.runBackgroundTask(new TaskRunnable<String, MainActivity>(this) {

            @Override
            public String doLongOperation() {
                int number = 0;
                for (int i = 0; i < 1000000000; i++) {
                    number++;
                }
                return String.valueOf(number);
            }

            @Override
            public void callback(String s) {
                for (int i = 0; i < 10000000; i++) {}
                textview.setText("result without handler");
                Log.d(TAG, "no handler result");
            }

            @Override
            public void callback(MainActivity mainActivity, String s) {
                mainActivity.setText("result with handler");
                Log.d(TAG, "handler result");
            }
        });
    }

    public void startPersistedTask(View view) {
        AsyncManager.runBackgroundTask(new PersistedTaskRunnable<Void, Void>() {

            @Override
            public Void doLongOperation() {
                int number = 0;
                for (int i = 0; i < 900000000; i++) {
                    number++;
                }
                Log.d(TAG, "number is " + number);
                return null;
            }

            @Override
            public void callback(Void aVoid) {
                for (int i = 0; i < 10000000; i++) {}
            }
        });
    }

    public void startListViewActivity(View view) {
        Intent intent = new Intent(this, ListViewActivity.class);
        startActivity(intent);
    }

    public void startSharePrefDemo(View view) {
        Intent intent = new Intent(this, UserSignInActivity.class);
        startActivity(intent);
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
