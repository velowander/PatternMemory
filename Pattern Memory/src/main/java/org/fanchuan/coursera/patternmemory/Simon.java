package org.fanchuan.coursera.patternmemory;

import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Simon {
    /* Mostly UI independent Simon implementation (although assumes it is passed a List<View> in the
    constructor which is Android specific).
    In: List<View> - only required to have 1 View, though classic game had 4. Views could be buttons but
    don't need to be; they do need to implement .setPressed() which is how Simon indicates to the UI
    that it is playing a melody.
     */
    final int PLAY_DURATION_MS = 600; // How long the computer presses the buttons during playback
    final int PAUSE_DURATION_MS = 200; // How long the computer pauses between playback button presses
    private final String TAG = Simon.class.getSimpleName();
    private final java.util.Random rand = new java.util.Random();
    private final Handler handlerPlay = new Handler();
    private List<View> playList;
    private List<View> deviceButtons;
    private Iterator playListIterator; //used to verify user is playing melody correctly

    /* Passing a List of the device's View (could be Button) objects is required. This is all that Simon
    * knows about the UI */
    protected Simon(List<View> deviceButtons) {
        super();
        if (deviceButtons == null || deviceButtons.size() == 0)
            throw new IllegalArgumentException();
        this.deviceButtons = deviceButtons;
        begin();
    }

    protected void begin() {
        /* Prior to begin() method, if the user started a new game while the melody was playing,
        the melody from the old game would continue playing while the first note from the new melody
        would start playing. begin() cancels any such pending activity.
         */
        handlerPlay.removeCallbacksAndMessages(null);
        playList = new ArrayList<View>();
        playListIterator = playList.iterator();
    }

    protected List<View> getDeviceButtons() {
        return this.deviceButtons;
    }

    protected boolean hasNext() {
        //Return true if the current melody iterator has any more notes, false otherwise
        try {
            return playListIterator.hasNext();
        } catch (NullPointerException e) {
            return false;
        }
    }

    protected void playOne(final View VW) {
        VW.setPressed(true); //if doesn't redraw on API10, add BTN.invalidate()!
        VW.postDelayed(new Runnable() {
            public void run() {
                VW.setPressed(false);
            }
        }, PLAY_DURATION_MS);
    }

    protected boolean play() {
        /* In: List of View objects (notes) to play
        Returns false if the melody won't play, true otherwise */
        int delayMultiplier = 1;
        if (playList == null) return false;
        for (final View vwPlay : playList) {
            handlerPlay.postDelayed(new Runnable() {
                public void run() {
                    playOne(vwPlay);
                }
            }, delayMultiplier * (PLAY_DURATION_MS + PAUSE_DURATION_MS));
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

    @SuppressWarnings("unused")
    protected synchronized void noteRandomAdd(int numberToAdd) {
        //Use this method to add Random buttons in bulk.
        for (int i = 0; i < numberToAdd; i++) {
            noteRandomAdder();
        }
        playListIterator = playList.iterator();
    }

    protected synchronized void noteRandomAdd() {
        noteRandomAdder();
        playListIterator = playList.iterator();
    }

    @SuppressWarnings("unused")
    protected void setPlayList(List<View> myPlayList) {
        this.playList = myPlayList;
    }

    boolean verifyButtonPress(View vw) {
        /* Verify the button pressed by the user - is it part of the melody?
        Return true if part of melody, false otherwise */
        return playListIterator.hasNext() && vw == playListIterator.next();
    }
}
