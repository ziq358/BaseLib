package com.ziq.baselib.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.utils.ShellUtil;
import com.ziq.baselib.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class ShellCmdActivity extends MvpBaseActivity implements View.OnClickListener {
    @BindView(R.id.input_cmd)
    EditText inputCmd;
    @BindView(R.id.result)
    TextView result;


    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_shell_cmd;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        inputCmd.setText("echo root");
    }

    @OnClick({R.id.run, R.id.run2})
    @Override
    public void onClick(View v) {
        ShellUtil.CommandResult commandResul = null;
        switch (v.getId()) {
            case R.id.run:
                commandResul = ShellUtil.execCommand(inputCmd.getText().toString(), false);
                result.setText("resultId：" + commandResul.result + " \n\n" +
                        "成功信息：" + commandResul.successMsg + " \n\n" +
                        "失败信息：" + commandResul.errorMsg);
                break;
            case R.id.run2:
                commandResul = ShellUtil.execCommand(inputCmd.getText().toString(), true);
                result.setText("resultId：" + commandResul.result + " \n\n" +
                        "成功信息：" + commandResul.successMsg + " \n\n" +
                        "失败信息：" + commandResul.errorMsg);
                break;
        }
    }
}
