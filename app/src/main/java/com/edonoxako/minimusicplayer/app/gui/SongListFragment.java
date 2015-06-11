package com.edonoxako.minimusicplayer.app.gui;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import com.edonoxako.minimusicplayer.app.PlayerController;
import com.edonoxako.minimusicplayer.app.R;
import com.edonoxako.minimusicplayer.app.model.MusicPlayerModel;
import com.edonoxako.minimusicplayer.app.model.MusicPlayerModelListener;
import com.edonoxako.minimusicplayer.app.model.SongMetaData;

import java.util.ArrayList;

public class SongListFragment extends ListFragment implements MusicPlayerModelListener {

    ArrayList<SongMetaData> data;
    private MusicPlayerModel model;
    private SongsAdapter adapter;
    private PlayerController controller;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controller = (PlayerController) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        controller.refresh();
        return true;
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
        Toast.makeText(getActivity(), getResources().getString(R.string.downloading), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void songDownloaded() {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void songListDeleting() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            setListAdapter(null);
            adapter = null;
        }
        Toast.makeText(getActivity(), getResources().getText(R.string.refreshing), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String errorMessage) {
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
    }
}
