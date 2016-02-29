package com.posteet.sample.logintemplate_android;

import android.content.Context;

import com.posteet.sample.logintemplate_android.LoginTemplate.ILogin;
import com.posteet.sample.logintemplate_android.LoginTemplate.LoginFactory;
import com.posteet.sample.logintemplate_android.LoginTemplate.LoginFactory.LoginType;

/**
 * Created by thomas on 16. 2. 29..
 */
public final class LoginController {

    private static LoginController instance;
    private final Context context;
    private ILogin login;

    public static LoginController from(Context context) {
        if (instance == null || instance.context != context) {
            instance = new LoginController(context);
        }

        return instance;
    }

    private LoginController(Context context) {
        this.context = context;

        setup(LoginType.None);
    }

    public ILogin setup(LoginType type) {
        switch (type) {
            case Naver:
                login = LoginFactory.createLogin(context, type, "W2R5FeAAr59WAKuuMxJO", "nzAhmB6spE");
                break;
            case Instagram:
                login = LoginFactory.createLogin(context, type, "54606716acba41acaa47eb2815049248", null, "adobeone4login://auth");
                break;
            default:
                login = LoginFactory.createLogin(context, type);
                break;
        }

        return login;
    }

    public ILogin getCurrentLogin() {
        return login;
    }
}
