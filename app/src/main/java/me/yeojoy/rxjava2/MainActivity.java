package me.yeojoy.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.yeojoy.rxjava2.rx.RxBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mTextView;
    private Button mButton;

    private static int mCount = 0;

    private SimpleDateFormat mSimpleDateFormatter = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss.SSS");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.text_view);
        mButton = findViewById(R.id.button);

        Observable.create(getObservable())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(str -> {
                            mTextView.append("\n");
                            mTextView.append(str);
                            Log.d(TAG, "onNext() time ::: " + mSimpleDateFormatter.format(new Date()));
                        }, throwable -> {
                            mTextView.append("\n");
                            mTextView.append("throw throable!");
                            Log.d(TAG, "onError() time ::: " + mSimpleDateFormatter.format(new Date()));
                        },
                        () -> {
                            mTextView.append("\n");
                            mTextView.append("onComplete()");
                            Log.d(TAG, "onComplete() time ::: " + mSimpleDateFormatter.format(new Date()));

                            clickButton();
                        }
                );

        RxBinding.clicksThrottleFirst(mButton, this::onClickButton);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCount = 0;
    }

    private void clickButton() {
        Log.i(TAG, "clickButton()");

        mTextView.postDelayed(() -> {
            mButton.performClick();
            Log.d(TAG, "click a button at " + mSimpleDateFormatter.format(new Date()));
            mCount++;
            if (mCount < 60) {
                clickButton();
            } else {
                mCount = 0;
            }
        }, 50L);
    }

    private void onClickButton(View view) {
        Log.i(TAG, "Name : " + view.getClass().getSimpleName());
        mTextView.append("\n");
        mTextView.append("clicked.");
        Log.v(TAG, "clicked. " + mSimpleDateFormatter.format(new Date()));
    }

    private ObservableOnSubscribe<String> getObservable() {

        return e -> {

            // onComplete 이후 UI 작업을 실행해 주면 된다. vero꺼 수정 중.
            e.setDisposable(new MainThreadDisposable() {
                @Override
                protected void onDispose() {
                    mTextView.append("\n");
                    mTextView.append("onDispose().");
                    Log.d(TAG, "onDispose() time ::: " + mSimpleDateFormatter.format(new Date()));
                }
            });

            Thread.sleep(1500L);
            e.onNext("Hello,");
            Thread.sleep(1500L);
            e.onNext("Hi!");
            Thread.sleep(1500L);
            e.onNext("World!");
            Thread.sleep(1500L);
//            e.onError(new NullPointerException("oops! cause NullPointerException!!!"));
            e.onComplete();

            e.setCancellable(() -> {
                Log.d(TAG, "onCancel() Not mainThread. cancel time ::: " + mSimpleDateFormatter.format(new Date()));
            });
        };
    }
}
