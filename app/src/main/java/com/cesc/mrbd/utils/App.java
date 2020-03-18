package com.cesc.mrbd.utils;

import android.Manifest;
import android.graphics.Typeface;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;

import java.util.ArrayList;

import io.fabric.sdk.android.Fabric;

/**
 * Created by Bynry01 on 22-08-2016.
 */
public class App extends MultiDexApplication {

    public static final String TAG = App.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private static App mInstance;
    public ArrayList<String> permissions;
    private static Typeface mRegularType, mBoldType;
    public static boolean welcome = false;
    public static boolean sms = false;
    public static String ReadingTakenBy;
    public static String ConsumerAddedBy;

    @Override
    public void onCreate() {
        super.onCreate();
//        Fabric.with(this, new Crashlytics());
        MultiDex.install(this);
        mInstance = this;
        permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.READ_PHONE_STATE);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.CALL_PHONE);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);

        mRegularType = Typeface.createFromAsset(App.getInstance().getAssets(), "fonts/Sansation_Regular_0.ttf");
        mBoldType = Typeface.createFromAsset(App.getInstance().getAssets(), "fonts/Sansation_Bold_0.ttf");
    }

    public static synchronized App getInstance() {
        return mInstance;
    }

    public static Typeface getSansationRegularFont() {
        return mRegularType;
    }

    public static Typeface getSansationBoldFont() {
        return mBoldType;
    }

    public static boolean getStatus() {
        return sms;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
