package me.yeojoy.rxjava2.rx;

import android.view.View;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by yeojoy on 2017. 12. 10..
 */

public class RxBinding {
    private static final String TAG = RxBinding.class.getSimpleName();

    private static final long DEFAULT_LOCK_TIME = 1000;
    private static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.MILLISECONDS;

    public static Observable<View> clicks(View view) {
        return Observable.create(new ViewClickOnSubscribe(view));
    }

    public static Observable<View> clicksThrottleFirst(View view) {
        return clicks(view)
                .throttleFirst(DEFAULT_LOCK_TIME, DEFAULT_TIME_UNIT, AndroidSchedulers.mainThread());
    }

    public static Disposable clicksThrottleFirst(View view, Consumer<View> onNext) {
        return clicksThrottleFirst(view)
                .subscribe(onNext);
    }

//    @SafeVarargs
//    public static Disposable mergeThrottleFirst(Consumer<View> onNext, Observable<View>... observables) {
//        return Observable.just(observables)
//                .throttleFirst(DEFAULT_LOCK_TIME, DEFAULT_TIME_UNIT, AndroidSchedulers.mainThread())
//                .subscribe(onNext);
//    }
}
