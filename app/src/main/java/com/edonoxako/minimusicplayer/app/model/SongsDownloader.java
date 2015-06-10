package com.edonoxako.minimusicplayer.app.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.parse.*;

import java.io.*;
import java.util.ArrayList;

public class SongsDownloader {

    private SongsDownloaderListener listener;
    private Context context;
    private ArrayList<SongMetaData> songList;

    public static final String SONG_LIST_COLUMN = "list";
    public static final String SONG_LIST_CLASS = "SongList";
    public static final String SONG_LIST_ID = "HVzNKc59cy";
    public static final String SONG_LIST_FILENAME = "song_list.txt";

    public static final String SONG_CLASS = "Song";
    public static final String SONG_COLUMN = "songName";

    private boolean isOnline = true;

    public SongsDownloader(SongsDownloaderListener lster, Context ctx) {
        listener = lster;
        context = ctx;
        songList = new ArrayList<SongMetaData>();
    }

    public void loadSongs() {
        if (context.getFileStreamPath(SONG_LIST_FILENAME).exists()) {
            getSongData(SONG_LIST_FILENAME);
            syncSongsList();
            listener.songsListDownloaded(songList);
        } else {
            if (isInternetAvailable()) downloadSongsList();
            else listener.internetUnavailable();
        }
    }

    public void downloadSongsList() {
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(SONG_LIST_CLASS);
        query.getInBackground(SONG_LIST_ID, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                if (e == null) {
                    ParseFile parseFile = (ParseFile) parseObject.get(SONG_LIST_COLUMN);
                    parseFile.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {

                            if (e == null) {
                                saveData(SONG_LIST_FILENAME, bytes);
                                getSongData(SONG_LIST_FILENAME);
                                listener.songsListDownloaded(songList);
                                syncSongsList();
                            } else {
                                //TODO: ��������� ������
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    //TODO: ��������� ������
                    e.printStackTrace();
                }

            }
        });
    }

    private void syncSongsList() {
        for (int i = 0; i < songList.size(); i++) {
            SongMetaData song = songList.get(i);
            String songName = song.getSongName();
            File songFile = context.getFileStreamPath(songName);
            if (songFile.exists()) {
                song.setIsDownloaded(true);
                song.setPath(songFile.getAbsolutePath());
            } else {
                downloadSong(i, song);
            }
        }
    }

    private void downloadSong(final int index, SongMetaData song) {
        if (!isInternetAvailable()){
            if (isOnline){
                listener.internetUnavailable();
                isOnline = false;
            }
            return;
        } else if (!isOnline){
            isOnline = true;
        }

        String songId = song.getPath().split("/")[0];
        final String songName = song.getSongName();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(SONG_CLASS);
        query.getInBackground(songId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {

                if (e == null) {
                    ParseFile file = (ParseFile) parseObject.get(SONG_COLUMN);
                    Log.d("downloader", "Downloading...");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {

                            if (e == null) {
                                Log.d("downloader", "Downloaded! Saving...");
                                saveData(songName, bytes);
                                Log.d("downloader", "Saved! Updating...");
                                updateSongsData(index, songName);
                                Log.d("downloader", "Updated!");
                            } else {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    e.printStackTrace();
                }

            }
        });
    }

    private void saveData(String fileName, byte[] bytes) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
            bos.write(bytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSongData(String fileName) {
        try {
            File songList = context.getFileStreamPath(fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(songList));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                createSongMeta(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createSongMeta(String info) {
        String[] elements = info.split("/");
        SongMetaData song = new SongMetaData();
        song.setPath(info);
        song.setSongName(elements[1]);
        song.setIsDownloaded(false);
        song.setIsPlaying(false);
        songList.add(song);
    }

    private void updateSongsData(int index, String fileName) {
        String absPath = context.getFileStreamPath(fileName).getAbsolutePath();
        SongMetaData song = songList.get(index);
        song.setPath(absPath);
        song.setIsDownloaded(true);
        listener.songDownloaded();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
