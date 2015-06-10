package com.edonoxako.minimusicplayer.app.gui;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.edonoxako.minimusicplayer.app.PlayerController;
import com.edonoxako.minimusicplayer.app.model.MusicPlayerModel;
import com.edonoxako.minimusicplayer.app.model.MusicPlayerModelListener;
import com.edonoxako.minimusicplayer.app.model.SongMetaData;

import java.util.ArrayList;

public class SongListFragment extends ListFragment implements MusicPlayerModelListener {

    ArrayList<SongMetaData> data;
    private MusicPlayerModel model;
    private SongsAdapter adapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
