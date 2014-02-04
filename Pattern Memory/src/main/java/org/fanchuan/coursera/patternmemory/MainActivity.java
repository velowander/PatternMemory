package org.fanchuan.coursera.patternmemory;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        List<Button> deviceButtons = new ArrayList<Button>();
        deviceButtons.add((Button) findViewById(R.id.buttonGreen));
        deviceButtons.add((Button) findViewById(R.id.buttonRed));
        deviceButtons.add((Button) findViewById(R.id.buttonBlue));
        deviceButtons.add((Button) findViewById(R.id.buttonYellow));
        SimonController simonCon = new SimonController(deviceButtons);
        List<Button> melody = new ArrayList<Button>();
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
        //Passing a List of the device's Button objects is required; then this class adds itself as a click listener.
        protected SimonController(List<Button> deviceButtons) {
            super();
            this.deviceButtons = deviceButtons;
            for (Button deviceButton: deviceButtons) {
                deviceButton.setOnClickListener(this);
            }
        }

        final int playDurationMs = 600; // How long the computer presses the buttons during playback
        final int pauseDurationMs = 200; // How long the computer pauses between playback button presses
        private List<Button> deviceButtons;
        private final java.util.Random rand = new java.util.Random();

        public void onClick(View vw) {
            String toastText = "NONE";
            switch (vw.getId()) {
                case R.id.buttonGreen:
                    toastText = "green";
                    break;
                case R.id.buttonRed:
                    toastText = "red";
                    break;
                case R.id.buttonYellow:
                    toastText = "yellow";
                    break;
                case R.id.buttonBlue:
                    toastText = "blue";
                    break;
            }
            Toast.makeText(vw.getContext(), toastText, Toast.LENGTH_SHORT).show();
        }
        private void playOneButton (final Button BTN) {
            BTN.setPressed(true); //if doesn't redraw on API10, add BTN.invalidate()!
            BTN.postDelayed(new Runnable() {
                public void run() {
                    BTN.setPressed(false);
                }
            }, playDurationMs);
        }

        private boolean playMelody(List<Button> melody) {
            /* In: List of Button objects (notes) to play
            Returns false if the melody won't play, true otherwise */
            int delayMultiplier = 1;
            if (melody == null) return false;
            for(final Button btnPlay : melody) {
                btnPlay.postDelayed(new Runnable() {
                    public void run() {
                        playOneButton(btnPlay);
                    }
                }, delayMultiplier * (playDurationMs + pauseDurationMs));
                delayMultiplier++; //Need this multiplier so that played back notes are consecutive in time
            }
            return true;
        }

        private void melodyAddRandom(List<Button> myMelody, int numberToAdd) {
            //Use this method to add Random buttons in bulk.
            for (int i = 0; i < numberToAdd; i++) {
                melodyAddRandom(myMelody);
            }
        }

        private void melodyAddRandom(List<Button> myMelody) {
            int deviceButtonIndex = rand.nextInt(deviceButtons.size());
            Log.v(TAG, "deviceButtonIndex: " + deviceButtonIndex);
            myMelody.add(deviceButtons.get(deviceButtonIndex));
        }
    }
}