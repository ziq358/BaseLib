package com.ziq.base.mvvm.demo;

import android.app.Application;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LoginDemoViewModelFactory extends ViewModelProvider.NewInstanceFactory{
    @Inject
    Application application;

    @Inject
    LoginDemoViewModel loginViewModel;

    @Inject
    public LoginDemoViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(modelClass.isAssignableFrom(LoginDemoViewModel.class)){
            return (T)loginViewModel;
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
