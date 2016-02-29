package com.posteet.sample.logintemplate_android.LoginTemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kakao.auth.AuthType;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.posteet.sample.logintemplate_android.LoginTemplate.LoginFactory.LoginType;

/**
 * Created by thomas on 16. 2. 29..
 */
public class KakaoLogin implements ILogin {
    private static final String TAG = "KakaoLogin";

    private Activity currentActivity;
    private ISessionCallback callback;
    private OnLoginCompletion onLoginCompletion;

    KakaoLogin(final Context context) {
        if (KakaoSDK.getAdapter() == null) {
            KakaoSDK.init(new KakaoAdapter() {
                @Override
                public IApplicationConfig getApplicationConfig() {
                    return new IApplicationConfig() {
                        @Override
                        public Activity getTopActivity() {
                            return currentActivity;
                        }

                        @Override
                        public Context getApplicationContext() {
                            return context;
                        }
                    };
                }
            });
        }
    }

    @Override
    public String getName() {
        return LoginType.Kakao.name();
    }

    @Override
    public boolean isLogin() {
        return Session.getCurrentSession().isOpened();
    }

    @Override
    public void login(Activity callerActivity, OnLoginCompletion loginCompletion) {
        onLoginCompletion = loginCompletion;
        callback = new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                requestKakaoUser();
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                LoginError error = null;

                if (exception != null) {
                    error = new LoginError(exception.getLocalizedMessage());
                    Log.d(TAG, "onSessionOpenFailed: " + exception);
                }

                notifyResult(null, error);
            }
        };

        Session.getCurrentSession().addCallback(callback);
        Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, callerActivity);
    }

    @Override
    public void logout() {
        Session.getCurrentSession().close();
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void handleActivityCreated(Activity activity) {
    }

    @Override
    public void handleActivityResumed(Activity activity) {
        currentActivity = activity;
    }

    @Override
    public void handleActivityPaused(Activity activity) {
        if (currentActivity == activity) {
            currentActivity = null;
        }
    }

    @Override
    public void handleActivityDestroyed(Activity activity) {
        Session.getCurrentSession().removeCallback(callback);
        callback = null;
        onLoginCompletion = null;
    }

    private void requestKakaoUser() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                notifyResult(null, new LoginError(errorResult.getErrorCode(), errorResult.getErrorMessage()));
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                notifyResult(null, new LoginError(errorResult.getErrorCode(), errorResult.getErrorMessage()));
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                notifyResult(new UserInfo(String.valueOf(userProfile.getId()), userProfile.getNickname(), ""), null);
            }

            @Override
            public void onNotSignedUp() {
                notifyResult(null, new LoginError("Kakao User is not signed up."));
            }
        });
    }

    private void notifyResult(UserInfo userInfo, LoginError error) {
        if (onLoginCompletion != null) {
            onLoginCompletion.onLoginCompleted(userInfo, error);
            onLoginCompletion = null;
        }
    }
}
