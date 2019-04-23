package com.ziq.baselib;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.ziq.base.recycleview.BaseViewHolder;
import com.ziq.base.recycleview.adapter.ListRecyclerAdapter;

import java.util.List;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public class MainRecycleViewAdapter extends ListRecyclerAdapter<MainActivity.DemoListItem> {

    public MainRecycleViewAdapter(Context context) {
        super(context);
    }

    public MainRecycleViewAdapter(Context context, List<MainActivity.DemoListItem> data) {
        super(context, data);
    }

    @Override
    public int getItemLayoutRes() {
        return R.layout.item_main;
    }

    @Override
    public void bindDataViewHolder(BaseViewHolder holder, int position) {
        final MainActivity.DemoListItem item = getItem(position);
        TextView title = holder.getViewById(R.id.title);
        title.setText(item.name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(v.getContext(), item.cls);
                v.getContext().startActivity(intent);
            }
        });
    }
}
