package com.edonoxako.minimusicplayer.app.model.media;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import com.edonoxako.minimusicplayer.app.MainActivity;
import com.edonoxako.minimusicplayer.app.R;
import com.edonoxako.minimusicplayer.app.model.SongMetaData;

import java.io.IOException;
import java.util.ArrayList;

public class MusicPlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    public static final int NOTIFICATION_ID = 1;

    private MediaPlayer mediaPlayer;
    private IBinder musicBinder = new MusicBinder();
    private ArrayList<SongMetaData> songsList;
    private int curSongPos = -1;
    private MusicPlayerServiceListener listener;

    private void initMusicPlayer() {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        initMusicPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBinder;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        songsList.get(curSongPos).setIsPlaying(false);

        curSongPos++;
        while (!songsList.get(curSongPos).isDownloaded() && curSongPos < songsList.size()) {
            curSongPos++;
        }

        if (curSongPos < songsList.size()) {
            try {
                mp.reset();
                mp.setDataSource(songsList.get(curSongPos).getPath());
                mp.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        songsList.get(curSongPos).setIsPlaying(true);
        listener.onMusicPlayerStateChanged();
        mp.start();
        prepareAndStartForeground();
    }

    private void prepareAndStartForeground() {
        String songName = songsList.get(curSongPos).getSongName();

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.small_icon)
                .setTicker(songName)
                .setOngoing(true)
                .setContentTitle("Now Playing")
                .setContentText(songName);
        Notification notification = builder.build();

        startForeground(NOTIFICATION_ID, notification);
    }

    public void setSongsList(ArrayList<SongMetaData> songs) {
        songsList = songs;
    }

    public ArrayList<SongMetaData> getSongsList() {
        return songsList;
    }

    public void playSong(int pos) {
        if (pos != curSongPos){
            try {
                if (curSongPos != -1) songsList.get(curSongPos).setIsPlaying(false);
                curSongPos = pos;
                mediaPlayer.reset();
                mediaPlayer.setDataSource(songsList.get(pos).getPath());
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            songsList.get(curSongPos).setIsPlaying(true);
            mediaPlayer.start();
        }
    }

    public void pauseSong(int pos) {
        songsList.get(curSongPos).setIsPlaying(false);
        mediaPlayer.pause();
    }

    public void registerListener(MusicPlayerServiceListener musicPlayerServiceListener) {
        listener = musicPlayerServiceListener;
    }

    public class MusicBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }

}
