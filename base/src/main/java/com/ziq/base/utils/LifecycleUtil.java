package com.ziq.base.utils;

import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.LifecycleTransformer;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;
import com.ziq.base.baserx.ActivityLifecycleProvider;
import com.ziq.base.baserx.FragmentLifecycleProvider;

public class LifecycleUtil {

    public static <T> LifecycleTransformer<T> bindToDestroy(LifecycleProvider lifecycleProvider) {
        if (lifecycleProvider instanceof ActivityLifecycleProvider) {
            return lifecycleProvider.bindUntilEvent(ActivityEvent.DESTROY);
        } else if (lifecycleProvider instanceof FragmentLifecycleProvider) {
            return lifecycleProvider.bindUntilEvent(FragmentEvent.DESTROY);
        } else {
            throw new IllegalArgumentException("Lifecycleable not match");
        }
    }

}
