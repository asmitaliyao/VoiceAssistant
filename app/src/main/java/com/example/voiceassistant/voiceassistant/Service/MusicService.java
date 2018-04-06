package com.example.voiceassistant.voiceassistant.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.example.voiceassistant.voiceassistant.bean.Music;

import java.io.IOException;


public class MusicService extends Service {
    private static final String TAG = "MusicService";

    public static MediaPlayer mediaPlayer = new MediaPlayer();
    private Music music;

    public final IBinder binder = new MyBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class MyBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    public void play() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
//            try {
//                mediaPlayer.prepare();
//                mediaPlayer.seekTo(0);
//            } catch (IOException e) {
//                Log.e(TAG, "stop: " + e.toString(), e);
//                e.printStackTrace();
//            }
        }
    }

    public void setMusic(Music music){
        stop();
        if (music != null ) {
            try {
                Log.d(TAG, "setMusic: path = " + music.getPath());
                mediaPlayer.setDataSource(music.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                Log.e(TAG, "MusicService: " + e.toString(), e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mediaPlayer.stop();
        super.onDestroy();
    }
}
