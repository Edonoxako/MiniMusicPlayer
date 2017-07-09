package com.edonoxako.minimusicplayer.app;

import android.media.MediaDescription;

/**
 * Created by edono on 09.07.2017.
 */

public interface PlaybackView {
    void onAudioServiceConnected();
    void onPlayTrack();
    void onPauseTrack();
    void setTrackMetaData(MediaDescription description);
    void showMessage(String message);
}
