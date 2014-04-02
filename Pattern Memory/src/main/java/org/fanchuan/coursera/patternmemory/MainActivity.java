package org.fanchuan.coursera.patternmemory;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends ActionBarActivity {

    final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //ScoreBarFragment observes Score changes in BoardFragment
        ScoreBarFragment scoreBarFragment = (ScoreBarFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_score_bar);
        BoardFragment boardFragment = (BoardFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_board);
        boardFragment.getScoreObservable().addObserver(scoreBarFragment);
        //GameBeginListener waits for user to press Begin Game button
        View vwBeginGame = findViewById(R.id.buttonBegin);
        vwBeginGame.setOnClickListener(new BeginGameListener());
        Log.v(TAG, MainActivity.class.getSimpleName() + " onStart");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_help:
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.help_title);
                builder.setMessage(R.string.help_text);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
                break;
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsActivity extends PreferenceActivity {
        final String TAG = SettingsActivity.class.getSimpleName();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                showPreferencesPreHoneycomb();
            } else {
                showPreferencesFragmentStyle(savedInstanceState);
            }
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        private void showPreferencesFragmentStyle(Bundle savedInstanceState) {
            if (savedInstanceState == null) {
                FragmentTransaction transaction = getFragmentManager()
                        .beginTransaction();
                android.app.Fragment fragment = new MyPreferencesFragment();
                transaction.replace(android.R.id.content, fragment);
                transaction.commit();
            }
        }

        @SuppressWarnings("deprecation")
        private void showPreferencesPreHoneycomb() {
            Log.d("TAG", "Build.VERSION.SDK_INT: " + Integer.toString(Build.VERSION.SDK_INT));
            addPreferencesFromResource(R.xml.preferences);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        public static class MyPreferencesFragment extends PreferenceFragment {
            final String TAG = MyPreferencesFragment.class.getSimpleName();

            @Override
            public void onAttach(Activity activity) {
                super.onAttach(activity);
                Log.d(TAG, "Attached to activity: " + activity.getClass().getSimpleName());
            }

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                this.addPreferencesFromResource(R.xml.preferences);
                return super.onCreateView(inflater, container, savedInstanceState);
            }
        }
    }

    public class BeginGameListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Log.d(TAG, MainActivity.class.getSimpleName() + " onClickBeginGame");
            try {
                ((BoardFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_board)).begin();
            } catch (Exception e) {
                Log.e(TAG, "BeginGameListener onClick Exception", e);
            }
        }
    }
}