package org.fanchuan.coursera.patternmemory;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Simon {
    /* UI independent Simon implementation. Major refactor and method swap with Board Fragment. All game
    * logic is now in Simon rather than distributed between BoardFragment and Simon. Simon no longer presses
    * buttons directly but rather tells the BoardHost to do it (methods moved into BoardFragment).
    * Simon no longer holds references to android Views, so it is the host's responsibility to give Simon
    * an index and to convert Simon's play requests to the BoardHost in to the actual Views.
    * This refactor is both for a reference of clearer class responsibilities than the previous version, and
    * hopefully the board can now undergo configuration changes without disrupting the game.
    * Previously, configuration changes would not crash the game but the new Activity would have different
    * View objects than the ones Simon references, causing both a memory leak from holding that reference and
    * causing all button presses to be "incorrect", as the original views were no longer available.
     */
    private final String TAG = Simon.class.getSimpleName();
    private final java.util.Random rand = new java.util.Random();
    private final byte MESSAGE_ROUND_WIN = 0; //for boardHost.gameMessage(MESSAGE_CODE)
    private final byte MESSAGE_LOSE = 1;
    private List<Byte> playList = new ArrayList<Byte>();
    private BoardHost boardHost;
    private ScoreBarUpdate scoreBarUpdate;
    private byte[] indexButtons;
    private Iterator playListIterator; //used to verify user is playing melody correctly

    protected Simon(byte[] indexButtons, BoardHost boardHost) {
        /* indexButtons - array of button indices. Typically for 4 buttons this would be {0,1,2,3} however the
        * index numbers can be positive or negative, non-consecutive and in any order.
        * boardHost, the entity hosting the board that can handle showing button presses and */
        super();
        if (indexButtons.length == 0) throw new IllegalArgumentException();
        this.indexButtons = indexButtons;
        this.boardHost = boardHost;
        playListIterator = playList.iterator();
    }

    protected void begin(ScoreBarUpdate scoreBarUpdate) {
        /*Simon does not know the score or what round it is, but it uses the reference to scoreBarUpdate to
        * notify of incrementing the score or round by 1. */
        playList = new ArrayList<Byte>();
        playListIterator = playList.iterator();
        this.scoreBarUpdate = scoreBarUpdate;
        noteRandomAdd();
        boardHost.play(playList);
    }

    public void buttonPressed(byte indexButton) {
        //Activity parentActivity = (Activity) vw.getContext();
        if (verifyButtonPressed(indexButton)) {
            //This button press matches the current one in the Iterator, get a point whether it is the last or not
            scoreBarUpdate.incrementScore();
            if (!hasNext()) {
                //No more buttons to press, at the end of the melody!! Next round.
                scoreBarUpdate.incrementRound();
                //String messageRoundWin = parentActivity.getResources().getString(R.string.message_round_win);
                try {
                    boardHost.gameMessage(MESSAGE_ROUND_WIN);
                    //Toast.makeText(parentActivity, messageRoundWin, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.w(TAG, "Unable to send Round Win Message");
                }
                noteRandomAdd();
                boardHost.play(playList);
            }
        } else {
            //String messageLose = parentActivity.getResources().getString(R.string.message_lose);
            try {
                boardHost.gameMessage(MESSAGE_LOSE);
                //Toast.makeText(parentActivity, messageLose, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.w(TAG, "Unable to send Game Lost Message");
            }
        }
    }

    protected boolean hasNext() {
        //Return true if the current melody iterator has any more notes, false otherwise
        try {
            return playListIterator.hasNext();
        } catch (NullPointerException e) {
            return false;
        }
    }

    private void noteRandomAdder() {
        //Typically not called directly as it does not create the iterator for verifying the user's input
        int deviceButtonIndex = rand.nextInt(indexButtons.length);
        Log.v(TAG, "deviceButtonIndex: " + deviceButtonIndex);
        playList.add(indexButtons[deviceButtonIndex]);
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
    protected void setPlayList(List<Byte> myPlayList) {
        this.playList = myPlayList;
    }

    boolean verifyButtonPressed(byte indexButton) {
        /* Verify the button pressed by the user - is it part of the melody?
        Return true if part of melody, false otherwise */
        return playListIterator.hasNext() && indexButton == playListIterator.next();
    }
}