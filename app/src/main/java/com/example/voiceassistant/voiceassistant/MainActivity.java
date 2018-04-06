package com.example.voiceassistant.voiceassistant;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.voiceassistant.voiceassistant.bean.DictationResult;
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

import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";

    private RecognizerDialog iatDialog;
    private TextView etText;

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

        init();
    }

    private void init() {
        SpeechUtility. createUtility( this, SpeechConstant. APPID + "=5a9cb8a4" );
        etText=findViewById(R.id.tv_help);
        iatDialog= new RecognizerDialog(this,mInitListener);
        iatDialog.setListener(new MyRecognizerDialogListener());
        findViewById(R.id.btn_mic).setOnClickListener(new MyOnClickListener());
    }

    class MyRecognizerDialogListener implements RecognizerDialogListener{

        String resultJson = "[";

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            if (!isLast) {
                resultJson += recognizerResult.getResultString() + ",";
            } else {
                resultJson += recognizerResult.getResultString() + "]";
            }

            if (isLast) {
                //解析语音识别后返回的json格式的结果
                Gson gson = new Gson();
                List<DictationResult> resultList = gson.fromJson(resultJson,
                        new TypeToken<List<DictationResult>>() {
                        }.getType());
                String result = "";
                for (int i = 0; i < resultList.size() - 1; i++) {
                    result += resultList.get(i).toString();
                }
                etText.setText(result);
            }
        }

        @Override
        public void onError(SpeechError speechError) {

        }
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_mic:
                    //开始听写，需将sdk中的assets文件下的文件夹拷入项目的assets文件夹下（没有的话自己新建）
                    iatDialog.show();
                    break;
            }
        }
    }

}
