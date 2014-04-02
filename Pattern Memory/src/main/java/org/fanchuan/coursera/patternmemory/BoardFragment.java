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

public class BoardFragment extends Fragment implements View.OnClickListener, Simon.BoardHost {
    /* This BoardFragment hosts everything to do with managing the board: the game logic class Simon,
    listening to and pressing the buttons via their .setPressed() methods.
     */
    final int PLAY_DURATION_MS = 600; // How long the computer presses the buttons during playback
    final int PAUSE_DURATION_MS = 200; // How long the computer pauses between playback button presses
    private final Handler handlerPlay = new Handler();
    private String TAG = BoardFragment.class.getSimpleName();
    private List<View> simonButtons;
    private Simon simon;

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
        if (!initialize(getView())) {
            Log.e(TAG, "Simon is not available");
            Toast.makeText(getActivity(), getActivity().getString(R.string.initialization_error), Toast.LENGTH_SHORT).show();
        }
    }

    //Respond to button presses
    public void onClick(View vw) {
        byte indexButton = (byte) simonButtons.indexOf(vw);
        try {
            simon.buttonPressed(indexButton);
        } catch (Exception e) {
            Log.e(TAG, "Simon unavailable", e);
        }
    }

    //"Business" logic
    void begin() {
        /* Assumes a game starting with 1 random note
        Prior to begin() method, if the user started a new game while the melody was playing,
        the melody from the old game would continue playing while the first note from the new melody
        would start playing. begin() cancels any such pending activity.
         */
        handlerPlay.removeCallbacksAndMessages(null);
        try {
            simon.begin();
        } catch (Exception e) {
            Log.e(TAG, "Simon unavailable", e);
        }
    }

    public void gameMessage(byte MESSAGE_CODE) {
        String[] boardHostMessages = getActivity().getResources().getStringArray(R.array.board_host_messages);
        Toast.makeText(getActivity(), boardHostMessages[MESSAGE_CODE], Toast.LENGTH_SHORT).show();
    }

    public void gameOver() {
        /* Received signal that game is over, cancel all callbacks on this handler */
        handlerPlay.removeCallbacksAndMessages(null);
    }

    Simon.ScoreObservable getScoreObservable() {
        /* Any observers interested in the score will need to observe this observable */
        return simon.getScoreObservable();
    }

    public boolean initialize(View rootView) {
        /* BoardFragment must have initialized its root view already; could call in onCreateView AFTER
        inflating the view or in, for example, on ActivityCreated(). Passing the rootView parameter
        guarantees that the View hierarchy is available, use getView() to get the rootView.
        If the parent Activity has a configuration change (e.g. orientation), we need to get all the new Views.
        We only initialize Simon if it is null because "the new" Simon stores button indexes not actual views
        and does not need to be refreshed when the Activity destroys and recreates.
        Return false if initialization fails, true otherwise
         */

        try {
            /* If we are running initialize() due to a configuration change, we need to reuse our existing
            * Simon instance which has the state of the current melody but we need to refresh our Views
            * as the previous Views were destroyed.*/
            simonButtons = new ArrayList<View>();
            simonButtons.add(rootView.findViewById(R.id.buttonGreen));
            simonButtons.add(rootView.findViewById(R.id.buttonRed));
            simonButtons.add(rootView.findViewById(R.id.buttonBlue));
            simonButtons.add(rootView.findViewById(R.id.buttonYellow));
            Log.d(TAG, "Simon initialized with device button count: " + simonButtons.size());
        } catch (Exception e) {
            Log.e(TAG, "Unable to create array of buttons", e);
            return false;
        }
        for (View simonButton : simonButtons) {
            simonButton.setOnClickListener(this);
        }
        try {
            if (simon == null) this.simon = new Simon((byte) simonButtons.size(), this);
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

    private List<View> getAllChildrenBFS(View v) {
        /* Iterative method to get all View children using Breadth First Search (BFS)
        http://stackoverflow.com/questions/18668897/android-get-all-children-elements-of-a-viewgroup
        It returns only the "non ViewGroup" children in the list */
        List<View> visited = new ArrayList<View>();
        List<View> unvisited = new ArrayList<View>();
        unvisited.add(v);

        while (!unvisited.isEmpty()) {
            View child = unvisited.remove(0);
            visited.add(child);
            if (!(child instanceof ViewGroup)) continue;
            ViewGroup group = (ViewGroup) child;
            final int childCount = group.getChildCount();
            for (int i = 0; i < childCount; i++) unvisited.add(group.getChildAt(i));
        }

        return visited;
    }
}