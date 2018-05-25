package com.ziq.base.recycleView;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author john.
 * @since 2018/5/21.
 * Des:
 */

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> mViews = new SparseArray<>();
    private View mRootView;

    public BaseViewHolder(View rootView) {
        super(rootView);
        this.mRootView = rootView;
        parseViews(rootView);
    }

    public View getRootView() {
        return mRootView;
    }

    private void parseViews(View view) {
        if (view.getId() != View.NO_ID) {
            mViews.put(view.getId(), view);
        }
        if (view instanceof ViewGroup) {
            int childCount = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = ((ViewGroup) view).getChildAt(i);
                if (child != null) {
                    parseViews(child);
                }
            }
        }
    }

    public <T extends View> T getViewById(int viewId) {
        return (T) (mViews.get(viewId));
    }

}
