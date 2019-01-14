package com.ziq.base.utils.performance;

import android.view.Choreographer;

public class BlockDetectByChoreographer {
    public static void start() {
        Choreographer.getInstance()
            .postFrameCallback(new Choreographer.FrameCallback() {
                @Override
                public void doFrame(long l) {
                    if (UIBlockMonitor.getInstance().isMonitoring()) {
                        UIBlockMonitor.getInstance().removeMonitor();
                    }
                    UIBlockMonitor.getInstance().startMonitor();
                    Choreographer.getInstance().postFrameCallback(this);
                }
        });
    }
}
