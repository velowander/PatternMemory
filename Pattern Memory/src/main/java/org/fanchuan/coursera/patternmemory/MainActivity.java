package org.fanchuan.coursera.patternmemory;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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
        SimonListener listener = new SimonListener(simon);
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
            //If I make a settings dialog, show it here.
            return true;
        }
    }

    public void showHelp() {
        Dialog help = new Dialog(this);
        help.setContentView(R.layout.dialog_help);
        help.show();
    }

    protected static class SimonListener implements View.OnClickListener {
        /* Helper class for using Simon; it is closer to the UI implementation as it refers to
        specific hardcoded UI elements; although still uses the List<View> collection for the buttons.
         */
        private String TAG = SimonListener.class.getSimpleName();
        private List<View> simonButtons;
        private Activity parentActivity;
        private Simon mySimon;

        protected SimonListener(Simon simon0) {
            if (simon0 == null)
                throw new IllegalArgumentException("must pass a valid Simon instance to constructor");
            this.mySimon = simon0;
            this.simonButtons = mySimon.getDeviceButtons();
            if (simonButtons.size() == 0)
                throw new IllegalArgumentException("Simon instance List<View> is empty");
            try {
                parentActivity = (Activity) simonButtons.get(0).getContext();
            } catch (Exception e) {
                Log.e(TAG, "Unable to get SimonListener instance's parent activity");
            }
            for (View deviceButton : simonButtons) {
                deviceButton.setOnClickListener(this);
            }
            TextView tvwScore = (TextView) parentActivity.findViewById(R.id.tvwScore);
            TextView tvwRound = (TextView) parentActivity.findViewById(R.id.tvwRound);
            tvwScore.setText(Integer.toString(0));
            tvwRound.setText(Integer.toString(0));
        }

        public void onClick(View vw) {
            if (mySimon.verifyButtonPress(vw)) {
                //This button press matches the current one in the Iterator, get a point whether it is the last or not
                try {
                    TextView tvwScore = (TextView) parentActivity.findViewById(R.id.tvwScore);
                    if (tvwScore == null)
                        Log.w(TAG, "tvwScore is null");
                    int score = Integer.parseInt(tvwScore.getText().toString());
                    score++;
                    tvwScore.setText(Integer.toString(score));
                } catch (Exception e) {
                    Log.e(TAG, "Unable to set score", e);
                }
                if (!mySimon.hasNext()) {
                    //No more buttons to press, at the end of the melody!! Next round.
                    try {
                        TextView tvwRound = (TextView) parentActivity.findViewById(R.id.tvwRound);
                        int round = Integer.parseInt(tvwRound.getText().toString());
                        round++;
                        tvwRound.setText(Integer.toString(round));
                    } catch (Exception e) {
                        Log.e(TAG, "Unable to set round", e);
                    }
                    String messageRoundWin = parentActivity.getResources().getString(R.string.message_round_win);
                    Toast.makeText(parentActivity, messageRoundWin, Toast.LENGTH_SHORT).show();
                    mySimon.noteRandomAdd();
                    mySimon.play();
                }
            } else {
                String messageLose = parentActivity.getResources().getString(R.string.message_lose);
                Toast.makeText(parentActivity, messageLose, Toast.LENGTH_SHORT).show();
                gameOver();
            }
        }

        private void gameOver() {
            for (View deviceButton : simonButtons) {
                deviceButton.setOnClickListener(null);
            }
        }
    }
}