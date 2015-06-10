package com.edonoxako.minimusicplayer.app.model;

import android.content.Context;
import android.util.Log;
import com.parse.*;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by Dasha on 10.06.2015.
 */
public class SongsDownloader {

    private SongsDownloaderListener listener;
    private Context context;
    private ArrayList<SongMetaData> songList;

    public static final String SONG_LIST_COLUMN = "list";
    public static final String SONG_LIST_CLASS = "SongList";
    public static final String SONG_LIST_ID = "HVzNKc59cy";
    public static final String SONG_LIST_FILENAME = "song_list.txt";

    public SongsDownloader(SongsDownloaderListener lster, Context ctx) {
        listener = lster;
        context = ctx;
        songList = new ArrayList<SongMetaData>();
    }

    public void loadSongs() {
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
                            } else {
                                //TODO: Обработка ошибки
                            }

                        }
                    });
                } else {
                    //TODO: Обработка ошибки
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

}
