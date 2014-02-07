package org.fanchuan.coursera.patternmemory;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    final String TAG = MainActivity.class.getSimpleName();
    Simon simon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickBeginGame(View vw) {
        simon = initializeSimon();
        simon.noteRandomAdd(5);
        simon.play();
    }

    protected Simon initializeSimon() {
        List<View> deviceButtons = new ArrayList<View>();
        deviceButtons.add(findViewById(R.id.buttonGreen));
        deviceButtons.add(findViewById(R.id.buttonRed));
        deviceButtons.add(findViewById(R.id.buttonBlue));
        deviceButtons.add(findViewById(R.id.buttonYellow));
        return new Simon(deviceButtons);
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

    protected class Simon implements View.OnClickListener {
        final int playDurationMs = 600; // How long the computer presses the buttons during playback
        final int pauseDurationMs = 200; // How long the computer pauses between playback button presses
        private final java.util.Random rand = new java.util.Random();
        private List<View> playList = new ArrayList<View>();
        private List<View> deviceButtons;
        private Iterator playListIterator; //used to verify user is playing melody correctly

        //Passing a List of the device's View (could be Button) objects is required; then this class adds itself as a click listener.
        protected Simon(List<View> deviceButtons) {
            super();
            this.deviceButtons = deviceButtons;
            for (View deviceButton : deviceButtons) {
                deviceButton.setOnClickListener(this);
            }
        }

        public void onClick(View vw) {
            if (simon.verifyButtonPress(vw)) {
                //This button press matches the current one in the Iterator
                if (!playListIterator.hasNext()) {
                    //No more buttons to press, at the end of the melody!!
                    String messageRoundWin = getResources().getString(R.string.message_round_win);
                    Toast.makeText(vw.getContext(), messageRoundWin, Toast.LENGTH_SHORT).show();
                }
            } else {
                String messageLose = getResources().getString(R.string.message_lose);
                Toast.makeText(vw.getContext(), messageLose, Toast.LENGTH_SHORT).show();
                gameOver();
            }
        }

        private void gameOver() {
            for (View deviceButton : deviceButtons) {
                deviceButton.setOnClickListener(null);
            }
        }


        protected void playOne(final View VW) {
            VW.setPressed(true); //if doesn't redraw on API10, add BTN.invalidate()!
            VW.postDelayed(new Runnable() {
                public void run() {
                    VW.setPressed(false);
                }
            }, playDurationMs);
        }

        protected boolean play() {
            /* In: List of View objects (notes) to play
            Returns false if the melody won't play, true otherwise */
            int delayMultiplier = 1;
            if (playList == null) return false;
            for (final View vwPlay : playList) {
                vwPlay.postDelayed(new Runnable() {
                    public void run() {
                        playOne(vwPlay);
                    }
                }, delayMultiplier * (playDurationMs + pauseDurationMs));
                delayMultiplier++; //Need this multiplier so that played back notes are consecutive in time
            }
            return true;
        }

        private void noteRandomAdder() {
            //Typically not called directly as it does not create the iterator for verifying the user's input
            int deviceButtonIndex = rand.nextInt(deviceButtons.size());
            Log.v(TAG, "deviceButtonIndex: " + deviceButtonIndex);
            playList.add(deviceButtons.get(deviceButtonIndex));
        }

        protected void noteRandomAdd(int numberToAdd) {
            //Use this method to add Random buttons in bulk.
            for (int i = 0; i < numberToAdd; i++) {
                noteRandomAdder();
            }
            playListIterator = playList.iterator();
        }

        protected void noteRandomAdd() {
            noteRandomAdder();
            playListIterator = playList.iterator();
        }

        protected void setPlayList(List<View> myPlayList) {
            this.playList = myPlayList;
        }

        boolean verifyButtonPress(View vw) {
            /* Verify the button pressed by the user - is it part of the melody?
            Return true if part of melody, false otherwise */
            if (playListIterator.hasNext())
                return vw == playListIterator.next();
            else
                return false; //user pressed too many keys, LOSER!!
        }
    }

}