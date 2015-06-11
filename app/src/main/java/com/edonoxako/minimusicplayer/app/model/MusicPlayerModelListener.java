package com.edonoxako.minimusicplayer.app.model;

public interface MusicPlayerModelListener {
    void songPrepared();
    void songDownloaded();
    void songListDeleting();
    void onError(String errorMessage);
}
