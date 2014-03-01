package org.fanchuan.coursera.patternmemory;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
        int id = item.getItemId();
        if (id == R.id.help_action) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.help_title);
            builder.setMessage(R.string.help_text);
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
            return true;
        } else {
            //TODO Create a settings screen? Configure note play duration?
            return true;
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