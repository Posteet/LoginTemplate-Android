package com.posteet.sample.logintemplate_android;

import android.support.v7.app.AppCompatActivity;

import com.facebook.appevents.AppEventsLogger;

/**
 * Created by thomas on 16. 2. 26..
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        GlobalApplication.setCurrentActivity(this);
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        GlobalApplication.setCurrentActivity(null);
        AppEventsLogger.deactivateApp(this);
    }
}
