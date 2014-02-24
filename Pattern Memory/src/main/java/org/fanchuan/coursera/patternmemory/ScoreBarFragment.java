package org.fanchuan.coursera.patternmemory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Observable;
import java.util.Observer;

public class ScoreBarFragment extends Fragment implements Observer {
    /* Takes care of updating the score and round when called through its ScoreBarUpdate interface.
    * In the event of a configuration change (orientation, for example)
    * this fragment maintains no member variables and does not maintain state if the host activity is destroyed.
    * The score and round persistence is external in TextViews and both need android:freezesText = "true" to
    * survive configuration changes. */
    private String TAG = ScoreBarFragment.class.getSimpleName();

    //Class: v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_score_bar, container, false);
    }

    //Interface: observable
    public void update(Observable observable, Object o) {
        if (observable instanceof Simon.ScoreObservable) {
            byte scoreRequest = ((Simon.ScoreObservable) observable).getRequest();
            switch (scoreRequest) {
                case Simon.ScoreObservable.RESET_SCORE:
                    resetScore();
                    break;
                case Simon.ScoreObservable.INCREMENT_ROUND:
                    incrementRound();
                    break;
                case Simon.ScoreObservable.INCREMENT_SCORE:
                    incrementScore();
                    break;
                default:
                    //do nothing
            }
            Log.d(TAG, "ScoreBarFragment: received ScoreUpdate");
        }
        Log.d(TAG, "ScoreBarFragment: received unknown observable");
    }

    //"Business" logic
    private void incrementRound() {
        try {
            TextView tvwRound = (TextView) getView().findViewById(R.id.tvwRound);
            int round = Integer.parseInt(tvwRound.getText().toString());
            round++;
            tvwRound.setText(Integer.toString(round));
        } catch (Exception e) {
            Log.e(TAG, "Unable to set round", e);
        }
    }

    private void incrementScore() {
        try {
            TextView tvwScore = (TextView) getView().findViewById(R.id.tvwScore);
            if (tvwScore == null) Log.w(TAG, "tvwScore is null");
            int score = Integer.parseInt(tvwScore.getText().toString());
            score++;
            tvwScore.setText(Integer.toString(score));
        } catch (Exception e) {
            Log.e(TAG, "Unable to set score", e);
        }
    }

    private void resetScore() {
        TextView tvwScore = (TextView) getView().findViewById(R.id.tvwScore);
        TextView tvwRound = (TextView) getView().findViewById(R.id.tvwRound);
        tvwScore.setText(Integer.toString(0));
        tvwRound.setText(Integer.toString(0));
    }
}
