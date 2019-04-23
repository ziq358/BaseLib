package com.ziq.base.baserx.dagger.module;

import android.content.Context;

import androidx.annotation.NonNull;

public interface ConfigModule {

    /**
     * 使用 {@link GlobalConfigModule.Builder} 给框架配置一些配置参数
     */
    void applyCustomConfig(@NonNull Context context, @NonNull GlobalConfigModule.Builder builder);

}
