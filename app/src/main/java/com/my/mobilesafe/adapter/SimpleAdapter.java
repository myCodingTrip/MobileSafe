package com.my.mobilesafe.adapter;

import android.content.Context;

import java.util.List;

/**
 * 使用BaseViewHolder的Adapter
 * Created by MY on 2016/9/16.
 */
public abstract class SimpleAdapter<T> extends BaseAdapter<T, BaseViewHolder>{
    public SimpleAdapter(Context context, List<T> data, int layoutResId) {
        super(context, data, layoutResId);
    }
}
