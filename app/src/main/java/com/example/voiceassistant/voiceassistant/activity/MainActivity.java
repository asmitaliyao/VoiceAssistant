package com.example.voiceassistant.voiceassistant.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voiceassistant.voiceassistant.R;
import com.example.voiceassistant.voiceassistant.bean.Data;
import com.example.voiceassistant.voiceassistant.utils.JsonParser;
import com.example.voiceassistant.voiceassistant.utils.RequestPermission;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private RecognizerDialog iatDialog;
    private TextView etText;
    private RequestPermission requestPermission;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Toast.makeText(MainActivity.this, "初始化失败，错误码：" + code, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPermission();
        init();
    }

    private void initPermission() {
        requestPermission = RequestPermission.getInstance(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO});
        if (!requestPermission.isAllGranted())
            requestPermission.requestPermissions();
    }

    private void init() {
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5a9cb8a4");
        etText = findViewById(R.id.tv_help);
        iatDialog = new RecognizerDialog(this, mInitListener);
        iatDialog.setListener(new MyRecognizerDialogListener());
        iatDialog.setParameter(SpeechConstant.ASR_PTT, "0");
        findViewById(R.id.btn_mic).setOnClickListener(new MyOnClickListener());
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            if (isLast) {
                return;
            }
            Log.d(TAG, "onResult: " + isLast);

            String text = JsonParser.parseIatResult(recognizerResult.getResultString());

            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(recognizerResult.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mIatResults.put(sn, text);
            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }
            String result = resultBuffer.toString();
            //解析result
            if (!TextUtils.isEmpty(result)) {
                resolveResult(result);
            }
            etText.setText(result);
        }

        @Override
        public void onError(SpeechError speechError) {

        }
    }

    /**
     * 解析result字符串：音乐/新闻
     *
     * @param result
     */
    private void resolveResult(String result) {
        if (result.length() < 2) {
            return;
        }

        String typeStr = result.substring(0, 2);
        if ("新闻".equals(typeStr)) {
            //18/4/6 进入新闻界面
            //解析音乐名称
            String newsStr = resolveNews(result);
            Log.d(TAG, "resolveNews: news = " + newsStr);
            if (TextUtils.isEmpty(newsStr)) {
                etText.setText("请您说出新闻版块名称\neg：新闻头条");
            } else {
                Intent intent = new Intent(this, NewsActivity.class);
                intent.putExtra("news", newsStr);
                startActivity(intent);
            }

        } else if ("音乐".equals(typeStr)) {
            //18/4/6 进入音乐界面
            //解析音乐名称
            String musicStr = resolveMusic(result);
            Log.d(TAG, "resolveResult: music = " + musicStr);
            if (TextUtils.isEmpty(musicStr)) {
                etText.setText("请您说出音乐名称\neg：音乐囚鸟");
            } else {
                Intent intent = new Intent(this, MusicPlayerActivity.class);
                intent.putExtra("music", musicStr);
                startActivity(intent);
            }
        }
    }

    /**
     * 解析音乐名称
     *
     * @param result
     */
    private String resolveMusic(String result) {
        if (result.length() <= 2) {
            return "";
        } else {
            return result.substring(2, result.length());
        }
    }
    /**
     * 解析新闻关键字
     *
     * @param result
     */
    private String resolveNews(String result) {
        if (result.length() <= 2) {
            return "";
        } else {
            return result.substring(2, result.length());
        }
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_mic:
                    mIatResults.clear();
                    //开始听写，需将sdk中的assets文件下的文件夹拷入项目的assets文件夹下（没有的话自己新建）
                    iatDialog.show();
                    break;
            }
        }
    }

}
