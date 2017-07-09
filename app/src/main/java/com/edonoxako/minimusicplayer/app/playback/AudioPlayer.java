package com.edonoxako.minimusicplayer.app.playback;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;

import java.io.IOException;

/**
 * Created by edono on 09.07.2017.
 */

public class AudioPlayer {

    private MediaPlayer player;
    private Context context;

    private Uri currentTrackUri;
    private PlayerState currentState;
    private PlayAction action;
    private boolean runOnPrepared = false;

    public AudioPlayer(Context context) {
        this.context = context;
        this.player = new MediaPlayer();
        this.currentState = new IdleState();
    }

    public void play(){
        action = PlayAction.PLAY;
        if (currentState.is(State.PREPARED)
                || currentState.is(State.STOPPED)
                || currentState.is(State.PAUSED)) {
            currentState.run();
        } else if (currentState.is(State.PREPARING)) {
            runOnPrepared = true;
        }
    }

    public void stop() {
        action = PlayAction.STOP;
        if (currentState.is(State.STARTED)) {
            currentState.run();
        }
    }

    public void pause() {
        action = PlayAction.PAUSE;
        if (currentState.is(State.STARTED)) {
            currentState.run();
        }
    }

    public void release() {
        if (player.isPlaying()) player.stop();
        player.reset();
        player.release();
    }

    public void setTrack(Uri trackUri) {
        currentTrackUri = trackUri;
        action = PlayAction.RESET;
        if (currentState.is(State.IDLE)
                || currentState.is(State.STOPPED)) {
            currentState.run();
        }
    }



    private enum State {
        PREPARING,
        IDLE,
        PREPARED,
        STARTED,
        STOPPED,
        PAUSED,
        COMPLETED
    }

    private enum PlayAction {
        PLAY,
        STOP,
        PAUSE,
        RESET
    }

    private class PlayerState {
        public boolean is(State state) {
            return state == State.PREPARING;
        }
        public void run() {}
    }

    private class IdleState extends PlayerState implements MediaPlayer.OnPreparedListener {
        public IdleState() {
            player.setOnPreparedListener(this);
        }

        @Override
        public boolean is(State state) {
            return state == State.IDLE;
        }

        @Override
        public void run() {
            try {
                player.setDataSource(context, currentTrackUri);
                currentState = new PlayerState();
                player.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            currentState = new PreparedState();
            if (runOnPrepared) {
                currentState.run();
                runOnPrepared = false;
            }
        }
    }

    private class PreparedState extends PlayerState {
        @Override
        public boolean is(State state) {
            return state == State.PREPARED;
        }

        @Override
        public void run() {
            player.start();
            currentState = new StartedState();
        }
    }

    private class StartedState extends PlayerState {
        @Override
        public boolean is(State state) {
            return state == State.STARTED;
        }

        @Override
        public void run() {
            switch (action) {
                case PAUSE:
                    player.pause();
                    currentState = new PausedState();
                    break;

                case STOP:
                    player.stop();
                    currentState = new StoppedState();
                    break;
            }
        }
    }

    private class PausedState extends PlayerState {
        @Override
        public boolean is(State state) {
            return state == State.PAUSED;
        }

        @Override
        public void run() {
            player.start();
            currentState = new StartedState();
        }
    }

    private class StoppedState extends PlayerState implements MediaPlayer.OnPreparedListener {
        public StoppedState() {
            player.setOnPreparedListener(this);
        }

        @Override
        public boolean is(State state) {
            return state == State.STOPPED;
        }

        @Override
        public void run() {
            switch (action) {
                case PLAY:
                    player.prepareAsync();
                    break;

                case RESET:
                    player.reset();
                    currentState = new IdleState();
                    currentState.run();
                    break;
            }
        }

        @Override
        public void onPrepared(MediaPlayer mp) {
            player.start();
            currentState = new StartedState();
        }
    }
}
