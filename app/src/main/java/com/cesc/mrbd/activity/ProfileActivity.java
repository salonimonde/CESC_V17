package com.cesc.mrbd.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cesc.mrbd.R;
import com.cesc.mrbd.callers.ServiceCaller;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.JobCard;
import com.cesc.mrbd.models.JsonResponse;
import com.cesc.mrbd.models.MeterImage;
import com.cesc.mrbd.models.MeterReading;
import com.cesc.mrbd.models.Response;
import com.cesc.mrbd.models.UserProfile;
import com.cesc.mrbd.preferences.SharedPrefManager;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.AppPreferences;
import com.cesc.mrbd.utils.CommonUtils;
import com.cesc.mrbd.utils.LocationManagerReceiver;
import com.cesc.mrbd.webservice.WebRequests;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by Avinesh on 8/22/2016.
 */
public class ProfileActivity extends ParentActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ServiceCaller {
    private static int RESULT_LOAD_IMG = 1, z = 0;
    private ImageView imgBack, cam, imgCall, imgLogOut, imgMyScore, imgcash;
    private CircleImageView imgView;
    private Button btn_rnt, btnMyScore;
    private Toolbar toolbar;
    private TextView tv_name, tv_city, tv_email, tv_phone, title, txtVersion;
    private String imgDecodableString;
    private UserProfile userProfile;
    private String meter_reader_id;
    private RelativeLayout relativeLayoutImage;
    private Context mContext;
    private LocationManagerReceiver mLocationManagerReceiver;
    Locale myLocale;
    private RadioButton btnEnglish, btnHindi;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private Location mLastLocation;
    private LinearLayout linearGetDatabases;
    private ProgressDialog pDialog;
    private Response response;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getUserProfileDetails();
        setupUI();
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
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

    private void setupUI() {
        mContext = this;
        Typeface regular = App.getSansationRegularFont();
        imgCall = (ImageView) findViewById(R.id.img_call);
        imgCall.setOnClickListener(this);
        imgcash = (ImageView) findViewById(R.id.img_cash);
        imgcash.setOnClickListener(this);
        imgLogOut = (ImageView) findViewById(R.id.img_logout);
        imgLogOut.setOnClickListener(this);
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        imgMyScore = (ImageView) findViewById(R.id.img_my_score);
        imgMyScore.setOnClickListener(this);
        tv_city = (TextView) findViewById(R.id.lbl_city);
        tv_city.setTypeface(regular);
        tv_name = (TextView) findViewById(R.id.lbl_namedata);
        tv_name.setTypeface(regular);
        tv_email = (TextView) findViewById(R.id.lbl_mr_email_id);
        tv_email.setTypeface(regular);
        title = (TextView) findViewById(R.id.title_bar);
        title.setTypeface(regular);
        tv_phone = (TextView) findViewById(R.id.lbl_phone_no);
        tv_phone.setTypeface(regular);
        btn_rnt = (Button) findViewById(R.id.rnt);
        btn_rnt.setTypeface(regular);
        btn_rnt.setOnClickListener(this);
        imgView = (CircleImageView) findViewById(R.id.iv_profile);
        cam = (ImageView) findViewById(R.id.ic_camera);
        cam.setOnClickListener(this);
        relativeLayoutImage = (RelativeLayout) findViewById(R.id.relative_image);
        btnMyScore = (Button) findViewById(R.id.btn_my_score);
        btnMyScore.setTypeface(regular);
        btnMyScore.setOnClickListener(this);

        linearGetDatabases = (LinearLayout) findViewById(R.id.linear_get_database);
        linearGetDatabases.setOnClickListener(this);

        txtVersion = (TextView) findViewById(R.id.lbl_version_no);
        try{

            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            txtVersion.setText(pInfo.versionName);

        }catch (Exception e){

        }

       /* String profile = AppPreferences.getInstance(this).getString(AppConstants.PROFILE_IMAGE, "");
        if(!profile.equals(""))
        {
            byte[] decodedString = Base64.decode(profile, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

            imgView.setImageBitmap(decodedByte);
            relativeLayoutImage.setBackground(new BitmapDrawable(getResources(), decodedByte));
        }*/

        if (userProfile != null) {
            tv_name.setText(userProfile.meter_reader_name);
            tv_phone.setText(userProfile.contact_no);
            tv_email.setText(userProfile.email_id);
            tv_city.setText(userProfile.city);
            String reader_name = userProfile.meter_reader_name;
            String reader_id = userProfile.meter_reader_id;
            tv_name.setText(reader_name);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                setTitle(getString(R.string.my_profile));
                toolbar.setSubtitle(reader_name + "(" + reader_id + ")");
                toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onBackPressed();
                    }
                });
            }
            if (userProfile.profile_image != null && !userProfile.profile_image.equals("")) {
                Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgView);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap myImage = getBitmapFromURL(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image);
                            relativeLayoutImage.setBackground(new BitmapDrawable(getResources(), myImage));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
            }
        }
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        btnEnglish = (RadioButton) findViewById(R.id.rb_english);
        btnEnglish.setOnClickListener(this);
        btnHindi = (RadioButton) findViewById(R.id.rb_hindi);
        btnHindi.setOnClickListener(this);

        String languageSelected = AppPreferences.getInstance(this).getString(AppConstants.LANGUAGE_SELECTED, "");
        if (languageSelected.equals("hindi")) {
            btnHindi.setChecked(true);
        } else {
            btnEnglish.setChecked(true);
        }
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

        /*String path = AppPreferences.getInstance(mContext).getString(AppConstants.PROFILE_IMAGE, "");
        loadImageFromStorage(path);*/
    }

    @Override
    public void onClick(View v) {
        if (v == btnEnglish) {
            AppPreferences.getInstance(this).putString(AppConstants.LANGUAGE_SELECTED, "english");
            setLocale("en");
        }
        if (v == btnHindi) {
            AppPreferences.getInstance(this).putString(AppConstants.LANGUAGE_SELECTED, "hindi");
            setLocale("hi");
        }

        if (v == btnMyScore) {
            /*MediaPlayer mp = MediaPlayer.create(this, R.raw.electro);
            mp.start();*/
            v.playSoundEffect(SoundEffectConstants.CLICK);
            Intent intent = new Intent(this, MyScoreActivity.class);
            startActivity(intent);
        }
        if (v == linearGetDatabases) {
            checkCounts();
        }
        switch (v.getId()) {
            case R.id.img_cash:
                Intent in = new Intent(this, MyPaymentActivity.class);
                startActivity(in);
                break;
            case R.id.ic_camera:
                changeImage();
                break;
           /* case R.id.rnt:
                if(CommonUtils.isNetworkAvaliable(this))
                     showDialog1(this, getString(R.string.are_you_sure), getString(R.string.want_to_make_reading_as_rnt));
                else
                    Toast.makeText(this, R.string.error_internet_not_connected,Toast.LENGTH_LONG).show();
                break;*/
            case R.id.img_logout:
                performLogout();
                break;
            case R.id.img_call:
                Intent intent1 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "8788610686"));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(intent1);

                break;
            case R.id.img_my_score:
                Intent intent = new Intent(this, MyScoreActivity.class);
                startActivity(intent);
                break;

        }
    }

    private void performLogout() {
        showLogoutDialog(this, getString(R.string.logout), getString(R.string.logout_string));

    }
    //Dialog for surety starts, Piyush : 02-03-17

    public void showLogoutDialog(final Activity activity, String title, String message) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View promptView = layoutInflater.inflate(R.layout.dialog_with_title, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(activity).create();
        TextView t = (TextView) promptView.findViewById(R.id.tv_title);
        t.setTypeface(regular);
        t.setText(title);
        TextView msg = (TextView) promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button yes = (Button) promptView.findViewById(R.id.btn_yes);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CommonUtils.logout(activity);
                try {
                    LandingActivity.landingFinish(LandingActivity.context);
                    BillDistributionLandingScreen.landingFinish(BillDistributionLandingScreen.context);
                } catch (Exception e) {
                }

                Intent in = new Intent(activity, LoginActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(in);
                activity.finish();
//                SharedPreferences settings = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//                settings.edit().clear().commit();
                alert.dismiss();
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
    //Dialog for Logout ends Piyush : 02-03-17


    public void showDialog1(final Activity activity, String title, String message) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View promptView = layoutInflater.inflate(R.layout.dialog_with_title, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(activity).create();
        TextView t = (TextView) promptView.findViewById(R.id.tv_title);
        t.setTypeface(regular);
        t.setText(title);
        TextView msg = (TextView) promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button yes = (Button) promptView.findViewById(R.id.btn_yes);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                yesSure();
                alert.dismiss();
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

    private void yesSure() {
//        showLoadingDialog(getString(R.string.please_wait));
//        new Handler().postDelayed(new Runnable(){
//            @Override
//            public void run()
//            {
        showLoadingDialog(getString(R.string.please_wait));

        new LongOperation().execute("");
//            }
//        }, 500);
    }
    //Dialog for surety ends, Piyush : 02-03-17

    /*   Method for click on cancel button on dilog */
    public void changeImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_profile_photo);
        builder.setItems(new CharSequence[]
                        {getString(R.string.gallery), getString(R.string.Remove)},
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                loadImagefromGallery();
                                break;
                            case 1:
                                setDefault();
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    public void loadImagefromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public void setDefault() {
        imgView.setImageResource(R.drawable.defaultprofile);
        // Changes for profile ui Starts Avinesh:3-03-17
        relativeLayoutImage.setBackgroundColor(CommonUtils.getColor(this, R.color.colorPrimary));
        userProfile.profile_image = "";
        DatabaseManager.saveImage(this, userProfile);

    }

    //Method for RNT Starts Avinesh : 02-03-17
    private void initProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(this);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
        }
    }

    private void dismissDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    /*private void completeAllReading() {

        ArrayList<JobCard> jobs = DatabaseManager.getJobCards(mContext, userProfile.meter_reader_id, AppConstants.JOB_CARD_STATUS_ALLOCATED, "False");
        if (jobs != null && jobs.size() > 0) {
            for (JobCard job : jobs) {
                MeterReading meter = new MeterReading();
                meter.job_card_id = job.job_card_id;
                meter.meter_no = job.meter_no;
                meter.meter_reader_id = job.meter_reader_id;
                meter.isRevisit = job.is_revisit;
                meter.reading_month = job.schedule_month;
                meter.meter_status = getString(R.string.normal);
                meter.reader_status = getString(R.string.ok);
                if(!job.prv_meter_reading.isEmpty())
                meter.current_meter_reading =String.valueOf(Integer.parseInt(job.prv_meter_reading)+10) ;
                meter.current_kvah_reading = "0";
                meter.current_kva_reading = "0";
                meter.current_kw_reading = "0";
                meter.current_pf_reading = "0";
                Bitmap bitmap = CommonUtils.addWaterMarkDate(BitmapFactory.decodeResource(getResources(), R.drawable.rnt_image), CommonUtils.getCurrentDateTime());
                meter.meter_image = new MeterImage("MeterReadingImage", CommonUtils.getBitmapEncodedString(bitmap), "");
                meter.prv_sequence = job.prv_sequence;
                meter.reading_taken_by = "Auto";
                meter.suspicious_activity = "False";
                meter.suspicious_activity_image = new MeterImage("", "", "");
                meter.suspicious_remark = " ";
                meter.reading_date = CommonUtils.getCurrentDateTime();
                meter.isUploaded = "False";
                meter.location_guidance = "";
                meter.panel_no = job.pole_no;
                meter.zone_code = job.zone_code;
                meter.panel_no = "";
                meter.iskwhroundcompleted = "0";
                meter.iskvahroundcompleted = "0";
                meter.mobile_no = job.phone_no;
                meter.meter_type = "NONE";
                //Save Routes in App Constants starts
                ArrayList<String> routes = DatabaseManager.getRoutes(mContext, meter_reader_id);
                for (int a = 0; a < routes.size(); a++) {
                    if (a == 0) {
                        AppPreferences.getInstance(mContext).putString(AppConstants.ROUTE_ID1, routes.get(0));
                    }
                    if (a == 1) {
                        AppPreferences.getInstance(mContext).putString(AppConstants.ROUTE_ID2, routes.get(1));
                    }
                    if (a == 2) {
                        AppPreferences.getInstance(mContext).putString(AppConstants.ROUTE_ID3, routes.get(2));
                    }
                    if (a == 3) {
                        AppPreferences.getInstance(mContext).putString(AppConstants.ROUTE_ID4, routes.get(3));
                    }
                }
                //Save Routes in App Constants ends
                // Sequence Logic starts
                String route1 = AppPreferences.getInstance(mContext).getString(AppConstants.ROUTE_ID1, "");
                String route2 = AppPreferences.getInstance(mContext).getString(AppConstants.ROUTE_ID2, "");
                String route3 = AppPreferences.getInstance(mContext).getString(AppConstants.ROUTE_ID3, "");
                String route4 = AppPreferences.getInstance(mContext).getString(AppConstants.ROUTE_ID4, "");

                String prv_sequence_saved1 = AppPreferences.getInstance(mContext).getString(AppConstants.PRV_SEQUENCE1, "");
                String prv_sequence_saved2 = AppPreferences.getInstance(mContext).getString(AppConstants.PRV_SEQUENCE2, "");
                String prv_sequence_saved3 = AppPreferences.getInstance(mContext).getString(AppConstants.PRV_SEQUENCE3, "");
                String prv_sequence_saved4 = AppPreferences.getInstance(mContext).getString(AppConstants.PRV_SEQUENCE4, "");

                if (route1.equals(job.route_code)) {
                    if (prv_sequence_saved1.isEmpty()) {
                        meter.new_sequence = job.meter_reader_id + "|" + job.bill_cycle_code + "|" + job.route_code + "|" + "0001";
                        AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE1, "0001");
                    } else {
                        int newSequence = Integer.parseInt(prv_sequence_saved1) + 1;
                        meter.new_sequence = job.meter_reader_id + "|" + job.bill_cycle_code + "|" + job.route_code + "|" + String.valueOf(String.format("%04d", newSequence));
                        AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE1, String.valueOf(newSequence));
                    }
                } else if (route2.equals(job.route_code)) {
                    if (prv_sequence_saved2.isEmpty()) {
                        meter.new_sequence = job.meter_reader_id + "|" + job.bill_cycle_code + "|" + job.route_code + "|" + "0001";
                        AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE2, "0001");
                    } else {
                        int newSequence = Integer.parseInt(prv_sequence_saved2) + 1;
                        meter.new_sequence = job.meter_reader_id + "|" + job.bill_cycle_code + "|" + job.route_code + "|" + String.valueOf(String.format("%04d", newSequence));
                        AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE2, String.valueOf(newSequence));
                    }
                } else if (route3.equals(job.route_code)) {
                    if (prv_sequence_saved3.isEmpty()) {
                        meter.new_sequence = job.meter_reader_id + "|" + job.bill_cycle_code + "|" + job.route_code + "|" + "0001";
                        AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE3, "0001");
                    } else {
                        int newSequence = Integer.parseInt(prv_sequence_saved3) + 1;
                        meter.new_sequence = job.meter_reader_id + "|" + job.bill_cycle_code + "|" + job.route_code + "|" + String.valueOf(String.format("%04d", newSequence));
                        AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE3, String.valueOf(newSequence));
                    }
                } else if (route4.equals(job.route_code)) {
                    if (prv_sequence_saved4.isEmpty()) {
                        meter.new_sequence = job.meter_reader_id + "|" + job.bill_cycle_code + "|" + job.route_code + "|" + "0001";
                        AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE4, "0001");
                    } else {
                        int newSequence = Integer.parseInt(prv_sequence_saved4) + 1;
                        meter.new_sequence = job.meter_reader_id + "|" + job.bill_cycle_code + "|" + job.route_code + "|" + String.valueOf(String.format("%04d", newSequence));
                        AppPreferences.getInstance(mContext).putString(AppConstants.PRV_SEQUENCE4, String.valueOf(newSequence));
                    }
                }
                // Sequence Logic ends
                //changes for Online Reading Starts Avinesh  7-04-17
//                mLocationManagerReceiver = new LocationManagerReceiver(this, meter, job);
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                if (mLastLocation != null) {
                    meter.cur_lat = String.valueOf(mLastLocation.getLatitude());
                    meter.cur_lng = String.valueOf(mLastLocation.getLongitude());

                } else {
                    meter.cur_lat = "0";
                    meter.cur_lng = "0";
                }
                DatabaseManager.saveMeterReadingRNT(mContext, meter);
                DatabaseManager.saveJobCardStatus(mContext, job, AppConstants.JOB_CARD_STATUS_COMPLETED);
                //changes for Online Reading Ends Avinesh  7-04-17

            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, getString(R.string.readings_punched_successfully), Toast.LENGTH_LONG).show();
                }
            });


        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, getString(R.string.no_job_cards_to_punch_rnt_readings), Toast.LENGTH_LONG).show();
                }
            });
        }

    }*/

    //Method for RNT ends Avinesh : 02-03-17
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imgDecodableString = cursor.getString(columnIndex);
                    cursor.close();
                }

//                imgView.setImageBitmap(BitmapFactory.decodeFile(imgDecodableString));
                // Changes for profile UI Starts Avinesh:3-03-17

                Bitmap bm = BitmapFactory.decodeFile(imgDecodableString);
//                saveToInternalStorage(bm);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] byteArrayImage = baos.toByteArray();
                String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                if (CommonUtils.isNetworkAvaliable(this)) {
                    initProgressDialog();
                    if (pDialog != null && !pDialog.isShowing()) {
                        pDialog.setMessage(" please wait..");
                        pDialog.show();
                    }
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("profile_image", encodedImage.toString() == null ? "" : encodedImage.toString());
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    JsonObjectRequest request = WebRequests.profileImageChange(this, Request.Method.POST, AppConstants.URL_USER_PROFILE_IMAGE, AppConstants.REQUEST_USER_PROFILE_IMAGE, this, obj, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
                    App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_USER_PROFILE_IMAGE);

                } else {
                    Toast.makeText(this, R.string.error_internet_not_connected, Toast.LENGTH_LONG).show();
                }
//                AppPreferences.getInstance(this).putString(AppConstants.PROFILE_IMAGE, encodedImage);

//                relativeLayoutImage.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeFile(imgDecodableString)));
                // Changes for profile UI Ends Avinesh:3-03-17
            } else {
                Toast.makeText(this, R.string.er_you_havent_picked_image, Toast.LENGTH_LONG).show();
            }
        } catch (OutOfMemoryError oom) {

            oom.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(this, R.string.er_something_wents_wrong, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
    }

    private void getUserProfileDetails() {
        userProfile = DatabaseManager.getUserProfile(this, SharedPrefManager.getStringValue(this, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;
        }
    }


    @Override
    public void onConnected( Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    public void setLocale(String lang) {

        myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, LandingActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }

    @Override
    public void onConnectionFailed( ConnectionResult connectionResult) {

    }

    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label) {
        switch (label) {

            case AppConstants.REQUEST_USER_PROFILE_IMAGE: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        response = jsonResponse.responsedata;
                        Toast.makeText(this, jsonResponse.message.toString(), Toast.LENGTH_SHORT).show();
                        CommonUtils.saveAuthToken(this, jsonResponse.authorization);
                        Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + jsonResponse.responsedata.user_info.get(0).
                                profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgView);
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Bitmap myImage = getBitmapFromURL(AppConstants.PROFILE_IMAGE_URL + response.user_info.get(0).profile_image);
                                    relativeLayoutImage.setBackground(new BitmapDrawable(getResources(), myImage));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        thread.start();
// Bitmap myImage = getBitmapFromURL(AppConstants.PROFILE_IMAGE_URL + jsonResponse.responsedata.user_info.get(0).
//                                profile_image);
//                        relativeLayoutImage.setBackground(new BitmapDrawable(getResources(), myImage));
                        DatabaseManager.saveImage(this, jsonResponse.responsedata.user_info.get(0));

                        dismissDialog();


                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        dismissDialog();
                    }
                } else
                    Toast.makeText(this, R.string.er_data_not_avaliable, Toast.LENGTH_LONG).show();
                dismissDialog();
            }
        }
    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response) {
        switch (label) {
            case AppConstants.REQUEST_USER_PROFILE_IMAGE: {
//                Log.i(label, "responseeeeeeeeeeee:" + response);
//                Log.i(label, "requestttttttttttttttttttttfail:" + message);
                dismissDialog();
                break;
            }
        }
    }


    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
//            completeAllReading();

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            dismissLoadingDialog();
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }

    private void checkCounts() {
        z++;
        if (z == 20) {
            z = 0;
            showDialogDatabase(this);
        }
    }

    //Dialog for surety begins, Piyush : 08-06-17
    public void showDialogDatabase(final Activity activity) {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View promptView = layoutInflater.inflate(R.layout.dialog_for_database, null);
        final android.support.v7.app.AlertDialog alert = new android.support.v7.app.AlertDialog.Builder(activity).create();

        final EditText edtUserName, edtPassword;
        final TextView txtTitle, txtError;

        txtTitle = (TextView) promptView.findViewById(R.id.txt_title);
        txtTitle.setTypeface(regular);
        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        txtError.setTypeface(regular);

        edtUserName = (EditText) promptView.findViewById(R.id.edt_user_name);
        edtUserName.setText("Piyush Kalmegh");
        edtPassword = (EditText) promptView.findViewById(R.id.edt_password);

        Button ok = (Button) promptView.findViewById(R.id.btn_ok);

        ok.setTypeface(regular);
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (edtUserName.getText().toString().trim().equals("Piyush Kalmegh") &&
                        edtPassword.getText().toString().trim().equals("GetDatabasePK")) {
                    txtError.setVisibility(View.GONE);
                    getDatabase();
                    alert.dismiss();
                    done();
                } else {
                    txtError.setVisibility(View.VISIBLE);
                }
            }
        });

        Button cancel = (Button) promptView.findViewById(R.id.btn_cancel);
        cancel.setTypeface(regular);
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtError.setVisibility(View.GONE);
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.show();
    }
    //Dialog for surety ends, Piyush : 08-06-17

    private void getDatabase() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String currentDBPath = "/data/data/" + getPackageName() + "/databases/CESC.db";
                String backupDBPath = "BackUpCESC.db";
                File currentDB = new File(currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void done() {
        Toast.makeText(mContext, "Database Retrieved Successfully", Toast.LENGTH_SHORT).show();
    }

    public String saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                loadImageFromStorage(directory.toString());
                AppPreferences.getInstance(this).putString(AppConstants.PROFILE_IMAGE, directory.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public void loadImageFromStorage(String path) {

        try {
            File f = new File(path, "profile.jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imgView.setImageBitmap(b);
            relativeLayoutImage.setBackground(new BitmapDrawable(getResources(), b));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}