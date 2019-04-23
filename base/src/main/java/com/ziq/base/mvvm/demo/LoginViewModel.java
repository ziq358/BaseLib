package com.ziq.base.mvvm.demo;

import android.app.Application;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class LoginViewModel extends AndroidViewModel implements ILoginViewModel{

    @Inject
    public LoginViewModel(@NonNull Application application) {
        super(application);
    }
}
