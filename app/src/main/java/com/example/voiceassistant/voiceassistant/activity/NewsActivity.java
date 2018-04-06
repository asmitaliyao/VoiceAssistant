package com.example.voiceassistant.voiceassistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voiceassistant.voiceassistant.R;
import com.example.voiceassistant.voiceassistant.adapter.NewsAdapter;
import com.example.voiceassistant.voiceassistant.bean.Data;
import com.example.voiceassistant.voiceassistant.utils.JsonParser;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.sunflower.FlowerCollector;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsActivity extends Activity {

    private static final String TAG = "NewsActivity";
    public static final String NEWS_APPKEY = "26015a44325349d4daeff7b41429ce81";
    public static final int MESSAGE_CODE_REQUEST_NEWS_DATA = 1000001;
    private String mNewsString;
    private Handler handler = null;
    private List<Data> newsList;
    private ImageView newsTypeImageView;
    private TextView newsTypeTextView;
    private ListView newsListView;

    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 默认发音人
    private String voicer = "xiaoyan";
    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;

    private Toast mToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Intent intent = getIntent();
        if (intent != null) {
            mNewsString = intent.getStringExtra("news");
        }

        initView();

        makeUrl(mNewsString);
        requestNewsJsonData(makeUrl(mNewsString));
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_CODE_REQUEST_NEWS_DATA){
                    newsList = (List<Data>) msg.obj;

                    showText();
                    playVoice();

                }
            }
        };

    }

    private void initView() {
        newsTypeImageView = findViewById(R.id.news_type_iv);
        newsTypeTextView = findViewById(R.id.news_type_tv);
        newsListView = findViewById(R.id.news_lv);
        mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);

        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(NewsActivity.this, mTtsInitListener);
    }

    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败,错误码："+code);
            } else {
                // 初始化成功，之后可以调用startSpeaking方法
                // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                // 正确的做法是将onCreate中的startSpeaking调用移至这里
            }
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    private void showText() {
        NewsAdapter newsAdapter = new NewsAdapter(this,newsList);
        newsListView.setAdapter(newsAdapter);
    }

    private void playVoice() {
        if( null == mTts ){
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip( "创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化" );
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 移动数据分析，收集开始合成事件
                FlowerCollector.onEvent(NewsActivity.this, "tts_play");

                // 设置参数
                setParam();
                String text = "";
                for (int i =0;i< newsList.size(); i++){
                    text = text + "第"+ (i + 1) + "条新闻：" + newsList.get(i).getTitle() + "。\n";
                }
                int code = mTts.startSpeaking(text, mTtsListener);

                if (code != ErrorCode.SUCCESS) {
                    showTip("语音合成失败,错误码: " + code);
                }
            }
        }).start();
    }

    private URL makeUrl(String mNewsString) {
        String urlStr;
        switch (mNewsString) {
            case "社会":
                newsTypeImageView.setImageResource(R.drawable.news_shehui);
                newsTypeTextView.setText("社会");
                urlStr = "shehui";
                break;
            case "国内":
                newsTypeImageView.setImageResource(R.drawable.news_guonei);
                newsTypeTextView.setText("国内");
                urlStr = "guonei";
                break;
            case "国际":
                newsTypeImageView.setImageResource(R.drawable.news_guoji);
                newsTypeTextView.setText("国际");
                urlStr = "guoji";
                break;
            case "娱乐":
                newsTypeImageView.setImageResource(R.drawable.news_yule);
                newsTypeTextView.setText("娱乐");
                urlStr = "yule";
                break;
            case "体育":
                newsTypeImageView.setImageResource(R.drawable.news_tiyu);
                newsTypeTextView.setText("体育");
                urlStr = "news_tiyu";
                break;
            case "军事":
                newsTypeImageView.setImageResource(R.drawable.news_junshi);
                newsTypeTextView.setText("军事");
                urlStr = "news_junshi";
                break;
            case "科技":
                newsTypeImageView.setImageResource(R.drawable.news_keji);
                newsTypeTextView.setText("科技");
                urlStr = "keji";
                break;
            case "财经":
                newsTypeImageView.setImageResource(R.drawable.news_caijing);
                newsTypeTextView.setText("财经");
                urlStr = "caijing";
                break;
            case "时尚":
                newsTypeImageView.setImageResource(R.drawable.new_shishang);
                newsTypeTextView.setText("时尚");
                urlStr = "shishang";
                break;
            default:
                newsTypeImageView.setImageResource(R.drawable.news_toutiao);
                newsTypeTextView.setText("头条");
                urlStr = "top";
        }
        urlStr = "http://v.juhe.cn/toutiao/index?type=" + urlStr + "&key=" + NEWS_APPKEY;
        try {
            URL url = new URL(urlStr);
            return url;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void requestNewsJsonData(final URL url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.d(TAG, "run: response.code()=" + response.code());
                        Log.d(TAG, "run: response.message()=" + response.message());
                        List<Data> dataList;
                        dataList = JsonParser.parseNewsRequestResult(response.body().string());

                        Message message = Message.obtain();
                        message.what = 1000001;
                        message.obj = dataList;
                        handler.sendMessage(message);

                        Iterator it = dataList.iterator();
                        while (it.hasNext()) {
                            Data data = (Data) it.next();
                            Log.d(TAG, "run: data title " + data.getTitle());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
            showTip("暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            showTip("继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
            showTip(String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
            showTip(String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                showTip("播放完成");
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };
    /**
     * 参数设置
     * @return
     */
    private void setParam(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            // 设置在线合成发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
            //设置合成语速
            mTts.setParameter(SpeechConstant.SPEED, "50");
            //设置合成音调
            mTts.setParameter(SpeechConstant.PITCH,  "50");
            //设置合成音量
            mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");
    }

    @Override
    protected void onDestroy() {
        mTts.stopSpeaking();
        super.onDestroy();
    }
}
