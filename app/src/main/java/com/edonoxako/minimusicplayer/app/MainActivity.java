package com.edonoxako.minimusicplayer.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.edonoxako.minimusicplayer.app.gui.SongListFragment;
import com.edonoxako.minimusicplayer.app.model.MusicPlayerModel;

public class MainActivity extends AppCompatActivity implements PlayerController {

    MusicPlayerModel mModel;
    SongListFragment mSongListFrag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSongListFrag = (SongListFragment) getFragmentManager().findFragmentById(R.id.music_content);
        mModel = new MusicPlayerModel(mSongListFrag);
        mSongListFrag.setModel(mModel);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mModel.initAppModel(this);
    }

    @Override
    protected void onDestroy() {
        mModel.destroyAppModel();
        super.onDestroy();
    }

    public void play(int pos) {
        mModel.playSong(pos);
        Log.d("controller", "Playing");
    }

    public void pause(int pos) {
        mModel.pauseSong(pos);
        Log.d("controller", "pausing");
    }

    @Override
    public void refresh() {
        mModel.refreshSongsList();
    }
}
