package com.ziq.baselib.Activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.utils.encryption.AesEncryptionUtil;
import com.ziq.baselib.R;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class AesEncryptionActivity extends MvpBaseActivity implements View.OnClickListener {
    @BindView(R.id.et_content)
    EditText mEtContent;
    @BindView(R.id.sp_model)
    Spinner mSpMode;
    @BindView(R.id.sp_padding)
    Spinner mSpPadding;
    @BindView(R.id.sp_key_length)
    Spinner mSpKeyLength;
    @BindView(R.id.et_key)
    EditText mEtKey;
    @BindView(R.id.et_offset)
    EditText mEtOffset;
    @BindView(R.id.sp_code)
    Spinner mSpCode;

    @BindView(R.id.btn_encrypt)
    Button mBtnEncrypt;
    @BindView(R.id.btn_decrypt)
    Button mBtnDecrypt;

    @BindView(R.id.tv_result)
    TextView mTvResult;


    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_aes_encryption;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
    }

    @OnClick({R.id.btn_encrypt, R.id.btn_decrypt})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_encrypt:
                String result = null;
                try {
                    String offset = mEtOffset.getText().toString();
                    if (TextUtils.isEmpty(offset)) {
                        offset = "1234567890123456";
                    }
                    result = AesEncryptionUtil.encrypt(mEtContent.getText().toString(), Integer.valueOf(mSpKeyLength.getSelectedItem().toString()), mEtKey.getText().toString(), offset,
                            mSpMode.getSelectedItem().toString(), mSpPadding.getSelectedItem().toString(), mSpCode.getSelectedItem().toString());
                } catch (Exception e) {
                    Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mTvResult.setText(result);
                break;
            case R.id.btn_decrypt:
                break;
        }
    }
}
