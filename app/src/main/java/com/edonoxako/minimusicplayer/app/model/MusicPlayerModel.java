package com.edonoxako.minimusicplayer.app.model;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.view.GestureDetector;
import com.edonoxako.minimusicplayer.app.R;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Dasha on 07.06.2015.
 */
public class MusicPlayerModel {

    private Activity mActivity;
    private MusicPlayerService musicPlayerService;
    private Intent playIntent;
    private boolean isBound = false;
    private ArrayList<SongMetaData> songsList;
    private MusicPlayerModelListener listener;

    private ServiceConnection musicServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) service;
            musicPlayerService = binder.getService();

            ArrayList<SongMetaData> data = musicPlayerService.getSongsList();
            if (data == null) {
                musicPlayerService.setSongsList(songsList);
                loadSongs();
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
        if (playIntent == null) {
            playIntent = new Intent(mActivity, MusicPlayerService.class);
            mActivity.bindService(playIntent, musicServiceConnection, Context.BIND_AUTO_CREATE);
            mActivity.startService(playIntent);
        }
    }

    private void loadSongs() {
        try {
            SongMetaData song = new SongMetaData();
            song.setIsPlaying(false);
            song.setIsDownloaded(false);
            song.setSongName("Regulator");
            song.setPath("wwwww");
            songsList.add(song);
            listener.songPrepared();

            File songsDir = mActivity.getDir("songs", Context.MODE_PRIVATE);
            File songFile = new File(songsDir, "regulator.mp3");
            BufferedInputStream bis = new BufferedInputStream(mActivity.getResources().openRawResource(R.raw.regulator));

            FileOutputStream os = new FileOutputStream(songFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = bis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            bis.close();
            os.close();

            song.setIsDownloaded(true);
            song.setPath(songFile.getAbsolutePath());
            listener.songDownloaded();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void destroyAppModel() {
        mActivity.unbindService(musicServiceConnection);
        if (mActivity.isFinishing()) {
            mActivity.stopService(playIntent);
        }
        musicPlayerService = null;
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

}
