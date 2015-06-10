package com.edonoxako.minimusicplayer.app.model.providers;

import com.edonoxako.minimusicplayer.app.model.SongMetaData;

import java.util.ArrayList;

public interface SongsDownloaderListener {
    void songsListDownloaded(ArrayList<SongMetaData> list);
    void songDownloaded();
    void internetUnavailable();
    void errorOccurred(int errorCode);
}
