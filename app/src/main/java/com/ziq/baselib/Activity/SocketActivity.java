package com.ziq.baselib.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.baselib.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import butterknife.BindView;
import butterknife.OnClick;

public class SocketActivity extends MvpBaseActivity {

    @BindView(R.id.tv_result)
    TextView resultView;

    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_socket;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {

    }

    @OnClick({R.id.btn_request})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_request:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        communcate();
                    }
                }).start();

                break;
        }
    }

    public void communcate() {
        StringBuilder stringBuilder = new StringBuilder();
        Socket socket = null;
        try {
            socket = new Socket("193.112.65.251", 1234);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 注意这里必须制定请求方式 地址 注意空格
        StringBuffer sb = new StringBuffer("POST http://193.112.65.251:1234/live/list HTTP/1.1\r\n");
        // 以下为请求头
        sb.append("token: 97550625103a11e991d10242ac110003\r\n");
        sb.append("Content-Type: application/json; charset=UTF-8\r\n");
        sb.append("Content-Length: 44\r\n");
        sb.append("Host: 193.112.65.251:1234\r\n");
        // 注意这里不要使用压缩 否则返回乱码, 看接口是否给的是压缩
        sb.append("Accept-Encoding: gzip\r\n");
        sb.append("Connection: keep-alive\r\n");
        // 注意这里要换行结束请求头
        sb.append("\r\n");
        sb.append("{\"game_type\":\"ow\",\"limit\":\"20\",\"offset\":\"0\"}\r\n");
        String request = "请求：---------------------------------\n"+sb.toString();
        stringBuilder.append(request);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultView.setText(stringBuilder.toString());
            }
        });
        System.out.println(request);
        try {
            OutputStream os = socket.getOutputStream();
            os.write(sb.toString().getBytes());

            InputStream is = socket.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = is.read(bytes)) != -1) {
                baos.write(bytes, 0, len);
            }
            String result = "结果：---------------------------------\n"+ new String(baos.toByteArray());
            stringBuilder.append("\r\n");
            stringBuilder.append(result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resultView.setText(stringBuilder.toString());
                }
            });
            System.out.println(result);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
