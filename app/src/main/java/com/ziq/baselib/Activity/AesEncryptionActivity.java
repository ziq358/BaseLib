package com.ziq.baselib.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ziq.base.mvp.BaseActivity;
import com.ziq.base.utils.encryption.AesEncryptionUtil;
import com.ziq.baselib.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author john.
 * @since 2018/5/25.
 * Des:
 */

public class AesEncryptionActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.et_content)
    EditText mEtContent;
    @Bind(R.id.sp_model)
    Spinner mSpMode;
    @Bind(R.id.sp_padding)
    Spinner mSpPadding;
    @Bind(R.id.sp_key_length)
    Spinner mSpKeyLength;
    @Bind(R.id.et_key)
    EditText mEtKey;
    @Bind(R.id.et_offset)
    EditText mEtOffset;
    @Bind(R.id.sp_code)
    Spinner mSpCode;

    @Bind(R.id.btn_encrypt)
    Button mBtnEncrypt;
    @Bind(R.id.btn_decrypt)
    Button mBtnDecrypt;

    @Bind(R.id.tv_result)
    TextView mTvResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aes_encryption);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_encrypt, R.id.btn_decrypt})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_encrypt:
                String result = null;
                try {
                    String offset = mEtOffset.getText().toString();
                    if(TextUtils.isEmpty(offset)){
                        offset = "1234567890123456";
                    }
                    result = AesEncryptionUtil.encrypt(mEtContent.getText().toString(), Integer.valueOf(mSpKeyLength.getSelectedItem().toString()), mEtKey.getText().toString(), offset,
                                            mSpMode.getSelectedItem().toString(), mSpPadding.getSelectedItem().toString(), mSpCode.getSelectedItem().toString());
                } catch (Exception e) {
                    Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mTvResult.setText(result);
                break;
            case R.id.btn_decrypt:
                break;
        }
    }
}
