package com.ziq.baselib.Activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.ziq.base.mvp.dagger.component.AppComponent;
import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.mvp.BaseFragment;
import com.ziq.baselib.R;

import butterknife.BindView;

/**
 * @author wuyanqiang
 * @date 2018/10/12
 */
public class FragmentTestActivity extends BaseActivity {
    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_fragment_test;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        addFragment(R.id.content, new MyFragment(), "ff", false);
    }


    public static class MyFragment extends BaseFragment{

        @BindView(R.id.label)
        TextView mTv;

        @Override
        public int initLayoutResourceId() {
            return R.layout.activity_fragment_ff;
        }

        @Override
        public void initForInject(AppComponent appComponent) {

        }

        @Override
        public void initData(@NonNull View view, @Nullable Bundle savedInstanceState) {
            mTv.setText("skskskks");
        }
    }

}
