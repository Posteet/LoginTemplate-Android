package com.posteet.sample.logintemplate_android.LoginTemplate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.data.OAuthLoginState;
import com.posteet.sample.logintemplate_android.LoginTemplate.LoginFactory.LoginType;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

/**
 * Created by thomas on 16. 2. 29..
 */
public class NaverLogin implements ILogin {

    private final Context context;
    private final OAuthLogin loginModule;

    NaverLogin(Context context, String clientId, String clientSecret) {
        this.context = context;

        final String clientName = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
        this.loginModule = OAuthLogin.getInstance();
        this.loginModule.init(context, clientId, clientSecret, clientName);
    }

    @Override
    public String getName() {
        return LoginType.Naver.name();
    }

    @Override
    public boolean isLogin() {
        return loginModule.getState(context) == OAuthLoginState.OK;
    }

    @Override
    public void login(Activity callerActivity, final OnLoginCompletion loginCompletion) {
        loginModule.startOauthLoginActivity(callerActivity, new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                if (loginCompletion == null) {
                    return;
                }

                if (success) {
                    final AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {

                        @Override
                        protected String doInBackground(Void... params) {
                            return loginModule.requestApi(context, loginModule.getAccessToken(context), "https://openapi.naver.com/v1/nid/getUserProfile.xml");
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            super.onPostExecute(result);

                            UserInfo userInfo = null;
                            LoginError error = null;
                            try {
                                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                                XmlPullParser parser = factory.newPullParser();
                                parser.setInput(new StringReader(result));

                                String id = "", name = "", email = "";
                                while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
                                    if (parser.getEventType() == XmlPullParser.START_TAG) {
                                        if (parser.getName().equals("id")) {
                                            id = parser.nextText();
                                        } else if (parser.getName().equals("name")) {
                                            name = parser.nextText();
                                        } else if (parser.getName().equals("email")) {
                                            email = parser.nextText();
                                        }
                                    }
                                    parser.next();
                                }

                                userInfo = new UserInfo(id, name, email);
                            } catch (Exception e) {
                                error = new LoginError(e.getLocalizedMessage());
                            }

                            loginCompletion.onLoginCompleted(userInfo, error);
                        }
                    };
                    asyncTask.execute();
                } else {
                    loginCompletion.onLoginCompleted(null, new LoginError(loginModule.getLastErrorDesc(context)));
                }
            }
        });
    }

    @Override
    public void logout() {
        loginModule.logout(context);
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
