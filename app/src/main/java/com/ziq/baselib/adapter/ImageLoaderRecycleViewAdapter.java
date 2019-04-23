package com.ziq.baselib.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.ziq.base.recycleview.BaseViewHolder;
import com.ziq.base.recycleview.adapter.ListRecyclerAdapter;
import com.ziq.baselib.R;

import java.util.List;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public class ImageLoaderRecycleViewAdapter extends ListRecyclerAdapter<String> {

    public ImageLoaderRecycleViewAdapter(Context context) {
        super(context);
    }

    public ImageLoaderRecycleViewAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    public int getItemLayoutRes() {
        return R.layout.item_image;
    }

    @Override
    public void bindDataViewHolder(BaseViewHolder holder, int position) {
        final String item = getItem(position);
        ImageView photo = holder.getViewById(R.id.iv_photo);
        ImageLoader.getInstance().displayImage(item, photo);
    }
}
