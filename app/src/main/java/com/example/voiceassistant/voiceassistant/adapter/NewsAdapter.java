package com.example.voiceassistant.voiceassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.voiceassistant.voiceassistant.R;
import com.example.voiceassistant.voiceassistant.bean.Data;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

public class NewsAdapter extends BaseAdapter {

    private Context context;
    private List<Data> dataList;

    public NewsAdapter(Context context, List<Data> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_news, parent,
                    false);
            holder = new ViewHolder();
            holder.newsIv = convertView.findViewById(R.id.news_item_iv);
            holder.newsTitleTv = convertView.findViewById(R.id.news_item_title_tv);
            holder.newsIdTv = convertView.findViewById(R.id.news_item_id);
            holder.newsAuthorTv = convertView.findViewById(R.id.news_item_author_tv);
            holder.newsDateTv = convertView.findViewById(R.id.news_item_date_tv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        URL url = null;
        try {
            url = new URL(dataList.get(position).getThumbnail_pic_s());
            Glide.with(context)
                    .load(url)
                    .into(holder.newsIv);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        holder.newsTitleTv.setText(dataList.get(position).getTitle());
        holder.newsIdTv.setText( "" +(position + 1));
        holder.newsAuthorTv.setText(dataList.get(position).getAuthor_name());
        holder.newsDateTv.setText(dataList.get(position).getDate());
        return convertView;
    }

    private class ViewHolder {
        private ImageView newsIv;
        private TextView newsTitleTv;
        private TextView newsIdTv;
        private TextView newsAuthorTv;
        private TextView newsDateTv;
    }
}
