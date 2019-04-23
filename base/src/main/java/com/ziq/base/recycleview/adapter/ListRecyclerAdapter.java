package com.ziq.base.recycleview.adapter;

import android.content.Context;
import androidx.annotation.LayoutRes;

import com.ziq.base.recycleview.BaseAdapter;
import com.ziq.base.recycleview.BaseViewHolder;
import com.ziq.base.recycleview.BaseViewType;
import com.ziq.base.recycleview.type.ListDataViewType;

import java.util.List;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public abstract class ListRecyclerAdapter<T> extends BaseAdapter<T> {

    public ListRecyclerAdapter(Context context) {
        super(context);
    }

    public ListRecyclerAdapter(Context context, List<T> data) {
        super(context, data);
    }

    @Override
    protected void initViewType(List<BaseViewType> viewTypesList) {
        viewTypesList.add(new ListDataViewType(this));
    }

    @LayoutRes
    public abstract int getItemLayoutRes();

    public abstract void bindDataViewHolder(BaseViewHolder holder, final int position);

}
