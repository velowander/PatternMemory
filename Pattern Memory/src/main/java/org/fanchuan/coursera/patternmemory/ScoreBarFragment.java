package org.fanchuan.coursera.patternmemory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ScoreBarFragment extends Fragment implements ScoreBarUpdate {
    /* Takes care of updating the score and round for the score bar, so it maintains a reference to
    both after inflating the fragment.
     */
    private String TAG = ScoreBarFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score_bar, container, false);
    }

    public void incrementRound() {
        try {
            TextView tvwRound = (TextView) getView().findViewById(R.id.tvwRound);
            int round = Integer.parseInt(tvwRound.getText().toString());
            round++;
            tvwRound.setText(Integer.toString(round));
        } catch (Exception e) {
            Log.e(TAG, "Unable to set round", e);
        }
    }

    public void incrementScore() {
        try {
            TextView tvwScore = (TextView) getView().findViewById(R.id.tvwScore);
            if (tvwScore == null)
                Log.w(TAG, "tvwScore is null");
            int score = Integer.parseInt(tvwScore.getText().toString());
            score++;
            tvwScore.setText(Integer.toString(score));
        } catch (Exception e) {
            Log.e(TAG, "Unable to set score", e);
        }
    }

    public void resetScore() {
        TextView tvwScore = (TextView) getView().findViewById(R.id.tvwScore);
        TextView tvwRound = (TextView) getView().findViewById(R.id.tvwRound);
        tvwScore.setText(Integer.toString(0));
        tvwRound.setText(Integer.toString(0));
    }
}

interface ScoreBarUpdate {
    //Handles the callback to update the score and round
    void incrementRound();

    void incrementScore();

    void resetScore();
}