package com.posteet.sample.logintemplate_android.LoginTemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.posteet.sample.logintemplate_android.LoginTemplate.LoginFactory.LoginType;
import com.steelkiwi.instagramhelper.InstagramHelper;
import com.steelkiwi.instagramhelper.InstagramHelperConstants;
import com.steelkiwi.instagramhelper.model.Data;

/**
 * Created by thomas on 16. 2. 29..
 */
public class InstagramLogin implements ILogin {

    private final Context context;
    private final InstagramHelper helper;
    private OnLoginCompletion onLoginCompletion;

    InstagramLogin(Context context, String clientId, String redirectUrl) {
        this.context = context;
        this.helper = new InstagramHelper.Builder()
                .withClientId(clientId)
                .withRedirectUrl(redirectUrl)
                .build();
    }

    @Override
    public String getName() {
        return LoginType.Instagram.name();
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public void login(Activity callerActivity, OnLoginCompletion loginCompletion) {
        this.onLoginCompletion = loginCompletion;
        helper.loginFromActivity(callerActivity);
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
        if (requestCode == InstagramHelperConstants.INSTA_LOGIN) {
            UserInfo userInfo = null;
            LoginError error = null;

            if (resultCode == Activity.RESULT_OK) {
                final Data userData = helper.getInstagramUser(context).getData();
                userInfo = new UserInfo(userData.getUsername(), userData.getFullName(), "");
            } else {
                error = new LoginError("Instagram Login failed.");
            }

            if (onLoginCompletion != null) {
                onLoginCompletion.onLoginCompleted(userInfo, error);
                onLoginCompletion = null;
            }

            return true;
        }

        return false;
    }
}
