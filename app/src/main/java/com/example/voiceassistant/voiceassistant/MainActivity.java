package com.example.voiceassistant.voiceassistant;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voiceassistant.voiceassistant.bean.DictationResult;
import com.example.voiceassistant.voiceassistant.utils.JsonParser;
import com.example.voiceassistant.voiceassistant.utils.RequestPermission;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
        findViewById(R.id.btn_mic).setOnClickListener(new MyOnClickListener());
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener {

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
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
            etText.setText(resultBuffer.toString());
        }

        @Override
        public void onError(SpeechError speechError) {

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

//                    try {
//                        URL url = new URL("http://v.juhe.cn/toutiao/index?type=top&key=26015a44325349d4daeff7b41429ce81");
//                        requestNewsJsonData(url);
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    }
                    break;
            }
        }
    }

    private String requestNewsJsonData(final URL url) {
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
                        Log.d(TAG, "run: response.body()=" + response.body().string());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return null;
    }

}
