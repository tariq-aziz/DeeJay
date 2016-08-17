package com.aziz.tariq.deejay;

import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.FileDescriptor;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by tariqaziz on 2016-07-06.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    //media player
    private MediaPlayer player;
    //song list
    private List<FirebaseTrack> firebaseTracks;
    private List<FileDescriptor> audioFilesList;
    //current position
    private int songPosn;

    private final IBinder musicBind = new MusicBinder();


    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        //create player
        player = new MediaPlayer();

        initMusicPlayer();

        audioFilesList = new ArrayList<FileDescriptor>();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        songPosn++;


        if (songPosn >= audioFilesList.size()) {
            songPosn = 0;
        }

        //DetailedStashionActivity.setLayout(songPosn);
        playSong();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);

    }


    public void addToList(FileDescriptor audioFile) {
        audioFilesList.add(audioFile);
        if (audioFilesList.size() == 1) {
            //DetailedStashionActivity.setLayout(songPosn);
            playSong();
        }
    }

    public void setList(List<FirebaseTrack> firebaseTracks) {
        this.firebaseTracks = firebaseTracks;
    }


    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    public void setSong(int songIndex) {

        for (int i = 0; i < audioFilesList.size(); i++) {
            Log.v("AUDIOFILE", String.valueOf(i));
        }
        songPosn = songIndex;

        if (songPosn >= audioFilesList.size()) {
            songPosn = 0;
        }
    }


    public void playSong() {
        player.reset();

        FileDescriptor audioFile = audioFilesList.get(songPosn);
        DetailedStashionActivity.setLayout(songPosn);

        /*
        //get song
        FirebaseTrack firebaseTrack = firebaseTracks.get(songPosn);
        //get id
        long currSong = firebaseTrack.serviceTrackId;
        //set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        */
        try {
            //player.setDataSource(getApplicationContext(), trackUri);
            player.setDataSource(audioFile);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        Log.v("SONG_POS", String.valueOf(songPosn));

        player.prepareAsync();


    }

    public void setSong(FileDescriptor songIndex) {

    }
}
