package com.ziq.baselib.test;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.widget.TextView;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.baselib.R;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import butterknife.BindView;

/**
 * @author wuyanqiang
 * @date 2018/9/19
 */
public class TestByteBufferActivity extends MvpBaseActivity {

    @BindView(R.id.tv_content)
    TextView mTvContent;

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_test_byte_buffer;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        StringBuilder stringBuilder = new StringBuilder();

        ByteBuffer buffer = ByteBuffer.allocate(10);
        stringBuilder.append(getByteBufferString("allocate 10 ", buffer));

        for (int i = 1; i < 6; i++) {
            buffer.put((byte) i);
        }
        stringBuilder.append(getByteBufferString("put 5 data ", buffer));
        for (int i = 0; i < 3; i++) {
            stringBuilder.append(buffer.get() + "\n");
        }
        stringBuilder.append(getByteBufferString("get 3 data ", buffer));

        buffer.flip();
        stringBuilder.append(getByteBufferString("flip ", buffer));
        for (int i = 0; i < 3; i++) {
            stringBuilder.append(buffer.get() + "\n");
        }
        stringBuilder.append(getByteBufferString("get 3 data ", buffer));

        buffer.clear();
        stringBuilder.append(getByteBufferString("clear ", buffer));
        mTvContent.setText(stringBuilder.toString());
    }


    private String getByteBufferString(String msg, ByteBuffer byteBuffer) {
        StringBuilder stringBuilder = new StringBuilder();
        byte[] bytes = byteBuffer.array();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            stringBuilder.append(b);
        }
        return msg + " ==  mark : " + mark(byteBuffer) + " , position : " + byteBuffer.position()
                + " , limit : " + byteBuffer.limit() + " , capacity : " + byteBuffer.capacity()
                + " , 内容 ：" + stringBuilder.toString() + "\n\n";
    }

    private int mark(ByteBuffer byteBuffer) {
        try {
            Field field = byteBuffer.getClass().getSuperclass().getSuperclass().getDeclaredField("mark");
            field.setAccessible(true);
            return field.getInt(byteBuffer);
        } catch (Exception e) {
        }
        return -100;
    }


}
