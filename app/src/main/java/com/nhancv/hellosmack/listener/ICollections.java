package com.nhancv.hellosmack.listener;

import android.content.DialogInterface;
import android.view.View;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class ICollections {

    public interface CallbackListener {
        /**
         * Callback
         */
        void callback();
    }

    public interface ObjectCallBack<T> {
        /**
         * Callback with item object
         *
         * @param item
         */
        void callback(T item);
    }

    public interface IDialogButton {
        /**
         * Handle positive click
         *
         * @param dialog
         * @param button
         * @param rootView
         */
        void positive(DialogInterface dialog, View button, View rootView);

        /**
         * Handle neutral click
         *
         * @param dialog
         * @param button
         * @param rootView
         */
        void neutral(DialogInterface dialog, View button, View rootView);

        /**
         * Handle negative click
         *
         * @param dialog
         * @param button
         * @param rootView
         */
        void negative(DialogInterface dialog, View button, View rootView);
    }

    public static abstract class IDialogAlertButtonImpl implements IDialogButton {
        @Override
        public void neutral(DialogInterface dialog, View button, View rootView) {

        }

        @Override
        public void negative(DialogInterface dialog, View button, View rootView) {

        }
    }

    public static abstract class IDialogConfirmButtonImpl implements IDialogButton {
        @Override
        public void neutral(DialogInterface dialog, View button, View rootView) {

        }
    }
}
