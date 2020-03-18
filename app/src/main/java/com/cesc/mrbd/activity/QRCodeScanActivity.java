package com.cesc.mrbd.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cesc.mrbd.R;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.BillCard;
import com.cesc.mrbd.models.Consumer;
import com.cesc.mrbd.models.Disconnection;
import com.cesc.mrbd.models.UploadDisconnectionNotices;
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
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by Bynry01 on 9/17/16.
 */
public class QRCodeScanActivity extends ParentActivity implements ZXingScannerView.ResultHandler, View.OnClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 500; // 10 sec
    private static int FATEST_INTERVAL = 500; // 5 sec
    private static int DISPLACEMENT = 0; // 1 meters
    public ZXingScannerView zXingScannerView;
    private TextView tv_status, title;
    private Handler handler = new Handler();
    private boolean isSuccessfulScanning = false;
    private ImageView mBack;
    private Typeface regular;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private String mscreen, mcycle, mbinder, msubdivision, mbillmonth, mbinderid, meterReaderId;
    private BillCard consumerJobCard;
    private Disconnection consumerdc;


    Timer timer;
    long timerInterval = 1000; //1 second
    long timerDelay = 1000; //1 second
    int Count = 0;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_qrcode_scan);
        regular = App.getSansationRegularFont();
        mContext = this;
        startTimer();
        Intent i = getIntent();
        if (i != null) {
            mscreen = i.getStringExtra("Screen_Name");
            meterReaderId = i.getStringExtra("meter_reader_id");
        }

        initUI();

        LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        PackageManager pm = mContext.getPackageManager();
        boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        if (isGPSEnabled && hasGps) {
            createLocationRequest();
//            startLocationUpdates();
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
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    private void initUI() {
        zXingScannerView = (ZXingScannerView) findViewById(R.id.zXingScannerView);

        tv_status = (TextView) findViewById(R.id.tv_status);
        tv_status.setTypeface(regular);
        title = (TextView) findViewById(R.id.title_bar);
        title.setTypeface(regular);
        mBack = (ImageView) findViewById(R.id.img_back);
        mBack.setOnClickListener(this);

        zXingScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        zXingScannerView.stopCamera();
        zXingScannerView.startCamera();
        handler.postDelayed(run, AppConstants.DEFAULT_SCANNING_TIME);
    }

    @Override
    protected void onDestroy() {
        zXingScannerView.stopCamera();
        handler.removeCallbacks(run);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(run);

    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            if (!isSuccessfulScanning) {
                zXingScannerView.stopCamera();
                tv_status.setText(R.string.stopped);
                showMessage(mContext, "Scanning Failed, Do you want to rescan?");
//                showAddNewConsumerDialog(mContext, "123456789012", getString(R.string.qr_code_successfully_scanned_consumer_info_not_found_do_you_want_to_add_new_consumer));

            }
        }
    };

    public void showScanFailedDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.rescan), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                isSuccessfulScanning = false;
                zXingScannerView.stopCamera();
                zXingScannerView.startCamera();
                tv_status.setText(R.string.scanning);
                handler.postDelayed(run, AppConstants.DEFAULT_SCANNING_TIME);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(getString(R.string.back), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                handler.removeCallbacks(run);
                zXingScannerView.stopCamera();
                tv_status.setText(R.string.stopped);
                QRCodeScanActivity.this.finish();
                stopTimer();
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
        nbutton.setTextColor(Color.BLACK);
        nbutton.setTextSize(15.0f);
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(Color.BLACK);
        pbutton.setTextSize(15.0f);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void handleResult(Result result) {
//        Toast.makeText(mContext, "handleResult: " + result, Toast.LENGTH_SHORT).show();
        LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        PackageManager pm = mContext.getPackageManager();
        boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        if (isGPSEnabled && hasGps) {
            createLocationRequest();
            startLocationUpdates();
        }
        isSuccessfulScanning = true;
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        String qr_text = result.getText().trim();
        ArrayList consumerInfo = null;
        consumerInfo = new ArrayList(Arrays.asList(qr_text.split("=")));
        if (consumerInfo.size() >= 2) {
            consumerInfo = new ArrayList(Arrays.asList(consumerInfo.get(1).toString().trim().split("\\|")));

            if (consumerInfo.size() >= 3) {
                if (mscreen.equalsIgnoreCase("bill"))
                    consumerJobCard = DatabaseManager.getBillCardsbyConsumerNo(this, consumerInfo.get(0).toString());
                else
                    consumerdc = DatabaseManager.getDcCardsbyConsumerNo(this, consumerInfo.get(0).toString());

                if (consumerJobCard != null) {
                    consumerJobCard.reading_date = CommonUtils.getCurrentDateTime();

                    if (mLastLocation != null) {
                        consumerJobCard.cur_lat = String.valueOf(mLastLocation.getLatitude());
                        consumerJobCard.cur_lon = String.valueOf(mLastLocation.getLongitude());
                    } else {
                        consumerJobCard.cur_lat = "0";
                        consumerJobCard.cur_lon = "0";
                    }
                    mbillmonth = consumerJobCard.billmonth;
                    mbinder = consumerJobCard.binder_code;
                    mbinderid = consumerJobCard.binder_id;
                    mcycle = consumerJobCard.cycle_code;
                    msubdivision = consumerJobCard.zone_code;
                    consumerJobCard.bill_distributed = "true";
                    consumerJobCard.bill_received = "true";
                    consumerJobCard.is_new = "false";
                    consumerJobCard.remark = "";
                    consumerJobCard.taken_by = "QR Scan";
                    consumerJobCard.time_taken = stopTimer();
                    DatabaseManager.saveBillCardStatus(this, consumerJobCard, AppConstants.JOB_CARD_STATUS_COMPLETED);
                    showMessageDialog(mContext, " Bill Ditributed For " + consumerJobCard.consumer_no + " ( " + consumerJobCard.consumer_name + " ) Successfully", "");


                } else if (consumerdc != null) {
                    UploadDisconnectionNotices uploadDisconnectionNotices = new UploadDisconnectionNotices();
                    uploadDisconnectionNotices.meter_reader_id = consumerdc.meter_reader_id;
                    uploadDisconnectionNotices.binder_code = consumerdc.binder_code;
                    uploadDisconnectionNotices.consumer_no = consumerdc.consumer_no;
                    uploadDisconnectionNotices.consumer_name = consumerdc.consumer_name;
                    uploadDisconnectionNotices.job_card_id = consumerdc.job_card_id;
                    uploadDisconnectionNotices.zone_code = consumerdc.zone_code;
                    uploadDisconnectionNotices.bill_month = consumerdc.bill_month;
                    uploadDisconnectionNotices.current_date = CommonUtils.getCurrentDateTime();
                    uploadDisconnectionNotices.delivery_status = getString(R.string.delivered);
                    uploadDisconnectionNotices.delivery_remark = "";

                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    if (mLastLocation != null) {
                        uploadDisconnectionNotices.current_latitude = String.valueOf(mLastLocation.getLatitude());
                        uploadDisconnectionNotices.current_longitude = String.valueOf(mLastLocation.getLongitude());
                    } else {
                        uploadDisconnectionNotices.current_latitude = "0";
                        uploadDisconnectionNotices.current_longitude = "0";
                    }

                    DatabaseManager.saveUploadDisconnectionNotices(mContext, uploadDisconnectionNotices);
                    DatabaseManager.updateDCJobCardStatus(mContext, consumerdc, AppConstants.JOB_CARD_STATUS_COMPLETED);
                    showMessageDialog(mContext, "DC Notice Ditributed Successfully For " + uploadDisconnectionNotices.consumer_no + " ( " + uploadDisconnectionNotices.consumer_name + " ) Successfully", "");


                } else {

                    if (consumerInfo.get(0).toString().length() == 12)
                        showAddNewConsumerDialog(mContext, consumerInfo.get(0).toString(), getString(R.string.qr_code_successfully_scanned_consumer_info_not_found_do_you_want_to_add_new_consumer));
                    else
                        showMessage(mContext, getString(R.string.invalid_qr_code_do_you_want_to_rescan));
                }

            } else showMessage(mContext, getString(R.string.invalid_qr_code_do_you_want_to_rescan));

        } else showMessage(mContext, getString(R.string.invalid_qr_code_do_you_want_to_rescan));
}

    public void showMessageDialog(Context mContext, String message, String mImageDisplay) {
//        CommonUtils.alertTone(mContext, R.raw.ping);
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_without_title, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();
        alert.setCancelable(false);
        ImageView imageView = (ImageView) promptView.findViewById(R.id.image_view);
        if (mImageDisplay.equals("error")) {
            imageView.setImageResource(R.drawable.high_importance);
        } else {
            imageView.setImageResource(R.drawable.checked_green);
        }
        TextView msg = (TextView) promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isSuccessfulScanning = false;
                zXingScannerView.stopCamera();
                zXingScannerView.startCamera();
                zXingScannerView.resumeCameraPreview(QRCodeScanActivity.this);
                handler.removeCallbacks(run);
                handler.postDelayed(run, AppConstants.DEFAULT_SCANNING_TIME);
                stopTimer();
                alert.dismiss();

            }
        });
        alert.setView(promptView);
        alert.show();
    }

    private void showAddNewConsumerDialog(final Context mContext, final String consumer, String message) {

        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_with_title, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();
        alert.setCancelable(false);
        TextView t = (TextView) promptView.findViewById(R.id.tv_title);
        t.setTypeface(regular);
//        t.setText(title);
        t.setVisibility(View.GONE);
        TextView msg = (TextView) promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button yes = (Button) promptView.findViewById(R.id.btn_yes);
        yes.setTypeface(regular);
        yes.setText("Add New");
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                consumerJobCard = new BillCard();
                consumerJobCard.consumer_no = consumer;
                consumerJobCard.time_taken = stopTimer();
                consumerJobCard.reading_date = CommonUtils.getCurrentDateTime();

                if (mLastLocation != null) {
                    consumerJobCard.cur_lat = String.valueOf(mLastLocation.getLatitude());
                    consumerJobCard.cur_lon = String.valueOf(mLastLocation.getLongitude());
                } else {
                    consumerJobCard.cur_lat = "0";
                    consumerJobCard.cur_lon = "0";
                }
                consumerJobCard.meter_reader_id = meterReaderId;
                if (mbillmonth != null) {
                    consumerJobCard.billmonth = mbillmonth;
                    consumerJobCard.binder_code = mbinder;
                    consumerJobCard.binder_id = mbinderid;
                    consumerJobCard.cycle_code = mcycle;
                    consumerJobCard.zone_code = msubdivision;
                } else {
                    ArrayList<BillCard> consumerJobCardarr = DatabaseManager.getBillCards(mContext, LandingActivity.meter_reader_id, AppConstants.BILL_CARD_STATUS_ALLOCATED);
                    consumerJobCard.billmonth = consumerJobCardarr.get(0).billmonth;
                    consumerJobCard.binder_code = consumerJobCardarr.get(0).binder_code;
                    consumerJobCard.binder_id = consumerJobCardarr.get(0).binder_id;
                    consumerJobCard.cycle_code = consumerJobCardarr.get(0).cycle_code;
                    consumerJobCard.zone_code = consumerJobCardarr.get(0).zone_code;
                }

                consumerJobCard.is_new = "true";
                consumerJobCard.remark = "";
                consumerJobCard.bill_distributed = "true";
                consumerJobCard.bill_received = "true";
                consumerJobCard.taken_by = "QR Scan";
                consumerJobCard.time_taken = stopTimer();
                DatabaseManager.saveBillCardStatus(mContext, consumerJobCard, AppConstants.JOB_CARD_STATUS_COMPLETED);
                showMessageDialog(mContext, "Bill Added Successfully", "");

//                isSuccessfulScanning = false;
//                zXingScannerView.resumeCameraPreview(QRCodeScanActivity.this);
//                handler.removeCallbacks(run);
//                handler.postDelayed(run, AppConstants.DEFAULT_SCANNING_TIME);

                alert.dismiss();
            }
        });

        Button no = (Button) promptView.findViewById(R.id.btn_no);
        no.setTypeface(regular);
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isSuccessfulScanning = false;
                zXingScannerView.stopCamera();
                zXingScannerView.startCamera();
                zXingScannerView.resumeCameraPreview(QRCodeScanActivity.this);
                handler.removeCallbacks(run);
                handler.postDelayed(run, AppConstants.DEFAULT_SCANNING_TIME);
                alert.dismiss();

            }
        });
        alert.setView(promptView);
        alert.show();
    }

    /* Display message */
    private void showMessage(Context mContext, String message) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_with_title, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();
        alert.setCancelable(false);
        TextView t = (TextView) promptView.findViewById(R.id.tv_title);
        t.setTypeface(regular);
//        t.setText(title);
        t.setVisibility(View.GONE);
        TextView msg = (TextView) promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button yes = (Button) promptView.findViewById(R.id.btn_yes);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                isSuccessfulScanning = false;
                zXingScannerView.stopCamera();
                zXingScannerView.startCamera();
                zXingScannerView.resumeCameraPreview(QRCodeScanActivity.this);
                handler.removeCallbacks(run);
                handler.postDelayed(run, AppConstants.DEFAULT_SCANNING_TIME);
                alert.dismiss();
            }
        });

        Button no = (Button) promptView.findViewById(R.id.btn_no);
        no.setTypeface(regular);
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                QRCodeScanActivity.this.stopTimer();
                QRCodeScanActivity.this.finish();

            }
        });
        alert.setView(promptView);
        alert.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        zXingScannerView.stopCamera();
        handler.removeCallbacks(run);
        stopTimer();
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == mBack) {
            zXingScannerView.stopCamera();
            handler.removeCallbacks(run);
            stopTimer();
            finish();
            this.onBackPressed();
        }
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
