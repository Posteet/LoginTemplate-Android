package com.posteet.sample.logintemplate_android.LoginTemplate;

/**
 * Created by thomas on 16. 2. 29..
 */
public class LoginError {
    public static final int CODE_LOGIN_FAILED = -123;

    public final int code;
    public final String message;

    LoginError(String message) {
        this(CODE_LOGIN_FAILED, message);
    }

    LoginError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
