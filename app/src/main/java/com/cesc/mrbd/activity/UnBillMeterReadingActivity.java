package com.cesc.mrbd.activity;

import android.Manifest;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cesc.mrbd.R;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.Consumer;
import com.cesc.mrbd.models.MeterImage;
import com.cesc.mrbd.models.Sequence;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;
import com.cesc.mrbd.utils.DialogCreator;
import com.cesc.mrbd.utils.LocationManagerReceiver;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

public class UnBillMeterReadingActivity extends ParentActivity implements View.OnClickListener, LocationListener, RadioGroup.OnCheckedChangeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private LinearLayout linearSuspicious, linearMeterGuidance, linearMeterIndexing, linearMobileNumber,
            linearMeterType, linearAirConditioner;
    private EditText edtObservation, edtComments, edtAboutMeter, edtPoleNo, edtKVAHReading, edtKWHReading, edtKVA,
            edtMobileNo, edtPanelNo, edtKW, edtPF;
    private ImageView cameraSuspicious, imgSuspicious, imgMeter, cameraMeter;
    private TextView mConsumerName, lblMeterReading, txtMeterStatus, txtReaderStatus, txtSuspiciousActivity, lblSusImage,
            txtConsumerMeterNumber, txtAboutMeter, txtMeterIndexing, txtMobileNumber, txtMeterType,
            txtAirConditionerExist,txtPlasticCoverCut;
    private Spinner mMeterStatus, mReaderStatus, mMeterType, mNoOfAirConditioners, mSuspiciousActivity;
    public final String METER_IMAGE_DIRECTORY_NAME = "meter";
    public final String SUSPICIOUS_IMAGE_DIRECTORY_NAME = "suspicious";
    private Button btnSubmit;
    private ImageView img_back, btnViewMore;
    private Consumer consumer;
    private RadioGroup radioGroup, airConditionYesNo, plasticCutYesNo;
    private Typeface regular, bold;
    private Context mContext;
    private LocationManagerReceiver mLocationManagerReceiver;
    private Bitmap mBitmapMeterSuspicious, mBitmapMeterReading;
    private String consumerName = "", consumerAddress = "", consumerNumber = "", consumerDtcNo = "", consumerPoleNo = "",
            consumerMeterNo = "", consumerMobileNo = "", mMeterReadingImageName = "", mMeterReadingSPImageName = "",
            meterStatusCode = "", meterType = "", consumerZoneCode = "", starttime, endtime, suspiciousActivityCode;
    private RadioButton radioYes, radioNo, btnRadioAboutMeterYes, btnRadioAboutMeterNo, btnMeterIndexingNo,
            btnMeterIndexingYes, btnMobileNumberNo, btnMobileNumberYes, btnMeterTypeNo, btnMeterTypeYes,
            btnRadioAirConditionerYes, btnRadioAirConditionerNo,
            btnRadioPlasticCutNo, btnRadioPlasticCutYes, btnRadioSuspiciousYes, btnRadioSuspiciousNo;
    private int arrayForReasonCode = R.array.reader_status;
    private RelativeLayout updatemetertype, updatemobileno, updateAboutmeter, updateindex;
    private ImageView iv_updatemetertype, iv_updatemobileno, iv_updateAboutmeter, iv_updateindex;
    private boolean applyCondition = true;


    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 500; // 10 sec
    private static int FATEST_INTERVAL = 500; // 5 sec
    private static int DISPLACEMENT = 0; // 1 meters

    private ArrayList<Sequence> localSequence;
    private int assignedSequence = 0;

    Timer timer;
    long timerInterval = 1000; //1 second
    long timerDelay = 1000; //1 second
    int Count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_bill_meter_reading_activity);
        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();
        mContext = this;
        startTimer();
        mLocationManagerReceiver = new LocationManagerReceiver(this);
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        initUI();

        /*// Find the last picture
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = this.getContentResolver()
                                          .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                                                  null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

// Put it in the image view
        if (cursor.moveToFirst()) {
            final ImageView imageView = (ImageView) findViewById(R.id.pictureView);
            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {
                // TODO: is there a better way to do this?
                Bitmap bm = BitmapFactory.decodeFile(imageLocation);
                imageView.setImageBitmap(bm);
            }
        }*/

    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void initUI() {
        txtMeterStatus = (TextView) findViewById(R.id.txt_meter_status);
        txtMeterStatus.setTypeface(bold);
        txtReaderStatus = (TextView) findViewById(R.id.txt_reader_status);
        txtReaderStatus.setTypeface(bold);
        txtSuspiciousActivity = (TextView) findViewById(R.id.Suspicious_Activity);
        txtSuspiciousActivity.setTypeface(bold);
        lblSusImage = (TextView) findViewById(R.id.lbl_Sus_img);
        lblSusImage.setTypeface(bold);
        lblMeterReading = (TextView) findViewById(R.id.lbl_meterreading);
        lblMeterReading.setTypeface(regular);


        txtPlasticCoverCut = (TextView) findViewById(R.id.txt_is_plastic_cover_cut);
        txtPlasticCoverCut.setTypeface(bold);
        txtAirConditionerExist = (TextView) findViewById(R.id.txt_air_conditioner_exist);
        txtAirConditionerExist.setTypeface(bold);


        /*edtMeterReading = (EditText) findViewById(R.id.edt_meter_reading);
        edtMeterReading.setTypeface(bold);*/
        edtObservation = (EditText) findViewById(R.id.edt_observation);
        edtObservation.setTypeface(bold);
        edtComments = (EditText) findViewById(R.id.comments);
        edtComments.setTypeface(bold);
        imgMeter = (ImageView) findViewById(R.id.img_meter);
        cameraMeter = (ImageView) findViewById(R.id.camera_meter);
        btnSubmit = (Button) findViewById(R.id.submit);
        btnSubmit.setTypeface(bold);
        imgSuspicious = (ImageView) findViewById(R.id.img_suspicious);
        cameraSuspicious = (ImageView) findViewById(R.id.camera_suspicious);
        img_back = (ImageView) findViewById(R.id.img_back);
        TextView lTilObservations = (TextView) findViewById(R.id.txt_observation);
        lTilObservations.setTypeface(regular);
        updateAboutmeter = (RelativeLayout) findViewById(R.id.updateaboutmeter);
        updateAboutmeter.setOnClickListener(this);
        updateindex = (RelativeLayout) findViewById(R.id.updatemeterindex);
        updateindex.setOnClickListener(this);
        updatemobileno = (RelativeLayout) findViewById(R.id.updatemetermobile);
        updatemobileno.setOnClickListener(this);
        updatemetertype = (RelativeLayout) findViewById(R.id.updatemetertype);
        updatemetertype.setOnClickListener(this);
        radioGroup = (RadioGroup) findViewById(R.id.rg_yesno);
        radioGroup.setOnCheckedChangeListener(this);

        airConditionYesNo = (RadioGroup) findViewById(R.id.rg_air_condition_yesno);
        airConditionYesNo.setOnCheckedChangeListener(this);

        plasticCutYesNo = (RadioGroup) findViewById(R.id.rg_plastic_cut_yesno);
        radioGroup.setOnCheckedChangeListener(this);

        Button mGetDB = (Button) findViewById(R.id.getDB);
        mGetDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String filePath = DatabaseManager.getDbPath(UnBillMeterReadingActivity.this);
                InputStream in;
                OutputStream out;
                try {
                    in = new FileInputStream(filePath);
                    System.out.println(getExternalCacheDir());
                    out = new FileOutputStream(getExternalCacheDir() + "CESC.db");
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    // write the output file
                    out.flush();
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        cameraMeter.setOnClickListener(this);
        img_back.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        cameraSuspicious.setOnClickListener(this);
        Intent i = getIntent();
        if (i != null) {
            if (consumer == null) {
                consumer = (Consumer) i.getSerializableExtra(AppConstants.CURRENT_CONSUMER_OBJ);
            }
        }

        Sequence getSeq = new Sequence();
        getSeq.meter_reader_id = consumer.meter_reader_id;
        getSeq.zone_code = consumer.zone_code;
        getSeq.route_code = consumer.route_code;
        getSeq.cycle_code = consumer.bill_cycle_code;

        localSequence = DatabaseManager.getSequence(mContext, getSeq);
        if (localSequence != null) {
            assignedSequence = Integer.parseInt(localSequence.get(0).sequence);
        }

        mNoOfAirConditioners = (Spinner) findViewById(R.id.enter_no_of_air_conditioners_spinner);
        setNoOfAirConditionersSpinner(R.array.number_of_air_conditioners);

        mSuspiciousActivity = (Spinner) findViewById(R.id.suspicious_activity_spinner);
        setSuspiciousActivitySpinner(R.array.suspicious_activity_array);


        btnRadioSuspiciousYes = (RadioButton) findViewById(R.id.rb_yes);
        btnRadioSuspiciousYes.setTypeface(bold);
        btnRadioSuspiciousYes.setOnClickListener(this);
        btnRadioSuspiciousNo = (RadioButton) findViewById(R.id.rb_no);
        btnRadioSuspiciousNo.setTypeface(bold);
        btnRadioSuspiciousNo.setOnClickListener(this);


//        Air conditioner Radio buttons
        btnRadioAirConditionerYes = (RadioButton) findViewById(R.id.rb_air_conditioner_yes);
        btnRadioAirConditionerYes.setTypeface(bold);
        btnRadioAirConditionerYes.setOnClickListener(this);

        btnRadioAirConditionerNo = (RadioButton) findViewById(R.id.rb_air_conditioner_no);
        btnRadioAirConditionerNo.setTypeface(bold);
        btnRadioAirConditionerNo.setOnClickListener(this);

        btnRadioPlasticCutNo = (RadioButton) findViewById(R.id.rb_plastic_cut_no);
        btnRadioPlasticCutNo.setTypeface(bold);
        btnRadioPlasticCutNo.setOnClickListener(this);

        btnRadioPlasticCutYes = (RadioButton) findViewById(R.id.rb_plastic_cut_yes);
        btnRadioPlasticCutYes.setTypeface(bold);
        btnRadioPlasticCutYes.setOnClickListener(this);

        mConsumerName = (TextView) findViewById(R.id.consumerName);
        mConsumerName.setTypeface(regular);
        txtConsumerMeterNumber = (TextView) findViewById(R.id.txt_consumer_meter_number);
        txtConsumerMeterNumber.setTypeface(regular);
        radioYes = (RadioButton) findViewById(R.id.rb_yes);
        radioYes.setOnClickListener(this);
        radioNo = (RadioButton) findViewById(R.id.rb_no);
        radioNo.setOnClickListener(this);
        linearSuspicious = (LinearLayout) findViewById(R.id.linear_suspicious);
        linearSuspicious.setVisibility(View.GONE);
        btnViewMore = (ImageView) findViewById(R.id.btn_view_more);
        btnViewMore.setOnClickListener(this);

        linearAirConditioner = (LinearLayout) findViewById(R.id.linear_air_conditioner);
        linearAirConditioner.setVisibility(View.GONE);

        btnRadioAboutMeterYes = (RadioButton) findViewById(R.id.btn_radio_yes);
        btnRadioAboutMeterYes.setOnClickListener(this);
        btnRadioAboutMeterNo = (RadioButton) findViewById(R.id.btn_radio_no);
        btnRadioAboutMeterNo.setOnClickListener(this);
        txtAboutMeter = (TextView) findViewById(R.id.txt_about_meter);
        txtAboutMeter.setTypeface(bold);
        edtAboutMeter = (EditText) findViewById(R.id.edt_about_meter);
        edtAboutMeter.setTypeface(bold);
        linearMeterGuidance = (LinearLayout) findViewById(R.id.linear_meter_guidance);

        //New fields for CESC starts, Piyush : 22/03/2017

        edtPoleNo = (EditText) findViewById(R.id.edt_pole_no);
        edtPoleNo.setTypeface(bold);
        edtKVAHReading = (EditText) findViewById(R.id.edt_kvah_reading);
        edtKVAHReading.setTypeface(bold);
        edtKWHReading = (EditText) findViewById(R.id.edt_kwh_reading);
        edtKWHReading.setTypeface(bold);
        edtKVA = (EditText) findViewById(R.id.edt_kva);
        edtKVA.setTypeface(bold);
        edtKW = (EditText) findViewById(R.id.edt_kw);
        edtKW.setTypeface(bold);
        edtPF = (EditText) findViewById(R.id.edt_pf);
        edtPF.setTypeface(bold);
        edtPanelNo = (EditText) findViewById(R.id.edt_panel_no);
        edtPanelNo.setTypeface(bold);
        edtMobileNo = (EditText) findViewById(R.id.edt_mobile_no);
        edtMobileNo.setTypeface(bold);
        linearMeterIndexing = (LinearLayout) findViewById(R.id.linear_meter_indexing);
        txtMeterIndexing = (TextView) findViewById(R.id.txt_meter_indexing);
        txtMeterIndexing.setTypeface(bold);
//        btnMeterIndexingNo = (RadioButton)findViewById(R.id.btn_radio_index_no);
//        btnMeterIndexingNo.setTypeface(bold);
//        btnMeterIndexingNo.setOnClickListener(this);
//        btnMeterIndexingYes = (RadioButton)findViewById(R.id.btn_radio_index_yes);
//        btnMeterIndexingYes.setTypeface(bold);
//        btnMeterIndexingYes.setOnClickListener(this);

        txtMobileNumber = (TextView) findViewById(R.id.txt_mobile_number);
        txtMobileNumber.setTypeface(bold);
        linearMobileNumber = (LinearLayout) findViewById(R.id.linear_mobile_number);
//        btnMobileNumberNo = (RadioButton)findViewById(R.id.btn_radio_mobile_no);
//        btnMobileNumberNo.setTypeface(bold);
//        btnMobileNumberNo.setOnClickListener(this);
//        btnMobileNumberYes = (RadioButton)findViewById(R.id.btn_radio_mobile_yes);
//        btnMobileNumberYes.setTypeface(bold);
//        btnMobileNumberYes.setOnClickListener(this);

        txtMeterType = (TextView) findViewById(R.id.txt_meter_type);
        txtMeterType.setTypeface(bold);
        linearMeterType = (LinearLayout) findViewById(R.id.linear_meter_type);
        /*btnMeterTypeNo = (RadioButton)findViewById(R.id.radio_meter_type_no);
        btnMeterTypeNo.setTypeface(bold);
        btnMeterTypeNo.setOnClickListener(this);
        btnMeterTypeYes = (RadioButton)findViewById(R.id.radio_meter_type_yes);
        btnMeterTypeYes.setTypeface(bold);
        btnMeterTypeYes.setOnClickListener(this);*/

        mMeterStatus = (Spinner) findViewById(R.id.meter_status_spinner);
        mReaderStatus = (Spinner) findViewById(R.id.reader_status_spinner);
        setReasonCodeSpinner(arrayForReasonCode);
        iv_updateAboutmeter = (ImageView) findViewById(R.id.updateabout);
        iv_updateindex = (ImageView) findViewById(R.id.updateindex);
        iv_updatemetertype = (ImageView) findViewById(R.id.updatetype);
        iv_updatemobileno = (ImageView) findViewById(R.id.updatemobile);


        //New fields for CESC ends, Piyush : 22/03/2017


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.update_meter_status_blank)) {
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
        mMeterStatus.setAdapter(adapter);
        mMeterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                meterStatusCode = mMeterStatus.getSelectedItem().toString().trim();
                String value = edtKWHReading.getText().toString().trim();
                if (value.isEmpty()) {
                    if (meterStatusCode.equals(getString(R.string.rcnt))) {
                        arrayForReasonCode = R.array.reader_status_rcnt_for_newconsumer;
                        setReasonCodeSpinner(arrayForReasonCode);
                        edtKWHReading.setEnabled(false);
                        edtKVAHReading.setEnabled(false);
                        edtKVA.setEnabled(false);
                        edtKW.setEnabled(false);
                        edtPF.setEnabled(false);

                    } else if (meterStatusCode.equals(getString(R.string.meter_status_faulty))) {
                        arrayForReasonCode = R.array.reader_status_faulty3;
                        setReasonCodeSpinner(arrayForReasonCode);
                        edtKWHReading.setEnabled(true);
                        edtKVAHReading.setEnabled(true);
                        edtKVA.setEnabled(true);
                        edtKW.setEnabled(true);
                        edtPF.setEnabled(true);
                    }
                } else if (meterStatusCode.equals(getString(R.string.normal))) {
                    arrayForReasonCode = R.array.reader_status_normal_un_bill;
                    setReasonCodeSpinner(arrayForReasonCode);
                    edtKWHReading.setEnabled(true);
                    edtKVAHReading.setEnabled(true);
                    edtKVA.setEnabled(true);
                    edtKW.setEnabled(true);
                    edtPF.setEnabled(true);
                } else if (meterStatusCode.equals(getString(R.string.meter_status_faulty))) {
                    arrayForReasonCode = R.array.reader_status_faulty1;
                    setReasonCodeSpinner(arrayForReasonCode);
                    edtKWHReading.setEnabled(true);
                    edtKVAHReading.setEnabled(true);
                    edtKVA.setEnabled(true);
                    edtKW.setEnabled(true);
                    edtPF.setEnabled(true);
                } else if (meterStatusCode.equals(getString(R.string.rcnt))) {
                    arrayForReasonCode = R.array.reader_status_rcnt_for_newconsumer;
                    setReasonCodeSpinner(arrayForReasonCode);
                    edtKWHReading.setEnabled(false);
                    edtKVAHReading.setEnabled(false);
                    edtKVA.setEnabled(false);
                    edtKW.setEnabled(false);
                    edtPF.setEnabled(false);
                } else {
                    arrayForReasonCode = R.array.reader_status;
                    setReasonCodeSpinner(arrayForReasonCode);
                    edtKWHReading.setEnabled(true);
                    edtKVAHReading.setEnabled(true);
                    edtKVA.setEnabled(true);
                    edtKW.setEnabled(true);
                    edtPF.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(UnBillMeterReadingActivity.this, mMeterStatus.getSelectedItem().toString().trim(), Toast.LENGTH_SHORT).show();
            }

        });

        mReaderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String readerStatus = mReaderStatus.getSelectedItem().toString().trim();
                if (readerStatus.equals(getString(R.string.meter_bypassed)) || readerStatus.equals(getString(R.string.not_connected)) || readerStatus.equals(getString(R.string.meter_stopped))) {
                    edtKWHReading.setEnabled(false);
                    edtKVAHReading.setEnabled(false);
                    edtKVA.setEnabled(false);
                    edtKW.setEnabled(false);
                    edtPF.setEnabled(false);
                } else if (readerStatus.equals(getString(R.string.meter_tampered)) || readerStatus.equals(getString(R.string.meter_burnt)) || readerStatus.equals(getString(R.string.glass_broken)) || readerStatus.equals(getString(R.string.seal_broken)) || readerStatus.equals(getString(R.string.meter_damaged)) || readerStatus.equals(getString(R.string.display_defective))) {
                    edtKWHReading.setEnabled(true);
                    edtKVAHReading.setEnabled(true);
                    edtKVA.setEnabled(true);
                    edtKW.setEnabled(true);
                    edtPF.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
//                Toast.makeText(mContext, mMeterStatus.getSelectedItem().toString().trim(), Toast.LENGTH_SHORT).show();
            }

        });

        mSuspiciousActivity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                suspiciousActivityCode = mSuspiciousActivity.getSelectedItem().toString().trim();

                if (applyCondition) {
                    if (suspiciousActivityCode.equals(getString(R.string.suspicious_activity_status))) {
                        edtObservation.setText("");
                        edtObservation.setVisibility(View.GONE);
                    } else if (suspiciousActivityCode.equals(getString(R.string.svc_cable_completely_not_visible_from_pole_to_meter))) {
                        edtObservation.setText("");
                        edtObservation.setVisibility(View.GONE);
                    } else if (suspiciousActivityCode.equals(getString(R.string.cut_in_svc_cable))) {
                        edtObservation.setText("");
                        edtObservation.setVisibility(View.GONE);
                    } else if (suspiciousActivityCode.equals(getString(R.string.tamper_done_in_meter))) {
                        edtObservation.setText("");
                        edtObservation.setVisibility(View.GONE);
                    } else if (suspiciousActivityCode.equals(getString(R.string.parallel_svc_line_exist))) {
                        edtObservation.setText("");
                        edtObservation.setVisibility(View.GONE);
                    } else if (suspiciousActivityCode.equals(getString(R.string.other))) {
                        edtObservation.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mMeterType = (Spinner) findViewById(R.id.meter_type_spinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.meter_type)) {
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
        mMeterType.setAdapter(adapter2);
        mMeterType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (!mMeterType.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.update_meter_type)))
                    meterType = mMeterType.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
//                Toast.makeText(UnBillMeterReadingActivity.this, mMeterStatus.getSelectedItem().toString().trim(), Toast.LENGTH_SHORT).show();
            }

        });

        edtMobileNo.addTextChangedListener(new TextWatcher() {
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

        mMeterReadingImageName = "ME_" + consumer.consumer_no + "_" + consumer.meter_reader_id;
        mMeterReadingSPImageName = "SP_" + consumer.consumer_no + "_" + consumer.meter_reader_id;
        edtKWHReading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                int currentReading;
                if (count == 0) {
                    setDynamicValues(R.array.update_meter_status_blank);
                } else {
                    setDynamicValues(R.array.update_meter_status_withoutrcnt);
                }

                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (s.toString().isEmpty()) {

                        } else {

                        }
                    }
                };
                handler.postDelayed(runnable, 3000);
            }

            @Override
            public void afterTextChanged(final Editable s) {

            }
        });
        initAllUIComponents();

        if (consumer.bill_cycle_code.equals("00")) {
            edtKVAHReading.setVisibility(View.VISIBLE);
            edtKVA.setVisibility(View.VISIBLE);
            edtKW.setVisibility(View.VISIBLE);
            edtPF.setVisibility(View.VISIBLE);
        } else {
            edtKVAHReading.setVisibility(View.GONE);
            edtKVA.setVisibility(View.GONE);
            edtKW.setVisibility(View.GONE);
            edtPF.setVisibility(View.GONE);
        }

    }

    private void setDynamicValues(int s) {

        setMeterStatusSpinner(s);

    }

    private void setMeterStatusSpinner(int meterStatus) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(meterStatus)) {
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
        mMeterStatus.setAdapter(adapter);
    }

    private void initAllUIComponents() {

        consumerName = (consumer.consumer_name == null) ? "" : consumer.consumer_name;
        consumerAddress = (consumer.address == null) ? "" : consumer.address;
        consumerNumber = (consumer.consumer_no == null) ? "" : consumer.consumer_no;
        consumerMeterNo = (consumer.meter_no == null) ? "" : consumer.meter_no;
        consumerDtcNo = (consumer.route_code == null) ? "" : consumer.route_code;
        consumerPoleNo = (consumer.pole_no == null) ? "" : consumer.pole_no;
        consumerMobileNo = (consumer.mobile_no == null) ? "" : consumer.mobile_no;
        String[] temp1 = (consumer.zone_code).split("\\(", 2);
        String[] temp2 = temp1[1].split("\\)", 2);
        consumerZoneCode = temp2[0];
        txtConsumerMeterNumber.setText(consumerNumber + " | " + consumerMeterNo);
        txtConsumerMeterNumber.setSelected(true);
        edtPoleNo.setHint(getString(R.string.pole_number_colon) + " " + consumerPoleNo);
        edtMobileNo.setText(consumerMobileNo);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppConstants.CAMERA_RESULT_CODE:
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                if (mLastLocation != null) {
                    consumer.cur_lat = String.valueOf(mLastLocation.getLatitude());
                    consumer.cur_lng = String.valueOf(mLastLocation.getLongitude());

                } else {
                    consumer.cur_lat = "0";
                    consumer.cur_lng = "0";
                }

                mBitmapMeterReading = getBitmapScaled(METER_IMAGE_DIRECTORY_NAME, "ME_" + consumer.consumer_no + "_" + consumer.meter_reader_id);
                if (mBitmapMeterReading != null)
                    imgMeter.setImageBitmap(mBitmapMeterReading);

                break;
            case AppConstants.CAMERA_SUSPICIOUS_RESULT_CODE:

                mBitmapMeterSuspicious = getBitmapScaled(SUSPICIOUS_IMAGE_DIRECTORY_NAME, "SP_" + consumer.consumer_no + "_" + consumer.meter_reader_id);
                if (mBitmapMeterSuspicious != null)
                    imgSuspicious.setImageBitmap(mBitmapMeterSuspicious);
                break;

            case AppConstants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mLocationManagerReceiver.getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        mLocationManagerReceiver = new LocationManagerReceiver(UnBillMeterReadingActivity.this);
//                        LocationReceiver locationReceiver = new LocationReceiver(this);
                        break;
                }
                break;
        }
    }

    private Bitmap getBitmapScaled(String dirname, String filename) {
        Bitmap compressedImage = null;
        try {
            File file = getMeterFilePath(dirname, filename);
            compressedImage = new Compressor.Builder(this)
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
            e.printStackTrace();
        }
        return compressedImage;
    }

    public Uri getOutputMediaFileUri(String dirname, String filename) throws IOException {
        File file = getMeterFilePath(dirname, filename);
        return FileProvider.getUriForFile(this, this.getPackageName() + ".provider", file);
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
        return new File(mediaStorageDir.getPath() + File.separator + filename + ".jpg");
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onClick(View v) {

        //To show the details of consumer starts, Piyush : 02-03-17
        if (v == btnViewMore) {
            DialogCreator.showConsumerDetailsDialog(this, consumerName, consumerAddress, consumerNumber, consumerMeterNo, consumer.bill_cycle_code, consumer.zone_code, consumer.mobile_no, consumer.route_code, 0);
        }
        //To show the details of consumer ends, Piyush : 02-03-17

        //To show/hide suspicious view starts, Piyush : 02-03-17
        if (v == radioYes) {
            linearSuspicious.setVisibility(View.VISIBLE);
        }

        if (v == radioNo) {
            edtObservation.setText("");
            mBitmapMeterSuspicious = null;
            imgSuspicious.setImageBitmap(mBitmapMeterSuspicious);
            linearSuspicious.setVisibility(View.GONE);
        }
        //To show/hide suspicious view ends, Piyush : 02-03-17

        if (v == cameraMeter) {
            mLocationManagerReceiver = new LocationManagerReceiver(this);
//            if(CommonUtils.isNetworkAvaliable(this))
//            {
            createLocationRequest();
            startLocationUpdates();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri fileUri = null;
            try {
                fileUri = getOutputMediaFileUri(METER_IMAGE_DIRECTORY_NAME, mMeterReadingImageName);
                List<ResolveInfo> resolvedIntentActivities = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;
                    this.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, AppConstants.CAMERA_RESULT_CODE);
//            }else
//            Toast.makeText(this, R.string.error_internet_not_connected,Toast.LENGTH_LONG).show();
        }
        if (v == cameraSuspicious && (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri fileUri = null;
            try {
                fileUri = getOutputMediaFileUri(SUSPICIOUS_IMAGE_DIRECTORY_NAME, mMeterReadingSPImageName);
                List<ResolveInfo> resolvedIntentActivities = this.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                    String packageName = resolvedIntentInfo.activityInfo.packageName;
                    this.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, AppConstants.CAMERA_SUSPICIOUS_RESULT_CODE);
        }
        if (v == btnSubmit) {
//            if(CommonUtils.isNetworkAvaliable(this))
            doSubmitOps(0);
//           else
//            Toast.makeText(this, R.string.error_internet_not_connected,Toast.LENGTH_LONG).show();
        }
        if (v == img_back) {
            stopTimer();
            finish();
        }
        if (v == updateAboutmeter) {
            if (linearMeterGuidance.getVisibility() == View.GONE) {
                iv_updateAboutmeter.setRotation(180);
                linearMeterGuidance.setVisibility(View.VISIBLE);
            } else {
                CommonUtils.hideKeyBoard(mContext);
                edtAboutMeter.setText("");
                linearMeterGuidance.setVisibility(View.GONE);
                iv_updateAboutmeter.setRotation(0);
            }
        }

        if (v == updateindex) {
            if (linearMeterIndexing.getVisibility() == View.GONE) {
                iv_updateindex.setRotation(180);
                linearMeterIndexing.setVisibility(View.VISIBLE);
            } else {
                CommonUtils.hideKeyBoard(mContext);
                edtPoleNo.setText("");
                edtPoleNo.setHint(getString(R.string.pole_number_colon) + " " + consumerPoleNo);
                iv_updateindex.setRotation(0);
                linearMeterIndexing.setVisibility(View.GONE);
            }
        }

        if (v == btnRadioSuspiciousYes) {
            linearSuspicious.setVisibility(View.VISIBLE);
        }
        if (v == btnRadioSuspiciousNo) {
            edtObservation.setText("");
            mBitmapMeterSuspicious = null;
            imgSuspicious.setImageBitmap(mBitmapMeterSuspicious);
            linearSuspicious.setVisibility(View.GONE);
        }
        //To show/hide suspicious view ends, Piyush : 02-03-17


        if (v == btnRadioAirConditionerYes) {
            linearAirConditioner.setVisibility(View.VISIBLE);
        }

        if (v == btnRadioAirConditionerNo) {
            setNoOfAirConditionersSpinner(R.array.number_of_air_conditioners);
            linearAirConditioner.setVisibility(View.GONE);

        }

        if (v == updatemobileno) {
            if (linearMobileNumber.getVisibility() == View.GONE) {
                iv_updatemobileno.setRotation(180);
                linearMobileNumber.setVisibility(View.VISIBLE);
            } else {
                CommonUtils.hideKeyBoard(mContext);
                iv_updatemobileno.setRotation(0);
                linearMobileNumber.setVisibility(View.GONE);
                edtMobileNo.setText(consumer.mobile_no == null || consumer.mobile_no.equals("None") ? "" : consumer.mobile_no);
            }
        }

        if (v == updatemetertype) {
            if (linearMeterType.getVisibility() == View.GONE) {
                iv_updatemetertype.setRotation(180);
                linearMeterType.setVisibility(View.VISIBLE);
            } else {
                CommonUtils.hideKeyBoard(mContext);
                iv_updatemetertype.setRotation(0);
                linearMeterType.setVisibility(View.GONE);
                meterType = "";
            }
        }
    }

    private void doSubmitOps(int whereToGo) {
        String current_kwh = edtKWHReading.getText().toString().trim();
        String meterStatus = mMeterStatus.getSelectedItem().toString().trim();
        String readerStatus = mReaderStatus.getSelectedItem().toString().trim();
        String suspiciousActivityStatus = mSuspiciousActivity.getSelectedItem().toString().trim();


        String meter_image = null;
        if (((BitmapDrawable) imgMeter.getDrawable()) != null) {
            meter_image = CommonUtils.getBitmapEncodedString(mBitmapMeterReading);
            MeterImage meterImage = new MeterImage();
            meterImage.image = meter_image;
            consumer.meter_image = meterImage;
            consumer.isUploaded = "False";

            /*if(meterStatus.equals(getString(R.string.meter_status_mandatory)))
            {
                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_meter_status), "error");
            }
            else {
                if(readerStatus.equals(getString(R.string.reader_status_mandatory))) {
                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_reason_code), "error");
                }
                *//*else {
                    if(meterStatus.equals(getString(R.string.rcnt)) || readerStatus.equals(getString(R.string.meter_bypassed)) || readerStatus.equals(getString(R.string.not_connected)) || readerStatus.equals(getString(R.string.meter_stopped)))
                    {
                        if(radioGroup.getCheckedRadioButtonId() == R.id.rb_yes)
                        {
                            consumer.suspicious_activity = "True";
                            String encodedImage = "";
                            if(mBitmapMeterSuspicious != null) {
                                encodedImage = CommonUtils.getBitmapEncodedString(mBitmapMeterSuspicious);
                                MeterImage suspicious_activity_image = new MeterImage();
                                suspicious_activity_image.image = encodedImage;
                                consumer.suspicious_activity_image = suspicious_activity_image;
                                if(!edtObservation.getText().toString().isEmpty()) {
                                    String obs = edtObservation.getText().toString().trim();
                                    consumer.suspicious_remark = obs;
                                }
                                if(validateNumber())
                                {
                                    submitReading(whereToGo);
                                }
                            }
                            else
                            {
                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_meter_image), "error");
                            }
                        }
                        else
                        {
                            consumer.suspicious_activity = "False";
                            if(validateNumber())
                            {
                                submitReading(whereToGo);
                            }
                        }
                    }*//*
                    else{*/
            if (current_kwh.isEmpty()) {
                if (meterStatus.equals(getString(R.string.rcnt))) {
                    if (readerStatus.equals(getString(R.string.reader_status_mandatory))) {
                        DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_reason_code), "error");
                    } else {
                        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes) {
                            consumer.suspicious_activity = "True";
                            String encodedImage = "";
                            if (mBitmapMeterSuspicious != null) {
                                encodedImage = CommonUtils.getBitmapEncodedString(mBitmapMeterSuspicious);
                                MeterImage suspicious_activity_image = new MeterImage();
                                suspicious_activity_image.image = encodedImage;
                                consumer.suspicious_activity_image = suspicious_activity_image;



                                if (suspiciousActivityStatus.equals(getString(R.string.suspicious_activity_status))) {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_activity_status), "error");
                                } else if (suspiciousActivityStatus.equals(getString(R.string.other)) && edtObservation.getText().toString().isEmpty()) {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_enter_observation_or_choose_suspicious_activity), "error");
                                }else if (suspiciousActivityStatus.equals(getString(R.string.other)) && !edtObservation.getText().toString().trim().isEmpty()) {
                                    consumer.suspicious_remark = edtObservation.getText().toString().trim();
                                    if (validateNumber()) {
//                                    submitReading(current_kwh);
                                        checkAirConditioner(whereToGo);
                                    }
                                } else {
                                    consumer.suspicious_remark = suspiciousActivityStatus;
                                    if (validateNumber()) {
//                                    submitReading(current_kwh);
                                        checkAirConditioner(whereToGo);
                                    }
                                }
                            } else {
                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_meter_image), "error");
                            }
                        } else {
                            consumer.suspicious_activity = "False";
                            consumer.suspicious_remark = "";
                            if (validateNumber()) {
//                                showMessageDialogForLocation(whereToGo);
                                checkAirConditioner(whereToGo);
                            }
                        }
                    }
                } else
                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_meter_reading), "error");
            } else {
                if (meterStatus.equals(getString(R.string.meter_status_mandatory))) {
                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_meter_status), "error");
                } else {
                    if (readerStatus.equals(getString(R.string.reader_status_mandatory))) {
                        DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_reason_code), "error");
                    } else {
                        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes) {
                            consumer.suspicious_activity = "True";
                            String encodedImage = "";
                            if (mBitmapMeterSuspicious != null) {
                                encodedImage = CommonUtils.getBitmapEncodedString(mBitmapMeterSuspicious);
                                MeterImage suspicious_activity_image = new MeterImage();
                                suspicious_activity_image.image = encodedImage;
                                consumer.suspicious_activity_image = suspicious_activity_image;


                                if (suspiciousActivityStatus.equals(getString(R.string.suspicious_activity_status))) {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_activity_status), "error");
                                } else if (suspiciousActivityStatus.equals(getString(R.string.other)) && edtObservation.getText().toString().isEmpty()) {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_enter_observation_or_choose_suspicious_activity), "error");
                                }else if (suspiciousActivityStatus.equals(getString(R.string.other)) && !edtObservation.getText().toString().trim().isEmpty()) {
                                    consumer.suspicious_remark = edtObservation.getText().toString().trim();
                                    if (validateNumber()) {
//                                    submitReading(current_kwh);
                                        checkAirConditioner(whereToGo);
                                    }
                                } else {
                                    consumer.suspicious_remark = suspiciousActivityStatus;
                                    if (validateNumber()) {
//                                    submitReading(current_kwh);
                                        checkAirConditioner(whereToGo);
                                    }
                                }
                            } else {
                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_meter_image), "error");
                            }
                        } else {
                            consumer.suspicious_activity = "False";
                            consumer.suspicious_remark = "";
                            if (validateNumber()) {
//                                showMessageDialogForLocation(whereToGo);
                                checkAirConditioner(whereToGo);
                            }
                        }
                    }
                }
            }
        } else {
            DialogCreator.showMessageDialog(this, getString(R.string.blank_meter_reading_image), getString(R.string.error));
        }
    }

    private void submitReading(int whereToGo) {

        // Sequence Logic starts
        consumer.new_sequence = consumer.meter_reader_id + "|" + consumer.zone_code + "|" + consumer.bill_cycle_code + "|" + consumer.route_code + "|" + String.valueOf(String.format("%04d", assignedSequence));

        /*String route1 = AppPreferences.getInstance(mContext).getString(AppConstants.ROUTE_ID1, "");
        String route2 = AppPreferences.getInstance(mContext).getString(AppConstants.ROUTE_ID2, "");
        String route3 = AppPreferences.getInstance(mContext).getString(AppConstants.ROUTE_ID3, "");
        String route4 = AppPreferences.getInstance(mContext).getString(AppConstants.ROUTE_ID4, "");

        String prv_sequence_saved1 = AppPreferences.getInstance(mContext).getString(AppConstants.PRV_SEQUENCE1, "");
        String prv_sequence_saved2 = AppPreferences.getInstance(mContext).getString(AppConstants.PRV_SEQUENCE2, "");
        String prv_sequence_saved3 = AppPreferences.getInstance(mContext).getString(AppConstants.PRV_SEQUENCE3, "");
        String prv_sequence_saved4 = AppPreferences.getInstance(mContext).getString(AppConstants.PRV_SEQUENCE4, "");

        if(route1.equals(consumer.route_code))
        {
            if(prv_sequence_saved1.isEmpty())
            {
                consumer.new_sequence = consumer.meter_reader_id+"|"+consumer.zone_code+"|"+consumer.bill_cycle_code+"|"+consumer.route_code+"|"+"0001";
                AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE1, "0001");
            }
            else
            {
                int newSequence = Integer.parseInt(prv_sequence_saved1) + 1;
                consumer.new_sequence = consumer.meter_reader_id+"|"+consumer.zone_code+"|"+consumer.bill_cycle_code+"|"+consumer.route_code+"|"+String.valueOf(String.format("%04d",newSequence));
                AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE1, String.valueOf(newSequence));
            }
        }
        else if(route2.equals(consumer.route_code))
        {
            if(prv_sequence_saved2.isEmpty())
            {
                consumer.new_sequence = consumer.meter_reader_id+"|"+consumer.zone_code+"|"+consumer.bill_cycle_code+"|"+consumer.route_code+"|"+"0001";
                AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE2, "0001");

            }
            else
            {
                int newSequence = Integer.parseInt(prv_sequence_saved2) + 1;
                consumer.new_sequence = consumer.meter_reader_id+"|"+consumer.zone_code+"|"+consumer.bill_cycle_code+"|"+consumer.route_code+"|"+String.valueOf(String.format("%04d",newSequence));
                AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE2, String.valueOf(newSequence));
            }
        }
        else if(route3.equals(consumer.route_code))
        {
            if(prv_sequence_saved3.isEmpty())
            {
                consumer.new_sequence = consumer.meter_reader_id+"|"+consumer.zone_code+"|"+consumer.bill_cycle_code+"|"+consumer.route_code+"|"+"0001";
                AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE3, "0001");
            }
            else
            {
                int newSequence = Integer.parseInt(prv_sequence_saved3) + 1;
                consumer.new_sequence = consumer.meter_reader_id+"|"+consumer.zone_code+"|"+consumer.bill_cycle_code+"|"+consumer.route_code+"|"+String.valueOf(String.format("%04d",newSequence));
                AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE3, String.valueOf(newSequence));
            }
        }
        else if(route4.equals(consumer.route_code))
        {
            if(prv_sequence_saved4.isEmpty())
            {
                consumer.new_sequence = consumer.meter_reader_id+"|"+consumer.zone_code+"|"+consumer.bill_cycle_code+"|"+consumer.route_code+"|"+"0001";
                AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE4, "0001");
            }
            else
            {
                int newSequence = Integer.parseInt(prv_sequence_saved4) + 1;
                consumer.new_sequence = consumer.meter_reader_id+"|"+consumer.zone_code+"|"+consumer.bill_cycle_code+"|"+consumer.route_code+"|"+String.valueOf(String.format("%04d",newSequence));
                AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE4, String.valueOf(newSequence));
            }
        }*/

        consumer.meter_status = meterStatusCode;
        consumer.reader_status = mReaderStatus.getSelectedItem().toString().trim();
        consumer.location_guidance = edtAboutMeter.getText().toString().trim();

        String kvah = edtKVAHReading.getText().toString();
        String kva = edtKVA.getText().toString();
        String kw = edtKW.getText().toString();
        String pf = edtPF.getText().toString();

        consumer.current_kvah_reading = kvah.equals("") || kvah == null ? "0" : kvah;
        consumer.current_kva_reading = kva.equals("") || kva == null ? "0" : kva;
        consumer.current_kw_reading = kw.equals("") || kw == null ? "0" : kw;
        consumer.current_pf_reading = pf.equals("") || pf == null ? "0" : pf;
        consumer.current_meter_reading = edtKWHReading.getText().toString().trim();
        consumer.panel_no = edtPanelNo.getText().toString().trim();
        consumer.zone_code = consumerZoneCode;





        if (btnRadioAirConditionerNo.isChecked()) {
            consumer.air_conditioner_exist = "0";
        }

        if (btnRadioPlasticCutYes.isChecked()) {
            consumer.is_plastic_cover_cut = "True";
        } else if (btnRadioPlasticCutNo.isChecked()){
            consumer.is_plastic_cover_cut = "False";
        }




        if (validateNumber()) {
            consumer.mobile_no = edtMobileNo.getText().toString().trim();
        }
        consumer.iskvahroundcompleted = "0";
        consumer.iskwhroundcompleted = "0";

        if (consumer.meter_type == null)
            consumer.meter_type = "";
        if (meterStatusCode.equalsIgnoreCase(getString(R.string.rcnt))) {
            consumer.current_meter_reading = "0";
        }
        consumer.pole_no = (edtPoleNo.getText().toString().trim() != null && !edtPoleNo.getText().toString().trim().equals("")) ? edtPoleNo.getText().toString().trim() : consumerPoleNo;
        consumer.reader_remark_comment = edtComments.getText().toString();
        consumer.isUploaded = "False";

        Sequence updateSequence = new Sequence();
        updateSequence.meter_reader_id = consumer.meter_reader_id;
        updateSequence.cycle_code = consumer.bill_cycle_code;
        updateSequence.route_code = consumer.route_code;
        updateSequence.zone_code = consumer.zone_code;
        updateSequence.sequence = String.valueOf(++assignedSequence);
        DatabaseManager.UpdateSequence(mContext, updateSequence);


        //delete saved images folder
        File meterFile = new File(Environment.getExternalStorageDirectory(), METER_IMAGE_DIRECTORY_NAME);
        if (meterFile.isDirectory()) {
            String[] children = meterFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(meterFile, children[i]).delete();
            }
            meterFile.delete();
        }
        File spFile = new File(Environment.getExternalStorageDirectory(), SUSPICIOUS_IMAGE_DIRECTORY_NAME);
        if (spFile.isDirectory()) {
            String[] children = spFile.list();
            for (int i = 0; i < children.length; i++) {
                new File(spFile, children[i]).delete();
            }
            spFile.delete();
        }
        consumer.time_taken = stopTimer();

        if (whereToGo == 0) {
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            PackageManager pm = this.getPackageManager();
            boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
            if (isGPSEnabled && hasGps) {
                mBitmapMeterReading = null;
                mBitmapMeterSuspicious = null;
                //Save it into the DB
                //Changes For Online Reading Starts Avinesh 6-04-17
//                mLocationManagerReceiver.saveConsumerWithLatLng(this, consumer, true);

                consumer.reading_taken_by = App.ConsumerAddedBy;
                consumer.reading_date = CommonUtils.getCurrentDateTime();

                DatabaseManager.saveUnbillConsumer(mContext, consumer);
                //Changes For Online Reading Ends Avinesh 6-04-17

                Toast.makeText(UnBillMeterReadingActivity.this, getString(R.string.readings_punched_successfully), Toast.LENGTH_LONG).show();
                finish();
                if (AddNewConsumerActivity.addNewConsumerActivity != null) {
                    AddNewConsumerActivity.addNewConsumerActivity.finish();
                }
            } else if (isNetworkEnabled && !hasGps) {
                mBitmapMeterReading = null;
                mBitmapMeterSuspicious = null;
                //Save it into the DB

                mLocationManagerReceiver.saveConsumerWithLatLng(this, consumer, true);
                Toast.makeText(UnBillMeterReadingActivity.this, getString(R.string.readings_punched_successfully), Toast.LENGTH_LONG).show();
                finish();
                if (AddNewConsumerActivity.addNewConsumerActivity != null) {
                    AddNewConsumerActivity.addNewConsumerActivity.finish();
                }
            } else
                mLocationManagerReceiver = new LocationManagerReceiver(this);
        } else if (whereToGo == 1) {
            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            PackageManager pm = this.getPackageManager();
            boolean hasGps = pm.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);
            if (isGPSEnabled && hasGps) {
                mBitmapMeterReading = null;
                mBitmapMeterSuspicious = null;
                mLocationManagerReceiver.saveConsumerWithLatLng(this, consumer, false);
                Toast.makeText(this, getString(R.string.readings_punched_successfully), Toast.LENGTH_LONG).show();
                Intent qrCodeIntent = new Intent(this, SearchActivity.class);
                qrCodeIntent.putExtra(AppConstants.CURRENT_METER_READER_ID, LandingActivity.meter_reader_id);
                startActivity(qrCodeIntent);
                if (AddNewConsumerActivity.addNewConsumerActivity != null) {
                    AddNewConsumerActivity.addNewConsumerActivity.finish();
                }
                this.finish();

            } else if (isNetworkEnabled && !hasGps) {
                mBitmapMeterReading = null;
                mBitmapMeterSuspicious = null;
                mLocationManagerReceiver.saveConsumerWithLatLng(this, consumer, false);
                Toast.makeText(this, getString(R.string.readings_punched_successfully), Toast.LENGTH_LONG).show();
                if (App.ConsumerAddedBy == getString(R.string.meter_reading_qr_code)) {
                    Intent qrCodeIntent = new Intent(this, SearchActivity.class);
                    qrCodeIntent.putExtra(AppConstants.CURRENT_METER_READER_ID, LandingActivity.meter_reader_id);
                    startActivity(qrCodeIntent);
                }
                if (AddNewConsumerActivity.addNewConsumerActivity != null) {
                    AddNewConsumerActivity.addNewConsumerActivity.finish();
                }
                this.finish();
            } else
                mLocationManagerReceiver = new LocationManagerReceiver(this);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes)
            edtObservation.setEnabled(true);
        else
            edtObservation.setEnabled(false);
    }

    private boolean validateNumber() {
        Pattern pattern1, pattern2;
        Matcher matcher1, matcher2;
        final String PHONE_PATTERN1 = "^[7-9][0-9]{9}$";
        final String PHONE_PATTERN2 = "";
        pattern1 = Pattern.compile(PHONE_PATTERN1);
        pattern2 = Pattern.compile(PHONE_PATTERN2);
        String phone = edtMobileNo.getText().toString().trim();
        matcher1 = pattern1.matcher(phone);
        matcher2 = pattern2.matcher(phone);

        if (matcher1.matches() || matcher2.matches()) {
            edtMobileNo.setError(null);
            return true;
        } else {
            edtMobileNo.setError(getString(R.string.enter_valid_mobile_no));
            return false;
        }
    }

    private void setReasonCodeSpinner(int reasonCode) {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(reasonCode)) {
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
        mReaderStatus.setAdapter(adapter1);
    }

    private void setNoOfAirConditionersSpinner(int noOfAirConditioners) {
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(noOfAirConditioners)) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                return v;
            }
        };
        mNoOfAirConditioners.setAdapter(adapter2);
        mNoOfAirConditioners.setSelection(0);
    }

    private void setSuspiciousActivitySpinner(int suspiciousActivity) {
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(suspiciousActivity)) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                return v;
            }
        };
        mSuspiciousActivity.setAdapter(adapter3);
        mSuspiciousActivity.setSelection(0);
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
        checkPlayServices();
        mLocationManagerReceiver = new LocationManagerReceiver(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTimer();
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

    public void checkAirConditioner( int whereToGo) {

        if (btnRadioAirConditionerYes.isChecked()) {
            consumer.air_conditioner_exist = "True";
            if (mNoOfAirConditioners.getSelectedItem().toString().trim().equals(getString(R.string.enter_number_of_air_conditioners).trim())) {
                dismissLoadingDialog();
                DialogCreator.showMessageDialog(mContext, getString(R.string.please_enter_the_number_of_air_conditioners), "error");
            } else {

                consumer.no_of_air_conditioners = mNoOfAirConditioners.getSelectedItem().toString().trim();
                showMessageDialogForLocation(whereToGo);
            }
        }
        else {
            consumer.air_conditioner_exist = "0";
            showMessageDialogForLocation(whereToGo);
        }
    }


    private void showMessageDialogForLocation(final int whereToGo) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_database, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(this).create();

        final EditText edtUserName, edtPassword;
        final TextView txtTitle, txtError;

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtTitle.setText("Is Meter Located Outside of Premise ?");
        txtTitle.setTypeface(regular);
        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);
        txtError.setVisibility(View.GONE);
        edtUserName = (EditText) promptView.findViewById(R.id.edt_user_name);
        edtUserName.setVisibility(View.GONE);
        edtPassword = (EditText) promptView.findViewById(R.id.edt_password);
        edtPassword.setVisibility(View.GONE);

        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setText("Yes");
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                consumer.meter_location = "Outside";
                showMessageDialogForMeterType(whereToGo);

                alert.dismiss();
            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setText("No");
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                consumer.meter_location = "Inside";
                showMessageDialogForMeterType(whereToGo);
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.setCancelable(false);
        if (mReaderStatus.getSelectedItem().toString().trim().equals(getString(R.string.site_not_found)) || mReaderStatus.getSelectedItem().toString().trim().equals(getString(R.string.reading_not_taken)))
            showMessageDialogForMeterType(whereToGo);
        else
            alert.show();
    }

    private void showMessageDialogForMeterType(final int whereToGo) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_database, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(this).create();

        final EditText edtUserName, edtPassword;
        final TextView txtTitle, txtError;

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtTitle.setText("Select Meter Type");
        txtTitle.setTypeface(regular);
        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);
        txtError.setVisibility(View.GONE);
        edtUserName = (EditText) promptView.findViewById(R.id.edt_user_name);
        edtUserName.setVisibility(View.GONE);
        edtPassword = (EditText) promptView.findViewById(R.id.edt_password);
        edtPassword.setVisibility(View.GONE);

        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                ok.getLayoutParams();
        params.weight = 0.5f;
        ok.setLayoutParams(params);
        ok.setTypeface(regular);
        ok.setText("Mechanical");
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                consumer.meter_type = "Analog";
                submitReading(whereToGo);
                alert.dismiss();
            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setBackground(getResources().getDrawable(R.drawable.positive_button));
        cancel.setText("Digital");
        cancel.setLayoutParams(params);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                consumer.meter_type = "Digital";
                submitReading(whereToGo);
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.setCancelable(false);
        if (mReaderStatus.getSelectedItem().toString().trim().equals(getString(R.string.obstacle)) || mReaderStatus.getSelectedItem().toString().trim().equals(getString(R.string.meter_stolen)))
            submitReading(whereToGo);
        else
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
