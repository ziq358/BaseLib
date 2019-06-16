package com.ziq.baselib.adapter;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ziq.base.glide.GlideRoundTransform;
import com.ziq.baselib.R;
import com.ziq.baselib.model.LiveListItemBean;

import java.util.List;

public class RecommendAdapter extends BaseQuickAdapter<LiveListItemBean, BaseViewHolder> {

    public RecommendAdapter(@Nullable List<LiveListItemBean> data) {
        super(R.layout.item_recommend_live_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, LiveListItemBean item) {
                helper.setText(R.id.tv_title, item.getLive_title());
        ImageView ivCover  = helper.getView(R.id.iv_cover);
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
