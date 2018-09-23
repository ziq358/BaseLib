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

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author wuyanqiang
 */
public abstract class BaseFragment<P extends IBasePresenter> extends Fragment {

    private View mContentView;
    private FragmentManager mChildFragmentManager;

    private Unbinder mUnbinder;

    @Inject
    protected P mPresenter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(initLayoutResourceId(), container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContentView = view;
        mUnbinder = ButterKnife.bind(mContentView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mChildFragmentManager = getChildFragmentManager();
        initData(mContentView, savedInstanceState);
    }

    public abstract @LayoutRes int initLayoutResourceId();
    public abstract void initData(@NonNull View view, @Nullable Bundle savedInstanceState);


    @Override
    public void onDestroy() {
        if(mUnbinder != null){
            mUnbinder.unbind();
            mUnbinder = null;
        }
        if(mPresenter != null){
            mPresenter.destory();
            mPresenter = null;
        }
        super.onDestroy();
    }

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
