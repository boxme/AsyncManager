package com.desmond.uibackgroundjob;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.PersistedTaskRunnable;
import com.desmond.asyncmanager.TaskRunnable;


public class UserSignInActivity extends AppCompatActivity {

    public static final String TAG = UserSignInActivity.class.getSimpleName();

    private EditText mUserNameEditText;
    private EditText mEmailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_sign_in);

        mUserNameEditText = (EditText) findViewById(R.id.username_edittext);
        mEmailEditText = (EditText) findViewById(R.id.email_edittext);

        AsyncManager.runBackgroundTask(new TaskRunnable<Void, String[], Void>() {

            @Override
            public String[] doLongOperation(Void aVoid) throws InterruptedException {
                String[] results = new String[2];
                results[0] = DemoApplication.getUserName();
                results[1] = DemoApplication.getUserEmail();
                return results;
            }

            @Override
            public void callback(String[] strings) {
                mUserNameEditText.setText(strings[0]);
                mEmailEditText.setText(strings[1]);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_sign_in, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void saveUserInfo(View view) {
        final String userName = mUserNameEditText.getText().toString();
        final String email = mEmailEditText.getText().toString();

        if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(email)) {
            String[] params = new String[2];
            params[0] = userName;
            params[1] = email;
            AsyncManager.runBackgroundTask(new SetUserInfoTask().setParams(params));

            finish();
        }
    }

    @Override
    protected void onDestroy() {
        AsyncManager.cancelAllNonPersistedTasks();
        super.onDestroy();
    }

    private static class SetUserInfoTask extends PersistedTaskRunnable<String[], Void, Void> {

        @Override
        public Void doLongOperation(String[] strings) throws InterruptedException {
            Log.d(TAG, strings[0] + " " + strings[1]);
            DemoApplication.saveUserName(strings[0]);
            DemoApplication.saveUserEmail(strings[1]);
            Log.d(TAG, "User info saved");
            return null;
        }
    }
}
