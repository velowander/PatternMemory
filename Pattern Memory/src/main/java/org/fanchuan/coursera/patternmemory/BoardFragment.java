package org.fanchuan.coursera.patternmemory;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends Fragment implements View.OnClickListener {
    /* This BoardFragment hosts everything to do with managing the board and most aspects of the game,
    other than the score.
    It needs a reference to ScoreBarUpdate but can't accept it via constructor to enable use in an
    XML layout; this was a design tradeoff. Pass the ScoreBarUpdate reference in the begin() method which
    starts the game.
     */
    private String TAG = BoardFragment.class.getSimpleName();
    private boolean gameOn = false; //
    private List<View> simonButtons;
    private ScoreBarUpdate scoreBarUpdate;
    private Simon simon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!initialize()) {
            Log.e(TAG, "Simon has failed to initialize");
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.initialization_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View vw) {
        Activity parentActivity = (Activity) vw.getContext();
        if (simon.verifyButtonPress(vw)) {
            //This button press matches the current one in the Iterator, get a point whether it is the last or not
            scoreBarUpdate.incrementScore();
            if (!simon.hasNext()) {
                //No more buttons to press, at the end of the melody!! Next round.
                scoreBarUpdate.incrementRound();
                String messageRoundWin = parentActivity.getResources().getString(R.string.message_round_win);
                try {
                    Toast.makeText(parentActivity, messageRoundWin, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.w(TAG, "Unable to send Toast: " + messageRoundWin);
                }
                simon.noteRandomAdd();
                simon.play();
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

    void begin(ScoreBarUpdate scoreBarUpdate) {
        //Assumes a game starting with 1 random note
        this.gameOn = true;
        this.scoreBarUpdate = scoreBarUpdate;
        simon.begin();
        simon.noteRandomAdd();
        simon.play();
    }

    @SuppressWarnings("unused")
    void gameOver() {
        this.gameOn = false;
    }

    public boolean initialize() {
        /* BoardFragment must have initialized its root view already; could call in onCreateView AFTER
        inflating the view or in, for example, on ActivityCreated()
        Return false if initialization fails, true otherwise
         */

        try {
            simonButtons = new ArrayList<View>();
            simonButtons.add(getView().findViewById(R.id.buttonGreen));
            simonButtons.add(getView().findViewById(R.id.buttonRed));
            simonButtons.add(getView().findViewById(R.id.buttonBlue));
            simonButtons.add(getView().findViewById(R.id.buttonYellow));
            Log.d(TAG, "Simon initialized with device button count: " + simonButtons.size());
        } catch (Exception e) {
            Log.e(TAG, "Unable to create array of buttons", e);
            return false;
        }
        for (View simonButton : simonButtons) {
            simonButton.setOnClickListener(this);
        }
        try {
            this.simon = new Simon(simonButtons);
        } catch (Exception e) {
            Log.e(TAG, "Unable to instantiate Simon, e");
            return false;
        }
        return true;
    }
}