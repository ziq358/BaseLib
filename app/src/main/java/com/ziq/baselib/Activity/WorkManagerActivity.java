package com.ziq.baselib.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.ziq.base.baserx.dagger.component.AppComponent;
import com.ziq.base.mvp.MvpBaseActivity;
import com.ziq.baselib.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class WorkManagerActivity extends MvpBaseActivity {
    @Override
    public int initLayoutResourceId() {
        return R.layout.activity_workmanager;
    }

    @Override
    public void initForInject(AppComponent appComponent) {

    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        Data data = new Data.Builder()
                .putString("time", dateFormat.format(new Date()))
                .build();
        OneTimeWorkRequest request = new OneTimeWorkRequest.Builder(DemoWorker.class)
                .setInputData(data).build();
        WorkManager.getInstance().enqueue(request);
        WorkManager.getInstance().getWorkInfoByIdLiveData(request.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        Log.e("ziq", "work onChanged: ");
                        if (workInfo.getState().isFinished()) {
                            Log.e("ziq", workInfo.getOutputData().getString("name"));
                        }
                    }
                });
    }

    //注意 不能写为内部类，不然会报WM-WorkerFactory: Could not instantiate
    public static class DemoWorker extends Worker {

        public DemoWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            Log.e("ziq", "doWork: "+Thread.currentThread() + "   "+getInputData().getString("time"));
            Data outputData = new Data.Builder()
                    .putString("name", "SouthernBox")
                    .build();
            return Result.success(outputData);
        }
    }

}
