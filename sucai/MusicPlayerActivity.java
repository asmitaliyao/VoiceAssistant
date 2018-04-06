package com.example.liyao.handlerdemo.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.liyao.handlerdemo.R;
import com.example.liyao.handlerdemo.bean.Music;
import com.example.liyao.handlerdemo.service.MusicService;
import com.example.liyao.handlerdemo.utils.SearchMusicUtil;

public class MusicPlayerActivity extends Activity {

    private Music music;
    private TextView currentMusic;
    private TextView totalDuration;
    private TextView musicSinger;
    private TextView musicPlay;
    private TextView musicStop;
    private TextView musicPause;
    private TextView currentDuration;
    private SeekBar seekBar;
    private MusicService musicService;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((MusicService.MyBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        Intent intent = getIntent();
        if (intent != null ){
           Bundle bundle = intent.getBundleExtra("bundle");
           if (bundle != null) {
               music = (Music) bundle.getSerializable("music");
           }
        }

        bindServiceConnection();

        initView();
    }

    private void bindServiceConnection() {
        Intent intent = new Intent(this, MusicService.class);
        intent.putExtra("music",music);
        startService(intent);
        bindService(intent, serviceConnection, this.BIND_AUTO_CREATE);
    }

    private void initView() {
        currentMusic = findViewById(R.id.player_current_music_textview);
        currentMusic.setText(music.getMusic());
        musicSinger = findViewById(R.id.player_music_singer_textview);
        musicSinger.setText(music.getSinger());
        currentDuration = findViewById(R.id.music_duration_current);

        totalDuration = findViewById(R.id.music_duration_total);
        totalDuration.setText(SearchMusicUtil.formatTime(music.getDuration()));

        seekBar = findViewById(R.id.music_seekbar);

        MyOnClickListener myOnClickListener = new MyOnClickListener();
        musicPlay = findViewById(R.id.music_player_play);
        musicPlay.setOnClickListener(myOnClickListener);
        musicStop = findViewById(R.id.music_player_stop);
        musicStop.setOnClickListener(myOnClickListener);
        musicPause = findViewById(R.id.music_player_pause);
        musicPause.setOnClickListener(myOnClickListener);

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
        Intent intent = new Intent(this, MusicService.class);
        stopService(intent);
        super.onDestroy();
    }
}
