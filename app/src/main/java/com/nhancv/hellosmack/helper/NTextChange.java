package com.nhancv.hellosmack.helper;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nhancao on 12/15/16.
 */

public class NTextChange implements TextWatcher {

    private Timer timer = new Timer();
    private TextListener doing;

    public NTextChange(TextListener doing) {
        this.doing = doing;
    }

    @Override
    public void afterTextChanged(final Editable editable) {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (doing != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                doing.after(editable);
                            } catch (Exception ignored) {
                            }
                        }
                    });
                }
            }
        }, 200);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // nothing to do here
        doing.before();
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * Run on ui thread
     *
     * @param runnable
     * @return
     */
    private Handler runOnUiThread(Runnable runnable) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
        return handler;
    }

    public interface TextListener {
        void after(Editable editable);

        void before();
    }
}