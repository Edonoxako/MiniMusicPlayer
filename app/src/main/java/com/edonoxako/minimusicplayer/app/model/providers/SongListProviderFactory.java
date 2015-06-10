package com.edonoxako.minimusicplayer.app.model.providers;

import android.content.Context;

public class SongListProviderFactory {
    public SongListProvider getProvider(Context context) {
        return new SongsDownloader(context);
    }
}
