package com.ex.serialport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ex.serialport.R;

/**
 * @author: liuwan
 * @date: 2018-05-28
 * @action:
 */
public class SpAdapter extends BaseAdapter {

    String[] datas;
    Context mContext;

    public SpAdapter(Context context) {
        this.mContext = context;
    }

    public void setDatas(String[] datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas == null ? 0 : datas.length;
    }

    @Override
    public Object getItem(int position) {
        return datas == null ? null : datas[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHodler hodler = null;
        if (convertView == null) {
            hodler = new ViewHodler();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_layout, null);
            hodler.mTextView = (TextView) convertView;
            convertView.setTag(hodler);
        } else {
            hodler = (ViewHodler) convertView.getTag();
        }

        hodler.mTextView.setText(datas[position]);

        return convertView;
    }

    private static class ViewHodler {
        TextView mTextView;
    }
}
