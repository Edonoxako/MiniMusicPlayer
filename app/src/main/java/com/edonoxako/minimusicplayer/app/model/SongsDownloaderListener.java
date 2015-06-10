package com.edonoxako.minimusicplayer.app.model;

import java.util.ArrayList;

/**
 * Created by Dasha on 10.06.2015.
 */
public interface SongsDownloaderListener {
    void songsListDownloaded(ArrayList<SongMetaData> list);
}
