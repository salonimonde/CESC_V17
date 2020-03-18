package com.cesc.mrbd.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.cesc.mrbd.R;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.utils.AppPreferences;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.Locale;


public class SplashActivity extends ParentActivity
{
    private Context mContext;
    private Locale myLocale;
    private String language = "", screenToOpen = "";
    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mContext = this;
        screenToOpen = AppPreferences.getInstance(mContext).getString(AppConstants.SCREEN_FROM_EXIT, "");
        AppPreferences.getInstance(mContext).putString(AppConstants.FILTER_BINDER, "All");
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                moveNext();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void moveNext()
    {
        Intent intent = null;
        if(CommonUtils.isLoggedIn(mContext))
        {
            if(screenToOpen.equals(getString(R.string.bill_distribution)))
            {
                intent = new Intent(mContext, BillDistributionLandingScreen.class);
            }
            else if(screenToOpen.equals(getString(R.string.payment)))
            {
                intent = new Intent(mContext, MyPaymentActivity.class);
            }
            else if(screenToOpen.equals(getString(R.string.disconnection)))
            {
                intent = new Intent(mContext, DisconnectionNoticeLandingActivity.class);
            }
            else
            {
                intent = new Intent(mContext, LandingActivity.class);
            }
        }
        else
        {
            intent = new Intent(mContext, LoginActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        overridePendingTransition(R.anim.slide_no_change, R.anim.slide_up);
        startActivity(intent);
        finish();
    }
}
