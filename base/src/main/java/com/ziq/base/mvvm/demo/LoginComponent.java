package com.ziq.base.mvvm.demo;

import com.ziq.base.baserx.dagger.ActivityScope;
import com.ziq.base.baserx.dagger.component.AppComponent;

import dagger.Component;

@ActivityScope
@Component(modules = {LoginModule.class}, dependencies = {AppComponent.class})
public interface LoginComponent {
    void inject(LoginActivity loginActivity);
}
