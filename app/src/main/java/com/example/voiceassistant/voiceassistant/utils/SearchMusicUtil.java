package com.example.voiceassistant.voiceassistant.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.example.voiceassistant.voiceassistant.bean.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索本地音乐文件，返回音乐列表
 */
public class SearchMusicUtil {
    public static List<Music> getMusicData(Context context) {
        List<Music> musicList = new ArrayList<Music>();
        //媒体库查询语句
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                null,
                null,
                MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Music music = new Music();
                music.setMusic(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)));
                music.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
                music.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
                music.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
                music.setSize(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));

                if (music.getSize() > 1024 * 5) {
                    if (music.getMusic().contains(" - ")) {
                        String[] strArray = music.getMusic().split(" - ");
                        music.setMusic(strArray[1]);
                        music.setSinger(strArray[0]);
                    }
                    musicList.add(music);
                }

            }
        }
        cursor.close();
        return musicList;
    }

    public static String formatTime(int time) {
        if (time / 1000 % 60 < 10) {
            return time / 1000 / 60 + ":0" + time / 1000 % 60;
        } else {
            return time / 1000 / 60 + ":" +time / 1000 % 60;
        }
    }
}
