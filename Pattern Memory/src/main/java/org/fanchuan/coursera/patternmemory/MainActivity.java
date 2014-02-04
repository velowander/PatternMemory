package org.fanchuan.coursera.patternmemory;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<View> deviceButtons = new ArrayList<View>();
        deviceButtons.add(findViewById(R.id.buttonGreen));
        deviceButtons.add(findViewById(R.id.buttonRed));
        deviceButtons.add(findViewById(R.id.buttonBlue));
        deviceButtons.add(findViewById(R.id.buttonYellow));
        SimonController simonCon = new SimonController(deviceButtons);
        List<View> melody = new ArrayList<View>();
        simonCon.melodyAddRandom(melody, 8);
        Log.v(TAG, "Count of melody: " + melody.size());
        simonCon.playMelody(melody);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected class SimonController implements View.OnClickListener {
        //Passing a List of the device's View (could be Button) objects is required; then this class adds itself as a click listener.
        protected SimonController(List<View> deviceButtons) {
            super();
            this.deviceButtons = deviceButtons;
            for (View deviceButton : deviceButtons) {
                deviceButton.setOnClickListener(this);
            }
        }
        final int playDurationMs = 600; // How long the computer presses the buttons during playback
        final int pauseDurationMs = 200; // How long the computer pauses between playback button presses
        private List<View> deviceButtons;
        private final java.util.Random rand = new java.util.Random();
        public void onClick(View vw) {

        }

        protected void playOneButton(final View VW) {
            VW.setPressed(true); //if doesn't redraw on API10, add BTN.invalidate()!
            VW.postDelayed(new Runnable() {
                public void run() {
                    VW.setPressed(false);
                }
            }, playDurationMs);
        }

        protected boolean playMelody(List<View> melody) {
            /* In: List of View objects (notes) to play
            Returns false if the melody won't play, true otherwise */
            int delayMultiplier = 1;
            if (melody == null) return false;
            for (final View vwPlay : melody) {
                vwPlay.postDelayed(new Runnable() {
                    public void run() {
                        playOneButton(vwPlay);
                    }
                }, delayMultiplier * (playDurationMs + pauseDurationMs));
                delayMultiplier++; //Need this multiplier so that played back notes are consecutive in time
            }
            return true;
        }

        private void melodyAdder(List<View> myMelody) {
            //Typically not called directly as it does not create the iterator for verifying the user's input
            int deviceButtonIndex = rand.nextInt(deviceButtons.size());
            Log.v(TAG, "deviceButtonIndex: " + deviceButtonIndex);
            myMelody.add(deviceButtons.get(deviceButtonIndex));
        }

        protected void melodyAddRandom(List<View> myMelody, int numberToAdd) {
            //Use this method to add Random buttons in bulk.
            for (int i = 0; i < numberToAdd; i++) {
                melodyAdder(myMelody);
            }
        }

        protected void melodyAddRandom(List<View> myMelody) {
            melodyAdder(myMelody);
        }
    }
}