package com.ziq.base.mvvm.demo;

import com.ziq.base.baserx.dagger.ActivityScope;
import com.ziq.base.baserx.dagger.component.AppComponent;

import dagger.Component;

@ActivityScope
@Component(modules = {LoginDemoModule.class}, dependencies = {AppComponent.class})
public interface LoginFragmentDemoComponent {
    void inject(LoginDemoFragment loginDemoFragment);
}
