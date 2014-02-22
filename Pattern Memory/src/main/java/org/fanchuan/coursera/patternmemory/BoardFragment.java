package org.fanchuan.coursera.patternmemory;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BoardFragment extends Fragment implements View.OnClickListener, BoardHost {
    /* This BoardFragment hosts everything to do with managing the board: the game logic class Simon,
    listening to and pressing the buttons via their .setPressed() methods.
    It needs a reference to ScoreBarUpdate but can't accept it via constructor to enable use in an
    XML layout; this was a design tradeoff. Pass the ScoreBarUpdate reference in the begin() method which
    starts the game.
     */
    final int PLAY_DURATION_MS = 600; // How long the computer presses the buttons during playback
    final int PAUSE_DURATION_MS = 200; // How long the computer pauses between playback button presses

    private String TAG = BoardFragment.class.getSimpleName();
    private List<View> simonButtons;
    private Simon simon;
    private final Handler handlerPlay = new Handler();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board_2x2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (!initialize()) {
            Log.e(TAG, "Simon has failed to initialize");
            Toast.makeText(getActivity(), getActivity().getString(R.string.initialization_error), Toast.LENGTH_SHORT).show();
        }
    }

    public void onClick(View vw) {
        //TODO delegate to Simon's buttonPressed
        byte indexButton = (byte) simonButtons.indexOf(vw);
        simon.buttonPressed(indexButton);
    }

    void begin(ScoreBarUpdate scoreBarUpdate) {
        /* Assumes a game starting with 1 random note
        Prior to begin() method, if the user started a new game while the melody was playing,
        the melody from the old game would continue playing while the first note from the new melody
        would start playing. begin() cancels any such pending activity.
         */
        handlerPlay.removeCallbacksAndMessages(null);
        simon.begin(scoreBarUpdate);
    }

    public void gameMessage(byte MESSAGE_CODE) {
        String[] boardHostMessages = getActivity().getResources().getStringArray(R.array.board_host_messages);
        Toast.makeText(getActivity(), boardHostMessages[MESSAGE_CODE], Toast.LENGTH_SHORT).show();
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
        byte[] buttonIndices = {0, 1, 2, 3};
        try {
            this.simon = new Simon(buttonIndices, this);
        } catch (Exception e) {
            Log.e(TAG, "Unable to instantiate Simon, e");
            return false;
        }
        return true;
    }

    public boolean play(List<Byte> playList) {
        /* In: List of byte objects (notes) to play
        Returns false if the melody won't play, true otherwise */
        int delayMultiplier = 1;
        if (playList == null) return false;
        for (final Byte buttonIndex : playList) {
            handlerPlay.postDelayed(new Runnable() {
                public void run() {
                    playOne(buttonIndex);
                }
            }, delayMultiplier * (PLAY_DURATION_MS + PAUSE_DURATION_MS));
            delayMultiplier++; //Need this multiplier so that played back notes are consecutive in time
        }
        return true;
    }

    public void playOne(byte buttonIndex) {

        /* convert the buttonIndex supplied by Simon to the actual View corresponding to that index.
        * Press the button (passive XML properties will make the button appear to "light up") */
        final View VW = simonButtons.get(buttonIndex);
        VW.setPressed(true); //if doesn't redraw on API10, add BTN.invalidate()!
        VW.postDelayed(new Runnable() {
            public void run() {
                VW.setPressed(false);
            }
        }, PLAY_DURATION_MS);
    }
}

interface BoardHost {
    public boolean play(List<Byte> playList);

    public void playOne(byte buttonIndex);

    public void gameMessage(byte MESSAGE_CODE);
}