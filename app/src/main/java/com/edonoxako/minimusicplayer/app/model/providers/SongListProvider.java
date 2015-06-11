package com.edonoxako.minimusicplayer.app.model.providers;

/**
 * Created by Dasha on 10.06.2015.
 */
public interface SongListProvider {
    void registerProviderListener(SongsDownloaderListener songsDownloaderListener);
    void loadSongs();
    void refresh();
}
