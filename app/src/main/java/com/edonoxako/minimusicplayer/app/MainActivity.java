package com.edonoxako.minimusicplayer.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.edonoxako.minimusicplayer.app.gui.SongListFragment;
import com.edonoxako.minimusicplayer.app.model.MusicPlayerModel;
import com.edonoxako.minimusicplayer.app.model.SongsDownloader;
import com.parse.ParseObject;

public class MainActivity extends FragmentActivity implements PlayerController {

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
}
