package com.posteet.sample.logintemplate_android.LoginTemplate;

/**
 * Created by thomas on 16. 2. 26..
 */

public class UserInfo {

    public final String id;
    public final String name;
    public final String email;

    public UserInfo(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
