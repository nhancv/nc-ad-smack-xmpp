package com.nhancv.hellosmack.helper;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.HashMap;

/**
 * Created by nhancao on 2/13/17.
 */

public class NKeyboard implements ViewTreeObserver.OnGlobalLayoutListener {

    private static HashMap<NKeyboardListener, NKeyboard> listenerMap = new HashMap<>();
    private NKeyboardListener callback;
    private View rootView;
    private boolean wasOpened;

    private NKeyboard(Activity act, NKeyboardListener listener) {
        callback = listener;

        rootView = ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(this);

    }

    public static void addListener(Activity act, NKeyboardListener listener) {
        removeListener(listener);
        listenerMap.put(listener, new NKeyboard(act, listener));
    }

    public static void removeListener(NKeyboardListener listener) {
        if (listenerMap.containsKey(listener)) {
            NKeyboard k = listenerMap.get(listener);
            k.removeListener();
            listenerMap.remove(listener);
        }
    }

    public static void removeAllListeners() {
        for (NKeyboardListener l : listenerMap.keySet()) {
            listenerMap.get(l).removeListener();
        }
        listenerMap.clear();
    }

    @Override
    public void onGlobalLayout() {
        Rect rect = new Rect();
        rootView.getWindowVisibleDisplayFrame(rect);

        int screenHeight = rootView.getHeight();
        int keyboardHeight = screenHeight - (rect.bottom - rect.top);

        boolean isOpen = keyboardHeight > screenHeight / 3;
        if (isOpen == wasOpened) {
            // keyboard state has not changed
            return;
        }
        wasOpened = isOpen;

        if (callback != null) {
            callback.onToggleSoftKeyboard(keyboardHeight > screenHeight / 3, screenHeight, keyboardHeight);
        }

    }

    private void removeListener() {
        callback = null;
        rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    public interface NKeyboardListener {
        void onToggleSoftKeyboard(boolean isVisible, int screenHeight, int keyboardHeight);
    }


}
