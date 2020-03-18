package com.cesc.mrbd.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cesc.mrbd.R;
import com.cesc.mrbd.adapters.BillDistributionOpenAdapter;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.BillCard;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.AppPreferences;
import com.cesc.mrbd.utils.CommonUtils;
import com.cesc.mrbd.utils.DialogCreator;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

public class BillDistributionListingActivity extends ParentActivity implements View.OnClickListener,
        LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context mContext;
    private ImageView imgBack, imgAutoComplete;
    private ImageButton btnSearch, btnAddNewConsumer, btnScanQrCode;
    private RecyclerView recyclerBillView;
    private String binderNo = "", zoneCode = "";
    private TextView historyOpen, historyRevisit, historyUnBill;
    private Button btnOpenHistory, btnRevisitHistory, btnUnBillHistory;

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 500; // 10 sec
    private static int FATEST_INTERVAL = 500; // 5 sec
    private static int DISPLACEMENT = 0; // 1 meters

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_distribution_listing);

        mContext = this;

        Typeface regular = App.getSansationRegularFont();

        TextView txtTitle = (TextView) findViewById(R.id.txt_toolbar_title);
        txtTitle.setText(getString(R.string.bill_distribution));
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        imgAutoComplete = (ImageView) findViewById(R.id.img_auto_complete);
        imgAutoComplete.setOnClickListener(this);

        btnSearch = (ImageButton) findViewById(R.id.landing_search_action);
        btnSearch.setOnClickListener(this);
        btnAddNewConsumer = (ImageButton) findViewById(R.id.landing_add_consumer_action);
        btnAddNewConsumer.setOnClickListener(this);
        btnScanQrCode = (ImageButton) findViewById(R.id.landing_scan_qr_action);
        btnScanQrCode.setOnClickListener(this);

        historyOpen = (TextView) findViewById(R.id.tv_open_history);
        historyOpen.setTypeface(regular);
        historyOpen.setText("Total");
        historyRevisit = (TextView) findViewById(R.id.tv_revisit_history);
        historyRevisit.setTypeface(regular);
        historyRevisit.setText("Open");
        historyUnBill = (TextView) findViewById(R.id.tv_unbill_history);
        historyUnBill.setTypeface(regular);
        historyUnBill.setText("Completed");

        btnOpenHistory = (Button) findViewById(R.id.open_history);
        btnOpenHistory.setTypeface(regular);
        btnOpenHistory.setText("0");
        btnRevisitHistory = (Button) findViewById(R.id.revisit_history);
        btnRevisitHistory.setTypeface(regular);
        btnRevisitHistory.setText("0");
        btnUnBillHistory = (Button) findViewById(R.id.unbill_history);
        btnUnBillHistory.setTypeface(regular);
        btnUnBillHistory.setText("0");

        Intent intent = getIntent();
        if (intent != null) {
            binderNo = intent.getStringExtra(AppConstants.BINDER_NO);
            zoneCode = intent.getStringExtra(AppConstants.ZONE_CODE);
        }

        recyclerBillView = (RecyclerView) findViewById(R.id.recycler_bill_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        recyclerBillView.setLayoutManager(linearLayoutManager);

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }
    }

    private void getData() {
        ArrayList<BillCard> open, close;
        int total = 0;

        open = DatabaseManager.getBinderWiseBillCard(mContext, BillDistributionLandingScreen.meter_reader_id,
                AppConstants.BILL_CARD_STATUS_ALLOCATED, binderNo, zoneCode);
        if (open != null) {
            btnRevisitHistory.setText(String.valueOf(open.size()));
            total += open.size();
        } else
            btnRevisitHistory.setText("0");
        close = DatabaseManager.getBinderWiseBillCard(mContext, BillDistributionLandingScreen.meter_reader_id,
                AppConstants.JOB_CARD_STATUS_COMPLETED, binderNo, zoneCode);
        if (close != null) {
            btnUnBillHistory.setText(String.valueOf(close.size()));
            total += close.size();
        } else
            btnUnBillHistory.setText("0");

        btnOpenHistory.setText(String.valueOf(total));
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(mContext, getString(R.string.this_device_is_not_supported), Toast.LENGTH_LONG).show();
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

    private void setRecyclerView() {

        getData();

        ArrayList<BillCard> mBillCards = DatabaseManager.getBinderWiseBillCard(mContext, BillDistributionLandingScreen.meter_reader_id,
                AppConstants.BILL_CARD_STATUS_ALLOCATED, binderNo, zoneCode);

        BillDistributionOpenAdapter adapter = new BillDistributionOpenAdapter(mContext, mBillCards, false);

        recyclerBillView.setAdapter(adapter);

        int percentage = AppPreferences.getInstance(mContext).getInt(AppConstants.PERCENTAGE, 0);
        if (mBillCards.size() > 0) {
            if (percentage == 0) {
                imgAutoComplete.setVisibility(View.VISIBLE);
            } else if (percentage > 0) {
                imgAutoComplete.setVisibility(View.GONE);
                calculatePercentage(percentage);
            }
        }
    }

    private void calculatePercentage(int percent) {

        double currentPercent;
        ArrayList<BillCard> totalBillCards = DatabaseManager.getBinderWiseBillCardTotal(mContext, BillDistributionLandingScreen.meter_reader_id, binderNo, zoneCode);
        ArrayList<BillCard> completedByQRScan = DatabaseManager.getBinderWiseBillCardTakenBy(mContext,
                BillDistributionLandingScreen.meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, binderNo, zoneCode, "QR Scan");
        ArrayList<BillCard> completedByManual = DatabaseManager.getBinderWiseBillCardTakenBy(mContext,
                BillDistributionLandingScreen.meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, binderNo, zoneCode, "Manual");

        if (totalBillCards != null) {

            /*if (completedByManual != null) {
                currentPercent = ((double) completedByManual.size() / (double) totalBillCards.size()) * 100;
                if (currentPercent > percent) {
                    imgAutoComplete.setVisibility(View.VISIBLE);
                    return;
                } else {
                    imgAutoComplete.setVisibility(View.GONE);
                }
            }*/

            if (completedByQRScan != null) {
                currentPercent = ((double) completedByQRScan.size() / (double) totalBillCards.size()) * 100;
                if (currentPercent > percent) {
                    imgAutoComplete.setVisibility(View.VISIBLE);
                    return;
                } else {
                    imgAutoComplete.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        if (v == imgBack) {
            finish();
        } else if (v == imgAutoComplete) {
            warningDialog();
        } else if (v == btnAddNewConsumer) {
            intent = new Intent(mContext, AddNewBillActivity.class);
            intent.putExtra("Screen_Name", "bill");
            startActivity(intent);
        } else if (v == btnSearch) {
            intent = new Intent(mContext, BillDistributionSearchActivity.class);
            intent.putExtra("Screen_Name", "bill");
            intent.putExtra(AppConstants.CURRENT_METER_READER_ID, BillDistributionLandingScreen.meter_reader_id);
            startActivity(intent);
        } else if (v == btnScanQrCode) {
            intent = new Intent(mContext, QRCodeScanActivity.class);
            intent.putExtra("Screen_Name", "bill");
            intent.putExtra("meter_reader_id", BillDistributionLandingScreen.meter_reader_id);
            startActivity(intent);
        }
    }

    private void warningDialog() {

        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_with_title, null);

        final AlertDialog alert = new AlertDialog.Builder(mContext).create();

        TextView txtTitle = (TextView) promptView.findViewById(R.id.tv_title);
        txtTitle.setTypeface(regular);
        txtTitle.setText(getString(R.string.auto_complete));

        TextView txtSubTitle = (TextView) promptView.findViewById(R.id.tv_msg);
        txtSubTitle.setTypeface(regular);
        txtSubTitle.setText("");

        Button yes = (Button) promptView.findViewById(R.id.btn_yes);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
                autoCompleteBD();
            }
        });

        Button no = (Button) promptView.findViewById(R.id.btn_no);
        no.setTypeface(regular);
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.show();
    }

    @SuppressLint("MissingPermission")
    private void autoCompleteBD() {

        ArrayList<BillCard> mBillCards = DatabaseManager.getBinderWiseBillCard(mContext, BillDistributionLandingScreen.meter_reader_id,
                AppConstants.BILL_CARD_STATUS_ALLOCATED, binderNo, zoneCode);

        if (mBillCards.size() > 0) {
            for (BillCard billCard : mBillCards) {
                BillCard mBillCard = new BillCard();

                mBillCard.jobcard_id = billCard.jobcard_id;
                mBillCard.meter_reader_id = billCard.meter_reader_id;
                mBillCard.binder_code = billCard.binder_code;
                mBillCard.cycle_code = billCard.cycle_code;
                mBillCard.end_date = billCard.end_date;
                mBillCard.start_date = billCard.start_date;
                mBillCard.billmonth = billCard.billmonth;
                mBillCard.binder_id = billCard.binder_id;
                mBillCard.consumer_name = billCard.consumer_name;
                mBillCard.consumer_no = billCard.consumer_no;
                mBillCard.address = billCard.address;
                mBillCard.prv_lat = billCard.prv_lat;
                mBillCard.prv_lon = billCard.prv_lon;
                mBillCard.meter_no = billCard.meter_no;
                mBillCard.zone_code = billCard.zone_code;
                mBillCard.phone_no = billCard.phone_no;
                mBillCard.account_no = billCard.account_no;
                mBillCard.zone_name = billCard.zone_name;

                mBillCard.time_taken = "0";
                mBillCard.bill_received = "true";
                mBillCard.bill_distributed = "true";
                mBillCard.remark = "";
                mBillCard.is_new = "false";
                mBillCard.taken_by = "Manual";
                mBillCard.reading_date = CommonUtils.getCurrentDateTime();

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    mBillCard.cur_lat = String.valueOf(mLastLocation.getLatitude());
                    mBillCard.cur_lon = String.valueOf(mLastLocation.getLongitude());
                } else {
                    mBillCard.cur_lat = "0";
                    mBillCard.cur_lon = "0";
                }

                DatabaseManager.saveBillCardStatus(mContext, mBillCard, AppConstants.JOB_CARD_STATUS_COMPLETED);
            }
        }
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setRecyclerView();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
    }
}
