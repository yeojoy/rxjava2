package me.yeojoy.rxjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.yeojoy.rxjava2.rx.RxBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView mTextView;
    private Button mButton1, mButton2;

    private static int mCount1 = 0, mCount2 = 0;

    private SimpleDateFormat mSimpleDateFormatter = new SimpleDateFormat("yyyy-MM-DD HH:mm:ss.SSS");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.text_view);
        mButton1 = findViewById(R.id.button_1);
        mButton2 = findViewById(R.id.button_2);

        Log.v(TAG, "Button1 ID : " + mButton1.getId() + ", Button2 ID : " + mButton2.getId());
        mTextView.setText("Button1 ID : " + mButton1.getId() + ", Button2 ID : " + mButton2.getId());

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

                            clickButton1();
                            clickButton2();
                        }
                );

//        RxBinding.clicksThrottleFirst(mButton1, this::onClickButton);
        List<Observable<View>> observables = new ArrayList<>();
        observables.add(RxBinding.clicks(mButton1));
        observables.add(RxBinding.clicks(mButton2));

        RxBinding.mergeThrottleFirst(this::onClickButton, observables);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCount1 = 0;
        mCount2 = 0;
    }

    private void clickButton1() {
        long time = (long) (Math.random() * 100);
        Log.i(TAG, "clickButton1(), time : " + time);

        mButton1.postDelayed(() -> {
            mButton1.performClick();
            Log.d(TAG, "click a button at " + mSimpleDateFormatter.format(new Date()));
            mCount1++;
            if (mCount1 < 500) {
                clickButton1();
            } else {
                mCount1 = 0;
            }
        }, time);
    }


    private void clickButton2() {
        long time = (long) (Math.random() * 100);
        Log.i(TAG, "clickButton2(), time : " + time);

        mButton2.postDelayed(() -> {
            mButton2.performClick();
            Log.d(TAG, "click a button at " + mSimpleDateFormatter.format(new Date()));
            mCount2++;
            if (mCount2 < 500) {
                clickButton2();
            } else {
                mCount2 = 0;
            }
        }, time);
    }

    private void onClickButton(View view) {
        Log.i(TAG, "view id : " + view.getId());
        String text = view.getId() + " > clicked. " + mSimpleDateFormatter.format(new Date());
        mTextView.append("\n");
        mTextView.append(text);
        Log.v(TAG, text);
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

            e.setCancellable(() -> Log.d(TAG, "onCancel() Not mainThread. cancel time ::: " + mSimpleDateFormatter.format(new Date())));
        };
    }
}
