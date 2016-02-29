package com.posteet.sample.logintemplate_android.LoginTemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.posteet.sample.logintemplate_android.LoginTemplate.LoginFactory.LoginType;

/**
 * Created by thomas on 16. 2. 29..
 */
public class GoogleLogin implements ILogin, OnConnectionFailedListener {

    private static final int GOOGLE_LOGIN = 22003;
    private OnLoginCompletion onLoginCompletion;
    private GoogleApiClient googleApiClient;

    GoogleLogin(Context context) {
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        this.googleApiClient = new GoogleApiClient.Builder(context)
                .enableAutoManage((FragmentActivity) context, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public String getName() {
        return LoginType.Google.name();
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public void login(Activity callerActivity, OnLoginCompletion loginCompletion) {
        this.onLoginCompletion = loginCompletion;

        final Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        callerActivity.startActivityForResult(signInIntent, GOOGLE_LOGIN);
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
        stopAutoManage();

        this.onLoginCompletion = null;
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GOOGLE_LOGIN) {
            final GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            return true;
        }

        return false;
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            final GoogleSignInAccount account = result.getSignInAccount();
            notifyResult(new UserInfo(account.getId(), account.getDisplayName(), account.getEmail()), null);
        } else {
            notifyResult(null, new LoginError("Google login failed."));
        }

        stopAutoManage();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        notifyResult(null, new LoginError(connectionResult.getErrorCode(), connectionResult.getErrorMessage()));
        stopAutoManage();
    }

    private void stopAutoManage() {
        if (googleApiClient != null) {
            googleApiClient.stopAutoManage((FragmentActivity) googleApiClient.getContext());
            googleApiClient = null;
        }
    }

    private void notifyResult(UserInfo userInfo, LoginError error) {
        if (onLoginCompletion != null) {
            onLoginCompletion.onLoginCompleted(userInfo, error);
            onLoginCompletion = null;
        }
    }
}
