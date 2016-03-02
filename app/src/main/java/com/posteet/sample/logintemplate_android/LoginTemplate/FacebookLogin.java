package com.posteet.sample.logintemplate_android.LoginTemplate;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.posteet.sample.logintemplate_android.LoginTemplate.LoginFactory.LoginType;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by thomas on 16. 2. 29..
 */
public class FacebookLogin implements ILogin {

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private OnLoginCompletion onLoginCompletion;
    private boolean isActivation;

    FacebookLogin() {
    }

    @Override
    public String getName() {
        return LoginType.Facebook.name();
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public void login(Activity callerActivity, OnLoginCompletion loginCompletion) {
        if (!FacebookSdk.isInitialized()) {
            FacebookSdk.sdkInitialize(callerActivity.getApplicationContext());
        }

        this.onLoginCompletion = loginCompletion;
        this.callbackManager = CallbackManager.Factory.create();
        this.loginManager = LoginManager.getInstance();

        if (!isActivation) {
            AppEventsLogger.activateApp(callerActivity);
        }

        loginManager.logInWithReadPermissions(callerActivity, Arrays.asList("public_profile", "email"));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestFacebookUser(loginResult);
            }

            @Override
            public void onCancel() {
                notifyResult(null, new LoginError("User cancelled facebook login."));
            }

            @Override
            public void onError(FacebookException error) {
                notifyResult(null, new LoginError(error.getLocalizedMessage()));
            }
        });
    }

    @Override
    public void logout() {
        loginManager.logOut();
    }

    @Override
    public void handleActivityCreated(Activity activity) {

    }

    @Override
    public void handleActivityResumed(Activity activity) {
        AppEventsLogger.activateApp(activity);
        isActivation = true;
    }

    @Override
    public void handleActivityPaused(Activity activity) {
        AppEventsLogger.deactivateApp(activity);
        isActivation = false;
    }

    @Override
    public void handleActivityDestroyed(Activity activity) {
        onLoginCompletion = null;
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void requestFacebookUser(LoginResult loginResult) {
        final Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email");

        final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    notifyResult(new UserInfo(object.getString("id"), object.getString("name"), object.getString("email")), null);
                } catch (JSONException e) {
                    notifyResult(null, new LoginError(e.getLocalizedMessage()));
                }
            }
        });

        request.setParameters(parameters);
        request.executeAsync();
    }

    private void notifyResult(UserInfo userInfo, LoginError error) {
        if (onLoginCompletion != null) {
            onLoginCompletion.onLoginCompleted(userInfo, error);
            onLoginCompletion = null;
        }
    }
}
