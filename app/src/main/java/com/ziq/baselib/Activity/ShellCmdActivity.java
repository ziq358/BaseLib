package com.ziq.baselib.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.FileUtil;
import com.ziq.base.utils.IntentUtil;
import com.ziq.base.utils.ShellUtil;
import com.ziq.baselib.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class ShellCmdActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.input_cmd)
    EditText inputCmd;
    @Bind(R.id.result)
    TextView result;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shell_cmd);
        ButterKnife.bind(this);
        inputCmd.setText("echo root");
    }

    @OnClick({R.id.run, R.id.run2})
    @Override
    public void onClick(View v) {
        ShellUtil.CommandResult commandResul = null;
        switch (v.getId()) {
            case R.id.run:
                commandResul = ShellUtil.execCommand(inputCmd.getText().toString(),false);
                result.setText("resultId："+ commandResul.result+" \n\n"+
                                "成功信息："+ commandResul.successMsg+" \n\n"+
                                "失败信息："+commandResul.errorMsg);
                break;
            case R.id.run2:
                commandResul = ShellUtil.execCommand(inputCmd.getText().toString(),true);
                result.setText("resultId："+ commandResul.result+" \n\n"+
                        "成功信息："+ commandResul.successMsg+" \n\n"+
                        "失败信息："+commandResul.errorMsg);
                break;
        }
    }
}
