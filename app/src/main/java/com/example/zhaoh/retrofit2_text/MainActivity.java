package com.example.zhaoh.retrofit2_text;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_hello:
                ApiService apiService = Utils.create(ApiService.class);
                String username = "1888888888";
                String password = "888888";
                apiService.register("register", username, password)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(() -> Timber.e("asd"))
                        .doOnSubscribe(() -> Timber.e("doOnSubscribe"))
                        .doAfterTerminate(() -> Timber.e("doAfterTerminate"))
                        .filter(registerResponse -> registerResponse != null)
                        .doOnNext(registerResponse -> {
                            if (!eqreq) {
                                throw new NullPointerException();
                            }
                        })
                        .map(RegisterResponse::getMessage)
                        .subscribe(message -> {
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                        })
                        .flatMap(registerResponse -> apiService.login(username, password))
                        .subscribe(new Subscriber<LoginResponse>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(LoginResponse loginResponse) {

                            }
                        });
                break;
            default:
                break;
        }
    }
}
