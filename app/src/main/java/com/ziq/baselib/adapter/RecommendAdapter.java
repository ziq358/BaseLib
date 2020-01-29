package com.ziq.baselib.adapter;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.ziq.base.glide.GlideRoundTransform;
import com.ziq.base.recycleview.BaseViewHolder;
import com.ziq.base.recycleview.adapter.ListRecyclerAdapter;
import com.ziq.baselib.R;
import com.ziq.baselib.model.LiveListItemBean;

import java.util.List;

public class RecommendAdapter extends ListRecyclerAdapter<LiveListItemBean> {

    public RecommendAdapter(Context context, List<LiveListItemBean> data) {
        super(context, data);
    }

    @Override
    public int getItemLayoutRes() {
        return R.layout.item_recommend_live_item;
    }

    @Override
    public void bindDataViewHolder(BaseViewHolder holder, int position) {
        final LiveListItemBean item = getItem(position);
        TextView tv_title = holder.getViewById(R.id.tv_title);
        tv_title.setText(item.getLive_title());
        ImageView ivCover  = holder.getViewById(R.id.iv_cover);
        RequestOptions requestOptions  = new RequestOptions()
                .placeholder(R.drawable.ic_picture_default_bg)
                .error(R.drawable.ic_picture_default_bg)
                .transform(new GlideRoundTransform(5));
        Glide.with(ivCover.getContext())
                .load(item.getLive_img())
                .apply(requestOptions)
                .into(ivCover);
    }
}
