package com.nhancv.hellosmack.helper;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class RxHelper {

    private static final Observable.Transformer SCHEDULERS_TRANSFORMER = new Observable.Transformer<Observable<Object>, Observable<Object>>() {
        @Override
        public Observable<Observable<Object>> call(Observable<Observable<Object>> observable) {
            return observable.subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    private static final Observable.Transformer SCHEDULERS_TRANSFORMER_NEW_THREAD = new Observable.Transformer<Observable<Object>, Observable<Object>>() {
        @Override
        public Observable<Observable<Object>> call(Observable<Observable<Object>> observable) {
            return observable.subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread());
        }
    };

    /**
     * Apply scheduler:
     * observable
     * .subscribeOn(Schedulers.computation())
     * .observeOn(AndroidSchedulers.mainThread());
     *
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return (Observable.Transformer<T, T>) SCHEDULERS_TRANSFORMER;
    }

    /**
     * Apply scheduler with newThread:
     * observable
     * .subscribeOn(Schedulers.newThread())
     * .observeOn(AndroidSchedulers.mainThread());
     *
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Observable.Transformer<T, T> applyNewThreadSchedulers() {
        return (Observable.Transformer<T, T>) SCHEDULERS_TRANSFORMER_NEW_THREAD;
    }

    /**
     * onNext with subscriber and item
     *
     * @param subscriber
     * @param item
     * @param <T>
     */
    public static <T> void onNext(Subscriber<T> subscriber, T item) {
        if (!subscriber.isUnsubscribed()) {
            subscriber.onNext(item);
        }
    }

    /**
     * onError with subscriber and exception
     *
     * @param subscriber
     * @param e
     */
    public static void onError(Subscriber<?> subscriber, Exception e) {
        if (!subscriber.isUnsubscribed()) {
            subscriber.onError(e);
        }
    }

    /**
     * onCompleted with subscriber
     *
     * @param subscriber
     */
    public static void onCompleted(Subscriber<?> subscriber) {
        if (!subscriber.isUnsubscribed()) {
            subscriber.onCompleted();
        }
    }

    /**
     * onStop with subscription
     *
     * @param subscription
     */
    public static void onStop(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }


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
    public static void runOnUi(RxCallBack doing) {
        new Handler(Looper.getMainLooper()).post(doing::execute);
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

    public interface RxCallBack {
        /**
         * Callback
         */
        void execute();
    }

}
