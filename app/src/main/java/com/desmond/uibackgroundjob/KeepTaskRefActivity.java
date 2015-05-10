package com.desmond.uibackgroundjob;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.BackgroundTask;
import com.desmond.asyncmanager.TaskRunnable;


public class KeepTaskRefActivity extends AppCompatActivity {

    private TextView mResult;
    private BackgroundTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_task_ref);

        mResult = (TextView) findViewById(R.id.result);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_keep_task_ref, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createTask(View view) {
        mTask = AsyncManager.runBackgroundTask(new TaskRunnable<Void, String, Void>() {

            @Override
            public String doLongOperation(Void aVoid) throws InterruptedException {
                int number = 0;
                checkForThreadInterruption();
                for (int i = 0; i < 1000000000; i++) {
                    number++;
                }
                return String.valueOf(number);
            }

            @Override
            public void callback(String s) {
                mResult.setText(s);
            }
        });
    }

    public void deleteTask(View view) {
        AsyncManager.cancelOneTask(mTask, false);
    }
}
