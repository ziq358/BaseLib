package com.ziq.baselib;

import android.content.Context;
import android.widget.TextView;

import com.ziq.base.recycleView.BaseViewHolder;
import com.ziq.base.recycleView.adapter.ListRecyclerAdapter;

import java.util.List;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public class MainRecycleViewAdapter extends ListRecyclerAdapter<String> {

    public MainRecycleViewAdapter(Context context) {
        super(context);
    }

    public MainRecycleViewAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public int getItemLayoutRes() {
        return R.layout.item_main;
    }

    @Override
    public void bindDataViewHolder(BaseViewHolder holder, int position) {
        TextView title = holder.getViewById(R.id.title);
        title.setText(getItem(position));

    }
}
