package com.cesc.mrbd.services;

import android.content.Context;
import android.util.Log;

import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.utils.AppPreferences;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Piyush on 25-02-2017.
 */

public class FirebaseIDService extends FirebaseInstanceIdService
{

    private static final String TAG = "FirebaseIDService";
    public String refreshedToken = "";
    private Context mContext;
    @Override
    public void onTokenRefresh()
    {
        mContext = getApplication();
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
//        Log.i(TAG, "Refreshed token: " + refreshedToken);
        AppPreferences.getInstance(mContext).putString(AppConstants.FCM_KEY, refreshedToken);
    }

}
