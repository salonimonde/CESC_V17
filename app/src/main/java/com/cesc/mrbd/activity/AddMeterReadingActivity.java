package com.cesc.mrbd.activity;

import android.Manifest;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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
import com.cesc.mrbd.models.JobCard;
import com.cesc.mrbd.models.MeterImage;
import com.cesc.mrbd.models.MeterReading;
import com.cesc.mrbd.models.Sequence;
import com.cesc.mrbd.models.UserProfile;
import com.cesc.mrbd.preferences.SharedPrefManager;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.AppPreferences;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import id.zelory.compressor.Compressor;

import static android.R.id.message;

public class AddMeterReadingActivity extends ParentActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static int UPDATE_INTERVAL = 500; // Location updates intervals in sec 10 sec
    private static int FASTEST_INTERVAL = 500; // Location updates intervals in sec 5 sec
    private static int DISPLACEMENT = 0; // 1 meters
    private final String METER_IMAGE_DIRECTORY_NAME = "meter";
    private final String SUSPICIOUS_IMAGE_DIRECTORY_NAME = "suspicious";
    private Context mContext;
    private LinearLayout linearSuspicious, linearMeterIndexing, linearAirConditioner, linearCategoryRemark;
    private EditText edtObservation, edtComments, edtAboutMeter, edtPoleNo, edtKVAHReading,
            edtKWHReading, edtKVA, edtPanelNo, edtMobileNo, edtMob, edtMeterNo, edtKW, edtPF,
            edtConsumerCategoryRemarks;
    private ImageView cameraSuspicious, imgSuspicious, imgMeter, cameraMeter, btnViewMore;
    private Toast toast;
    private int previousMeterReading = 0, advanceReading = 0, avgConsumption = 0, meterDigits = 0, assignedSequence = 0;
    private JobCard userJobCard;
    private TextView txtSuspiciousActivity, lblSusiImg, lblMeterReading, txtMeterStatus, txtReaderStatus,
            txtConsumerMeterNumber, txtAboutMeter, txtMeterIndexing, txtMobileNumber, txtMeterType, txtAirConditionerExist,
            txtConsumerCategory, txtChange, txtUnchange, txtPlasticCoverCut;
    private Spinner mMeterStatus, mReaderStatus, mMeterType, mMeterImageReadable, mNoOfAirConditioners, mSuspiciousActivity;
    private Button mSubmitAndNextBtn;
    private RadioGroup radioGroup, airConditionYesNo, plasticCutYesNo;
    private Typeface regular, bold;
    private String mMeterReadingImageName, meterStatusCode, readerStatusCode, mMeterReadingSPImageName, previous_status = "",
            prv_sequence, mMeterReaderId = "", meterType = "", mobileNo = "", meterImageSelected = "",
            meterStatusOnDialog = "", suspiciousActivityCode;
    private Bitmap mBitmapMeterSuspicious = null, mBitmapMeterReading = null;
    private LocationManagerReceiver mLocationManagerReceiver;
    private RadioButton btnRadioSuspiciousYes, btnRadioSuspiciousNo,
            btnMeterIndexingNo, btnMeterIndexingYes, btnMobileNumberNo, btnMobileNumberYes,
            btnRadioAirConditionerYes, btnRadioAirConditionerNo,
            btnRadioPlasticCutNo, btnRadioPlasticCutYes;
    private String consumerName = "", consumerAddress = "", consumerNumber = "", consumerMeterNo = "", phoneNo = "",
            faultyStatus = "", start_date = "", end_date = "";
    private UserProfile userProfile;
    private int arrayForReasonCode = R.array.reader_status,
            arrayForMeterStatus = R.array.update_meter_status_all;
    private AlertDialog alertForAbnormalReading, alertForOverConsumption, alertForRoundComplete;
    public AlertDialog alertForPreviousStatusCheck;
    private boolean applyCondition = true;
    private MeterReading meterReading;
    private MeterImage suspicious_activity_image;
    private RelativeLayout updateMeterType, updateMobileNo, updateAboutMeter, updateIndex;
    private ImageView iv_updateindex;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private int currentReading = 0, meterDigit5 = 95000, meterDigit6 = 995000, meterDigit7 = 9995000, meterDigit8 = 99995000, maxLimit = 5000;
    private Handler handler;
    private Runnable runnable;
    private boolean valid = false;
    private ArrayList<Sequence> localSequence;
    private ImageView imgBack, imgMap, imgRouteImage, imgCall;
    private TextView txtToolbarTitle;
    private String latitude = "", longitude = "";
    private String newLatitude = "", newLongitude = "";
    private DecimalFormat df;
    private String latLongOK;
    double distance = 0.0;
    private String locationIssue;
    private double prvLat;
    private double prvLong;
    boolean isCheckValidation = false;

    Timer timer;
    long timerInterval = 1000; //1 second
    long timerDelay = 1000; //1 second
    int Count = 0;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meter_reading);
        mContext = this;
        startTimer();
        df = new DecimalFormat("#.####");
//        start_date = CommonUtils.getCurrentDateTime();
        meterReading = new MeterReading();
        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();
        mLocationManagerReceiver = new LocationManagerReceiver(mContext);
        alertForAbnormalReading = new AlertDialog.Builder(mContext).create();
        alertForOverConsumption = new AlertDialog.Builder(mContext).create();
        alertForRoundComplete = new AlertDialog.Builder(mContext).create();
        alertForPreviousStatusCheck = new AlertDialog.Builder(mContext).create();
        lblSusiImg = (TextView) findViewById(R.id.lbl_Sus_img);
        lblSusiImg.setTypeface(bold);
       /* lblMeterReading = (TextView) findViewById(R.id.lbl_meterreading);
        lblMeterReading.setTypeface(bold);*/
        txtMeterStatus = (TextView) findViewById(R.id.txt_meter_status);
        txtMeterStatus.setTypeface(bold);
        txtReaderStatus = (TextView) findViewById(R.id.txt_reader_status);
        txtReaderStatus.setTypeface(bold);
        txtSuspiciousActivity = (TextView) findViewById(R.id.txt_suspicious_activity);
        txtSuspiciousActivity.setTypeface(bold);

        txtPlasticCoverCut = (TextView) findViewById(R.id.txt_is_plastic_cover_cut);
        txtPlasticCoverCut.setTypeface(bold);


        txtAirConditionerExist = (TextView) findViewById(R.id.txt_air_conditioner_exist);
        txtAirConditionerExist.setTypeface(bold);

        txtConsumerCategory = (TextView) findViewById(R.id.txt_consumer_category);
        txtConsumerCategory.setTypeface(bold);

        txtChange = (TextView) findViewById(R.id.txt_change);
        txtChange.setTypeface(bold);
        txtChange.setOnClickListener(this);

        txtUnchange = (TextView) findViewById(R.id.txt_unchange);
        txtUnchange.setTypeface(bold);
        txtUnchange.setOnClickListener(this);


        txtConsumerMeterNumber = (TextView) findViewById(R.id.txt_consumer_meter_number);
        txtConsumerMeterNumber.setTypeface(bold);
        imgSuspicious = (ImageView) findViewById(R.id.img_suspicious);
        cameraSuspicious = (ImageView) findViewById(R.id.camera_suspicious);
        cameraSuspicious.setOnClickListener(this);
        radioGroup = (RadioGroup) findViewById(R.id.rg_yesno);
        radioGroup.setOnCheckedChangeListener(this);

        airConditionYesNo = (RadioGroup) findViewById(R.id.rg_air_condition_yesno);
        airConditionYesNo.setOnCheckedChangeListener(this);

        plasticCutYesNo = (RadioGroup) findViewById(R.id.rg_plastic_cut_yesno);
        radioGroup.setOnCheckedChangeListener(this);


        imgMeter = (ImageView) findViewById(R.id.img_meter);
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }
        imgMeter = (ImageView) findViewById(R.id.img_meter);
        cameraMeter = (ImageView) findViewById(R.id.camera_meter);
        cameraMeter.setOnClickListener(this);
        edtObservation = (EditText) findViewById(R.id.edt_observation);
        edtObservation.setTypeface(bold);
        edtObservation.setVisibility(View.GONE);

        edtConsumerCategoryRemarks = (EditText) findViewById(R.id.edt_consumer_category_remarks);
        edtConsumerCategoryRemarks.setTypeface(bold);

        mSubmitAndNextBtn = (Button) findViewById(R.id.submit_and_next);
        mSubmitAndNextBtn.setTypeface(regular);
        mSubmitAndNextBtn.setOnClickListener(this);
        edtComments = (EditText) findViewById(R.id.comments);
        edtComments.setTypeface(bold);

        mMeterStatus = (Spinner) findViewById(R.id.meter_status_spinner);
        setMeterStatusSpinner(R.array.update_meter_status_all);
        mReaderStatus = (Spinner) findViewById(R.id.reader_status_spinner);
        setReasonCodeSpinner(arrayForReasonCode);

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


        linearSuspicious = (LinearLayout) findViewById(R.id.linear_suspicious);
        linearSuspicious.setVisibility(View.GONE);

        linearAirConditioner = (LinearLayout) findViewById(R.id.linear_air_conditioner);
        linearAirConditioner.setVisibility(View.GONE);

        linearCategoryRemark = (LinearLayout) findViewById(R.id.linear_category_remark);
        linearCategoryRemark.setVisibility(View.GONE);


        btnViewMore = (ImageView) findViewById(R.id.btn_view_more);
        btnViewMore.setOnClickListener(this);
       /* updateAboutMeter = (RelativeLayout) findViewById(R.id.rlupdatelocation);
        updateAboutMeter.setOnClickListener(this);*/
        updateIndex = (RelativeLayout) findViewById(R.id.rlupdatemeterindex);
        updateIndex.setOnClickListener(this);
       /* updateMobileNo = (RelativeLayout) findViewById(R.id.rlupdatemobile);
        updateMobileNo.setOnClickListener(this);*/
        updateMeterType = (RelativeLayout) findViewById(R.id.rlmetertype);
        updateMeterType.setOnClickListener(this);
      /*  txtAboutMeter = (TextView) findViewById(R.id.txt_about_meter);
        txtAboutMeter.setTypeface(bold);*/
        edtAboutMeter = (EditText) findViewById(R.id.edt_about_meter);
        edtAboutMeter.setTypeface(bold);
        edtPoleNo = (EditText) findViewById(R.id.edt_pole_no);
        edtPoleNo.setTypeface(bold);
        //New fields for CESC starts, Piyush : 22/03/2017

        edtKVAHReading = (EditText) findViewById(R.id.edt_kvah_reading);
        edtKVAHReading.setTypeface(bold);
        edtKWHReading = (EditText) findViewById(R.id.edt_kwh_reading);
        edtKWHReading.setTypeface(bold);
        edtKWHReading.setEnabled(true);
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
        btnMeterIndexingNo = (RadioButton) findViewById(R.id.btn_radio_index_no);
        btnMeterIndexingNo.setTypeface(bold);
        btnMeterIndexingNo.setOnClickListener(this);
        btnMeterIndexingYes = (RadioButton) findViewById(R.id.btn_radio_index_yes);
        btnMeterIndexingYes.setTypeface(bold);
        btnMeterIndexingYes.setOnClickListener(this);
        imgMap = (ImageView) findViewById(R.id.img_map);
        imgMap.setOnClickListener(this);

        /*txtMobileNumber = (TextView) findViewById(R.id.txt_mobile_number);
        txtMobileNumber.setTypeface(bold);*/
       /* btnMobileNumberNo = (RadioButton) findViewById(R.id.btn_radio_mobile_no);
        btnMobileNumberNo.setTypeface(bold);
        btnMobileNumberNo.setOnClickListener(this);*/
       /* btnMobileNumberYes = (RadioButton) findViewById(R.id.btn_radio_mobile_yes);
        btnMobileNumberYes.setTypeface(bold);
        btnMobileNumberYes.setOnClickListener(this);*/

        txtMeterType = (TextView) findViewById(R.id.txt_meter_type);
        txtMeterType.setTypeface(bold);
        iv_updateindex = (ImageView) findViewById(R.id.updateindex);

        txtToolbarTitle = (TextView) findViewById(R.id.txt_toolbar_title);
        txtToolbarTitle.setTypeface(regular);

        imgCall = (ImageView) findViewById(R.id.img_call);
        imgCall.setOnClickListener(this);
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        imgMap = (ImageView) findViewById(R.id.img_map);
        imgMap.setVisibility(View.VISIBLE);
        imgMap.setOnClickListener(this);
        imgRouteImage = (ImageView) findViewById(R.id.img_route);
        imgRouteImage.setVisibility(View.VISIBLE);
        imgRouteImage.setOnClickListener(this);
        //New fields for CESC ends, Piyush : 22/03/2017

        TextView txtObservations = (TextView) findViewById(R.id.txt_observation);
        txtObservations.setTypeface(regular);
        TextView txtComments = (TextView) findViewById(R.id.txt_comments);
        txtComments.setTypeface(bold);


        Intent i = getIntent();
        if (i != null) {
            if (userJobCard == null) {
                userJobCard = (JobCard) i.getSerializableExtra(AppConstants.CURRENT_JOB_CARD);
                AppPreferences.getInstance(mContext).putString(AppConstants.DESTINATION_LAT, userJobCard.lattitude);
                AppPreferences.getInstance(mContext).putString(AppConstants.DESTINATION_LONG, userJobCard.longitude);
                if (App.ReadingTakenBy == getString(R.string.meter_reading_qr_code)) {
                    toast = Toast.makeText(mContext, getString(R.string.one_consumer_matched), Toast.LENGTH_SHORT);
                    View toastView = toast.getView();
                    TextView toastMessage = (TextView) toastView.findViewById(message);
                    toastMessage.setTextSize(10);
                    toastMessage.setTextColor(CommonUtils.getColor(mContext, R.color.colorWhite));
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        toastView.setBackgroundColor(CommonUtils.getColor(mContext, R.color.black));
                    }
                    toast.show();
                }
                /*userJobCard.snf="false";
                userJobCard.pdc="false";*/
            }
        }

        final Sequence getSeq = new Sequence();
        getSeq.meter_reader_id = userJobCard.meter_reader_id;
        getSeq.zone_code = userJobCard.zone_code;
        getSeq.route_code = userJobCard.route_code;
        getSeq.cycle_code = userJobCard.bill_cycle_code;

        localSequence = DatabaseManager.getSequence(mContext, getSeq);
        if (localSequence != null) {
            assignedSequence = Integer.parseInt(localSequence.get(0).sequence);
        }
        initAllUIComponents();

        mMeterStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //            alertForPreviousStatusCheck = new AlertDialog.Builder(mContext).create();
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                meterStatusCode = mMeterStatus.getSelectedItem().toString().trim();
                String value = edtKWHReading.getText().toString().trim();
                if (value.isEmpty()) {
                    faultyStatus = "blank";
                }

                if (applyCondition) {
                    if (meterStatusCode.equals(getString(R.string.normal))) {
                        arrayForReasonCode = R.array.reader_status_normal;
                        setReasonCodeSpinner(arrayForReasonCode);
                        if (faultyStatus.equals("less")) {
                            arrayForReasonCode = R.array.reader_status_normal;
                            setReasonCodeSpinner(arrayForReasonCode);
                        }
                        if (faultyStatus.equals("greater")) {
                            arrayForReasonCode = R.array.reader_status_normal;
                            setReasonCodeSpinner(arrayForReasonCode);
                        }
                        edtKWHReading.setEnabled(true);
                        edtKVAHReading.setEnabled(true);
                        edtKVA.setEnabled(true);
                        edtKW.setEnabled(true);
                        edtPF.setEnabled(true);
                    } else if (meterStatusCode.equals(getString(R.string.meter_status_faulty))) {
                        if (faultyStatus.equals("greater")) {
                            arrayForReasonCode = R.array.reader_status_faulty1;
                            setReasonCodeSpinner(arrayForReasonCode);
                        } else if (faultyStatus.equals("equal")) {
                            arrayForReasonCode = R.array.reader_status_faulty1;
                            setReasonCodeSpinner(arrayForReasonCode);
                        } else if (faultyStatus.equals("less")) {
                            arrayForReasonCode = R.array.reader_status_faulty2;
                            setReasonCodeSpinner(arrayForReasonCode);
                        } else if (faultyStatus.equals("blank")) {
                            arrayForReasonCode = R.array.reader_status_faulty;
                            setReasonCodeSpinner(arrayForReasonCode);
                        }
                        edtKWHReading.setEnabled(true);
                        edtKVAHReading.setEnabled(true);
                        edtKVA.setEnabled(true);
                        edtKW.setEnabled(true);
                        edtPF.setEnabled(true);
                    } else if (meterStatusCode.equals(getString(R.string.rcnt)) && userJobCard.snf.equalsIgnoreCase("True") && userJobCard.pdc.equalsIgnoreCase("True")) {
                        arrayForReasonCode = R.array.reader_status_rcnt_pdc_snf;
                        setReasonCodeSpinner(arrayForReasonCode);
                        edtKWHReading.setEnabled(false);
                        edtKVAHReading.setEnabled(false);
                        edtKVA.setEnabled(false);
                        edtKW.setEnabled(false);
                        edtPF.setEnabled(false);
                    } else if (meterStatusCode.equals(getString(R.string.rcnt)) && userJobCard.snf.equalsIgnoreCase("False") && userJobCard.pdc.equalsIgnoreCase("True")) {
                        arrayForReasonCode = R.array.reader_status_rcnt_pdc_without_snf;
                        setReasonCodeSpinner(arrayForReasonCode);
                        edtKWHReading.setEnabled(false);
                        edtKVAHReading.setEnabled(false);
                        edtKVA.setEnabled(false);
                        edtKW.setEnabled(false);
                        edtPF.setEnabled(false);
                    } else if (meterStatusCode.equals(getString(R.string.rcnt)) && userJobCard.pdc.equalsIgnoreCase("False") && userJobCard.snf.equalsIgnoreCase("True")) {
                        arrayForReasonCode = R.array.reader_status_rcnt_snf_without_pdc;
                        setReasonCodeSpinner(arrayForReasonCode);
                        edtKWHReading.setEnabled(false);
                        edtKVAHReading.setEnabled(false);
                        edtKVA.setEnabled(false);
                        edtKW.setEnabled(false);
                        edtPF.setEnabled(false);
                    } else if (meterStatusCode.equals(getString(R.string.rcnt)) && userJobCard.pdc.equalsIgnoreCase("False") && userJobCard.pdc.equalsIgnoreCase("False")) {
                        arrayForReasonCode = R.array.reader_status_rcnt_without_snf_pdc;
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                Toast.makeText(mContext, mMeterStatus.getSelectedItem().toString().trim(), Toast.LENGTH_SHORT).show();
            }

        });

        mReaderStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                alertForPreviousStatusCheck = new AlertDialog.Builder(mContext).create();
                readerStatusCode = mReaderStatus.getSelectedItem().toString().trim();
                if (readerStatusCode.equals(getString(R.string.meter_bypassed)) || readerStatusCode.equals(getString(R.string.not_connected)) || readerStatusCode.equals(getString(R.string.meter_stopped))) {
                    edtKWHReading.setText("");
                    edtKVAHReading.setText("");
                    edtKVA.setText("");
                    edtKW.setText("");
                    edtPF.setText("");
                    edtKWHReading.setEnabled(false);
                    edtKVAHReading.setEnabled(false);
                    edtKVA.setEnabled(false);
                    edtKW.setEnabled(false);
                    edtPF.setEnabled(false);
                } else if (readerStatusCode.equals(getString(R.string.meter_tampered)) || readerStatusCode.equals(getString(R.string.meter_burnt)) || readerStatusCode.equals(getString(R.string.glass_broken)) || readerStatusCode.equals(getString(R.string.seal_broken)) || readerStatusCode.equals(getString(R.string.meter_damaged)) || readerStatusCode.equals(getString(R.string.display_defective))) {
                    edtKWHReading.setEnabled(true);
                    edtKVAHReading.setEnabled(true);
                    edtKVA.setEnabled(true);
                    edtKW.setEnabled(true);
                    edtPF.setEnabled(true);
                }

                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (readerStatusCode.equals(getString(R.string.meter_changed))) {
                            showMessageDialogForMeterNo(mContext);
                        }
                    }
                };
                handler.postDelayed(runnable, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
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
                    }
                    else if (suspiciousActivityCode.equals(getString(R.string.other))){
                        edtObservation.setText("");
                        edtObservation.setVisibility(View.VISIBLE);

                    }
                    else
                        {
                        edtObservation.setText("");
                        edtObservation.setVisibility(View.GONE);
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
                meterType = mMeterType.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
//                Toast.makeText(mContext, mMeterStatus.getSelectedItem().toString().trim(), Toast.LENGTH_SHORT).show();
            }

        });

/*        mMeterImageReadable = (Spinner) findViewById(R.id.meter_image_spinner);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.select_image_inout_array)) {
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
        mMeterImageReadable.setAdapter(adapter3);
        mMeterImageReadable.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                meterImageSelected = mMeterImageReadable.getSelectedItem().toString().trim();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
//                Toast.makeText(mContext, mMeterStatus.getSelectedItem().toString().trim(), Toast.LENGTH_SHORT).show();
            }

        });*/

        mMeterReadingImageName = "ME_" + userJobCard.job_card_id + "_" + userJobCard.meter_reader_id;
        mMeterReadingSPImageName = "SP_" + userJobCard.job_card_id + "_" + userJobCard.meter_reader_id;

//        edtMobileNo.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                validateNumber();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                validateNumber();
//            }
//        });

        edtKWHReading.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    applyCondition = true;
                    arrayForMeterStatus = R.array.update_meter_status_all;
                    setMeterStatusSpinner(arrayForMeterStatus);
                } else {
                    setDynamicValues(s.toString());
                }

                handler = new Handler();
                runnable = new Runnable() {
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

        //Delete reference of previous photo taken starts, Piyush : 07-03-17
        File fileDeleteMeter = getMeterFilePath(METER_IMAGE_DIRECTORY_NAME, mMeterReadingImageName);
        File fileDeleteSuspicious = getMeterFilePath(SUSPICIOUS_IMAGE_DIRECTORY_NAME, mMeterReadingSPImageName);
        try {
            if (fileDeleteMeter.exists() || fileDeleteSuspicious.exists()) {
                fileDeleteMeter.delete();
                fileDeleteSuspicious.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Delete reference of previous photo taken ends, Piyush : 07-03-17


        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            mMeterReaderId = userProfile.meter_reader_id;
        }

        try {
//            phoneNo = userJobCard.phone_no;
            phoneNo = "8788610686";
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (userJobCard.bill_cycle_code.equals("00")) {
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

    private void initAllUIComponents() {
        consumerName = (userJobCard.consumer_name == null) ? "" : userJobCard.consumer_name;
        consumerAddress = (userJobCard.address == null) ? "" : userJobCard.address;
        consumerNumber = (userJobCard.consumer_no == null) ? "" : userJobCard.consumer_no;
        consumerMeterNo = (userJobCard.meter_no == null) ? "" : userJobCard.meter_no;
        mobileNo = userJobCard.phone_no == null || userJobCard.phone_no.equals("None") || userJobCard.phone_no.equals("0") ? "" : userJobCard.phone_no;
        previous_status = userJobCard.prv_status;
//        Log.i("aaaaaa", "pvvvvvvvv  " + userJobCard.prv_status);
//        Log.i("laaaaaattt "+userJobCard.lattitude , "lonnnnnnnnnnn  " + userJobCard.longitude );
//        Log.e("pppppp"+userJobCard.prv_meter_reading,"aaaaaaa"+(int) Math.round(Math.floor(Double.valueOf(userJobCard.prv_meter_reading))));
        previousMeterReading = (userJobCard.prv_meter_reading == null || userJobCard.prv_meter_reading.equalsIgnoreCase("None")) ? 0 : (int) Math.round(Math.floor(Double.valueOf(userJobCard.prv_meter_reading)));
//        previousMeterReading = 5000;
//        Log.i("Logggggggg","avg_consum"+ userJobCard.meter_digit );
        avgConsumption = (userJobCard.avg_consumption == null || userJobCard.avg_consumption.equals("") || userJobCard.avg_consumption.equals("None")) ? 0 : Integer.parseInt(userJobCard.avg_consumption);
//        avgConsumption = 500;
        meterDigits = (userJobCard.meter_digit == null || userJobCard.meter_digit.equals("") || userJobCard.meter_digit.equals("None")) ? 0 : Integer.parseInt(userJobCard.meter_digit);
//        meterDigits = 5;

        txtConsumerMeterNumber.setText(userJobCard.account_no + " | " + consumerMeterNo);
        txtConsumerMeterNumber.setSelected(true);

        txtConsumerCategory.setText(userJobCard.category_id);
        txtConsumerCategory.setSelected(true);


        edtAboutMeter.setText(userJobCard.location_guidance == null ? "" : userJobCard.location_guidance);
        edtPoleNo.setHint(getString(R.string.pole_number_colon) + " " + (userJobCard.pole_no == null ? "" : userJobCard.pole_no));
        edtMobileNo.setText(mobileNo);

        edtKWHReading.setFilters(new InputFilter[]{new InputFilter.LengthFilter(meterDigits)});

        if (userJobCard.phone_no.equalsIgnoreCase("") || userJobCard.phone_no.equalsIgnoreCase(" ") || userJobCard.phone_no.equalsIgnoreCase("0")) {
            imgCall.setVisibility(View.INVISIBLE);
        } else
            imgCall.setVisibility(View.VISIBLE);

        latitude = userJobCard.lattitude;
        longitude = userJobCard.longitude;

        if (latitude.length() > 3 && longitude.length() > 3)
            imgMap.setVisibility(View.VISIBLE);
        else
            imgMap.setVisibility(View.GONE);
    }

    private void setDynamicValues(String value) {
        Double read = Double.parseDouble(value);
        if (read > previousMeterReading) {
            applyCondition = true;
            faultyStatus = "greater";
            setMeterStatusSpinner(R.array.update_meter_status_normal_faulty);
        }
        if (read == previousMeterReading) {
            faultyStatus = "equal";
            setMeterStatusSpinner(R.array.update_meter_status_normal_faulty);
            applyCondition = true;
        }
        if (read < previousMeterReading) {
            applyCondition = true;
            faultyStatus = "less";
            setMeterStatusSpinner(R.array.update_meter_status_normal_faulty);
        }
    }

    private void checkValidation() {
        currentReading = Integer.parseInt(edtKWHReading.getText().toString().trim());
        if (currentReading > previousMeterReading) {
            int advance = currentReading - previousMeterReading;
            if (avgConsumption > 0) {
                if (advance > (2 * avgConsumption)) {
                    overConsumptionDialog(mContext);
                } else {
//                    submitReading(String.valueOf(currentReading));
                    showMessageDialogForLocation();
                }
            } else {
//                submitReading(String.valueOf(currentReading));
                showMessageDialogForLocation();

            }
        } else {
            if (currentReading < previousMeterReading) {
                if (meterDigits == 5) {
                    if (previousMeterReading > meterDigit5) {
                        checkForRoundComplete();
                    } else {
                        abnormalCondition();
                    }
                } else {
                    if (meterDigits == 6) {
                        if (previousMeterReading > meterDigit6) {
                            checkForRoundComplete();
                        } else {
                            abnormalCondition();
                        }
                    } else {
                        if (meterDigits == 7) {
                            if (previousMeterReading > meterDigit7) {
                                checkForRoundComplete();
                            } else {
                                abnormalCondition();
                            }
                        } else {
                            if (meterDigits == 8) {
                                if (previousMeterReading > meterDigit8) {
                                    checkForRoundComplete();
                                } else {
                                    abnormalCondition();
                                }
                            } else {
                                abnormalCondition();
                            }
                        }
                    }
                }
            } else {
                if (currentReading == previousMeterReading) {
//                    submitReading(String.valueOf(currentReading));
                    showMessageDialogForLocation();

                }
            }
        }
    }

    private void checkForRoundComplete() {
        currentReading = Integer.parseInt(edtKWHReading.getText().toString().trim());
        if (currentReading < maxLimit) {
            roundCompleteDialog(mContext);
        } else {
            abnormalCondition();
        }
    }

    private void abnormalCondition() {
        abnormalReadingDialog(mContext);
    }

    private void checkKWHReading(int currentMeterReading) {
        advanceReading = currentMeterReading - previousMeterReading;
        if (advanceReading > 0 && avgConsumption > 0) {
            if (advanceReading > (2 * avgConsumption)) {
                CommonUtils.hideKeyBoard(mContext);
                faultyStatus = "";
                setMeterStatusSpinner(R.array.update_meter_status_normal_faulty);
//                setReasonCodeSpinner(R.array.reader_status_faulty1);
                overConsumptionDialog(mContext);
                applyCondition = true;
            } else {
                if (currentMeterReading > previousMeterReading) {
                    CommonUtils.hideKeyBoard(mContext);
                    faultyStatus = "greater";
                    setMeterStatusSpinner(R.array.update_meter_status_normal_faulty);
                } else if (currentMeterReading == previousMeterReading) {
                    CommonUtils.hideKeyBoard(mContext);
                    faultyStatus = "equal";
                    setMeterStatusSpinner(R.array.update_meter_status_normal_faulty);
                    applyCondition = true;
                } else if (currentMeterReading < previousMeterReading) {
                    double advance = (Math.pow(10, meterDigits) + currentMeterReading) - previousMeterReading;
                    if (advance > 0 && avgConsumption > 0) {
                        if (advance > (2 * avgConsumption)) {
                            CommonUtils.hideKeyBoard(mContext);
                            applyCondition = false;
                            setMeterStatusSpinner(R.array.update_meter_status_normal);
                            setReasonCodeSpinner(R.array.reader_status_round_complete);
                            roundCompleteDialog(mContext);
                        }
                    } else {
                        CommonUtils.hideKeyBoard(mContext);
                        faultyStatus = "less";
                        setMeterStatusSpinner(R.array.update_meter_status_faulty);
                        abnormalReadingDialog(mContext);
                    }
                }
            }
        } else {
            if (currentMeterReading > previousMeterReading) {
                CommonUtils.hideKeyBoard(mContext);
                faultyStatus = "greater";
                setMeterStatusSpinner(R.array.update_meter_status_normal_faulty);
            } else if (currentMeterReading == previousMeterReading) {
                CommonUtils.hideKeyBoard(mContext);
                faultyStatus = "equal";
                setMeterStatusSpinner(R.array.update_meter_status_normal_faulty);
                applyCondition = true;
            } else if (currentMeterReading < previousMeterReading) {
                double advance = (Math.pow(10, meterDigits) + currentMeterReading) - previousMeterReading;
                if (advance > 0 && avgConsumption > 0) {
                    if (advance > (2 * avgConsumption)) {
                        CommonUtils.hideKeyBoard(mContext);
                        applyCondition = false;
                        setMeterStatusSpinner(R.array.update_meter_status_normal);
                        setReasonCodeSpinner(R.array.reader_status_round_complete);
                        roundCompleteDialog(mContext);
                    }
                } else {
                    CommonUtils.hideKeyBoard(mContext);
                    faultyStatus = "less";
                    setMeterStatusSpinner(R.array.update_meter_status_faulty);
                    abnormalReadingDialog(mContext);
                }
            }
        }
    }

    //    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("MissingPermission")
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppConstants.CAMERA_RESULT_CODE:

                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    newLatitude = String.valueOf(mLastLocation.getLatitude());
                    newLongitude = String.valueOf(mLastLocation.getLongitude());
                } else {
                    newLatitude = "0";
                    newLongitude = "0";
                }



                mLocationManagerReceiver = new LocationManagerReceiver(mContext);
                mBitmapMeterReading = getBitmapScaled(METER_IMAGE_DIRECTORY_NAME, mMeterReadingImageName);
                if (mBitmapMeterReading != null) {
                    imgMeter.setImageBitmap(mBitmapMeterReading);
                }
                break;
            case AppConstants.CAMERA_SUSPICIOUS_RESULT_CODE:
                mLocationManagerReceiver = new LocationManagerReceiver(mContext);
                mBitmapMeterSuspicious = getBitmapScaled(SUSPICIOUS_IMAGE_DIRECTORY_NAME, mMeterReadingSPImageName);
                if (mBitmapMeterSuspicious != null) {
                    imgSuspicious.setImageBitmap(mBitmapMeterSuspicious);
                }
                break;

            case AppConstants.REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        mLocationManagerReceiver.getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        mLocationManagerReceiver = new LocationManagerReceiver(mContext);
                        break;
                }
                break;
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (handler != null && runnable != null)
            handler.removeCallbacks(runnable);
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            stopTimer();
            finish();
        }
        if (v == cameraMeter) {
            mLocationManagerReceiver = new LocationManagerReceiver(mContext);

            LocationManager locationManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);
            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
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

        if (v == cameraSuspicious && (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            Uri fileUri = null;
            fileUri = getOutputMediaFileUri(SUSPICIOUS_IMAGE_DIRECTORY_NAME, mMeterReadingSPImageName);
            List<ResolveInfo> resolvedIntentActivities = mContext.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
                String packageName = resolvedIntentInfo.activityInfo.packageName;

                mContext.grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            startActivityForResult(intent, AppConstants.CAMERA_SUSPICIOUS_RESULT_CODE);
        }

        //To show/hide suspicious view starts, Piyush : 02-03-17
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


        if (v == txtChange) {
            linearCategoryRemark.setVisibility(View.VISIBLE);
            txtChange.setVisibility(View.GONE);
            txtUnchange.setVisibility(View.VISIBLE);
        }

        if (v == txtUnchange) {
            txtChange.setVisibility(View.VISIBLE);
            txtUnchange.setVisibility(View.GONE);
            edtConsumerCategoryRemarks.setText("");
            linearCategoryRemark.setVisibility(View.GONE);
        }

        //To show the details of consumer starts, Piyush : 02-03-17
        if (v == btnViewMore) {
            DialogCreator.showConsumerDetailsDialog(mContext, consumerName, consumerAddress, consumerNumber, consumerMeterNo, userJobCard.bill_cycle_code, userJobCard.zone_code, userJobCard.phone_no, userJobCard.route_code, 0);
        }
        //To show the details of consumer ends, Piyush : 02-03-17
        if (v.getId() == R.id.submit_and_next) {
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
        if (v == updateIndex) {
            if (linearMeterIndexing.getVisibility() == View.GONE) {
                linearMeterIndexing.setVisibility(View.VISIBLE);
                iv_updateindex.setRotation(180);
            } else {
                CommonUtils.hideKeyBoard(mContext);
                edtPoleNo.setText("");
                edtPoleNo.setHint(getString(R.string.pole_number_colon) + " " + (userJobCard.pole_no == null ? "" : userJobCard.pole_no));
                linearMeterIndexing.setVisibility(View.GONE);
                iv_updateindex.setRotation(0);
            }
        }

        if (v == imgCall) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", userJobCard.phone_no, null));
            startActivity(intent);
        }
        if (v == imgMap) {
            Intent intent = new Intent(mContext, GoogleMapActivity.class);
            startActivity(intent);
        }
    }

    private void doSubmitOps() {
        String current_kwh = edtKWHReading.getText().toString().trim();

        meterReading.job_card_id = userJobCard.job_card_id;
        meterReading.meter_reader_id = userJobCard.meter_reader_id;
        meterReading.isRevisit = userJobCard.is_revisit;

        String meter_image = null;
        if (((BitmapDrawable) imgMeter.getDrawable()) != null) {
            meter_image = CommonUtils.getBitmapEncodedString(mBitmapMeterReading);
            MeterImage meterImage = new MeterImage();
            meterImage.image = meter_image;
            meterReading.meter_image = meterImage;
            meterReading.isUploaded = "False";

            String meterStatus = mMeterStatus.getSelectedItem().toString().trim();
            String readerStatus = mReaderStatus.getSelectedItem().toString().trim();
            String suspiciousActivityStatus = mSuspiciousActivity.getSelectedItem().toString().trim();



            if (meterStatus.equals(getString(R.string.meter_status_mandatory))) {
                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_meter_status), "error");
            } else {
                if (readerStatus.equals(getString(R.string.reader_status_mandatory))) {
                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_reason_code), "error");
                } else {
                    if (meterStatus.equals(getString(R.string.rcnt)) || readerStatus.equals(getString(R.string.meter_burnt)) || readerStatus.equals(getString(R.string.meter_dead_stopped))
                            || readerStatus.equals(getString(R.string.glass_broken)) || readerStatus.equalsIgnoreCase(getString(R.string.meter_changed))) {
                        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes) {
                            meterReading.suspicious_activity = "True";
                            String encodedImage = "";
                            if (mBitmapMeterSuspicious != null) {
                                encodedImage = CommonUtils.getBitmapEncodedString(mBitmapMeterSuspicious);
                                suspicious_activity_image = new MeterImage();
                                suspicious_activity_image.image = encodedImage;
                                meterReading.suspicious_activity_image = suspicious_activity_image;

                                if (suspiciousActivityStatus.equals(getString(R.string.suspicious_activity_status))) {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_activity_status), "error");
                                } else if (suspiciousActivityStatus.equals(getString(R.string.other)) && edtObservation.getText().toString().isEmpty()) {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_enter_observation_or_choose_suspicious_activity), "error");
                                } else if (suspiciousActivityStatus.equals(getString(R.string.other)) && !edtObservation.getText().toString().trim().isEmpty()) {
                                    meterReading.suspicious_remark = edtObservation.getText().toString().trim();
                                    if (validateNumber()) {
                                        isCheckValidation = false;
                                        checkCategoryRemark();
                                    }
                                } else {
                                    meterReading.suspicious_remark = suspiciousActivityStatus;
                                    if (validateNumber()) {
                                        isCheckValidation = false;
                                        checkCategoryRemark();
                                    }
                                }
                            } else {
                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_meter_image), "error");
                            }
                        } else {
                            meterReading.suspicious_activity = "False";
                            meterReading.suspicious_remark = "";
                            if (validateNumber()) {
                                isCheckValidation = false;
                                checkCategoryRemark();
                            }
                        }
                    } else {
                        if (current_kwh.isEmpty()) {
                            DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_meter_reading), "error");
                        } else {
                            if (meterStatus.equals(getString(R.string.meter_status_mandatory))) {
                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_meter_status), "error");
                            } else {
                                if (readerStatus.equals(getString(R.string.reader_status_mandatory))) {
                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_reason_code), "error");
                                } else {
                                    if (readerStatus.equalsIgnoreCase(getString(R.string.meter_changed))) {
                                        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes) {
                                            meterReading.suspicious_activity = "True";
                                            String encodedImage = "";
                                            if (mBitmapMeterSuspicious != null) {
                                                encodedImage = CommonUtils.getBitmapEncodedString(mBitmapMeterSuspicious);
                                                suspicious_activity_image = new MeterImage();
                                                suspicious_activity_image.image = encodedImage;
                                                meterReading.suspicious_activity_image = suspicious_activity_image;

                                                if (suspiciousActivityStatus.equals(getString(R.string.suspicious_activity_status))) {
                                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_activity_status), "error");
                                                } else if (suspiciousActivityStatus.equals(getString(R.string.other)) && edtObservation.getText().toString().isEmpty()) {
                                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_enter_observation_or_choose_suspicious_activity), "error");
                                                } else if (suspiciousActivityStatus.equals(getString(R.string.other)) && !edtObservation.getText().toString().trim().isEmpty()) {
                                                    meterReading.suspicious_remark = edtObservation.getText().toString().trim();
                                                    if (validateNumber()) {
                                                        isCheckValidation = false;
                                                        checkCategoryRemark();
                                                    }
                                                } else {
                                                    meterReading.suspicious_remark = suspiciousActivityStatus;
                                                    if (validateNumber()) {
                                                        isCheckValidation = false;
                                                        checkCategoryRemark();
                                                    }
                                                }
                                            } else {
                                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_meter_image), "error");
                                            }
                                        } else {
                                            meterReading.suspicious_activity = "False";
                                            meterReading.suspicious_remark = "";
                                            if (validateNumber()) {
                                                isCheckValidation = true;
                                                checkCategoryRemark();
                                            }
                                        }
                                    } else {
                                        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes) {
                                            meterReading.suspicious_activity = "True";
                                            String encodedImage = "";
                                            if (mBitmapMeterSuspicious != null) {
                                                encodedImage = CommonUtils.getBitmapEncodedString(mBitmapMeterSuspicious);
                                                suspicious_activity_image = new MeterImage();
                                                suspicious_activity_image.image = encodedImage;
                                                meterReading.suspicious_activity_image = suspicious_activity_image;


                                                if (suspiciousActivityStatus.equals(getString(R.string.suspicious_activity_status))) {
                                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_activity_status), "error");
                                                } else if (suspiciousActivityStatus.equals(getString(R.string.other)) && edtObservation.getText().toString().isEmpty()) {
                                                    DialogCreator.showMessageDialog(mContext, getString(R.string.please_enter_observation_or_choose_suspicious_activity), "error");
                                                } else if (suspiciousActivityStatus.equals(getString(R.string.other)) && !edtObservation.getText().toString().trim().isEmpty()) {
                                                    meterReading.suspicious_remark = edtObservation.getText().toString().trim();
                                                    if (validateNumber()) {
                                                        isCheckValidation = true;
                                                        checkCategoryRemark();
                                                    }
                                                } else {
                                                    meterReading.suspicious_remark = suspiciousActivityStatus;
                                                    if (validateNumber()) {
                                                        isCheckValidation = true;
                                                        checkCategoryRemark();
                                                    }
                                                }
                                            } else {
                                                DialogCreator.showMessageDialog(mContext, getString(R.string.please_provide_suspicious_meter_image), "error");
                                            }
                                        } else {
                                            meterReading.suspicious_activity = "False";
                                            meterReading.suspicious_remark = "";
                                            if (validateNumber()) {
                                                isCheckValidation = true;
                                                checkCategoryRemark();

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        } else {
            DialogCreator.showMessageDialog(mContext, getString(R.string.blank_meter_reading_image), "error");
            return;
        }


    }


    private void submitReading(String current_kwh) {
        showLoadingDialog(getString(R.string.saving_data));
        String prv_kva = userJobCard.prv_kva_reading;
        String prv_kwh = userJobCard.prv_meter_reading;
        String current_kvah = edtKVAHReading.getText().toString().trim();
        String current_kva = edtKVA.getText().toString().trim();
        String current_kw = edtKW.getText().toString().trim();
        String current_pf = edtPF.getText().toString().trim();
        String poleNo = edtPoleNo.getText().toString().trim();
        String addComments = edtComments.getText().toString().trim();

        String addObservation = edtObservation.getText().toString().trim();
        String addConsumerCategoryRemark = edtConsumerCategoryRemarks.getText().toString().trim();
        String mobileNo = edtMobileNo.getText().toString().trim();
        String panelNo = edtPanelNo.getText().toString().trim();
        String locationGuidance = edtAboutMeter.getText().toString().trim();

        if (current_kwh.equals("")) {
            meterReading.current_meter_reading = "0";
        } else {
            meterReading.current_meter_reading = current_kwh;
        }


        meterReading.job_card_id = userJobCard.job_card_id;
        meterReading.meter_reader_id = userJobCard.meter_reader_id;
        meterReading.isRevisit = userJobCard.is_revisit;

        meterReading.current_kvah_reading = current_kvah == null || current_kvah.equalsIgnoreCase(" ") ? "0" : current_kvah;
        meterReading.current_kva_reading = current_kva == null || current_kva.equalsIgnoreCase(" ") ? "0" : current_kva;
        meterReading.current_kw_reading = current_kw == null || current_kw.equalsIgnoreCase(" ") ? "0" : current_kw;
        meterReading.current_pf_reading = current_pf == null || current_pf.equalsIgnoreCase(" ") ? "0" : current_pf;
        if (readerStatusCode != null) {
            if (readerStatusCode.equalsIgnoreCase(getString(R.string.round_complete))) {
                if (!current_kwh.equalsIgnoreCase("") && current_kwh != null) {
                    if (Integer.parseInt(current_kwh) < Integer.parseInt(prv_kwh)) {
                        meterReading.iskwhroundcompleted = "1";
                    } else {
                        meterReading.iskwhroundcompleted = "0";
                    }
                } else {
                    meterReading.iskwhroundcompleted = "0";

                }

                if (!current_kva.equalsIgnoreCase("") && current_kva != null) {
                    if (Integer.parseInt(current_kva) < Integer.parseInt(prv_kva)) {
                        meterReading.iskvahroundcompleted = "1";
                    } else {
                        meterReading.iskvahroundcompleted = "0";
                    }
                } else {
                    meterReading.iskvahroundcompleted = "0";

                }
            } else {
                meterReading.iskwhroundcompleted = "0";
                meterReading.iskvahroundcompleted = "0";
            }
        } else {
            meterReading.iskwhroundcompleted = "0";
            meterReading.iskvahroundcompleted = "0";
        }
        meterReading.reader_remark_comment = addComments;
//        new addition
        meterReading.consumer_category_remark = addConsumerCategoryRemark;
//        closed new addition
        meterReading.mobile_no = mobileNo != null || mobileNo.equalsIgnoreCase(" ") ? "0" : mobileNo;
        meterReading.panel_no = panelNo;
        meterReading.reader_status = readerStatusCode;
        meterReading.meter_status = meterStatusCode;
        meterReading.reading_month = userJobCard.schedule_month;
        meterReading.meter_no = userJobCard.meter_no;
        meterReading.location_guidance = locationGuidance == "" ? userJobCard.location_guidance : locationGuidance;
        meterReading.pole_no = poleNo == null ? userJobCard.pole_no : poleNo;
        meterReading.zone_code = userJobCard.zone_code;
//        meterReading.is_lat_long_verified = "0";


        if (btnRadioPlasticCutYes.isChecked()) {
            meterReading.is_plastic_cover_cut = "True";
        } else if (btnRadioPlasticCutNo.isChecked()){
            meterReading.is_plastic_cover_cut = "False";
        }


        if (meterReading.meter_type == null) {
            meterReading.meter_type = "";
        }
      /*  if (meterImageSelected.equals(getString(R.string.meter_location))) {
            meterReading.location_guidance = "";
        } else {
            meterReading.location_guidance = meterImageSelected;
        }*/

        // Sequence Logic starts
        meterReading.prv_sequence = userJobCard.prv_sequence;
        meterReading.new_sequence = userJobCard.meter_reader_id + "|" + userJobCard.zone_code + "|" + userJobCard.bill_cycle_code + "|" + userJobCard.route_code + "|" + String.valueOf(String.format("%04d", assignedSequence));

        //Save it into the DB
        //Changes for Online Reading Starts Avinesh 6-04-17
        String sequence = userJobCard.prv_sequence;
        if (sequence.length() > 20) {
            int a = sequence.lastIndexOf("|");
            int seq = Integer.parseInt(sequence.substring(a + 1));
            prv_sequence = sequence.substring(0, a + 1) + "" + String.format("%04d", ++seq);
        } else if (sequence.length() > 0) {
            prv_sequence = userJobCard.prv_sequence;
        }
        Sequence updateSequence = new Sequence();
        updateSequence.meter_reader_id = userJobCard.meter_reader_id;
        updateSequence.cycle_code = userJobCard.bill_cycle_code;
        updateSequence.route_code = userJobCard.route_code;
        updateSequence.zone_code = userJobCard.zone_code;
        updateSequence.sequence = String.valueOf(++assignedSequence);
        DatabaseManager.UpdateSequence(mContext, updateSequence);
        meterReading.reading_date = CommonUtils.getCurrentDateTime();
        meterReading.reading_taken_by = App.ReadingTakenBy;
        if (readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle)) || readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked)))
            meterReading.sms_sent = "True";
        else
            meterReading.sms_sent = "False";


        meterReading.time_taken = stopTimer();
        if ((readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle)) && userJobCard.attempt.equalsIgnoreCase(""))
                || (readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked)) && userJobCard.attempt.equalsIgnoreCase(""))
                || (readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle)) && userJobCard.attempt.equalsIgnoreCase("P-A1"))
                || (readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked)) && userJobCard.attempt.equalsIgnoreCase("O-A1"))) {
            if (readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked)))
                userJobCard.attempt = "P-A1";
            else
                userJobCard.attempt = "O-A1";
            DatabaseManager.saveJobCardStatus(mContext, userJobCard, AppConstants.JOB_CARD_STATUS_ALLOCATED);
        } else {
//            CommonUtils.alertTone(mContext, R.raw.sent);
            DatabaseManager.saveMeterReading(mContext, meterReading);
            DatabaseManager.saveJobCardStatus(mContext, userJobCard, AppConstants.JOB_CARD_STATUS_COMPLETED);
        }



        //Changes for Online Reading Ends Avinesh 6-04-17





        if (mLastLocation != null) {
            meterReading.cur_lat = newLatitude.trim();
            meterReading.cur_lng = newLongitude.trim();
        } else {
            meterReading.cur_lat = "0";
            meterReading.cur_lng = "0";
        }




        mBitmapMeterReading = null;
        mBitmapMeterSuspicious = null;

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

        if (mMeterStatus.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.meter_status_lock_premise))
                || mMeterStatus.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.meter_status_meter_missing))
                || mMeterStatus.getSelectedItem().toString().trim().equalsIgnoreCase(getString(R.string.meter_status_inaccessible))) {
            if (CommonUtils.checkAndRequestPermissions(mContext, this)) {
                if (phoneNo.length() != 0) {
                    /*String sendMessage = "Your Meter Reading " + edtMeterReading.getText().toString().trim() +
                            " of Meter No " + userJobCard.meter_no + " on txtDate " +
                            CommonUtils.getCurrentDate() + "  has been taken ";*/
                    if (CommonUtils.isNetworkAvaliable(mContext)) {
                        AsyncCallWS task = new AsyncCallWS();
                        task.execute();
                    } else {
                        Toast.makeText(mContext, R.string.error_internet_not_connected, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.message_can_not_send_to_this_consumer), Toast.LENGTH_LONG).show();
                }
            }
        }
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
//                CommonUtils.alertTone(mContext, R.raw.sent);
                dismissLoadingDialog();
                launchNext();
            }
        };
        handler.postDelayed(runnable, 1000);

    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (radioGroup.getCheckedRadioButtonId() == R.id.rb_yes)
            edtObservation.setEnabled(true);
        else
            edtObservation.setEnabled(false);
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
//        createLocationRequest();
//        Log.d("Latitude: "+mLastLocation.getLatitude(), "Longitude: "+mLastLocation.getLongitude());
    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat
            // #requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    public void launchNext() {
        ArrayList<JobCard> job = DatabaseManager.getJobCardBySequence(mContext, mMeterReaderId, AppConstants.JOB_CARD_STATUS_ALLOCATED, prv_sequence, userJobCard.route_code);
        if (job != null) {
            Intent i = new Intent(mContext, AddMeterReadingActivity.class);
            i.putExtra(AppConstants.CURRENT_JOB_CARD, job.get(0));
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        } else {
            Intent i = new Intent(mContext, LandingActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }
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
            showMessageDialogForMobileNo(mContext);
            return false;
        }
    }

    //Dialog for validation Dialog start Piyush : 01-04-17
    public void abnormalReadingDialog(final Context context) {
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_validations, null);

        final TextView txtTitle, txtErrorMessage, txtError;
        Button positive, negative;
        final EditText editText;

        //Initialising all fields starts
        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtErrorMessage = (TextView) promptView.findViewById(R.id.txt_error_message);
        positive = (Button) promptView.findViewById(R.id.btn_yes);
        negative = (Button) promptView.findViewById(R.id.btn_no);
        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        editText = (EditText) promptView.findViewById(R.id.edt_kwh_reading);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(meterDigits)});
        final Spinner mMeterStatusDialog = (Spinner) promptView.findViewById(R.id.meter_status);
        final Spinner mReaderStatusDialog = (Spinner) promptView.findViewById(R.id.reason_code);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.update_meter_status_faulty)) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(14f);
                return v;
            }
        };
        mMeterStatusDialog.setAdapter(adapter);
        mMeterStatusDialog.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                meterStatusOnDialog = mMeterStatusDialog.getSelectedItem().toString().trim();
                if (meterStatusOnDialog.equals(getString(R.string.normal))) {
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item,
                            getResources().getStringArray(R.array.reader_status_normal_dialog)) {
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);
                            ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                            ((TextView) v).setTextSize(14f);
                            return v;
                        }

                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View v = super.getDropDownView(position, convertView, parent);
                            ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                            ((TextView) v).setTextSize(14f);
                            return v;
                        }
                    };
                    mReaderStatusDialog.setAdapter(adapter1);
                } else if (meterStatusOnDialog.equals(getString(R.string.meter_status_faulty))) {
                    ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item,
                            getResources().getStringArray(R.array.reader_status_faulty_dialog)) {
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);
                            ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                            ((TextView) v).setTextSize(14f);
                            return v;
                        }

                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View v = super.getDropDownView(position, convertView, parent);
                            ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                            ((TextView) v).setTextSize(14f);
                            return v;
                        }
                    };
                    mReaderStatusDialog.setAdapter(adapter1);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //Initialising all fields ends

        //Setting font style to all fields starts
        txtTitle.setTypeface(bold);
        txtErrorMessage.setTypeface(bold);
        positive.setTypeface(regular);
        negative.setTypeface(regular);
        txtError.setTypeface(regular);
        editText.setTypeface(regular);
        //Setting font style to all fields ends

        //Button code starts
        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!valid) {
                    if (editText.getText().toString().trim().isEmpty()) {
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText(getString(R.string.please_enter_kwh_reading));
                    } else {
                        txtError.setVisibility(View.GONE);
                        int val = Integer.parseInt(editText.getText().toString().trim());
                        if (currentReading == val) {
                            mMeterStatusDialog.setVisibility(View.VISIBLE);
                            mReaderStatusDialog.setVisibility(View.VISIBLE);
                            valid = true;
                        } else {
                            txtError.setVisibility(View.VISIBLE);
                            txtError.setText(getString(R.string.kwh_reading_does_not_matched));
                        }
                    }
                } else {
                    if (mMeterStatusDialog.getSelectedItem().equals(getString(R.string.meter_status_mandatory)) ||
                            mReaderStatusDialog.getSelectedItem().equals(getString(R.string.reader_status_mandatory))) {
                    } else {
                        valid = false;
                        txtError.setVisibility(View.GONE);
                        meterStatusCode = mMeterStatusDialog.getSelectedItem().toString();
                        readerStatusCode = mReaderStatusDialog.getSelectedItem().toString();
//                        submitReading(String.valueOf(currentReading));
                        showMessageDialogForLocation();

                        alertForAbnormalReading.dismiss();
                    }
                }
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.setText("");
                txtError.setVisibility(View.GONE);
                mMeterStatusDialog.setVisibility(View.GONE);
                mReaderStatusDialog.setVisibility(View.GONE);
                edtKWHReading.setText("");
                setMeterStatusSpinner(R.array.update_meter_status_all);
                valid = false;
                alertForAbnormalReading.dismiss();
            }
        });

        alertForAbnormalReading.setView(promptView);
        alertForAbnormalReading.setCancelable(false);
        if (alertForAbnormalReading.isShowing()) {
            alertForOverConsumption.dismiss();
            alertForRoundComplete.dismiss();
        } else {
            alertForAbnormalReading.show();
        }
        //OK button code ends
    }

    //Dialog for validation Dialog start Piyush : 01-04-17
    public void overConsumptionDialog(final Context context) {
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_validation_2, null);

        final TextView txtTitle, txtError;
        Button positive, negative;
        final EditText editText;

        //Initialising all fields starts
        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        positive = (Button) promptView.findViewById(R.id.btn_yes);
        negative = (Button) promptView.findViewById(R.id.btn_no);
        editText = (EditText) promptView.findViewById(R.id.edt_kwh_reading);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(meterDigits)});
        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        //Initialising all fields ends

        //Setting font style to all fields starts
        txtTitle.setTypeface(bold);
        positive.setTypeface(regular);
        negative.setTypeface(regular);
        editText.setTypeface(regular);
        txtError.setTypeface(regular);
        //Setting font style to all fields ends

        //Button code starts
        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editText.getText().toString().trim().isEmpty()) {
                    txtError.setVisibility(View.VISIBLE);
                } else {
                    txtError.setVisibility(View.GONE);
                    int val = Integer.parseInt(editText.getText().toString().trim());
                    if (currentReading == val) {
//                        submitReading(String.valueOf(currentReading));
                        showMessageDialogForLocation();
                        alertForOverConsumption.dismiss();
                    } else {
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText(getString(R.string.kwh_reading_does_not_matched));
                    }
                }
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.setText("");
                txtError.setVisibility(View.GONE);
                edtKWHReading.setText("");
                setMeterStatusSpinner(R.array.update_meter_status_all);
                alertForOverConsumption.dismiss();
            }
        });

        alertForOverConsumption.setView(promptView);
        alertForOverConsumption.setCancelable(false);
        if (alertForOverConsumption.isShowing()) {
            alertForAbnormalReading.dismiss();
            alertForRoundComplete.dismiss();
        } else {
            alertForOverConsumption.show();
        }
        //OK button code ends
    }

    //Dialog for validation Dialog start Piyush : 01-04-17
    public void roundCompleteDialog(final Context context) {
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_validation_3, null);

        final TextView txtTitle, txtError;
        Button positive, negative;
        final EditText editText;

        //Initialising all fields starts
        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        positive = (Button) promptView.findViewById(R.id.btn_yes);
        negative = (Button) promptView.findViewById(R.id.btn_no);
        editText = (EditText) promptView.findViewById(R.id.edt_kwh_reading);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(meterDigits)});
        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setVisibility(View.GONE);
        //Initialising all fields ends

        //Setting font style to all fields starts
        txtTitle.setTypeface(bold);
        positive.setTypeface(regular);
        negative.setTypeface(regular);
        editText.setTypeface(regular);
        txtError.setTypeface(regular);
        //Setting font style to all fields ends

        //Button code starts
        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (editText.getText().toString().trim().isEmpty()) {
                    txtError.setVisibility(View.VISIBLE);
                } else {
                    txtError.setVisibility(View.GONE);
                    int value = Integer.parseInt(editText.getText().toString().trim());
                    if (currentReading == value) {
                        meterStatusCode = "Normal";
                        readerStatusCode = "Round Complete";
                        double advance = (Math.pow(10, meterDigits) + currentReading) - previousMeterReading;
                        int valueAdvance = (int) advance;
//                        submitReading(Integer.toString(valueAdvance));
                        showMessageDialogForLocation();
                        alertForRoundComplete.dismiss();
                    } else {
                        txtError.setVisibility(View.VISIBLE);
                        txtError.setText(getString(R.string.kwh_reading_does_not_matched));
                    }
                }
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editText.setText("");
                txtError.setVisibility(View.GONE);
                edtKWHReading.setText("");
                setMeterStatusSpinner(R.array.update_meter_status_all);
                alertForRoundComplete.dismiss();
            }
        });

        alertForRoundComplete.setView(promptView);
        alertForRoundComplete.setCancelable(false);
        if (alertForRoundComplete.isShowing()) {
            alertForAbnormalReading.dismiss();
            alertForOverConsumption.dismiss();
        } else {
            alertForRoundComplete.show();
        }
        //OK button code ends
    }
    //Dialog for validation Dialog ends Piyush : 01-04-17

    private void showMessageDialogForMobileNo(final Context mContext) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_mobile_number, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();

//        edtMob = (EditText) promptView.findViewById(R.id.edt_mobile_no);
//        edtMob.setTypeface(regular);
//
//        edtMob.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                validateNumberPop();
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                validateNumberPop();
//            }
//        });

        final TextView txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);

        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                if (edtMob.getText().toString().trim().isEmpty()) {
//                    txtError.setVisibility(View.VISIBLE);
//                } else {
//                    if (validateNumberPop()) {
//                        edtMobileNo.setText(edtMob.getText().toString().trim());
//                        showMessageDialogForMobileNo(mContext);
//                        alert.dismiss();
//                    }
//                }
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }
    //Dialog for validation Dialog ends Piyush : 01-04-17

    private void showMessageDialogForMeterNo(Context mContext) {
//        CommonUtils.alertTone(mContext, R.raw.ping);
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_meter_number, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();

        edtMeterNo = (EditText) promptView.findViewById(R.id.edt_meter_no);
        edtMeterNo.setTypeface(regular);

        final TextView txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);

        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (edtMeterNo.getText().toString().trim().isEmpty()) {
                    edtPanelNo.setText("");
                    txtError.setVisibility(View.VISIBLE);
                } else {
                    edtPanelNo.setText(edtMeterNo.getText().toString().trim());
                    alert.dismiss();
                }
            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtKWHReading.setText("");
                setMeterStatusSpinner(R.array.update_meter_status_all);
                alert.dismiss();
            }
        });

        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }
    //Dialog for validation Dialog ends Piyush : 01-04-17

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
        mReaderStatus.setSelection(0);
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

    private boolean validateNumberPop() {
        Pattern pattern1, pattern2;
        Matcher matcher1, matcher2;
        final String PHONE_PATTERN1 = "^[7-9][0-9]{9}$";
        final String PHONE_PATTERN2 = "";
        pattern1 = Pattern.compile(PHONE_PATTERN1);
        pattern2 = Pattern.compile(PHONE_PATTERN2);
        String phone = edtMob.getText().toString().trim();
        matcher1 = pattern1.matcher(phone);
        matcher2 = pattern2.matcher(phone);

        if (matcher1.matches() || matcher2.matches()) {
            edtMob.setError(null);
            return true;
        } else {
            edtMob.setError(getString(R.string.enter_valid_mobile_no));
            return false;
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class AsyncCallWS extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {
//            CommonUtils.sendSMS(phoneNo, sendMessage);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }

    }

    public void checkPreviousStatus() {
        meterReading.status_changed = "False";
        String show = "yes";
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_alert, null);

        final TextView txtTitle, txtErrorMessage, txtError;
        Button positive, negative;
        final EditText editText;

        //Initialising all fields starts
        txtTitle = (TextView) promptView.findViewById(R.id.txt_alertmsg);
        positive = (Button) promptView.findViewById(R.id.btn_yes);
        negative = (Button) promptView.findViewById(R.id.btn_no);
        //Initialising all fields ends

        if (previous_status != null || !previous_status.equalsIgnoreCase("")) {
            if ((previous_status.equalsIgnoreCase(getString(R.string.ok)) && readerStatusCode.equalsIgnoreCase(getString(R.string.meter_dead_stopped)))
                    || (previous_status.equalsIgnoreCase(getString(R.string.ok)) && readerStatusCode.equalsIgnoreCase(getString(R.string.meter_burnt)))
                    || (previous_status.equalsIgnoreCase(getString(R.string.ok)) && readerStatusCode.equalsIgnoreCase(getString(R.string.glass_broken)))
                    || (previous_status.equalsIgnoreCase(getString(R.string.ok)) && readerStatusCode.equalsIgnoreCase(getString(R.string.body_seal_broken)))
                    || (previous_status.equalsIgnoreCase(getString(R.string.ok)) && readerStatusCode.equalsIgnoreCase(getString(R.string.box_seal_broken)))
                    || (previous_status.equalsIgnoreCase(getString(R.string.ok)) && readerStatusCode.equalsIgnoreCase(getString(R.string.terminal_seal_broken)))
                    || (previous_status.equalsIgnoreCase(getString(R.string.meter_changed)) && readerStatusCode.equalsIgnoreCase(getString(R.string.body_seal_broken)))
                    || (previous_status.equalsIgnoreCase(getString(R.string.meter_changed)) && readerStatusCode.equalsIgnoreCase(getString(R.string.box_seal_broken)))
                    || (previous_status.equalsIgnoreCase(getString(R.string.meter_changed)) && readerStatusCode.equalsIgnoreCase(getString(R.string.terminal_seal_broken)))
                    || (previous_status.equalsIgnoreCase(getString(R.string.meter_changed)) && readerStatusCode.equalsIgnoreCase(getString(R.string.meter_dead_stopped)))
                    || (previous_status.equalsIgnoreCase(getString(R.string.meter_changed)) && readerStatusCode.equalsIgnoreCase(getString(R.string.meter_burnt)))
                    || (previous_status.equalsIgnoreCase(getString(R.string.meter_changed)) && readerStatusCode.equalsIgnoreCase(getString(R.string.glass_broken)))) {
                txtTitle.setText("Are You Sure that Meter is Faulty?");
                meterReading.status_changed = "True";

            } else if ((previous_status.equalsIgnoreCase(getString(R.string.ok)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_changed)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.glass_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_burnt)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.box_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.body_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_dead_stopped)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.terminal_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.display_out)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.premises_locked)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.reading_not_taken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.obstacle)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_stolen)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.site_not_found)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.area_without_supply)) && readerStatusCode.equalsIgnoreCase(getString(R.string.display_out)))) {
                txtTitle.setText("Are You Sure that Display of Meter is not working?");
                meterReading.status_changed = "True";
            } else if ((previous_status.equalsIgnoreCase(getString(R.string.ok)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_changed)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.site_not_found)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_stolen)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.obstacle)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.body_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.box_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.terminal_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.site_not_found)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.area_without_supply)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.glass_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_burnt)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.reading_not_taken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_dead_stopped)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.display_out)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.premises_locked)) && readerStatusCode.equalsIgnoreCase(getString(R.string.premises_locked)))) {
                if (!userJobCard.lattitude.equals("0") && !userJobCard.longitude.equals("0") && !meterReading.cur_lat.equals("0") && !meterReading.cur_lng.equals("0")) {
                    if (df.format(Double.parseDouble(userJobCard.lattitude)).equals(df.format(Double.parseDouble(meterReading.cur_lat))) && df.format(Double.parseDouble(userJobCard.longitude)).equals(df.format(Double.parseDouble(meterReading.cur_lng)))) {
                        show = "no";
                        showMessageDialogForLocation();
                    } else {
                        txtTitle.setText("Are you sure this is Consumer's Correct Location?");
                        positive.setText("Yes");
                        negative.setText("No");
                        meterReading.status_changed = "True";
                    }
                } else {
                    txtTitle.setText("Are you sure this is Consumer's Correct Location?");
                    positive.setText("Yes");
                    negative.setText("No");
                    meterReading.status_changed = "True";
                }
            } else if ((previous_status.equalsIgnoreCase(getString(R.string.ok)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_changed)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.glass_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_burnt)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.box_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.body_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.terminal_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_dead_stopped)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.display_out)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.premises_locked)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.reading_not_taken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.obstacle)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_stolen)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.site_not_found)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.area_without_supply)) && readerStatusCode.equalsIgnoreCase(getString(R.string.obstacle)))) {
                txtTitle.setText("Are you sure that you are not able to take reading due to some Obstacle?\n" +
                        "(Note - Please mention some details about the obstacle in 'Remarks'.)");
                meterReading.status_changed = "True";
            } else if ((previous_status.equalsIgnoreCase(getString(R.string.glass_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.ok))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.glass_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.round_complete))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_burnt)) && readerStatusCode.equalsIgnoreCase(getString(R.string.ok))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_burnt)) && readerStatusCode.equalsIgnoreCase(getString(R.string.round_complete))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.body_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.ok))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.box_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.round_complete))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.terminal_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.ok))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_dead_stopped)) && readerStatusCode.equalsIgnoreCase(getString(R.string.round_complete))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_dead_stopped)) && readerStatusCode.equalsIgnoreCase(getString(R.string.ok))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.box_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.round_complete))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.body_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.ok))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.terminal_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.round_complete))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.display_out)) && readerStatusCode.equalsIgnoreCase(getString(R.string.ok))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.display_out)) && readerStatusCode.equalsIgnoreCase(getString(R.string.round_complete))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_stolen)) && readerStatusCode.equalsIgnoreCase(getString(R.string.ok))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_stolen)) && readerStatusCode.equalsIgnoreCase(getString(R.string.round_complete)))) {
                txtTitle.setText("Are You Sure the Condition of Meter is Normal-OK?");
                meterReading.status_changed = "True";
            } else if ((previous_status.equalsIgnoreCase(getString(R.string.ok)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.glass_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_changed)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_burnt)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.premises_locked)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_dead_stopped)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.obstacle)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.terminal_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.body_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.box_seal_broken)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.display_out)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.meter_stolen)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found))) ||
                    (previous_status.equalsIgnoreCase(getString(R.string.area_without_supply)) && readerStatusCode.equalsIgnoreCase(getString(R.string.site_not_found)))) {
                txtTitle.setText("This consumer’s site was found in last billing cycle, Are You Sure that You Still Want to mark this consumer as ‘Site Not Found’ ?");
                meterReading.status_changed = "True";
            }
            else if (previous_status.equalsIgnoreCase(getString(R.string.ok)) && readerStatusCode.equalsIgnoreCase(getString(R.string.connection_not_found))){
                txtTitle.setText(getString(R.string.are_you_sure_that_there_is_no_connection_to_meter));
                meterReading.status_changed = "True";
            }
            else {
                show = "no";
                showMessageDialogForLocation();
                alertForPreviousStatusCheck.dismiss();
            }
        } else {
            show = "no";
            showMessageDialogForLocation();
            alertForPreviousStatusCheck.dismiss();
        }

        //Setting font style to all fields starts
        txtTitle.setTypeface(bold);
        positive.setTypeface(regular);
        negative.setTypeface(regular);
        //Setting font style to all fields ends

        //Button code starts
        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showMessageDialogForLocation();
                alertForPreviousStatusCheck.dismiss();
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setMeterStatusSpinner(R.array.update_meter_status_all);
                alertForPreviousStatusCheck.dismiss();
                alertForPreviousStatusCheck.cancel();
            }
        });

        alertForPreviousStatusCheck.setView(promptView);
        alertForPreviousStatusCheck.setCancelable(false);
        if (alertForPreviousStatusCheck.isShowing()) {
            alertForAbnormalReading.dismiss();
            alertForOverConsumption.dismiss();
            alertForRoundComplete.dismiss();
        } else if (show.equalsIgnoreCase("yes")) {
            alertForPreviousStatusCheck.show();
        }
        //OK button code ends
    }

    private void checkCategoryRemark(){
        if (txtChange.getVisibility() == View.GONE){
            if (edtConsumerCategoryRemarks.getText().toString().trim().isEmpty()){
                DialogCreator.showMessageDialog(mContext, getString(R.string.please_add_remark_if_category_differs), "error");
            }
            else {
                meterReading.consumer_category_remark = edtConsumerCategoryRemarks.getText().toString().trim();
                checkLatLong(true);
            }
        }
        else{
            meterReading.consumer_category_remark = "";
            checkLatLong(true);

        }
    }

    private void checkAirConditioner() {

        if (btnRadioAirConditionerYes.isChecked()) {
            meterReading.air_conditioner_exist = "True";
            if (mNoOfAirConditioners.getSelectedItem().toString().trim().equals(getString(R.string.enter_number_of_air_conditioners))) {
                dismissLoadingDialog();
                DialogCreator.showMessageDialog(mContext, getString(R.string.please_enter_the_number_of_air_conditioners), "error");
            } else {

                meterReading.no_of_air_conditioners = mNoOfAirConditioners.getSelectedItem().toString().trim();
                if (isCheckValidation)
                    checkValidation();
                else
                    checkPreviousStatus();
            }

        } else {
            meterReading.air_conditioner_exist = "0";
            if (isCheckValidation)
                checkValidation();

            else
                checkPreviousStatus();

        }


    }




    @SuppressLint("MissingPermission")
    private void checkLatLong(boolean isPrvStatusHasToCheck) {


        if (isPrvStatusHasToCheck) {

            if (!userJobCard.lattitude.equals("0") && !userJobCard.longitude.equals("0")) {


                if (userJobCard.prv_status.equals(getString(R.string.ok))) {

                    Location startPoint = new Location("Previous Location");
                    startPoint.setLatitude(Double.parseDouble(userJobCard.lattitude));
                    startPoint.setLongitude(Double.parseDouble(userJobCard.longitude));

                    Location endPoint = new Location("Current Location");
                    endPoint.setLatitude(Double.parseDouble(newLatitude));
                    endPoint.setLongitude(Double.parseDouble(newLongitude));

                    distance = endPoint.distanceTo(startPoint);


                    if (distance > 50.0) {
                        showMessageDialogForDistance();

                    } else if (distance <= 50.0) {
                        meterReading.cur_lat = newLatitude;
                        meterReading.cur_lng = newLongitude;
                        meterReading.is_lat_long_verified = "True";
                        meterReading.distance = Double.toString(distance);
                        checkAirConditioner();

                    }


                } else {
                    if (mLastLocation != null) {
                        meterReading.cur_lat = newLatitude;
                        meterReading.cur_lng = newLongitude;
//                    latLongOK = "No";
                        meterReading.distance = "0.0";
                        meterReading.is_lat_long_verified = "False";
                        checkAirConditioner();
                    } else {
                        meterReading.cur_lat = "0";
                        meterReading.cur_lng = "0";
                        meterReading.is_lat_long_verified = "False";
                        checkAirConditioner();
                    }
                }
            } else {
                if (mLastLocation != null) {
                    meterReading.cur_lat = newLatitude;
                    meterReading.cur_lng = newLongitude;
//                    latLongOK = "No";
                    meterReading.distance = "0.0";
                    locationIssue = "No";
                    meterReading.is_lat_long_verified = "False";
                    checkAirConditioner();
                } else {
                    meterReading.cur_lat = "0";
                    meterReading.cur_lng = "0";
                    meterReading.is_lat_long_verified = "False";
                    checkAirConditioner();
                }
            }


        }


    }

    public void onDestroy() {
        Cleanup();
        super.onDestroy();
    }

    private void Cleanup() {
//        mBitmapMeterReading.recycle();
        System.gc();
        Runtime.getRuntime().gc();
    }

    private void showMessageDialogForMeterType() {
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
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
//                ok.getLayoutParams();
//        params.weight = 0.5f;
//        ok.setLayoutParams(params);
        ok.setTypeface(regular);
        ok.setText("Mechanical");
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                meterReading.meter_type = "Analog";
                submitReading(edtKWHReading.getText().toString().trim());
                alert.dismiss();
            }
        });
        Button btn_3 = (Button) promptView.findViewById(R.id.btn_3);
        btn_3.setVisibility(View.GONE);
        btn_3.setTypeface(regular);
        btn_3.setBackground(getResources().getDrawable(R.drawable.positive_button));
        btn_3.setText("RF");
//        btn_3.setLayoutParams(params);
        btn_3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                meterReading.meter_type = "RF";
                submitReading(edtKWHReading.getText().toString().trim());
                alert.dismiss();
            }
        });
        Button btn_4 = (Button) promptView.findViewById(R.id.btn_4);
        btn_4.setVisibility(View.GONE);
        btn_4.setTypeface(regular);
//        btn_4.setLayoutParams(params);
        btn_4.setBackground(getResources().getDrawable(R.drawable.positive_button));
        btn_4.setText("Old");
        btn_4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                meterReading.meter_type = "Old";
                submitReading(edtKWHReading.getText().toString().trim());
                alert.dismiss();
            }
        });
        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setBackground(getResources().getDrawable(R.drawable.positive_button));
        cancel.setText("Digital");
//        cancel.setLayoutParams(params);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                meterReading.meter_type = "Digital";
                submitReading(edtKWHReading.getText().toString().trim());
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.setCancelable(false);
        if (readerStatusCode.equals(getString(R.string.site_not_found)) || readerStatusCode.equals(getString(R.string.obstacle)) || readerStatusCode.equals(getString(R.string.premises_locked)) || readerStatusCode.equals(getString(R.string.meter_stolen))) {
            submitReading(edtKWHReading.getText().toString().trim());
        } else {
            if (alert.isShowing())
                alertForPreviousStatusCheck.dismiss();
            else
                alert.show();
        }
    }

    private void showMessageDialogForLocation() {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_database, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(this).create();

        final EditText edtUserName, edtPassword;
        final TextView txtTitle, txtError;

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        if (userProfile.city.equalsIgnoreCase("Kota-CD1"))
            txtTitle.setText("Is Hostel Premise ?");
        else
            txtTitle.setText(" Meter Located ?");
        txtTitle.setTypeface(regular);
        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);
        txtError.setVisibility(View.GONE);
        edtUserName = (EditText) promptView.findViewById(R.id.edt_user_name);
        edtUserName.setVisibility(View.GONE);
        edtPassword = (EditText) promptView.findViewById(R.id.edt_password);
        edtPassword.setVisibility(View.GONE);
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        if (userProfile.city.equalsIgnoreCase("Kota-CD1"))
            ok.setText("Yes");
        else
            ok.setText("Outside");
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (userProfile.city.equalsIgnoreCase("Kota-CD1"))
                    meterReading.meter_location = "Yes";
                else
                    meterReading.meter_location = "Outside";
                showMessageDialogForMeterType();
//                submitReading(edtKWHReading.getText().toString().trim());
                alert.dismiss();
            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        if (userProfile.city.equalsIgnoreCase("Kota-CD1"))
            cancel.setText("No");
        else
            cancel.setText("Inside");
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showMessageDialogForMeterType();
                if (userProfile.city.equalsIgnoreCase("Kota-CD1"))
                    meterReading.meter_location = "No";
                else
                    meterReading.meter_location = "Inside";
//                submitReading(edtKWHReading.getText().toString().trim());
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.setCancelable(false);
        if (readerStatusCode.equals(getString(R.string.site_not_found)) || readerStatusCode.equals(getString(R.string.reading_not_taken))) {
            submitReading(edtKWHReading.getText().toString().trim());
        } else if (readerStatusCode.equals(getString(R.string.premises_locked))) {
            meterReading.meter_location = "Inside";
            submitReading(edtKWHReading.getText().toString().trim());
        } else {
            if (alert.isShowing())
                alertForPreviousStatusCheck.dismiss();
            else
                alert.show();
        }
    }

    private void showMessageDialogForDistance() {

        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_database, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(this).create();

        final EditText edtUserName, edtPassword;
        final TextView txtTitle, txtError;

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtTitle.setText(R.string.are_you_sure_that_the_reading_is_taken_at_correct_location);
        txtTitle.setTypeface(regular);
        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);
        txtError.setVisibility(View.GONE);
        edtUserName = (EditText) promptView.findViewById(R.id.edt_user_name);
        edtUserName.setVisibility(View.GONE);
        edtPassword = (EditText) promptView.findViewById(R.id.edt_password);
        edtPassword.setVisibility(View.GONE);
//        CommonUtils.alertTone(mContext, R.raw.warning);
        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setText(R.string.yes);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                meterReading.cur_lat = newLatitude;
                meterReading.cur_lng = newLongitude;

                meterReading.is_lat_long_verified = "False";
                meterReading.distance = String.valueOf(distance);

                alert.dismiss();
                checkAirConditioner();
//                showMessageDialogForMeterType();
//                submitReading(edtKWHReading.getText().toString().trim());

            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setText(R.string.no);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mBitmapMeterReading = null;
                imgMeter.setImageBitmap(mBitmapMeterReading);
                setMeterStatusSpinner(R.array.update_meter_status_all);
                setReasonCodeSpinner(arrayForReasonCode);
                edtKWHReading.setText("");

//                showMessageDialogForMeterType();

//                submitReading(edtKWHReading.getText().toString().trim());
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
        /*if (readerStatusCode.equals(getString(R.string.site_not_found)) || readerStatusCode.equals(getString(R.string.reading_not_taken))) {
            submitReading(edtKWHReading.getText().toString().trim());
        } else if (readerStatusCode.equals(getString(R.string.premises_locked))) {
            meterReading.meter_location = "Inside";
            submitReading(edtKWHReading.getText().toString().trim());
        } else {
            if (alert.isShowing())
                alertForPreviousStatusCheck.dismiss();
            else
                alert.show();
        }*/
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
