package com.posteet.sample.logintemplate_android.LoginTemplate;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by thomas on 16. 2. 29..
 */
public interface ILogin {

    String getName();

    boolean isLogin();
    void login(Activity callerActivity, OnLoginCompletion loginCompletion);
    void logout();

    void handleActivityCreated(Activity activity);
    void handleActivityResumed(Activity activity);
    void handleActivityPaused(Activity activity);
    void handleActivityDestroyed(Activity activity);

    boolean handleActivityResult(int requestCode, int resultCode, Intent data);

    interface OnLoginCompletion {
        void onLoginCompleted(UserInfo userInfo, LoginError error);
    }
}

