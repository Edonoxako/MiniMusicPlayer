package com.edonoxako.minimusicplayer.app;

import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.edonoxako.minimusicplayer.app.playback.AudioBrowserService;

/**
 * Created by edono on 09.07.2017.
 */

public class AudioPlaybackPresenter implements PlaybackPresenter {

    private static final String TAG = "AudioPlaybackPresenter";

    private AppCompatActivity activity;
    private PlaybackView view;

    private MediaBrowserCompat mMediaBrowser;

    private final MediaBrowserCompat.ConnectionCallback mConnectionCallbacks =
            new MediaBrowserCompat.ConnectionCallback() {
                @Override
                public void onConnected() {
                    initMediaController();
                }
            };

    MediaControllerCompat.Callback controllerCallback = new MediaControllerCompat.Callback() {
        @Override
        public void onMetadataChanged(MediaMetadataCompat metadata) {}

        @Override
        public void onPlaybackStateChanged(PlaybackStateCompat state) {}
    };


    public AudioPlaybackPresenter(AppCompatActivity activity, PlaybackView view) {
        this.activity = activity;
        this.view = view;
    }

    private void initMediaController() {
        // Get the token for the MediaSession
        MediaSessionCompat.Token token = mMediaBrowser.getSessionToken();

        // Create a MediaControllerCompat
        MediaControllerCompat mediaController = null;
        try {
            mediaController = new MediaControllerCompat(activity, token);
            mediaController.registerCallback(controllerCallback);

            // Save the controller
            MediaControllerCompat.setMediaController(activity, mediaController);

            // Finish building the UI
            view.onAudioServiceConnected();
        } catch (RemoteException e) {
            Log.e(TAG, "Error during media controller creation", e);
            view.showMessage("Unable to connect to audio service");
        }
    }

    @Override
    public void initialize() {
        // Create MediaBrowserServiceCompat
        mMediaBrowser = new MediaBrowserCompat(activity, new ComponentName(activity, AudioBrowserService.class),
                mConnectionCallbacks, null);
    }

    @Override
    public void play() {
        if (MediaControllerCompat.getMediaController(activity) != null) {
            MediaControllerCompat.getMediaController(activity).getTransportControls().play();
        }
    }

    @Override
    public void play(String mediaId) {
        if (MediaControllerCompat.getMediaController(activity) != null) {
            MediaControllerCompat.getMediaController(activity).getTransportControls().playFromMediaId(mediaId, null);
        }
    }

    @Override
    public void stop() {
        if (MediaControllerCompat.getMediaController(activity) != null) {
            MediaControllerCompat.getMediaController(activity).getTransportControls().stop();
        }
    }

    @Override
    public void pause() {
        if (MediaControllerCompat.getMediaController(activity) != null) {
            MediaControllerCompat.getMediaController(activity).getTransportControls().pause();
        }
    }

    @Override
    public void next() {

    }

    @Override
    public void previous() {

    }

    @Override
    public void onStop() {
        // (see "stay in sync with the MediaSession")
        if (MediaControllerCompat.getMediaController(activity) != null) {
            MediaControllerCompat.getMediaController(activity).unregisterCallback(controllerCallback);
        }
        mMediaBrowser.disconnect();
    }

    @Override
    public void onStart() {
        mMediaBrowser.connect();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }
}
