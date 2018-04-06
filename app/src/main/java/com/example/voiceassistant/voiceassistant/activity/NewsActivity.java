package com.example.voiceassistant.voiceassistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.voiceassistant.voiceassistant.R;
import com.example.voiceassistant.voiceassistant.bean.Data;
import com.example.voiceassistant.voiceassistant.utils.JsonParser;

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
    private List<Data> newList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Intent intent = getIntent();
        if (intent != null) {
            mNewsString = intent.getStringExtra("news");
        }

        makeUrl(mNewsString);
        requestNewsJsonData(makeUrl(mNewsString));
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == MESSAGE_CODE_REQUEST_NEWS_DATA){
                    newList = (List<Data>) msg.obj;

                    showText();
                    playVoice();

                }
            }
        };

    }

    private void showText() {

    }

    private void playVoice() {

    }

    private URL makeUrl(String mNewsString) {
        String urlStr;
        switch (mNewsString) {
            case "社会":
                urlStr = "shehui";
                break;
            case "国内":
                urlStr = "guonei";
                break;
            case "国际":
                urlStr = "guoji";
                break;
            case "娱乐":
                urlStr = "yule";
                break;
            case "体育":
                urlStr = "tiyu";
                break;
            case "军事":
                urlStr = "junshi";
                break;
            case "科技":
                urlStr = "keji";
                break;
            case "财经":
                urlStr = "caijing";
                break;
            case "时尚":
                urlStr = "shishang";
                break;
            default:
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
}
