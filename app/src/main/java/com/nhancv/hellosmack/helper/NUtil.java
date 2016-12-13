package com.nhancv.hellosmack.helper;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.nhancv.hellosmack.listener.ICollections;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class NUtil {

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
     * Run method in new thread with async mode
     *
     * @param onSubscribe
     * @param <T>
     * @return
     */
    public static <T> Subscription aSyncTaskNewThread(Observable.OnSubscribe<T> onSubscribe) {
        return aSyncTaskNewThread(onSubscribe, null);
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
        return aSyncTask(onSubscribe, onNext, null, null);
    }

    /**
     * Run method in aSync mode
     *
     * @param onSubscribe
     * @param onNext
     * @param onError
     * @param onCompleted
     * @param <T>
     * @return
     */
    public static <T> Subscription aSyncTask(@NonNull Observable.OnSubscribe<T> onSubscribe, @Nullable rx.functions.Action1<? super T> onNext, @Nullable rx.functions.Action1<Throwable> onError, @Nullable rx.functions.Action0 onCompleted) {
        if (onNext == null) {
            return Observable.create(onSubscribe).compose(RxHelper.applySchedulers()).subscribe();
        } else if (onError == null) {
            return Observable.create(onSubscribe).compose(RxHelper.applySchedulers()).subscribe(onNext);
        } else if (onCompleted == null) {
            return Observable.create(onSubscribe).compose(RxHelper.applySchedulers()).subscribe(onNext, onError);
        }
        return Observable.create(onSubscribe).compose(RxHelper.applySchedulers()).subscribe(onNext, onError, onCompleted);
    }

    /**
     * Run method in new thread with async mode
     *
     * @param onSubscribe
     * @param onNext
     * @param <T>
     * @return
     */
    public static <T> Subscription aSyncTaskNewThread(@NonNull Observable.OnSubscribe<T> onSubscribe, @Nullable rx.functions.Action1<? super T> onNext) {
        if (onNext == null) {
            return Observable.create(onSubscribe).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread()).subscribe();
        }
        return Observable.create(onSubscribe).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread()).subscribe(onNext);
    }

    /**
     * Run method in new thread with async mode
     *
     * @param onSubscribe
     * @param onNext
     * @param onError
     * @param onCompleted
     * @param <T>
     * @return
     */
    public static <T> Subscription aSyncTaskNewThread(@NonNull Observable.OnSubscribe<T> onSubscribe, @Nullable rx.functions.Action1<? super T> onNext, @Nullable rx.functions.Action1<Throwable> onError, @Nullable rx.functions.Action0 onCompleted) {
        if (onNext == null) {
            return Observable.create(onSubscribe).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread()).subscribe();
        } else if (onError == null) {
            return Observable.create(onSubscribe).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread()).subscribe(onNext);
        } else if (onCompleted == null) {
            return Observable.create(onSubscribe).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread()).subscribe(onNext, onError);
        }
        return Observable.create(onSubscribe).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread()).subscribe(onNext, onError, onCompleted);
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
        new Handler(Looper.getMainLooper()).post(doing::callback);
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


    /**
     * Adjust alpha
     *
     * @param color
     * @param factor
     * @return color was adjusted
     */
    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }
}
