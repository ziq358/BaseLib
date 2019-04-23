package com.ziq.baselib.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.utils.LogUtil;
import com.ziq.baselib.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class DataTranslateTestActivity extends MvpBaseActivity implements View.OnClickListener {
    @BindView(R.id.input)
    EditText input;
    @BindView(R.id.result)
    TextView result;


    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_data_translate;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
    }

    @OnClick({R.id.translate})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.translate:
                String text = input.getText().toString();
                byte[] bytes = text.getBytes();
                for (int i = 0; i < bytes.length; i++) {
                    LogUtil.i("ziq", "onClick: " + i + "   " + bytes[i]);
                    int tempI = bytes[i] & 0xFF;//byte:8bit,int:32bit;高位相与.
                    LogUtil.i("ziq", "tempI: " + i + "   " + tempI);
                    String str = Integer.toHexString(tempI);
                    LogUtil.i("ziq", "str: " + i + "   " + str);
                }


                String str = Integer.toHexString(Integer.valueOf(input.getText().toString()));
                result.setText(str);
                break;
        }
    }
}
