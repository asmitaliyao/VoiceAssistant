package com.example.voiceassistant.voiceassistant.utils;

import android.util.Log;

import com.example.voiceassistant.voiceassistant.bean.Data;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

/**
 * Json结果解析类
 */
public class JsonParser {
    private static final String TAG = "JsonParser";

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
//				如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    public static List<Data> parseNewsRequestResult(String json) {
        List<Data> dataList =  new ArrayList<>();
        StringBuffer ret = new StringBuffer();
        JSONTokener tokener = new JSONTokener(json);
        try {
            JSONObject jsonRootBean = new JSONObject(tokener);
            JSONObject result = jsonRootBean.getJSONObject("result");
            JSONArray datas = result.getJSONArray("data");
            for (int i = 0; i < datas.length(); i++) {
                Log.d(TAG, "parseNewsRequestResult: " + datas.get(i).toString());
                Data data = new Data();
                data.setAuthor_name(datas.getJSONObject(i).getString("author_name"));
                data.setCategory(datas.getJSONObject(i).getString("category"));
                data.setDate(datas.getJSONObject(i).getString("date"));
                data.setThumbnail_pic_s(datas.getJSONObject(i).getString("thumbnail_pic_s"));
                data.setTitle(datas.getJSONObject(i).getString("title"));
                data.setUniquekey(datas.getJSONObject(i).getString("uniquekey"));
                data.setUrl(datas.getJSONObject(i).getString("url"));
                dataList.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return dataList;
    }
}
