package com.ziq.baselib.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.ziq.base.recycleview.BaseViewHolder;
import com.ziq.base.recycleview.adapter.ListRecyclerAdapter;
import com.ziq.baselib.R;

import java.util.List;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public class BluetoothRecycleViewAdapter extends ListRecyclerAdapter<BluetoothDevice> {

    public BluetoothRecycleViewAdapter(Context context) {
        super(context);
    }
    OnItemClickListener mOnItemClickListener;
    public BluetoothRecycleViewAdapter(Context context, List<BluetoothDevice> data) {
        super(context, data);
    }

    @Override
    public int getItemLayoutRes() {
        return R.layout.item_blue_tooth_device;
    }

    @Override
    public void bindDataViewHolder(BaseViewHolder holder, int position) {
        final BluetoothDevice item = getItem(position);
        TextView title = holder.getViewById(R.id.title);
        String name = item.getName();
        String address = item.getAddress();
        if (TextUtils.isEmpty(name)) {
            title.setText(address);
        } else {
            title.setText(name);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mOnItemClickListener != null){
                    mOnItemClickListener.onClick(item);
                }
            }
        });
    }


    public void setItemClickListerer(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onClick(BluetoothDevice item);
    }

}
