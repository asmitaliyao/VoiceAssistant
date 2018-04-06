package com.example.liyao.handlerdemo.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.liyao.handlerdemo.bean.Music;

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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            music = (Music) intent.getSerializableExtra("music");
        }
        if (music != null ) {
            try {
                mediaPlayer.setDataSource(music.getPath());
                mediaPlayer.prepare();
            } catch (IOException e) {
                Log.e(TAG, "MusicService: " + e.toString(), e);
                e.printStackTrace();
            }
        }

        return super.onStartCommand(intent, flags, startId);
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
            try {
                mediaPlayer.prepare();
                mediaPlayer.seekTo(0);
            } catch (IOException e) {
                Log.e(TAG, "stop: " + e.toString(), e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mediaPlayer.stop();
        return super.onUnbind(intent);
    }
}
