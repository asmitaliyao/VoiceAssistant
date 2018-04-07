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

    private static MediaPlayer mediaPlayer;

    public final IBinder binder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
    }

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

    public void playOrPause() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        } else {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (Exception e) {
                Log.e(TAG, "stop: " + e.toString());
            }
        }
    }

    public boolean isPlay(){
        if (mediaPlayer != null){
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    public int getCurrentPosition(){
        if (mediaPlayer != null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void setSeek(int progress){
        if (mediaPlayer != null){
            mediaPlayer.seekTo(progress);
        }
    }

    public void setMusic(Music music){
        Log.d(TAG, "setMusic: music == null ? " + (music == null));
        if (music != null ) {
            try {
                Log.d(TAG, "setMusic: path = " + music.getPath());
                mediaPlayer.setDataSource(music.getPath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                Log.e(TAG, "MusicService: " + e.toString());
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
}
