package org.fanchuan.coursera.patternmemory;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    final String TAG = MainActivity.class.getSimpleName();
    Simon simon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @SuppressWarnings("unused")
    public void onClickBeginGame(View vw) {
        simon = initializeSimon();
        TextView tvwScore = (TextView) findViewById(R.id.tvwScore);
        TextView tvwRound = (TextView) findViewById(R.id.tvwRound);
        SimonListener listener = new SimonListener(simon, tvwScore, tvwRound);
        simon.noteRandomAdd();
        simon.play();
    }

    protected Simon initializeSimon() {
        List<View> deviceButtons = new ArrayList<View>();
        deviceButtons.add(findViewById(R.id.buttonGreen));
        deviceButtons.add(findViewById(R.id.buttonRed));
        deviceButtons.add(findViewById(R.id.buttonBlue));
        deviceButtons.add(findViewById(R.id.buttonYellow));
        Log.d(TAG, "Simon initialized with device button count: " + deviceButtons.size());
        return new Simon(deviceButtons);
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
            showHelp();
            return true;
        } else {
            //TODO Create a settings screen? Configure note play duration?
            return true;
        }
    }

    public void showHelp() {
        Dialog help = new Dialog(this);
        help.setContentView(R.layout.dialog_help);
        help.show();
    }
}