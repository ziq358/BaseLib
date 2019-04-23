package com.ziq.baselib;

import android.content.Context;

import com.ziq.base.baserx.dagger.module.ConfigModule;
import com.ziq.base.baserx.dagger.module.GlobalConfigModule;

import androidx.annotation.NonNull;

public class GlobalConfiguration implements ConfigModule {
    @Override
    public void applyCustomConfig(@NonNull Context context, @NonNull GlobalConfigModule.Builder builder) {
        builder.setBaseurl(Constants.BASE_URL);
    }
}
