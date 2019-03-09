package com.keepyoga.vipyoga;

import android.app.Application;
import android.support.annotation.Nullable;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import io.realm.Realm;
import com.keepyoga.vipyoga.utils.Toaster;

public class App extends Application {

    private static App sApp;

    private static Toaster sToaster;

    public static String sMonoToken = null;

    public static App getInstance() {
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp = this;
        Fresco.initialize(getApplicationContext());
        Realm.init(getApplicationContext());
        Logger.addLogAdapter(new AndroidLogAdapter() {
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                if (!BuildConfig.DEBUG) {
                    return false;
                }
                return super.isLoggable(priority, tag);
            }
        });
    }

    public static void showToast(String msg) {
        if (sToaster == null)
            sToaster = new Toaster();
        sToaster.showToast(msg);
    }

    public static void showToast(int resId) {
        showToast(App.getInstance().getString(resId));
    }

}
