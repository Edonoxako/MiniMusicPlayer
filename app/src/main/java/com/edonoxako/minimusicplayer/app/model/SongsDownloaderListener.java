package com.edonoxako.minimusicplayer.app.model;

import java.util.ArrayList;

public interface SongsDownloaderListener {
    void songsListDownloaded(ArrayList<SongMetaData> list);
    void songDownloaded();
    void internetUnavailable();
}
