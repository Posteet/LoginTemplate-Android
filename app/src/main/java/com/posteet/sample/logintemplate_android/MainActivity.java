package com.posteet.sample.logintemplate_android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.posteet.sample.logintemplate_android.LoginTemplate.ILogin;
import com.posteet.sample.logintemplate_android.LoginTemplate.ILogin.OnLoginCompletion;
import com.posteet.sample.logintemplate_android.LoginTemplate.LoginError;
import com.posteet.sample.logintemplate_android.LoginTemplate.LoginFactory.LoginType;
import com.posteet.sample.logintemplate_android.LoginTemplate.UserInfo;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextView statusTextView;
    private LoginController loginController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = (TextView) findViewById(R.id.status_text);

        loginController = LoginController.from(this);
        loginController.getCurrentLogin().handleActivityCreated(this);


        findViewById(R.id.btn_google_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginViaGoogle(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        loginController.getCurrentLogin().handleActivityResumed(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        loginController.getCurrentLogin().handleActivityPaused(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        loginController.getCurrentLogin().handleActivityDestroyed(this);
    }

    private void login(LoginType type) {
        final ILogin login = loginController.setup(type);

        if (login.isLogin()) {
            login.logout();
        }

        login.login(this, new OnLoginCompletion() {
            @Override
            public void onLoginCompleted(UserInfo userInfo, LoginError error) {
                if (userInfo != null) {
                    statusTextView.setText(login.getName() + " Login Success.\n" + userInfo.toString());
                } else if (error != null) {
                    statusTextView.setText(login.getName() + " Login Failed.\n" + error.message);
                }
            }
        });
    }

    public void onLoginViaKakao(View view) {
        login(LoginType.Kakao);
    }

    public void onLoginViaNaver(View view) {
        login(LoginType.Naver);
    }

    public void onLoginViaFacebook(View view) {
        login(LoginType.Facebook);
    }

    public void onLoginViaInstagram(View view) {
        login(LoginType.Instagram);
    }

    public void onLoginViaGoogle(View view) {
        login(LoginType.Google);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        loginController.getCurrentLogin().handleActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            final GoogleSignInAccount acct = result.getSignInAccount();
            statusTextView.setText(acct.getDisplayName() + "\n"
                    + acct.getEmail());
        } else {
            Toast.makeText(this, "Google Login failed", Toast.LENGTH_SHORT).show();
        }
    }

}