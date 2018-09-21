package com.ziq.base.mvp;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author wuyanqiang
 * @date 2018/9/21
 */
public abstract class BaseFragment extends Fragment {

    private View mContentView;
    private FragmentManager mChildFragmentManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(initLayoutResourceId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentView = view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mChildFragmentManager = getChildFragmentManager();
        initData(mContentView, savedInstanceState);
    }

    public abstract @LayoutRes int initLayoutResourceId();
    public abstract void initData(@NonNull View view, @Nullable Bundle savedInstanceState);


    public void addFragment(@IdRes int contentId, Fragment fragment, String tag, boolean isAddToBackStack) {
        if (mChildFragmentManager != null) {
            FragmentTransaction transaction = mChildFragmentManager.beginTransaction();
            transaction.add(contentId, fragment, tag);
            if (isAddToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commitAllowingStateLoss();
        }
    }

    public void replaceFragment(@IdRes int contentId, Fragment fragment, String tag, boolean isAddToBackStack) {
        if (mChildFragmentManager != null) {
            FragmentTransaction transaction = mChildFragmentManager.beginTransaction();
            transaction.replace(contentId, fragment, tag);
            if (isAddToBackStack) {
                transaction.addToBackStack(tag);
            }
            transaction.commitAllowingStateLoss();
        }
    }

}
