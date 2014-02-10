package org.fanchuan.coursera.patternmemory;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

class SimonListener implements View.OnClickListener {
    /* Helper class for using Simon, it monitors the buttons, updates the score and round, and sends messages
    to the parent Activity */
    private String TAG = SimonListener.class.getSimpleName();
    private List<View> simonButtons;
    private TextView tvwRound;
    private TextView tvwScore;
    private Simon mySimon;

    protected SimonListener(Simon simon0, TextView tvwScore, TextView tvwRound) {
        if (simon0 == null || tvwScore == null || tvwRound == null)
            throw new IllegalArgumentException("Constructor instances must not be null");
        this.mySimon = simon0;
        this.simonButtons = mySimon.getDeviceButtons();
        if (simonButtons.size() == 0)
            throw new IllegalArgumentException("Simon instance List<View> is empty");
        this.tvwRound = tvwRound;
        this.tvwScore = tvwScore;
        for (View deviceButton : simonButtons) {
            deviceButton.setOnClickListener(this);
        }
        tvwScore.setText(Integer.toString(0));
        tvwRound.setText(Integer.toString(0));
    }

    public void onClick(View vw) {
        Activity parentActivity = (Activity) vw.getContext();
        if (mySimon.verifyButtonPress(vw)) {
            //This button press matches the current one in the Iterator, get a point whether it is the last or not
            try {
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
