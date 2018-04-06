package com.example.voiceassistant.voiceassistant;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.voiceassistant.voiceassistant.adapter.MusicAdapter;
import com.example.voiceassistant.voiceassistant.bean.Music;
import com.example.voiceassistant.voiceassistant.utils.SearchMusicUtil;

import java.util.ArrayList;
import java.util.List;


public class MusicPlayerActivity extends Activity {
    private static final int MSG_NO_MUSIC = 0;
    private static final int MSG_ONE_MUSIC = 1;
    private static final int MSG_MULTIPLE_MUSIC = 2;

    private Music music;
    private List<Music> mMusicList = new ArrayList<>();
    private String mMusicName;
    private MusicAdapter mAdapter;
    private Intent intent;

    private LinearLayout mLlSearchMusic;
    private LinearLayout mLlMusicController;

    private TextView mTvMusicName;
    private TextView mTvSearching;
    private ListView mLvMusics;
    private TextView currentMusic;
    private TextView totalDuration;
    private TextView musicSinger;
    private TextView musicPlay;
    private TextView musicStop;
    private TextView musicPause;
    private TextView currentDuration;
    private SeekBar seekBar;
    private MusicService musicService;

    /**
     * 用于判断如果有多首歌曲涵盖该名称，
     * 如果超过两秒没有选择歌曲，
     * 则自动隐藏搜索信息，
     * 如果选择，则立即隐藏搜索信息
     */
    private boolean isSelected = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MyBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_NO_MUSIC:
                    mTvSearching.setText("没有找到该音乐");
                    break;
                case MSG_ONE_MUSIC:
                    mLlSearchMusic.setVisibility(View.GONE);
                    mLlMusicController.setVisibility(View.VISIBLE);
                    initControllerInfo();
                    //  18/4/6 直接播放该音乐
                    if (musicService != null){
                        musicService.setMusic(music);
                    }
                    break;
                case MSG_MULTIPLE_MUSIC:
                    mTvSearching.setVisibility(View.GONE);
                    initControllerInfo();
                    //  18/4/6 展示所有的音乐，并播放第一首，2s后列表消失
                    mAdapter.notifyDataSetChanged();

                    if (musicService != null){
                        musicService.setMusic(music);
                    }
                    dismissSearchInfo();
                    break;
                default:
                    break;
            }
        }
    };

    private void dismissSearchInfo() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!isSelected){
            mLlSearchMusic.setVisibility(View.GONE);
            mLlMusicController.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Intent intent = getIntent();
        if (intent != null) {
            mMusicName = intent.getStringExtra("music");

        }

        bindServiceConnection();

        initView();


        startSearchMusic(mMusicName);
    }

    /**
     * 开始搜索音乐
     *
     * @param musicName
     */
    private void startSearchMusic(final String musicName) {
        new Thread() {
            @Override
            public void run() {
                List<Music> musicList = SearchMusicUtil.getMusicData(MusicPlayerActivity.this);

                //  18/4/6 判断手机中是否只有一首歌曲的名称如此
                realSearchMusic(musicList, musicName);

            }
        }.start();
    }

    /**
     * 从列表中查找该音乐
     * @param musicList
     * @param musicName
     */
    private void realSearchMusic(List<Music> musicList, String musicName) {
        for (Music music: musicList){
            if (music.getMusic().contains(musicName)){
                mMusicList.add(music);
            }
        }

        if (mMusicList == null || mMusicList.size() == 0) {
            mHandler.sendEmptyMessage(MSG_NO_MUSIC);
        } else if (mMusicList.size() == 1){
            music = mMusicList.get(0);
            mHandler.sendEmptyMessage(MSG_ONE_MUSIC);
        } else {
            music = mMusicList.get(0);
            mHandler.sendEmptyMessage(MSG_MULTIPLE_MUSIC);
        }
    }

    private void bindServiceConnection() {
        intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        mLlMusicController = findViewById(R.id.ll_play_controller);
        mLlSearchMusic = findViewById(R.id.ll_search_music);

        mTvMusicName = findViewById(R.id.tv_music_name);
        mTvMusicName.setText(mMusicName);
        mTvSearching = findViewById(R.id.tv_searching);
        mLvMusics = findViewById(R.id.lv_musics);
        //  18/4/6 初始化listview
        mAdapter = new MusicAdapter(this, mMusicList);
        mLvMusics.setAdapter(mAdapter);


        currentMusic = findViewById(R.id.player_current_music_textview);
        musicSinger = findViewById(R.id.player_music_singer_textview);
        currentDuration = findViewById(R.id.music_duration_current);
        totalDuration = findViewById(R.id.music_duration_total);
        seekBar = findViewById(R.id.music_seekbar);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        musicPlay = findViewById(R.id.music_player_play);
        musicPlay.setOnClickListener(myOnClickListener);
        musicStop = findViewById(R.id.music_player_stop);
        musicStop.setOnClickListener(myOnClickListener);
        musicPause = findViewById(R.id.music_player_pause);
        musicPause.setOnClickListener(myOnClickListener);

        mLvMusics.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //  18/4/6 停止上一首歌曲，播放该歌曲
                isSelected = true;
                music = mMusicList.get(i);
                if (musicService != null){
                    musicService.setMusic(music);
                }
            }
        });
    }

    private void initControllerInfo(){
        currentMusic.setText(music.getMusic());
        musicSinger.setText(music.getSinger());
        totalDuration.setText(SearchMusicUtil.formatTime(music.getDuration()));
    }

    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.music_player_play:
                    musicService.play();
                    musicPlay.setTextColor(Color.parseColor("#222222"));
                    musicPause.setTextColor(Color.parseColor("#999999"));
                    musicStop.setTextColor(Color.parseColor("#999999"));
                    break;
                case R.id.music_player_stop:
                    musicService.stop();
                    musicPlay.setTextColor(Color.parseColor("#999999"));
                    musicPause.setTextColor(Color.parseColor("#999999"));
                    musicStop.setTextColor(Color.parseColor("#222222"));
                    break;
                case R.id.music_player_pause:
                    musicService.pause();
                    musicPlay.setTextColor(Color.parseColor("#999999"));
                    musicPause.setTextColor(Color.parseColor("#222222"));
                    musicStop.setTextColor(Color.parseColor("#999999"));
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
//        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
        super.onDestroy();
    }
}
