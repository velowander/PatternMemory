package org.fanchuan.coursera.patternmemory;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity {

    final String TAG = MainActivity.class.getSimpleName();
    //Simon simon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SuppressWarnings("unused")
    public void onClickBeginGame(View vw) {
        ScoreBarUpdate scoreBarUpdate = (ScoreBarUpdate) getSupportFragmentManager().findFragmentById(R.id.fragment_score_bar);
        scoreBarUpdate.resetScore();
        BoardFragment boardFragment = (BoardFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_board);
        boardFragment.begin(scoreBarUpdate);
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
            Dialog help = new Dialog(this);
            help.setContentView(R.layout.dialog_help);
            help.show();
            return true;
        } else {
            //TODO Create a settings screen? Configure note play duration?
            return true;
        }
    }
}