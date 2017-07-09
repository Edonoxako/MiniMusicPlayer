package com.edonoxako.minimusicplayer.app;

/**
 * Created by edono on 09.07.2017.
 */

public interface PlaybackPresenter {
    void initialize();
    void play();
    void play(String mediaId);
    void stop();
    void pause();
    void next();
    void previous();
    void onStop();
    void onStart();
    void onPause();
    void onResume();
}
