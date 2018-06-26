package com.ziq.baselib.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class BluetoothMusicButtonReceiver extends BroadcastReceiver {

    private final String TAG = "BluetoothMusicButton";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, " "+intent.getAction());

        if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(
                    Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                    Log.d(TAG, "KEYCODE_HEADSETHOOK / KEYCODE_MEDIA_PLAY_PAUSE");
                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    Log.d(TAG, "KEYCODE_MEDIA_PLAY");
                    break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                    Log.d(TAG, "KEYCODE_MEDIA_PAUSE");
                    break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    Log.d(TAG, "KEYCODE_MEDIA_STOP");
                    break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                    Log.d(TAG, "KEYCODE_MEDIA_NEXT");
                    break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                    Log.d(TAG, "KEYCODE_MEDIA_PREVIOUS");
                    break;
            }
        }
    }
}
