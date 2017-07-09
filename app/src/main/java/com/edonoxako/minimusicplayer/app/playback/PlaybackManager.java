package com.edonoxako.minimusicplayer.app.playback;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import static android.content.ContentValues.TAG;

/**
 * Created by edono on 09.07.2017.
 */

public class PlaybackManager {

    private Context context;
    private AudioPlayer player;
    private final PlayListProvider playList;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateListener listener;

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {

        }
    };

    public interface PlaybackStateListener {
        void onStartPlaying();
        void onPausePlaying();
        void onStopPlaying();
    }

    public PlaybackManager(Context context, AudioPlayer player, PlayListProvider playListProvider) {
        this.context = context;
        this.player = player;
        this.playList = playListProvider;
        initMediaSession(context);
    }

    public MediaSessionCompat.Token getSessionToken() {
        return mMediaSession.getSessionToken();
    }

    public void setPlaybackStateListener(PlaybackStateListener listener) {
        this.listener = listener;
    }

    private void initMediaSession(Context context) {
        // Create a MediaSessionCompat
        mMediaSession = new MediaSessionCompat(context, TAG);

        // Enable callbacks from MediaButtons and TransportControls
        mMediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
        PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY | PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        // MySessionCallback() has methods that handle callbacks from a media controller
        mMediaSession.setCallback(new MediaSessionCallback());
    }

    public void release() {
        player.release();
        mMediaSession.release();
    }

    private AudioManager getAudioManager() {
        return (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    private class MediaSessionCallback extends MediaSessionCompat.Callback {

        @Override
        public void onPlay() {
            AudioManager am = getAudioManager();
            int result = am.requestAudioFocus(afChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                if (listener != null) {
                    listener.onStartPlaying();
                }
                mMediaSession.setActive(true);
                player.play();
            }
        }

        @Override
        public void onStop() {
            AudioManager am = getAudioManager();
            // Abandon audio focus
            am.abandonAudioFocus(afChangeListener);
            // Start the service
            if (listener != null) listener.onStopPlaying();
            // Set the session inactive  (and update metadata and state)
            mMediaSession.setActive(false);
            // stop the player (custom call)
            player.stop();
        }

        @Override
        public void onPause() {
            AudioManager am = getAudioManager();
            am.abandonAudioFocus(afChangeListener);
            if (listener != null) listener.onPausePlaying();
            // pause the player (custom call)
            player.pause();
        }

        @Override
        public void onPlayFromMediaId(String mediaId, Bundle extras) {
            AudioManager am = getAudioManager();
            int result = am.requestAudioFocus(afChangeListener,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);

            if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                if (listener != null) {
                    listener.onStartPlaying();
                }
                mMediaSession.setActive(true);

                MediaBrowserCompat.MediaItem track = playList.getTrack(mediaId);
                player.setTrack(track.getDescription().getMediaUri());
                player.play();
            }
        }
    }
}
