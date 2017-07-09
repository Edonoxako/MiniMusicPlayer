package com.edonoxako.minimusicplayer.app.playback;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaBrowserServiceCompat;
import android.util.Log;

import java.util.List;

/**
 * Created by edono on 09.07.2017.
 */

public class AudioBrowserService extends MediaBrowserServiceCompat implements PlaybackManager.PlaybackStateListener {

    private static final String TAG = "AudioBrowserService";
    public static final String MEDIA_ROOT_ID = "mediaRootId";

    private PlaybackManager playbackManager;
    private PlayListProvider playListProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        playListProvider = new PlayListProvider(this);
        playbackManager = new PlaybackManager(this, new AudioPlayer(this), playListProvider);
        // Set the session's token so that client activities can communicate with it.
        setSessionToken(playbackManager.getSessionToken());
        playbackManager.setPlaybackStateListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        playbackManager.release();
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        Log.d(TAG, "onGetRoot() called with: clientPackageName = [" + clientPackageName + "], clientUid = [" + clientUid + "], rootHints = [" + rootHints + "]");
        return new BrowserRoot(MEDIA_ROOT_ID, null);
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        Log.d(TAG, "onLoadChildren() called with: parentId = [" + parentId + "], result = [" + result + "]");
        result.sendResult(playListProvider.getAllTracks());
    }

    @Override
    public void onStartPlaying() {
        startService(new Intent(this, AudioBrowserService.class));

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("I am playing!")
                .build();
        startForeground(0, notification);
    }

    @Override
    public void onPausePlaying() {
        stopForeground(false);
    }

    @Override
    public void onStopPlaying() {
        stopForeground(false);
        stopSelf();
    }
}
