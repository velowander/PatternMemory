package org.fanchuan.coursera.patternmemory;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

class SimonListener implements View.OnClickListener {
    /* Helper class for using Simon, it monitors the buttons, updates the score and round, and sends messages
    to the parent Activity */
    private String TAG = SimonListener.class.getSimpleName();
    private List<View> simonButtons;
    private ScoreBarUpdate scoreBarUpdate;
    private Simon mySimon;

    protected SimonListener(Simon simon0, ScoreBarUpdate scoreBarUpdate) {
        if (simon0 == null || scoreBarUpdate == null)
            throw new IllegalArgumentException("Constructor instances must not be null");
        this.mySimon = simon0;
        this.simonButtons = mySimon.getDeviceButtons();
        if (simonButtons.size() == 0)
            throw new IllegalArgumentException("Simon instance List<View> is empty");
        this.scoreBarUpdate = scoreBarUpdate;
        for (View deviceButton : simonButtons) {
            deviceButton.setOnClickListener(this);
        }
    }

    public void onClick(View vw) {
        Activity parentActivity = (Activity) vw.getContext();
        if (mySimon.verifyButtonPress(vw)) {
            //This button press matches the current one in the Iterator, get a point whether it is the last or not
            scoreBarUpdate.incrementScore();
            if (!mySimon.hasNext()) {
                //No more buttons to press, at the end of the melody!! Next round.
                scoreBarUpdate.incrementRound();
                String messageRoundWin = parentActivity.getResources().getString(R.string.message_round_win);
                try {
                    Toast.makeText(parentActivity, messageRoundWin, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.w(TAG, "Unable to send Toast: " + messageRoundWin);
                }
                mySimon.noteRandomAdd();
                mySimon.play();
            }
        } else {
            String messageLose = parentActivity.getResources().getString(R.string.message_lose);
            try {
                Toast.makeText(parentActivity, messageLose, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.w(TAG, "Unable to send Toast: " + messageLose);
            }
            gameOver();
        }
    }

    private void gameOver() {
        for (View deviceButton : simonButtons) {
            deviceButton.setOnClickListener(null);
        }
    }
}
