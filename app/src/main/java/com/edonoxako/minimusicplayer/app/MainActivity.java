package com.edonoxako.minimusicplayer.app;

import android.content.ComponentName;
import android.media.MediaDescription;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.edonoxako.minimusicplayer.app.playback.AudioBrowserService;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PlaybackView {

    private static final String TAG = "MainActivity";

    private AudioPlaybackPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new AudioPlaybackPresenter(this, this);
        presenter.initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.onPause();
    }

    @Override
    public void onAudioServiceConnected() {
        Button playButton = (Button) findViewById(R.id.button_play);
        Button stopButton = (Button) findViewById(R.id.button_stop);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.play("some-media-id");
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.stop();
            }
        });
    }

    @Override
    public void onPlayTrack() {

    }

    @Override
    public void onPauseTrack() {

    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void setTrackMetaData(MediaDescription description) {

    }

    @Override
    public void showMessage(String message) {

    }
}
