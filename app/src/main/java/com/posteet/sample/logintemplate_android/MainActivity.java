package com.posteet.sample.logintemplate_android;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.nhn.android.naverlogin.OAuthLogin;
import com.nhn.android.naverlogin.OAuthLoginHandler;
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton;
import com.steelkiwi.instagramhelper.InstagramHelper;
import com.steelkiwi.instagramhelper.InstagramHelperConstants;
import com.steelkiwi.instagramhelper.model.InstagramUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.Arrays;

public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    private static final int GOOGLE_LOGIN = 22003;

    private CallbackManager callbackManager;
    private InstagramHelper instagramHelper;
    private TextView statusTextView;
    private ISessionCallback callback;
    private OAuthLoginButton naverLoginButton;
    private OAuthLogin naverLoginModule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusTextView = (TextView) findViewById(R.id.status_text);

        // facebook
        callbackManager = CallbackManager.Factory.create();

        // instagram
        instagramHelper = new InstagramHelper.Builder()
                .withClientId("54606716acba41acaa47eb2815049248")
                .withRedirectUrl("adobeone4login://auth")
                .build();

        findViewById(R.id.btn_google_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginViaGoogle(v);
            }
        });

        // kakao
        callback = new ISessionCallback() {
            @Override
            public void onSessionOpened() {
                requestKakaoUser();
            }

            @Override
            public void onSessionOpenFailed(KakaoException exception) {
                if (exception != null) {
                    Log.d(TAG, "onSessionOpenFailed: " + exception);
                }
            }
        };
        Session.getCurrentSession().addCallback(callback);

        // naver
        naverLoginModule = OAuthLogin.getInstance();
        naverLoginModule.init(this, "W2R5FeAAr59WAKuuMxJO", "nzAhmB6spE", getApplicationInfo().loadLabel(getPackageManager()).toString());

        naverLoginButton = (OAuthLoginButton) findViewById(R.id.btn_naver_login);
        naverLoginButton.setOAuthLoginHandler(new OAuthLoginHandler() {
            @Override
            public void run(boolean success) {
                final Context context = MainActivity.this;
                if (success) {
                    final AsyncTask<Void, Void, String> asyncTask = new AsyncTask<Void, Void, String>() {

                        @Override
                        protected String doInBackground(Void... params) {
                            return naverLoginModule.requestApi(context, naverLoginModule.getAccessToken(context), "https://openapi.naver.com/v1/nid/getUserProfile.xml");
                        }

                        @Override
                        protected void onPostExecute(String result) {
                            super.onPostExecute(result);

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

                                statusTextView.setText(id + "\n" +
                                        name + "\n" +
                                        email);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    asyncTask.execute();
                } else {
                    Toast.makeText(context, "Naver Login failed : " + naverLoginModule.getLastErrorDesc(context), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Session.getCurrentSession().removeCallback(callback);
    }

    public void onLoginViaFacebook(View view) {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                requestFacebookUser(loginResult);
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });
    }

    public void onLoginViaInstagram(View view) {
        instagramHelper.loginFromActivity(this);
//        jeongthomas5491
    }

    public void onLoginViaGoogle(View view) {
        final GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        final GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, GOOGLE_LOGIN);
    }

    private void requestKakaoUser() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                statusTextView.setText(userProfile.getId() + "\n"
                        + userProfile.getNickname());
            }

            @Override
            public void onNotSignedUp() {
            }
        });
    }

    private void requestFacebookUser(LoginResult loginResult) {
        final Bundle parameters = new Bundle();
        parameters.putString("fields", "id, name, email");

        final GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    statusTextView.setText(object.getString("id") + "\n" +
                            object.getString("name") + "\n" +
                            object.getString("email"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == InstagramHelperConstants.INSTA_LOGIN) {
            if (resultCode == RESULT_OK) {
                InstagramUser user = instagramHelper.getInstagramUser(this);
                statusTextView.setText(user.getData().getUsername() + "\n"
                        + user.getData().getFullName() + "\n"
                        + user.getData().getWebsite());

            } else {
                Toast.makeText(this, "Instagram Login failed", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
                // nothing to do;
            } else {
                callbackManager.onActivityResult(requestCode, resultCode, data);
            }
        }
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Login failed - " + connectionResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
    }
}