package com.ziq.base.recycleview.type;

import com.ziq.base.recycleview.BaseViewHolder;
import com.ziq.base.recycleview.BaseViewType;
import com.ziq.base.recycleview.adapter.ListRecyclerAdapter;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public class ListDataViewType extends BaseViewType<ListRecyclerAdapter> {

    public ListDataViewType(ListRecyclerAdapter adapter) {
        super(adapter);
    }

    @Override
    protected boolean isMatchViewType(int position) {
        return true;
    }

    @Override
    public int getItemViewType() {
        return 0;
    }

    @Override
    protected int getLayoutRes() {
        return getAdapter().getItemLayoutRes();
    }

    @Override
    protected void onBindViewHolder(BaseViewHolder holder, int position) {
        getAdapter().bindDataViewHolder(holder, position);
    }
}
