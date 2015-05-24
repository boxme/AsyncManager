package com.desmond.uibackgroundjob;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.desmond.asyncmanager.AsyncManager;
import com.desmond.asyncmanager.TaskRunnable;


public class ListViewActivity extends AppCompatActivity {

    public static final String TAG = ListViewActivity.class.getSimpleName();

    private ListView mListView;
    private ListViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_acitvity);

        mListView = (ListView) findViewById(R.id.listview);

        setupAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_view_acitvity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.use_asycn_manager) {
            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            mAdapter.setIsUsingAsyncManager(item.isChecked());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupAdapter() {
        mAdapter = new ListViewAdapter(true);
        mListView.setAdapter(mAdapter);
    }

    private static class ListViewAdapter extends BaseAdapter {

        private boolean mIsUsingAsyncManager;

        public ListViewAdapter(boolean isUsingAsyncManager) {
            super();
            mIsUsingAsyncManager = isUsingAsyncManager;
        }

        @Override
        public int getCount() {
            return 100;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }

            viewHolder = (ViewHolder) convertView.getTag();
            final TextView textView = viewHolder.textView;
            textView.setTag(false);
            if (mIsUsingAsyncManager) {
                AsyncManager.runBackgroundTask(new TaskRunnable<Void, String, Void>() {

                    @Override
                    public String doLongOperation(Void aVoid) throws InterruptedException {
                        int value = 0;
                        for (int i = 0; i < 10000000; ++i) {
                            value++;
                        }
                        return value + "";
                    }

                    @Override
                    public void callback(String s) {
                        textView.setText(s);
                    }
                });

                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textView.setTag(true);
                        AsyncManager.runBackgroundTask(new TaskRunnable<Void, String, Void>() {

                            @Override
                            public String doLongOperation(Void aVoid) throws InterruptedException {
                                int value = 0;
                                for (int i = 0; i < 200000000; ++i) {
                                    value++;
                                }
                                return value + "";
                            }


                            @Override
                            public void callback(String s) {
                                if ((boolean) textView.getTag()) {
                                    textView.setText(s);
                                }
                            }
                        });
                    }
                });
            } else {
                int value = 0;
                for (int i = 0; i < 10000000; ++i) {
                    value++;
                }
                viewHolder.textView.setText(value + "");


                viewHolder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int value = 0;
                        for (int i = 0; i < 200000000; ++i) {
                            value++;
                        }
                        textView.setText(value + "");
                    }
                });
            }

            return convertView;
        }

        public void setIsUsingAsyncManager(boolean isUsing) {
            mIsUsingAsyncManager = isUsing;
        }

        private static class ViewHolder {
            TextView textView;
            Button button;

            ViewHolder(View parentView) {
                textView = (TextView) parentView.findViewById(R.id.item_textview);
                button = (Button) parentView.findViewById(R.id.get_new_value_btn);
            }
        }
    }
}
