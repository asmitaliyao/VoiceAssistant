package com.example.voiceassistant.voiceassistant.bean; /**
 * Copyright 2018 bejson.com
 */

import com.example.voiceassistant.voiceassistant.bean.Data;

import java.util.List;

/**
 * Auto-generated: 2018-04-06 15:4:48
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Result {

    private String stat;
    private List<Data> data;
    public void setStat(String stat) {
        this.stat = stat;
    }
    public String getStat() {
        return stat;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
    public List<Data> getData() {
        return data;
    }

}