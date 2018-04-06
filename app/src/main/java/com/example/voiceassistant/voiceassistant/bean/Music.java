package com.example.voiceassistant.voiceassistant.bean;

import java.io.Serializable;

public class Music implements Serializable {
    private String singer;  //歌手
    private String music;   //音乐名称
    private String path;    //文件路径
    private int duration;   //音乐长度
    private long size;      //音乐大小

    public Music() {
    }

    public Music(String singer, String music, String path, int duration, long size) {

        this.singer = singer;
        this.music = music;
        this.path = path;
        this.duration = duration;
        this.size = size;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
