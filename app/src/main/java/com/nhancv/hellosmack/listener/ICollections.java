package com.nhancv.hellosmack.listener;

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

}
