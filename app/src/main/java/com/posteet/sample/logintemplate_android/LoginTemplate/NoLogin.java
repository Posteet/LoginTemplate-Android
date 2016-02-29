package com.posteet.sample.logintemplate_android.LoginTemplate;

import android.app.Activity;
import android.content.Intent;

import com.posteet.sample.logintemplate_android.LoginTemplate.LoginFactory.LoginType;

/**
 * Created by thomas on 16. 2. 29..
 */
public class NoLogin implements ILogin {

    NoLogin() {
    }

    @Override
    public String getName() {
        return LoginType.None.name();
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public void login(Activity callerActivity, OnLoginCompletion loginCompletion) {
        if (loginCompletion != null) {
            loginCompletion.onLoginCompleted(new UserInfo("", "", ""), null);
        }
    }

    @Override
    public void logout() {
    }

    @Override
    public void handleActivityCreated(Activity activity) {
    }

    @Override
    public void handleActivityResumed(Activity activity) {
    }

    @Override
    public void handleActivityPaused(Activity activity) {
    }

    @Override
    public void handleActivityDestroyed(Activity activity) {
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return false;
    }
}
