package com.nhancv.hellosmack;

import android.os.Handler;
import android.os.Looper;

import com.nhancv.hellosmack.listener.CallbackListener;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class Utils {
    public static void postToUi(final CallbackListener callbackListener) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                callbackListener.callback();
            }
        });
    }
}
