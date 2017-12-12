package me.yeojoy.rxjava2.rx;

import android.view.View;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;

/**
 * Created by yeojoy on 2017. 12. 10..
 */

public class ViewClickOnSubscribe implements ObservableOnSubscribe<View> {

    private final View mView;

    ViewClickOnSubscribe(View view) {
        mView = view;
    }

    @Override
    public void subscribe(ObservableEmitter<View> e) throws Exception {
        View.OnClickListener clickListener = v -> {
            if (!e.isDisposed()) {
                e.onNext(mView);
            }
        };

        mView.setOnClickListener(clickListener);

        e.setDisposable(new MainThreadDisposable() {
            @Override
            protected void onDispose() {
                mView.setOnClickListener(null);
            }
        });
    }
}
