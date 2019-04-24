package com.ziq.base.mvvm.demo;

import android.app.Application;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class LoginDemoViewModel extends AndroidViewModel implements ILoginDemoViewModel {

    @Inject
    public LoginDemoViewModel(@NonNull Application application) {
        super(application);
    }


    private String testActivityValue = "activity 来自 view model";
    private String testFragmentValue = "fragment 来自 view model";

    public String getTestFragmentValue() {
        return testFragmentValue;
    }

    public String getTestActivityValue() {
        return testActivityValue;
    }
}
