package com.nhancv.hellosmack.helper;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.nhancv.hellosmack.helper.RxHelper;
import com.nhancv.hellosmack.listener.ICollections;

import rx.Observable;
import rx.Subscription;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class Utils {

    /**
     * Run method in aSync mode
     *
     * @param onSubscribe
     * @param <T>
     * @return Subscription
     */
    public static <T> Subscription aSyncTask(Observable.OnSubscribe<T> onSubscribe) {
        return aSyncTask(onSubscribe, null);
    }

    /**
     * Run method in aSync mode
     *
     * @param onSubscribe
     * @param onNext
     * @param <T>
     * @return Subscription
     */
    public static <T> Subscription aSyncTask(@NonNull Observable.OnSubscribe<T> onSubscribe, @Nullable rx.functions.Action1<? super T> onNext) {
        if (onNext == null) {
            return Observable.create(onSubscribe).compose(RxHelper.applySchedulers()).subscribe();
        }
        return Observable.create(onSubscribe).compose(RxHelper.applySchedulers()).subscribe(onNext);
    }

    /**
     * Run method on Ui
     *
     * @param onNext
     * @return Subscription
     */
    public static Subscription runOnUi(rx.functions.Action1<? super Object> onNext) {
        return Observable.create(subscriber -> {
            subscriber.onNext(new Object());
        }).compose(RxHelper.applySchedulers()).subscribe(onNext);
    }

    /**
     * Run method on Ui
     *
     * @param doing
     */
    public static void runOnUi(ICollections.CallbackListener doing) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                doing.callback();
            }
        });
    }

    /**
     * Run method with sync mode
     *
     * @param onSubscribe
     * @param <T>
     * @return Subscription
     */
    public static <T> Subscription syncTask(Observable.OnSubscribe<T> onSubscribe) {
        return Observable.create(onSubscribe).subscribe();
    }

    /**
     * Show toast
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

}
