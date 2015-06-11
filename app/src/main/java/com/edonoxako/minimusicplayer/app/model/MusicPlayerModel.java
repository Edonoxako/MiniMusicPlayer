package com.edonoxako.minimusicplayer.app.model;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.edonoxako.minimusicplayer.app.R;
import com.edonoxako.minimusicplayer.app.model.media.MusicPlayerService;
import com.edonoxako.minimusicplayer.app.model.media.MusicPlayerServiceListener;
import com.edonoxako.minimusicplayer.app.model.providers.SongListProvider;
import com.edonoxako.minimusicplayer.app.model.providers.SongListProviderFactory;
import com.edonoxako.minimusicplayer.app.model.providers.SongsDownloaderListener;

import java.util.ArrayList;

public class MusicPlayerModel implements SongsDownloaderListener {

    private Activity mActivity;
    private MusicPlayerService musicPlayerService;
    private Intent playIntent;
    private boolean isBound = false;
    private ArrayList<SongMetaData> songsList;
    private MusicPlayerModelListener listener;
    private SongListProvider downloader;

    private MusicPlayerServiceListener musicPlayerServiceListener = new MusicPlayerServiceListener() {
        @Override
        public void onMusicPlayerStateChanged() {
            listener.songDownloaded();
        }
    };

    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) service;
            musicPlayerService = binder.getService();
            musicPlayerService.registerListener(musicPlayerServiceListener);

            ArrayList<SongMetaData> data = musicPlayerService.getSongsList();
            if (data == null) {
                downloader.loadSongs();
            } else {
                songsList = data;
                listener.songPrepared();
            }

            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    public MusicPlayerModel(MusicPlayerModelListener lst) {
        songsList = new ArrayList<SongMetaData>();
        listener = lst;
    }

    public void initAppModel(Activity activity) {
        mActivity = activity;
        downloader = new SongListProviderFactory().getProvider(mActivity);
        downloader.registerProviderListener(this);
        if (playIntent == null) {
            playIntent = new Intent(mActivity, MusicPlayerService.class);
            mActivity.bindService(playIntent, musicServiceConnection, Context.BIND_AUTO_CREATE);
            mActivity.startService(playIntent);
        }
    }

    public void destroyAppModel() {
        mActivity.unbindService(musicServiceConnection);
        if (mActivity.isFinishing()) {
            mActivity.stopService(playIntent);
        }
        musicPlayerService = null;
    }

    public void refreshSongsList() {
        downloader.refresh();
    }

    public ArrayList<SongMetaData> getSongsList() {
        return songsList;
    }

    public void playSong(int pos) {
        musicPlayerService.playSong(pos);
    }

    public void pauseSong(int pos) {
        musicPlayerService.pauseSong(pos);
    }

    @Override
    public void songsListDownloaded(ArrayList<SongMetaData> list) {
        songsList = list;
        musicPlayerService.setSongsList(list);
        listener.songPrepared();
    }

    @Override
    public void songDownloaded() {
        listener.songDownloaded();
    }

    @Override
    public void internetUnavailable() {
        listener.onError(mActivity.getResources().getString(R.string.internet_unavailable));
    }

    @Override
    public void errorOccurred(int errorCode) {
        listener.onError(mActivity.getResources().getString(R.string.error) + errorCode);
    }

    @Override
    public void songListDeleting() {
        if (musicPlayerService.getSongsList() != null) {
            musicPlayerService.getSongsList().clear();
        }
        listener.songListDeleting();
    }
}
