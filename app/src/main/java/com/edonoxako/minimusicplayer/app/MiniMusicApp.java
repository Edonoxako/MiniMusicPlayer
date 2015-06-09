package com.edonoxako.minimusicplayer.app;

import android.app.Application;
import com.parse.Parse;

/**
 * Created by Dasha on 09.06.2015.
 */
public class MiniMusicApp extends Application{

    public static final String APPLICATION_ID = "eqOw1vg4CR249u7sw5fLXQctiqrxWTuEggiPKJ9k";
    public static final String CLIENT_ID = "0M7SSNdeIVJiySRyhrViw6kx4pehwszadFsLNzNs";

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.enableLocalDatastore(this);
        Parse.initialize(this, APPLICATION_ID, CLIENT_ID);
    }
}
