package com.edonoxako.minimusicplayer.app.playback;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaDescriptionCompat;

import com.edonoxako.minimusicplayer.app.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by edono on 09.07.2017.
 */

public class PlayListProvider {

    private final Uri songUri;

    public PlayListProvider(Context context) {
        songUri = Uri.parse("android.resource://" + context.getPackageName() + R.raw.regulator);
    }

    public MediaBrowserCompat.MediaItem getTrack(String mediaId) {
        return null;
    }

    public List<MediaBrowserCompat.MediaItem> getAllTracks() {
        MediaDescriptionCompat description = new MediaDescriptionCompat.Builder()
                .setTitle("Regulator")
                .setSubtitle("Clutch")
                .setMediaUri(songUri)
                .setMediaId("some-media-id")
                .build();

        MediaBrowserCompat.MediaItem item = new MediaBrowserCompat.MediaItem(description,
                MediaBrowserCompat.MediaItem.FLAG_PLAYABLE);

        return Collections.singletonList(item);
    }

    private MediaBrowserCompat.MediaItem getNextTrack() {
        return null;
    }

    private MediaBrowserCompat.MediaItem getPreviousTrack() {
        return null;
    }
}
