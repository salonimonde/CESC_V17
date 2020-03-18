package com.cesc.mrbd.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cesc.mrbd.R;
import com.cesc.mrbd.callers.ServiceCaller;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.JsonResponse;
import com.cesc.mrbd.models.UserProfile;
import com.cesc.mrbd.preferences.SharedPrefManager;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.AppPreferences;
import com.cesc.mrbd.utils.CommonUtils;
import com.cesc.mrbd.utils.DialogCreator;
import com.cesc.mrbd.utils.LocationManagerReceiver;
import com.cesc.mrbd.webservice.WebRequests;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends ParentActivity implements ServiceCaller, OnClickListener {
    // UI references.
    private Context mContext;
    private EditText mEmailView;
    private EditText mPasswordView;
    private String user_email;
    private RelativeLayout rl_login_view;
    private Typeface regular;
    private TextView Forget;
    private ProgressDialog pDialog;
    private String deviceToken = "";
    private UserProfile userProfile;
    private int versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        regular = App.getSansationRegularFont();
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        setupUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CommonUtils.askForPermissions(this, rl_login_view, App.getInstance().permissions);
        }
    }

    private void initProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(mContext);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
        }
    }

    private void dismissDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    // Set up the login form.
    private void setupUI() {
        initProgressDialog();
        mEmailView = (EditText) findViewById(R.id.act_email);
        mEmailView.setTypeface(regular);
        mEmailView.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        rl_login_view = (RelativeLayout) findViewById(R.id.rl_login_view);
        mPasswordView = (EditText) findViewById(R.id.ed_password);
        mPasswordView.setTypeface(regular);
        Forget = (TextView) findViewById(R.id.forget);
        Forget.setTypeface(regular);
        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setTypeface(regular);
        mEmailSignInButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.email_sign_in_button:
                CommonUtils.hideKeyBoard(this);
                performLogin();
                break;
        }
    }

    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label) {
        switch (label) {
            case AppConstants.REQUEST_LOGIN: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.responsedata != null && jsonResponse.responsedata.user_info != null && jsonResponse.responsedata.user_info.size() > 0) {
                            dismissDialog();

                            deviceToken = AppPreferences.getInstance(mContext).getString(AppConstants.FCM_KEY, "");

                            String fcmToken = "";
                            if (!deviceToken.isEmpty()) {
                                fcmToken = deviceToken;
                            } else {
                                userProfile = DatabaseManager.getUserProfile(this, SharedPrefManager.getStringValue(this, SharedPrefManager.USER_ID));
                                if (userProfile != null) {
                                    fcmToken = userProfile.fcm_token;
                                }
                            }
                            CommonUtils.saveCredentials(this, user_email, jsonResponse.authorization);
                            DatabaseManager.saveLoginDetails(this, user_email, jsonResponse.responsedata.user_info, fcmToken);

                            String mrId = mEmailView.getText().toString().trim();
                            JsonObjectRequest request = WebRequests.updateDeviceToken(this, Request.Method.POST, AppConstants.URL_UPDATE_DEVICE_TOKEN,
                                    AppConstants.UPDATE_DEVICE_TOKEN, this, mrId, fcmToken, String.valueOf(versionCode));
                            App.getInstance().addToRequestQueue(request, AppConstants.UPDATE_DEVICE_TOKEN);

                            App.welcome = true;
                            showMainActivity();
                        }
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        dismissDialog();
                        DialogCreator.showMessageDialog(this, jsonResponse.message != null ? jsonResponse.message : getString(R.string.er_please_contact_server_admin), getString(R.string.error));
                    }
                } else
                    Toast.makeText(this,
                            R.string.er_data_not_avaliable, Toast.LENGTH_LONG).show();
            }
            break;
            case AppConstants.UPDATE_DEVICE_TOKEN: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        System.out.println("Update Device Token Api Success");
                    }

                } else
                    System.out.println("Update Device Token Api Fails");

            }
            break;
        }
        dismissDialog();
    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response) {
        switch (label) {
            case AppConstants.REQUEST_LOGIN: {
                try {
                    String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    if (res != null) {
                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(res, JsonResponse.class);
                        if (jsonResponse.responsedata.error_code.equals("101")) {
                            DialogCreator.showMessageDialog(this, getString(R.string.login_error_101), getString(R.string.error));
                        } else if (jsonResponse.responsedata.error_code.equals("102")) {
                            DialogCreator.showMessageDialog(this, getString(R.string.login_error_102), getString(R.string.error));
                        } else if (jsonResponse.responsedata.error_code.equals("103")) {
                            DialogCreator.showMessageDialog(this, getString(R.string.login_error_103), getString(R.string.error));
                        }
                    }
                } catch (Exception e) {
                    DialogCreator.showMessageDialog(this, getString(R.string.login_error_null), getString(R.string.error));
                    e.printStackTrace();
                }
                dismissDialog();
                break;
            }
            case AppConstants.DEVICE_FCM_TOKEN: {
                System.out.println("Update Device Token Api Fails Completely");
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void performLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        user_email = mEmailView.getText().toString().trim();
        String user_password = mPasswordView.getText().toString().trim();

        // Check for a valid password, if the user entered one.
        if (CommonUtils.isNetworkAvaliable(this) == true) {
            if (!TextUtils.isEmpty(user_email)) {
                if (isEmailValid(user_email)) {
                    if (!TextUtils.isEmpty(user_password)) {
                        if (isPasswordValid(user_password)) {
                            if (pDialog != null && !pDialog.isShowing()) {
                                pDialog.setMessage(getString(R.string.logging_in_please_wait));
                                pDialog.show();
                            }
                            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//                            String imeiNumber = telephonyManager.getDeviceId();
                            String imeiNumber = /*"911555000227422"*/"867453048699279";
                            JsonObjectRequest request = WebRequests.loginRequest(this, Request.Method.POST, AppConstants.URL_LOGIN, AppConstants.REQUEST_LOGIN, this, user_email, user_password, imeiNumber);
                            App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_LOGIN);
                        } else
                            mPasswordView.setError(getString(R.string.error_invalid_password));
                    } else
                        mPasswordView.setError(getString(R.string.error_empty_password));
                } else
                    mEmailView.setError(getString(R.string.error_invalid_email));
            } else
                mEmailView.setError(getString(R.string.error_field_required));
        } else
            DialogCreator.showMessageDialog(mContext, getString(R.string.error_internet_not_connected), getString(R.string.error));
    }


    private void showMainActivity() {
        Intent i = new Intent(LoginActivity.this, LandingActivity.class);
        startActivity(i);
        finish();
    }

    private boolean isEmailValid(String email) {
//        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
//        return email.matches(emailPattern);
        if (email.length() <= 8 && email.length() > 0)
            return true;
        else
            return false;

    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case AppConstants.ALL_PERMISSIONS_RESULT:
                boolean someAccepted = false;
                boolean someRejected = false;
                ArrayList<String> permissionsRejected = new ArrayList<String>();
                for (String perms : App.getInstance().permissions) {
                    if (CommonUtils.hasPermission(this, perms)) {
                        someAccepted = true;
                    } else {
                        someRejected = true;
                        permissionsRejected.add(perms);
                    }
                }
                if (permissionsRejected.size() > 0) {
                    someRejected = true;
                }
                if (someRejected) {
                    CommonUtils.showPostPermissionsShackBar(this, rl_login_view, permissionsRejected);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        DialogCreator.showExitDialog(this, getString(R.string.exit), getString(R.string.exit_message), getString(R.string.login_screen));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case AppConstants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        LocationManagerReceiver locationManagerReceiver = new LocationManagerReceiver(this);
                        break;
                }
                break;
        }
    }
}

