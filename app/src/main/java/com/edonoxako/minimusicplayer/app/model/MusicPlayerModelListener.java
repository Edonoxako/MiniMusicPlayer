package com.edonoxako.minimusicplayer.app.model;

public interface MusicPlayerModelListener {
    void songPrepared();
    void songDownloaded();
    void onError(String errorMessage);
}
