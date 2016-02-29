package com.posteet.sample.logintemplate_android.LoginTemplate;

import android.content.Context;

/**
 * Created by thomas on 16. 2. 29..
 */
public final class LoginFactory {

    public enum LoginType {
        None, Kakao, Naver, Facebook, Instagram, Google
    }

    public static ILogin createLogin(Context context, LoginType type) {
        return createLogin(context, type, null, null, null);
    }

    public static ILogin createLogin(Context context, LoginType type, String clientId, String clientSecret) {
        return createLogin(context, type, clientId, clientSecret, null);
    }

    public static ILogin createLogin(Context context, LoginType type, String clientId, String clientSecret, String redirectUrl) {
        switch (type) {
            case Kakao:
                return new KakaoLogin(context);
            case Naver:
                return new NaverLogin(context, clientId, clientSecret);
            case Facebook:
                return new FacebookLogin();
            case Instagram:
                return new InstagramLogin(context, clientId, redirectUrl);
            case Google:
                return new GoogleLogin(context);
        }

        return new NoLogin();
    }
}
