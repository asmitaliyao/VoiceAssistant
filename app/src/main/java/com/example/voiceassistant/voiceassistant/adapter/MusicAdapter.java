package com.example.voiceassistant.voiceassistant.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.voiceassistant.voiceassistant.R;
import com.example.voiceassistant.voiceassistant.bean.Music;

import java.util.List;

/**
 * @author 邹晓邦
 * @date 18/4/6.
 */
public class MusicAdapter extends BaseAdapter {
    private Context mContext;
    private List<Music> mMusicList;

    public MusicAdapter(Context context, List<Music> musicList){
        mContext = context;
        mMusicList = musicList;
    }

    @Override
    public int getCount() {
        return mMusicList.size();
    }

    @Override
    public Object getItem(int i) {
        return mMusicList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewHolder holder = null;

        if (converView == null){
            converView = LayoutInflater.from(mContext).inflate(R.layout.item_music, parent,
                    false);
            holder = new ViewHolder();
            holder.tvName = converView.findViewById(R.id.tv_music_name);
            holder.tvAuthor = converView.findViewById(R.id.tv_music_author);
            converView.setTag(holder);
        } else {
            holder = (ViewHolder) converView.getTag();
        }

        holder.tvName.setText(mMusicList.get(position).getMusic());
        holder.tvAuthor.setText(mMusicList.get(position).getSinger());

        return converView;
    }

    private class ViewHolder{
        private TextView tvName;
        private TextView tvAuthor;


    }
}
