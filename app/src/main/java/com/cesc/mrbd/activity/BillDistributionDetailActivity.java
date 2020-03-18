package com.cesc.mrbd.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesc.mrbd.R;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.BillCard;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.AppPreferences;
import com.cesc.mrbd.utils.CommonUtils;
import com.cesc.mrbd.utils.LocationManagerReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Timer;
import java.util.TimerTask;

public class BillDistributionDetailActivity extends ParentActivity implements View.OnClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private TextView lblConsumersReceived, txtConsumersReceived, lblBillMonth, txtBillMonth, lblSubDivisionName, txtSubDivisionName, txtcycleName, lblBinderNo, txtBinderNo, lblConsumers, lblcycleName,
            txtConsumers, txtIsCompleted;
    private EditText edtDistributed, edtRemark;
    private RadioButton radioYes, radioNo, radioYes1, radioNo1;
    private RadioGroup radioYesno, radioYesno1;
    private ImageView imgBack, imgMap, imgadd;
    private Button btnSubmit;
    private BillCard mBillCard;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 500; // 10 sec
    private static int FATEST_INTERVAL = 500; // 5 sec
    private static int DISPLACEMENT = 0; // 1 meters
    private RelativeLayout rl_radio1;

    Timer timer;
    long timerInterval = 1000; //1 second
    long timerDelay = 1000; //1 second
    int Count = 0;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_distribution_detail);
        mContext = this;
        startTimer();

        Intent i = this.getIntent();
        if (i != null) {
            if (mBillCard == null) {
                mBillCard = (BillCard) i.getSerializableExtra(AppConstants.CURRENT_BIll_CARD);
            }
        }
        AppPreferences.getInstance(mContext).putString(AppConstants.DESTINATION_LAT, mBillCard.prv_lon);
        AppPreferences.getInstance(mContext).putString(AppConstants.DESTINATION_LONG, mBillCard.prv_lat);
        Typeface regular = App.getSansationRegularFont();
        if (checkPlayServices()) {
            buildGoogleApiClient();
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        lblBillMonth = (TextView) findViewById(R.id.lbl_bill_month);
        lblBillMonth.setTypeface(regular);
        lblBillMonth.setText(mBillCard.billmonth);
        lblSubDivisionName = (TextView) findViewById(R.id.lbl_sub_division_name);
        lblSubDivisionName.setTypeface(regular);
        lblSubDivisionName.setText(mBillCard.cycle_code + " - " + mBillCard.binder_code);
        lblcycleName = (TextView) findViewById(R.id.lbl_sub_division_name);
        lblcycleName.setTypeface(regular);
        lblBinderNo = (TextView) findViewById(R.id.lbl_binder_no);
        lblBinderNo.setTypeface(regular);
        lblConsumers = (TextView) findViewById(R.id.lbl_consumers);
        lblConsumers.setTypeface(regular);
        lblConsumersReceived = (TextView) findViewById(R.id.lbl_consumers_recevied);
        lblConsumersReceived.setTypeface(regular);
        imgMap = (ImageView) findViewById(R.id.img_map);
        imgMap.setOnClickListener(this);
        imgadd = (ImageView) findViewById(R.id.img_add);
        imgadd.setOnClickListener(this);

        rl_radio1 = (RelativeLayout) findViewById(R.id.rl_radio1);

        txtConsumersReceived = (TextView) findViewById(R.id.txt_consumers_recevied);
        txtConsumersReceived.setTypeface(regular);
        txtConsumersReceived.setText(mBillCard.consumer_name);

        txtBillMonth = (TextView) findViewById(R.id.txt_bill_month);
        txtBillMonth.setTypeface(regular);
        txtBillMonth.setText(mBillCard.zone_name);

        txtSubDivisionName = (TextView) findViewById(R.id.txt_sub_division_name);
        txtSubDivisionName.setTypeface(regular);
        txtSubDivisionName.setText(mBillCard.consumer_no);

        txtcycleName = (TextView) findViewById(R.id.txt_cycle_name);
        txtcycleName.setTypeface(regular);
        txtcycleName.setText(mBillCard.cycle_code);

        txtBinderNo = (TextView) findViewById(R.id.txt_binder_no);
        txtBinderNo.setTypeface(regular);
        txtBinderNo.setText(mBillCard.binder_code);

        txtConsumers = (TextView) findViewById(R.id.txt_consumers);
        txtConsumers.setTypeface(regular);
        txtConsumers.setText(mBillCard.consumer_no);

        txtIsCompleted = (TextView) findViewById(R.id.txt_is_completed);
        txtIsCompleted.setTypeface(regular);

        edtDistributed = (EditText) findViewById(R.id.edt_distributed);
        edtDistributed.setTypeface(regular);

        edtRemark = (EditText) findViewById(R.id.edt_remark);
        edtRemark.setTypeface(regular);

        radioYesno = (RadioGroup) findViewById(R.id.rg_yesno);
        radioYes = (RadioButton) findViewById(R.id.radio_yes);
        radioYes.setTypeface(regular);
        radioYes.setOnClickListener(this);
        radioNo = (RadioButton) findViewById(R.id.radio_no);
        radioNo.setTypeface(regular);
        radioNo.setOnClickListener(this);

        radioYesno1 = (RadioGroup) findViewById(R.id.rg_yesno1);
        radioYes1 = (RadioButton) findViewById(R.id.radio_yes1);
        radioYes1.setTypeface(regular);
        radioYes1.setOnClickListener(this);
        radioNo1 = (RadioButton) findViewById(R.id.radio_no1);
        radioNo1.setTypeface(regular);
        radioNo1.setOnClickListener(this);
        radioNo1.setChecked(true);

        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setTypeface(regular);
        btnSubmit.setOnClickListener(this);

        rl_radio1.setVisibility(View.GONE);
        edtRemark.setVisibility(View.GONE);

        LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        PackageManager pm = mContext.getPackageManager();
        boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        if (isGPSEnabled && hasGps) {
            createLocationRequest();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkPlayServices();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(mContext, "This device is not supported.", Toast.LENGTH_LONG).show();
            }
            return false;
        }

        return true;
    }

    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onClick(View v) {
        if (v == radioYes) {
            rl_radio1.setVisibility(View.VISIBLE);
            edtRemark.setVisibility(View.VISIBLE);
            radioNo1.setChecked(true);
        } else if (v == radioNo) {
            radioNo1.setChecked(true);
            rl_radio1.setVisibility(View.GONE);
            edtRemark.setVisibility(View.GONE);

        } else if (v == radioYes1) {
            edtRemark.setVisibility(View.GONE);

        } else if (v == radioNo1) {
            edtRemark.setVisibility(View.VISIBLE);

        } else if (v == btnSubmit) {
            createLocationRequest();
            startLocationUpdates();
            if (edtRemark.getVisibility() == View.VISIBLE)
                if (edtRemark.getText().toString().trim().length() > 0)
                    submitBtnClicked();
                else
                    Toast.makeText(this, "Please Enter Remark", Toast.LENGTH_SHORT).show();
            else
                submitBtnClicked();

        } else if (v == imgBack) {
            stopTimer();
            finish();
        }
        if (v == imgMap) {
            Intent intent = new Intent(mContext, GoogleMapActivity.class);
            startActivity(intent);
        }
        if (v == imgadd) {
            showMessageDialog(mContext, mBillCard.address);
        }
    }

    @SuppressLint("MissingPermission")
    private void submitBtnClicked() {

        mBillCard.time_taken = stopTimer();

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (radioYesno.getCheckedRadioButtonId() == R.id.radio_yes)
            mBillCard.bill_received = "true";
        else if (radioYesno.getCheckedRadioButtonId() == R.id.radio_no)
            mBillCard.bill_received = "false";

        if (radioYesno1.getCheckedRadioButtonId() == R.id.radio_yes1)
            mBillCard.bill_distributed = "true";
        else if (radioYesno1.getCheckedRadioButtonId() == R.id.radio_no1)
            mBillCard.bill_distributed = "false";

        if (edtRemark.getVisibility() == View.VISIBLE)
            mBillCard.remark = edtRemark.getText().toString();
        else
            mBillCard.remark = "";

        if (mLastLocation != null) {
            mBillCard.cur_lat = String.valueOf(mLastLocation.getLatitude());
            mBillCard.cur_lon = String.valueOf(mLastLocation.getLongitude());
        } else {
            mBillCard.cur_lat = "0";
            mBillCard.cur_lon = "0";
        }

        mBillCard.is_new = "false";
        mBillCard.taken_by = "Manual";
        mBillCard.reading_date = CommonUtils.getCurrentDateTime();

        DatabaseManager.saveBillCardStatus(mContext, mBillCard, AppConstants.JOB_CARD_STATUS_COMPLETED);

        finish();
    }

    public void showMessageDialog(Context mContext, String message) {

        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_with_title, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();
        alert.setCancelable(false);
        ImageView imageView = (ImageView) promptView.findViewById(R.id.image_view);
        imageView.setVisibility(View.GONE);

        TextView title = (TextView) promptView.findViewById(R.id.tv_title);
        title.setTypeface(regular);
        title.setText("Address");

        TextView msg = (TextView) promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button no = (Button) promptView.findViewById(R.id.btn_no);
        no.setVisibility(View.GONE);
        Button ok = (Button) promptView.findViewById(R.id.btn_yes);
        ok.setTypeface(regular);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.show();
    }

    private void startTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
//                Log.i("startTimer", Count + " s");
                Count++;
            }
        }, timerDelay, timerInterval);
    }

    private String stopTimer() {
//        Log.i("stopTimer", "Stopped after " + Count + " s");
        int hours = (int) Count / 3600;
        int remainder = (int) Count - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;
//        Log.i("stopTimer", "Stopped time  " + hours + "  hr " + mins + " min " + secs + " sec ");

        Count = 0;
        if (timer != null) {
            timer.cancel();
        }
        if (hours != 0)
            return hours + " hr " + mins + " min " + secs + " sec";
        else
            return mins + " min " + secs + " sec";
    }

}
