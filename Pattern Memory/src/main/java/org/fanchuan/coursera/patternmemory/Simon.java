package org.fanchuan.coursera.patternmemory;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

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
    private boolean gameOn = false;
    private List<Byte> playList;
    private BoardHost boardHost;
    private ScoreObservable scoreObservable = new ScoreObservable();
    private byte[] indexButtons;
    private Iterator playListIterator; //used to verify user is playing melody correctly

    protected Simon(final byte[] indexButtons, final BoardHost boardHost) {
        /* indexButtons - array of button indices. Typically for 4 buttons this would be {0,1,2,3} however the
        * index numbers can be positive or negative, non-consecutive and in any order.
        * boardHost, the entity hosting the board that can handle showing button presses and */
        super();
        if (indexButtons.length == 0) throw new IllegalArgumentException();
        this.indexButtons = indexButtons;
        this.boardHost = boardHost;
    }

    protected void begin() {
        /* Start the game, start generating random notes and watching the keys to match to the playList */
        gameOn = true;
        scoreObservable.sendRequest(ScoreObservable.RESET_SCORE);
        playList = new ArrayList<Byte>();
        playListIterator = playList.iterator();
        noteRandomAdd();
        boardHost.play(playList);
    }

    public void buttonPressed(final byte indexButton) {
        if (!gameOn) return; //Simon ignores the button press if the game isn't in progress.
        if (verifyButtonPressed(indexButton)) {

            scoreObservable.sendRequest(ScoreObservable.INCREMENT_SCORE);
            if (!hasNext()) {
                //No more buttons to press, at the end of the melody!! Next round.
                scoreObservable.sendRequest(ScoreObservable.INCREMENT_ROUND);
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
            //Incorrect key press, the game is over.
            try {
                boardHost.gameMessage(MESSAGE_LOSE);
            } catch (Exception e) {
                Log.w(TAG, "Unable to send Game Lost Message");
            } finally {
                boardHost.gameOver();
                gameOn = false;
            }
        }
    }

    ScoreObservable getScoreObservable() {
        //  For any observers that need to know changes to the score or round, they should observe the returned object
        return scoreObservable;
    }


    private boolean hasNext() {
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
    protected synchronized void noteRandomAdd(final int numberToAdd) {
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

    boolean verifyButtonPressed(final byte indexButton) {
        /* Verify the button pressed by the user - is it part of the melody?
        The verification is very simple, just a comparison. We check .hasNext to make sure the user doesn't
        type too many keys - automatic fail during the game.
        Return true if part of melody, false otherwise */
        return playListIterator.hasNext() && indexButton == playListIterator.next();
    }

    public class ScoreObservable extends Observable {
        /* A request for a score bar update, to pass to any observers that wish to know the score
         * The ScoreBarUpdate will always have one of the listed values
         * sample usage: scoreObservable.requestCode(scoreObservable.INCREMENT_SCORE) to tell Observers that score has changed. */
        final static byte RESET_SCORE = 1;
        final static byte INCREMENT_ROUND = 2;
        final static byte INCREMENT_SCORE = 4;
        private byte requestCode;

        byte getRequest() {
            return requestCode;
        }

        void sendRequest(final byte REQUEST_CODE) {
            if (REQUEST_CODE == RESET_SCORE || REQUEST_CODE == INCREMENT_ROUND || REQUEST_CODE == INCREMENT_SCORE) {
                this.requestCode = REQUEST_CODE;
                setChanged();
                notifyObservers();
            }
        }
    }
}