package com.edonoxako.minimusicplayer.app.gui;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import com.edonoxako.minimusicplayer.app.PlayerController;
import com.edonoxako.minimusicplayer.app.model.MusicPlayerModel;
import com.edonoxako.minimusicplayer.app.model.MusicPlayerModelListener;
import com.edonoxako.minimusicplayer.app.model.SongMetaData;

import java.util.ArrayList;

public class SongListFragment extends ListFragment implements MusicPlayerModelListener {

    SongMetaData song;
    ArrayList<SongMetaData> data;
    private MusicPlayerModel model;
    private SongsAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void setPlaying() {
        boolean pl = song.isPlaying();
        song.setIsPlaying(!pl);
    }

    public void setModel(MusicPlayerModel m) {
        model = m;
    }

    @Override
    public void songPrepared() {
        if (model != null) {
            data = model.getSongsList();
            adapter = new SongsAdapter(getActivity(), (PlayerController) getActivity(), data);
            setListAdapter(adapter);
        }
    }

    @Override
    public void songDownloaded() {
        Log.d("frag", "Song Downloaded");
        adapter.notifyDataSetChanged();
    }
}
