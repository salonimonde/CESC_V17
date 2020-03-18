package com.cesc.mrbd.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cesc.mrbd.R;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.BillCard;
import com.cesc.mrbd.models.Consumer;
import com.cesc.mrbd.models.MeterImage;
import com.cesc.mrbd.models.UploadDisconnectionNotices;
import com.cesc.mrbd.models.UserProfile;
import com.cesc.mrbd.preferences.SharedPrefManager;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;
import com.cesc.mrbd.utils.LocationManagerReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

public class AddNewBillActivity extends ParentActivity implements View.OnClickListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private EditText edtConsumerNo, edtMeterNo, edtPoleNo, edtConsumerName, edtConsumerAddress,
            edtPhoneNo, edtEmail;
    private String meter_reader_id, screen;
    private LinearLayout linearBinderDetails;
    private Button btnAddTaskReading;
    private ImageView imgBack, imgMeter, cameraMeter;
    private TextView consumerDetails, feederDetails, title, txtName, txtConsumerNo, txtMeterNo, txtEmailId,
            txtAddress, txtPoleNo, txtPhoneNo, feederDetailSubTitle, consumerDetailsSubTitle;
    private UserProfile userProfile;
    public static AddNewBillActivity addNewConsumerActivity;
    private Consumer consumer;
    private String consumer_no, mMeterReadingImageName;
    private Typeface regular, bold;
    private Spinner edtBillMonth, edtRouteId, edtBillCycleCode, edtZoneCode;
    private ArrayList<Consumer> con = new ArrayList<Consumer>();
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 500; // 10 sec
    private static int FATEST_INTERVAL = 500; // 5 sec
    private static int DISPLACEMENT = 0; // 1 meters
    private LocationManagerReceiver mLocationManagerReceiver;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private Context mContext;
    private Bitmap mBitmapMeterReading = null;
    private final String METER_IMAGE_DIRECTORY_NAME = "meter";
    private UploadDisconnectionNotices uploadDisconnectionNotices;



    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_bill);
        mContext = this;
        uploadDisconnectionNotices = new UploadDisconnectionNotices();
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();
        addNewConsumerActivity = this;
        consumerDetails = (TextView) findViewById(R.id.consumer_details);
        consumerDetails.setTypeface(bold);
        Intent i = getIntent();
        if (i != null) {
            screen = i.getStringExtra("Screen_Name");
            consumer_no = i.getStringExtra("consumer_no");
        }

        title = (TextView) findViewById(R.id.title_bar);
        title.setTypeface(regular);
        consumerDetailsSubTitle = (TextView) findViewById(R.id.consumer_details_sub_title);
        consumerDetailsSubTitle.setTypeface(regular);
        feederDetailSubTitle = (TextView) findViewById(R.id.feeder_delaits_sub_title);
        feederDetailSubTitle.setTypeface(regular);
        feederDetails = (TextView) findViewById(R.id.feeder_delaits);
        feederDetails.setTypeface(bold);
        txtPhoneNo = (TextView) findViewById(R.id.til_phone_no);
        txtPhoneNo.setTypeface(regular);
        txtAddress = (TextView) findViewById(R.id.til_consumer_address);
        txtAddress.setTypeface(regular);
        txtEmailId = (TextView) findViewById(R.id.til_consumer_email);
        txtEmailId.setTypeface(regular);
        txtConsumerNo = (TextView) findViewById(R.id.til_consumer_no);
        txtConsumerNo.setTypeface(regular);
        txtMeterNo = (TextView) findViewById(R.id.til_meter_no);
        txtMeterNo.setTypeface(regular);
        txtName = (TextView) findViewById(R.id.til_consumer_name);
        txtName.setTypeface(regular);
        txtPoleNo = (TextView) findViewById(R.id.til_pole_no);
        txtPoleNo.setTypeface(regular);
//        consumer = (Consumer) getIntent().getSerializableExtra(AppConstants.CONSUMER_OBJ);
        edtConsumerNo = (EditText) findViewById(R.id.edt_consumer_no);
        edtConsumerNo.setTypeface(bold);
        edtConsumerNo.setText(consumer_no != null ? consumer_no != null ? consumer_no : "" : "");
        edtMeterNo = (EditText) findViewById(R.id.edt_meter_no);
        edtMeterNo.setTypeface(bold);
        edtMeterNo.setText(consumer != null ? consumer.meter_no != null ? consumer.meter_no : "" : "");
        edtBillCycleCode = (Spinner) findViewById(R.id.edt_bill_cycle_code);
        edtRouteId = (Spinner) findViewById(R.id.edt_route_id);
        edtPoleNo = (EditText) findViewById(R.id.edt_pole_no);
        edtPoleNo.setTypeface(bold);
        edtConsumerName = (EditText) findViewById(R.id.edt_consumer_name);
        edtConsumerName.setTypeface(bold);
        edtEmail = (EditText) findViewById(R.id.edt_consumer_email);
        edtEmail.setTypeface(bold);
        edtMeterNo.setText(consumer != null ? consumer.meter_no != null ? consumer.meter_no : "" : "");
        edtMeterNo.setTypeface(bold);
        edtEmail = (EditText) findViewById(R.id.edt_consumer_email);
        edtEmail.setTypeface(bold);
        edtConsumerName.setText(consumer != null ? consumer.consumer_name != null ? consumer.consumer_name : "" : "");
        edtConsumerAddress = (EditText) findViewById(R.id.edt_consumer_address);
        edtConsumerAddress.setTypeface(bold);
        edtConsumerAddress.setVisibility(View.GONE);
        edtPhoneNo = (EditText) findViewById(R.id.edt_phone_no);
        edtPhoneNo.setTypeface(bold);
        edtBillMonth = (Spinner) findViewById(R.id.edt_bill_month);
        imgBack = (ImageView) findViewById(R.id.img_back);
        btnAddTaskReading = (Button) findViewById(R.id.btn_add_task_reading);
        imgBack.setOnClickListener(this);
        btnAddTaskReading.setTypeface(regular);
        btnAddTaskReading.setOnClickListener(this);
        edtZoneCode = (Spinner) findViewById(R.id.edt_zone_code);
        linearBinderDetails = (LinearLayout) findViewById(R.id.linear_binder_details);

        imgMeter = (ImageView) findViewById(R.id.img_meter);

        cameraMeter = (ImageView) findViewById(R.id.camera_meter);
        cameraMeter.setOnClickListener(this);


        getUserProfileDetails();


        LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        PackageManager pm = mContext.getPackageManager();
        boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        if (isGPSEnabled && hasGps) {
            createLocationRequest();
//            startLocationUpdates();
        }
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateEmail();
            }
        });

        edtPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateNumber();
            }

            @Override
            public void afterTextChanged(Editable s) {
                validateNumber();
            }
        });

        if (screen.equalsIgnoreCase("dc")) {
            edtConsumerName.setVisibility(View.GONE);
            edtEmail.setVisibility(View.GONE);
            edtPhoneNo.setVisibility(View.GONE);
            edtConsumerAddress.setVisibility(View.GONE);
            linearBinderDetails.setVisibility(View.GONE);
            title.setText(R.string.add_new_disconnection_notice);
            btnAddTaskReading.setText(R.string.save_notice);
        } else {
            setRouteData(meter_reader_id);
        }
    }

    private boolean validateNumber() {
        Pattern pattern1, pattern2;
        Matcher matcher1, matcher2;
        final String PHONE_PATTERN1 = "^[7-9][0-9]{9}$";
        final String PHONE_PATTERN2 = "";
        pattern1 = Pattern.compile(PHONE_PATTERN1);
        pattern2 = Pattern.compile(PHONE_PATTERN2);
        String phone = edtPhoneNo.getText().toString().trim();
        matcher1 = pattern1.matcher(phone);
        matcher2 = pattern2.matcher(phone);

        if (matcher1.matches() || matcher2.matches()) {
            edtPhoneNo.setError(null);
            return true;
        } else {
            edtPhoneNo.setError(getString(R.string.enter_valid_mobile_no));
            edtPhoneNo.requestFocus();
            return false;
        }
    }

    private boolean validateEmail() {
        Pattern pattern1, pattern2;
        Matcher matcher1, matcher2;
        final String EMAIL_PATTERN1 =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,3})$";
        final String EMAIL_PATTERN2 = "";
        pattern1 = Pattern.compile(EMAIL_PATTERN1);
        pattern2 = Pattern.compile(EMAIL_PATTERN2);
        String email = edtEmail.getText().toString().trim();
        matcher1 = pattern1.matcher(email);
        matcher2 = pattern2.matcher(email);
        if (matcher1.matches() || matcher2.matches()) {
            edtEmail.setError(null);
            return true;
        } else {
            edtEmail.setError(getString(R.string.enter_valid_email_id));
            edtEmail.requestFocus();
            return false;
        }
    }

    @Override
    public void onClick(View view) {

        if (view == imgBack){
            finish();
        }
        if (view == cameraMeter){

            if (edtConsumerNo.getText().toString().trim().isEmpty()) {
                if (edtConsumerNo.getText().toString().trim().equals(12)){
                    Toast.makeText(mContext, R.string.please_enter_valid_consumer_number, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(mContext, getString(R.string.please_enter_consumer_number), Toast.LENGTH_SHORT).show();
                }
                return;
            } else{
                mMeterReadingImageName = "DC_" + edtConsumerNo.getText().toString() + "_" + meter_reader_id;


                mLocationManagerReceiver = new LocationManagerReceiver(mContext);

                LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
                boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                PackageManager pm = mContext.getPackageManager();
                boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

                if (isGPSEnabled && hasGps) {
                    createLocationRequest();
                    startLocationUpdates();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri fileUri = null;
                    fileUri = getOutputMediaFileUri(METER_IMAGE_DIRECTORY_NAME, mMeterReadingImageName);

                    List<ResolveInfo> resolvedIntentActivities = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                        String packageName = resolvedIntentInfo.activityInfo.packageName;
                        mContext.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(intent, AppConstants.CAMERA_RESULT_CODE);
                } else {
                    mLocationManagerReceiver = new LocationManagerReceiver(mContext);
                }
            }
        }

        if (view.getId() == R.id.btn_add_task_reading){
            LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            PackageManager pm = mContext.getPackageManager();
            boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
            if (isGPSEnabled && hasGps) {
                doSubmitOps();
            } else {
                mLocationManagerReceiver = new LocationManagerReceiver(mContext);
            }
        }

    }

    private void checkValidation() {


       /* //check bill month is added or not
        if (edtBillMonth.getSelectedItemPosition() != 0) {
            //check bill cycle is added or not
            if (edtZoneCode.getSelectedItemPosition() != 0) {
                //check txtBinderCode id is added or not
                if (edtBillCycleCode.getSelectedItemPosition() != 0) {
                    //check zone_code code is added or not
                    if (edtRouteId.getSelectedItemPosition() != 0) {
                        // if all conditions are satisfied then call below method
                        validationComplete();
                    } else {
                        Toast.makeText(this, getString(R.string.please_add_valid_binder), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.please_add_valid_cycle), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getString(R.string.please_add_valid_sub_division), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.please_add_valid_bill_month), Toast.LENGTH_SHORT).show();
        }*/
        validationComplete();
    }

    private void validationComplete() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        PackageManager pm = this.getPackageManager();
        boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
        if (isGPSEnabled && hasGps) {
            createLocationRequest();
            startLocationUpdates();
            takeMeterReading();
        } else if (isNetworkEnabled && !hasGps) {
            createLocationRequest();
            startLocationUpdates();
            takeMeterReading();
        } else {
            mLocationManagerReceiver = new LocationManagerReceiver(this);
        }
    }

    @SuppressLint("MissingPermission")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case AppConstants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        //startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        mLocationManagerReceiver = new LocationManagerReceiver(this);
//                        LocationReceiver locationReceiver = new LocationReceiver(this);
                        break;
                }
            case AppConstants.CAMERA_RESULT_CODE:
                mLocationManagerReceiver = new LocationManagerReceiver(mContext);
                mBitmapMeterReading = getBitmapScaled(METER_IMAGE_DIRECTORY_NAME, mMeterReadingImageName);
                if (mBitmapMeterReading != null) {
                    imgMeter.setImageBitmap(mBitmapMeterReading);
                }
                break;
        }
    }


    private void setRouteData(String meter_reader_id) {
        ArrayList<String> routes = new ArrayList<>();
        routes.add("Binder*");
        if (DatabaseManager.getRoutesall(this, meter_reader_id, screen) != null)
            routes.addAll(DatabaseManager.getRoutesall(this, meter_reader_id, screen));
        if (routes != null && routes.size() > 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, routes) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTypeface(bold);
                    ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                    ((TextView) v).setTextSize(14f);
                    return v;
                }

                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View v = super.getDropDownView(position, convertView, parent);
                    ((TextView) v).setTypeface(bold);
                    ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                    ((TextView) v).setTextSize(14f);
                    return v;
                }
            };
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            edtRouteId.setAdapter(dataAdapter);
        }
        ArrayList<String> billcyclecode = new ArrayList<>();
        billcyclecode.add("Cycle*");
        if (DatabaseManager.getbillcyclecode(screen, this, meter_reader_id) != null)
            billcyclecode.addAll(DatabaseManager.getbillcyclecode(screen, this, meter_reader_id));
        if (billcyclecode != null && billcyclecode.size() > 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, billcyclecode) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTypeface(bold);
                    ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                    ((TextView) v).setTextSize(14f);
                    return v;
                }

                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View v = super.getDropDownView(position, convertView, parent);
                    ((TextView) v).setTypeface(bold);
                    ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                    ((TextView) v).setTextSize(14f);
                    return v;
                }
            };
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            edtBillCycleCode.setAdapter(dataAdapter);
        }

        ArrayList<String> billmonth = new ArrayList<>();
        billmonth.add("Bill Month*");
        if (DatabaseManager.getbillmonth(this, meter_reader_id, screen) != null)
            billmonth.addAll(DatabaseManager.getbillmonth(this, meter_reader_id, "bill"));
        if (billmonth != null && billmonth.size() > 0) {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, billmonth) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTypeface(bold);
                    ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                    ((TextView) v).setTextSize(14f);
                    return v;
                }

                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View v = super.getDropDownView(position, convertView, parent);
                    ((TextView) v).setTypeface(bold);
                    ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                    ((TextView) v).setTextSize(14f);
                    return v;
                }
            };
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            edtBillMonth.setAdapter(dataAdapter);
        }

        ArrayList<String> zoneCode = new ArrayList<>();
        zoneCode.add("Subdivision*");
        if (DatabaseManager.getZoneNames(this, meter_reader_id, screen) != null)
            zoneCode.addAll(DatabaseManager.getZoneNames(this, meter_reader_id, "bill"));
        if (zoneCode != null && zoneCode.size() > 0) {
            ArrayAdapter<String> dataAdapterForZone = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, zoneCode) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = super.getView(position, convertView, parent);
                    ((TextView) v).setTypeface(bold);
                    ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                    ((TextView) v).setTextSize(14f);
                    return v;
                }

                public View getDropDownView(int position, View convertView, ViewGroup parent) {
                    View v = super.getDropDownView(position, convertView, parent);
                    ((TextView) v).setTypeface(bold);
                    ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                    ((TextView) v).setTextSize(14f);
                    return v;
                }
            };
            dataAdapterForZone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            edtZoneCode.setAdapter(dataAdapterForZone);
        }
    }

    @SuppressLint("MissingPermission")
    private void takeMeterReading() {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (screen.equalsIgnoreCase("bill")) {
            BillCard consumer = new BillCard();
            consumer.meter_reader_id = meter_reader_id;
            consumer.consumer_name = edtConsumerName.getText().toString().trim();
            consumer.consumer_no = (edtConsumerNo.getText().toString().length() > 0 && edtConsumerNo.getText().toString().length() == 12) ? edtConsumerNo.getText().toString().trim() : "123456789111";
            consumer.meter_no = edtMeterNo.getText().toString().trim();
            consumer.cycle_code = edtBillCycleCode.getSelectedItem().toString().trim();
            consumer.is_new = "true";
            if (mLastLocation != null) {
                consumer.cur_lat = String.valueOf(mLastLocation.getLatitude());
                consumer.cur_lon = String.valueOf(mLastLocation.getLongitude());
            } else {
                consumer.cur_lat = "0";
                consumer.cur_lon = "0";
            }
            consumer.remark = "true";
            consumer.address = edtConsumerAddress.getText().toString().trim();
            consumer.binder_code = edtRouteId.getSelectedItem().toString().trim();
            consumer.billmonth = edtBillMonth.getSelectedItem().toString().trim();
            consumer.zone_code = edtZoneCode.getSelectedItem().toString().trim();
            consumer.taken_by = "Manual";
//            ArrayList consumerInfo = new ArrayList(Arrays.asList(consumer.zone_name.split("\\(")));
//            ArrayList consumerInfo1 = new ArrayList(Arrays.asList(String.valueOf(consumerInfo.get(1)).split("\\)")));
//            consumer.zone_code=String.valueOf(consumerInfo1.get(0));
            consumer.reading_date = CommonUtils.getCurrentDateTime();
            consumer.jobcard_status = AppConstants.JOB_CARD_STATUS_COMPLETED;

            DatabaseManager.saveNewBillConsumer(this, consumer);
            finish();
        } else {
            String meter_image = null;
            if (((BitmapDrawable)imgMeter.getDrawable()) != null) {
                Log.d("aaaaaaaa","bbbbbbb"+imgMeter.getDrawable());

                meter_image = CommonUtils.getBitmapEncodedString(mBitmapMeterReading);
                MeterImage meterImage = new MeterImage();
                meterImage.image = meter_image;
                uploadDisconnectionNotices.meter_image = meterImage;


                uploadDisconnectionNotices.meter_reader_id = meter_reader_id;
                uploadDisconnectionNotices.binder_code = "1801";
                uploadDisconnectionNotices.consumer_no = (edtConsumerNo.getText().toString().length() > 0 && edtConsumerNo.getText().toString().length() == 12) ? edtConsumerNo.getText().toString().trim() : "";
                uploadDisconnectionNotices.meter_no = (edtMeterNo.getText().toString().length() > 0) ? edtMeterNo.getText().toString().trim() : "";
                Log.d("gsghdfhsgad","sahdhajkdh"+ edtMeterNo.getText().toString().trim());
                uploadDisconnectionNotices.consumer_name = "Disconnection";
                uploadDisconnectionNotices.zone_code = "18";
                uploadDisconnectionNotices.bill_month = "201903";
                uploadDisconnectionNotices.current_date = CommonUtils.getCurrentDateTime();
                uploadDisconnectionNotices.delivery_status = getString(R.string.delivered);
                uploadDisconnectionNotices.delivery_remark = "";
                uploadDisconnectionNotices.is_new = "true";



                if (mLastLocation != null) {
                    uploadDisconnectionNotices.current_latitude = String.valueOf(mLastLocation.getLatitude());
                    uploadDisconnectionNotices.current_longitude = String.valueOf(mLastLocation.getLongitude());
                } else {
                    uploadDisconnectionNotices.current_latitude = "0";
                    uploadDisconnectionNotices.current_longitude = "0";
                }

                DatabaseManager.saveUploadDisconnectionNotices(mContext, uploadDisconnectionNotices);
                finish();
            }else {
                Toast.makeText(mContext, R.string.please_take_image, Toast.LENGTH_SHORT).show();
            }
        }

        mBitmapMeterReading = null;

        //delete saved images folder
        File meterFile = new File(Environment.getExternalStorageDirectory(), METER_IMAGE_DIRECTORY_NAME);
        if (meterFile.isDirectory()) {
            String[] children = meterFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(meterFile, children[i]).delete();
            }
            meterFile.delete();
        }
    }

    private void getUserProfileDetails() {
        userProfile = DatabaseManager.getUserProfile(this, SharedPrefManager.getStringValue(this, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;
        }
    }

    private void doSubmitOps(){
        if (edtConsumerNo.getText().toString().trim().length() == 12) {
            //check meter no is entered or not
            //then check meter no is in range 1-20 if Yes
            if (edtMeterNo.getText().toString().trim().length() > 4 && edtMeterNo.getText().toString().trim().length() <= 10) {
                //check consumer name is entered or not
                if (edtEmail.getText().toString().trim().length() > 0) {
                    //if email id is entered then check it is valid or not
                    if (validateEmail()) {
                        checkValidation();
                    } else {
                        Toast.makeText(this, getString(R.string.please_enter_valid_email_id), Toast.LENGTH_SHORT).show();
                    }
                }
                //if email Id is not entered
                else {
                    checkValidation();
                }
            } else {
                Toast.makeText(this, getString(R.string.please_enter_valid_meter_number), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.please_enter_valid_consumer_number), Toast.LENGTH_SHORT).show();
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
        mLocationManagerReceiver = new LocationManagerReceiver(mContext);
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

    private Bitmap getBitmapScaled(String dirname, String filename) {
        Bitmap compressedImage = null;
        try {
            File file = getMeterFilePath(dirname, filename);
            compressedImage = new Compressor.Builder(mContext)
                    .setMaxWidth(640)
                    .setMaxHeight(480)
                    .setQuality(1)
                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                    .build()
                    .compressToBitmap(file);
            compressedImage = Bitmap.createScaledBitmap(compressedImage, 640, 480, false);
            if (compressedImage != null)
                compressedImage = CommonUtils.addWaterMarkDate(compressedImage, CommonUtils.getCurrentDateTime());
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return compressedImage;
    }

    public Uri getOutputMediaFileUri(String dirname, String filename) {
        File file = getMeterFilePath(dirname, filename);
        return FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);
    }

    public File getMeterFilePath(String dirname, String filename) {
        // External sdcard location
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), dirname);

        // Create imageDir
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File createdFile = new File(mediaStorageDir.getPath() + File.separator + filename + ".jpg");
        return createdFile;
    }
}
