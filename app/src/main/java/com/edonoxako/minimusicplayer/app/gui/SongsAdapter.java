package com.edonoxako.minimusicplayer.app.gui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import com.edonoxako.minimusicplayer.app.PlayerController;
import com.edonoxako.minimusicplayer.app.R;
import com.edonoxako.minimusicplayer.app.model.SongMetaData;

import java.util.ArrayList;

public class SongsAdapter extends BaseAdapter {

    private ArrayList<SongMetaData> songList;
    private LayoutInflater listItemInflater;
    private PlayerController controller;
    private Context context;
    private View curPlayingView;

    public SongsAdapter(Context ctx, PlayerController ctrl,ArrayList<SongMetaData> songs) {
        context = ctx;
        songList = songs;
        controller = ctrl;
        listItemInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return songList.size();
    }

    @Override
    public Object getItem(int position) {
        return songList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = listItemInflater.inflate(R.layout.song_list_item, parent, false);
        }

        ImageButton control = (ImageButton) view.findViewById(R.id.songControl);
        TextView songInfoText = (TextView) view.findViewById(R.id.songName);

        SongMetaData song = (SongMetaData) getItem(position);
        if (song.isDownloaded()) {
            control.setVisibility(View.VISIBLE);
            control.setOnClickListener(controlButtonListener);
            control.setTag(position);

            if (song.isPlaying()) {
                control.setBackgroundResource(R.mipmap.pause_icon);
                curPlayingView = control;
            }
            else control.setBackgroundResource(R.mipmap.play_icon);

            songInfoText.setText(song.getSongName());
        } else {
            control.setVisibility(View.GONE);
            songInfoText.setText(song.getPath());
        }

        return view;
    }

    View.OnClickListener controlButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (Integer) v.getTag();
            SongMetaData song = (SongMetaData) getItem(pos);
            if (song.isPlaying()) {
                controller.pause(pos);
                v.setBackgroundResource(R.mipmap.play_icon);
            } else {
                if (curPlayingView != null) curPlayingView.setBackgroundResource(R.mipmap.play_icon);
                curPlayingView = v;
                controller.play(pos);
                v.setBackgroundResource(R.mipmap.pause_icon);
            }
        }
    };
}
