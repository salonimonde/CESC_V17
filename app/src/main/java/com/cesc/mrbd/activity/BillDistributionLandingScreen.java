package com.cesc.mrbd.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.cesc.mrbd.fragments.BillDistributionCompletedFragment;
import com.cesc.mrbd.fragments.BillDistributionHistoryFragment;
import com.cesc.mrbd.fragments.BillDistributionOpenFragment;
import com.cesc.mrbd.models.BillCard;
import com.cesc.mrbd.models.JsonResponse;
import com.cesc.mrbd.models.UploadBillHistory;
import com.cesc.mrbd.models.UserProfile;
import com.cesc.mrbd.preferences.SharedPrefManager;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.AppPreferences;
import com.cesc.mrbd.utils.CommonUtils;
import com.cesc.mrbd.utils.DialogCreator;
import com.cesc.mrbd.webservice.WebRequests;
import com.google.gson.Gson;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class BillDistributionLandingScreen extends ParentActivity implements View.OnClickListener, ServiceCaller, NavigationView.OnNavigationItemSelectedListener {

    private Context mContext;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProgressDialog pDialog;
    private Menu menu;
    public static String meter_reader_id;
    private UserProfile userProfile;
    private Typeface regular, bold;
    private CircleImageView imgMrProfile, imgDrawerProfile;
    private TextView txtName, txtDrawerMRName, txtDrawerMobileNo, historyOpen, historyRevisit, historyUnBill;
    private LinearLayout toolbarLinearProfile;
    private int onCompleted = 0;
    private ViewPagerAdapter adapter;
    private ArrayList<BillCard> readingToUpload;
    private static boolean show = false;
    public static Activity context;
    private DrawerLayout drawer;
    private Intent intent;
    private Timer buttonTimer;
    private Button btnOpenHistory, btnRevisitHistory, btnUnBillHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initProgressDialog();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_distribution_landing_screen);
        mContext = this;
        context = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getUserProfileDetails();

        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();
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
        AppPreferences.getInstance(mContext).putString(AppConstants.SCREEN_FROM_EXIT, getString(R.string.bill_distribution));
        imgMrProfile = (CircleImageView) findViewById(R.id.image_profile);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtName.setTypeface(regular);
        txtName.setSelected(true);
        txtName.setText(userProfile.meter_reader_name);
        toolbarLinearProfile = (LinearLayout) findViewById(R.id.linear_profile);
        toolbarLinearProfile.setOnClickListener(this);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setFontOnTabs();
        final RelativeLayout mainView = (RelativeLayout) findViewById(R.id.mainView);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.app_name, R.string.app_name) {
            public void onDrawerClosed(View view) {
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                mainView.setTranslationX(-(slideOffset * drawerView.getWidth()));
            }
        };
        drawer.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        toolbar.setNavigationIcon(null);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_right);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(1).setChecked(true);

        txtDrawerMRName = (TextView) header.findViewById(R.id.txt_drawer_mr_name);
        txtDrawerMRName.setTypeface(bold);
        txtDrawerMRName.setText(userProfile.email_id + " | " + userProfile.meter_reader_name);
        txtDrawerMobileNo = (TextView) header.findViewById(R.id.txt_drawer_mobile_no);
        txtDrawerMobileNo.setTypeface(regular);
        txtDrawerMobileNo.setText(userProfile.contact_no);
        imgDrawerProfile = (CircleImageView) header.findViewById(R.id.img_drawer_profile);

        if (userProfile.profile_image != null && !userProfile.profile_image.equals("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setFontOnTabs();
            }

            @Override
            public void onPageSelected(int position) {
                if (viewPager.getCurrentItem() == 0) {
                    onCompleted = 0;
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.download_from_cloud));
                } else if (viewPager.getCurrentItem() == 1) {
                    onCompleted = 1;
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.upload_to_the_cloud));
                } else if (viewPager.getCurrentItem() == 2) {
                    onCompleted = 0;
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.download_from_cloud));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        reinitializeViewPager();
    }

    private void initProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(BillDistributionLandingScreen.this);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
        }
    }

    private void dismissDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void getData() {
        ArrayList<BillCard> open, close;
        int total = 0;
        open = DatabaseManager.getBillCards(mContext, meter_reader_id, AppConstants.BILL_CARD_STATUS_ALLOCATED);
        if (open != null) {
            btnRevisitHistory.setText(String.valueOf(open.size()));
            total += open.size();
        } else
            btnRevisitHistory.setText("0");
        close = DatabaseManager.getBillCards(mContext, meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED);
        if (close != null) {
            btnUnBillHistory.setText(String.valueOf(close.size()));
            total += close.size();
        } else
            btnUnBillHistory.setText("0");

        btnOpenHistory.setText(String.valueOf(total));
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BillDistributionOpenFragment(), getString(R.string.open));
        adapter.addFragment(new BillDistributionCompletedFragment(), getString(R.string.completed));
        adapter.addFragment(new BillDistributionHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);
        getData();
    }

    private void reinitializeViewPager() {
        adapter = null;
        viewPager.removeAllViews();
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BillDistributionOpenFragment(), getString(R.string.open));
        adapter.addFragment(new BillDistributionCompletedFragment(), getString(R.string.completed));
        adapter.addFragment(new BillDistributionHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);
        invalidateOptionsMenu();
        getData();
    }

    public void demoData() {
        String response = "{\"responsedata\":{\"billcards\":[{\"consumer_no\":\"111111\",\"jobcard_id\":\"11642\",\"subdivision_name\":\"D1 - Rampuriya\",\"cycle_code\":\"21\",\"binder_code\":\"2113\",\"start_date\":\"2018-07-05\",\"end_date\":\"2018-07-10\",\"consumer_assigned\":\"140\",\"meter_reader_id\":\"301\",\"jobcard_status\":\"Started\",\"distributed\":\"\",\"remark\":\"\",\"billmonth\":\"201806\",\"reading_date\":\"\",\"bill_received_count\":\"140\"},{\"jobcard_id\":\"11643\",\"zone_name\":\"D1 - Rampuriya\",\"cycle_code\":\"21\",\"binder_code\":\"2114\",\"start_date\":\"2018-07-05\",\"end_date\":\"2018-07-10\",\"consumer_assigned\":\"340\",\"meter_reader_id\":\"301\",\"jobcard_status\":\"Started\",\"distributed\":\"\",\"remark\":\"\",\"billmonth\":\"201806\",\"reading_date\":\"\",\"bill_received_count\":\"340\"},{\"jobcard_id\":\"11644\",\"subdivision_name\":\"D1 - Rampuriya\",\"cycle_code\":\"21\",\"binder_code\":\"2115\",\"start_date\":\"2018-07-05\",\"end_date\":\"2018-07-10\",\"consumer_assigned\":\"200\",\"meter_reader_id\":\"301\",\"jobcard_status\":\"Started\",\"distributed\":\"\",\"remark\":\"\",\"billmonth\":\"201806\",\"reading_date\":\"\",\"bill_received_count\":\"150\"},{\"jobcard_id\":\"11645\",\"subdivision_name\":\"D1 - Rampuriya\",\"cycle_code\":\"21\",\"binder_code\":\"2116\",\"start_date\":\"2018-07-05\",\"end_date\":\"2018-07-10\",\"consumer_assigned\":\"900\",\"meter_reader_id\":\"301\",\"jobcard_status\":\"Started\",\"distributed\":\"\",\"remark\":\"\",\"billmonth\":\"201806\",\"reading_date\":\"\",\"bill_received_count\":\"900\"}]}}";
        Gson gson = new Gson();
        JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
        if (jsonResponse.responsedata != null && jsonResponse.responsedata.billcards != null && jsonResponse.responsedata.billcards.size() > 0)
            DatabaseManager.saveBillCards(mContext, jsonResponse.responsedata.billcards);
        reinitializeViewPager();

    }

    @Override
    public void onClick(View v) {
        if (v == toolbarLinearProfile) {
            intent = new Intent(mContext, ProfileActivity.class);
            startActivity(intent);
        }
    }

    private void setFontOnTabs() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(regular);
                }
            }
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    private void getUserProfileDetails() {
        userProfile = DatabaseManager.getUserProfile(this, SharedPrefManager.getStringValue(this, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;

            if (userProfile.city.equalsIgnoreCase("Bikaner")) {
                AppPreferences.getInstance(mContext).putInt(AppConstants.PERCENTAGE, 0);
            } else if (userProfile.city.equalsIgnoreCase("Kota - CD1") || userProfile.city.equalsIgnoreCase("Kota - CD2")) {
                AppPreferences.getInstance(mContext).putInt(AppConstants.PERCENTAGE, 30);
            } else if (userProfile.city.equalsIgnoreCase("Bharatpur")) {
                AppPreferences.getInstance(mContext).putInt(AppConstants.PERCENTAGE, 30);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_action_hamburger));
        //To show count of notification on icon starts, Piyush : 28-02-17
        Drawable dre = ContextCompat.getDrawable(this, R.drawable.notification_icon);
        int count = DatabaseManager.getCount(this, "false", AppPreferences.getInstance(mContext).getString(AppConstants.METER_READER_ID, ""));
        if (count > 0)
            ActionItemBadge.update(this, menu.findItem(R.id.action_notifications), dre, ActionItemBadge.BadgeStyles.YELLOW, count);
        //To show count of notification on icon starts, Piyush : 29-02-17
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                if (onCompleted == 1) {
                    doBillUpload();
                } else {
                    show = true;
                    getBillCards();
                }
//                demoData();
                return true;
            case R.id.action_logout:
                drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
                return true;
            case R.id.action_notifications:
                callNotification();
//                demoData();
                return true;
            default:
                break;
        }
        return false;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_meter_reading) {
            drawer.closeDrawer(GravityCompat.END);
            intent = new Intent(mContext, LandingActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_bill_distribution) {
            drawer.closeDrawer(GravityCompat.END);
        } else if (id == R.id.nav_disconnetion) {
            drawer.closeDrawer(GravityCompat.END);
            intent = new Intent(mContext, DisconnectionNoticeLandingActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_payment) {
            drawer.closeDrawer(GravityCompat.END);
            intent = new Intent(mContext, MyPaymentActivity.class);
            startActivity(intent);
            finish();
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    private void getBillCards() {
        if (CommonUtils.isNetworkAvaliable(this) == true) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Your dialog code.
                    if (pDialog != null) {
                        pDialog.setMessage(getString(R.string.updating_your_assigned_consumer_information_please_wait));
                        pDialog.show();
                    }
                }
            });

            JsonObjectRequest request = WebRequests.getBillCards(Request.Method.GET, AppConstants.URL_GET_BILLING_DETAILS,
                    AppConstants.REQUEST_GET_BILLING_DETAILS, this,
                    SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
            App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_GET_BILLING_DETAILS);
        } else {
            dismissDialog();
            DialogCreator.showMessageDialog(this, getString(R.string.error_internet_not_connected), getString(R.string.error));
        }
    }

    private void getDeassignBillCards() {
        if (CommonUtils.isNetworkAvaliable(this) == true) {
            invalidateOptionsMenu();
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Your dialog code.
                    if (pDialog != null) {
                        pDialog.setMessage(getString(R.string.updating_your_deassign_consumer_data_please_wait));
                        pDialog.show();
                    }
                }
            });

            JsonObjectRequest request = WebRequests.getDeassignedBillCards(Request.Method.GET, AppConstants.URL_GET_DEASSIGNED_REASSIGNED_BILLING_DETAILS, AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_BILLING_DETAILS, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
            App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_BILLING_DETAILS);
        } else {
            dismissDialog();
            DialogCreator.showMessageDialog(this, getString(R.string.error_internet_not_connected), getString(R.string.error));
        }

    }

    private void callNotification() {
        intent = new Intent(this, NotificationActivity.class);
        intent.putExtra(AppConstants.CURRENT_METER_READER_ID, meter_reader_id);
        startActivity(intent);
    }

    private void doBillUpload() {
        if (CommonUtils.isNetworkAvaliable(this) == true) {
            readingToUpload = DatabaseManager.getBillMeterReadings(mContext, meter_reader_id, AppConstants.UPLOAD_COUNT);
            if (readingToUpload != null && readingToUpload.size() > 0) {
                JSONObject object = getMeterReadingJson(mContext, readingToUpload);
                uploadBillMeterReading(object);
            } else {
                Toast.makeText(mContext, getString(R.string.no_readings_available_to_be_uploaded), Toast.LENGTH_SHORT).show();
            }
        } else {
            DialogCreator.showMessageDialog(this, getString(R.string.error_internet_not_connected), getString(R.string.error));
        }
    }

    private void uploadBillMeterReading(JSONObject object) {
        showLoadingDialog();

        JsonObjectRequest request = WebRequests.uploadMeterReading(mContext, meter_reader_id, object, Request.Method.POST, AppConstants.URL_UPLOAD_BILLING_DISTRIBUTION, AppConstants.REQUEST_UPLOAD_BILLING_DISTRIBUTION, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
        App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_UPLOAD_BILLING_DISTRIBUTION);
    }

    public JSONObject getMeterReadingJson(Context context, ArrayList<BillCard> readings) {
        JSONObject jsonObject = null;
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(readings);
            JSONArray jsonArray = new JSONArray(jsonString);
            jsonObject = new JSONObject();
            jsonObject.put("readings", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            DialogCreator.showExitDialog(this, getString(R.string.exit_app), getString(R.string.do_you_want_to_exit), getString(R.string.bill_distribution_screen));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        reinitializeViewPager();

        if (userProfile.profile_image != null && !userProfile.profile_image.equalsIgnoreCase("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgMrProfile);
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
        } else {
            imgMrProfile.setImageResource(R.drawable.defaultprofile);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final MenuItem item = menu.findItem(R.id.action_sync);
        if (show) {
            item.setEnabled(false);
            item.getIcon().setAlpha(125);
            buttonTimer = new Timer();
            buttonTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            item.setEnabled(true);
                            item.getIcon().setAlpha(255);
                            show = false;
                        }
                    });
                }
            }, 10000);
        }
        return true;

    }

    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label) {
        switch (label) {
            case AppConstants.REQUEST_GET_BILLING_DETAILS: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.error_code != null && jsonResponse.error_code.equalsIgnoreCase("201")) {
                            dismissDialog();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogCreator.showMessageDialog(BillDistributionLandingScreen.this, getString(R.string.your_consumer_data_is_not_ready_please_try_after_sometime), getString(R.string.error));
                                }
                            });

                        } else {
                            if (jsonResponse.responsedata != null && jsonResponse.responsedata.billcards != null && jsonResponse.responsedata.billcards.size() > 0) {
                                DatabaseManager.saveBillCards(mContext, jsonResponse.responsedata.billcards);
                                reinitializeViewPager();
                                dismissDialog();
                                Toast.makeText(BillDistributionLandingScreen.this, getString(R.string.consumer_information_downloaded), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(BillDistributionLandingScreen.this, getString(R.string.consumer_information_not_assigned_to_you_for_reading_as_of_now), Toast.LENGTH_SHORT).show();
                            }

                            if (jsonResponse.authorization != null) {
                                CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                            }
                        }
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        Toast.makeText(mContext, R.string.please_contact_server_admin, Toast.LENGTH_SHORT).show();
                    }
                }
                dismissDialog();
                getDeassignBillCards();
            }
            break;

            case AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_BILLING_DETAILS: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.error_code != null && jsonResponse.error_code.equalsIgnoreCase("201")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogCreator.showMessageDialog(BillDistributionLandingScreen.this, getString(R.string.your_consumer_data_is_not_ready_please_try_after_sometime), getString(R.string.error));
                                }
                            });
                        }
                        if (jsonResponse.responsedata != null && jsonResponse.responsedata.re_de_bd_jobcards != null && jsonResponse.responsedata.re_de_bd_jobcards.size() > 0) {
                            DatabaseManager.handleAssignedDeassignedbillJobs(mContext, jsonResponse.responsedata.re_de_bd_jobcards, meter_reader_id);
                            //refresh summary UI
                            reinitializeViewPager();
                            dismissDialog();
                            Toast.makeText(BillDistributionLandingScreen.this, getString(R.string.updated_reassign_deassign_consumer_info_successfully), Toast.LENGTH_SHORT).show();

                        }
                        if (jsonResponse.authorization != null) {
                            CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                        }
                        dismissDialog();
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        dismissDialog();
                    }
                }
            }
            break;
            case AppConstants.REQUEST_UPLOAD_BILLING_DISTRIBUTION: {

                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        //changes for count in historyTab Starts Avinesh : 01-03-17
                        for (int i = 0; i < readingToUpload.size(); i++) {
                            if (readingToUpload != null) {
                                UploadBillHistory lUploadsHistory = new UploadBillHistory();
                                lUploadsHistory.binder_code = readingToUpload.get(i).binder_code;
                                lUploadsHistory.zone_name = readingToUpload.get(i).zone_name;
                                lUploadsHistory.zone_code = readingToUpload.get(i).zone_code;
                                lUploadsHistory.billmonth = readingToUpload.get(i).billmonth;
                                lUploadsHistory.consumer_name = readingToUpload.get(i).consumer_name;
                                lUploadsHistory.meter_reader_id = readingToUpload.get(i).meter_reader_id;
                                lUploadsHistory.reading_date = CommonUtils.getCurrentDate();
                                lUploadsHistory.cycle_code = readingToUpload.get(i).cycle_code;
                                lUploadsHistory.jobcard_id = readingToUpload.get(i).jobcard_id;
                                lUploadsHistory.consumer_no = readingToUpload.get(i).consumer_no;
                                lUploadsHistory.meter_no = readingToUpload.get(i).meter_no;
                                lUploadsHistory.is_new = readingToUpload.get(i).is_new;
                                DatabaseManager.saveUploadbillHistory(mContext, lUploadsHistory);
                                dismissLoadingDialog();
                            }
                        }
                        //changes for count in historyTab Ends Avinesh:01-03-17
                        DatabaseManager.deletebillJobs(mContext, readingToUpload, meter_reader_id);
                        doBillUpload();
                    }
                }
            }
            //refresh summary UI
            reinitializeViewPager();
            dismissDialog();
            break;
        }
    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response) {
        switch (label) {
            case AppConstants.REQUEST_GET_BILLING_DETAILS: {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                getDeassignBillCards();
            }
            break;
            case AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_BILLING_DETAILS: {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
            default:
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                break;
        }
        dismissDialog();
        invalidateOptionsMenu();
    }

    public static void landingFinish(Activity context) {
        context.finish();
    }

    @Override
    protected void onDestroy() {
        if (buttonTimer != null)
            buttonTimer.cancel();
        CommonUtils.deleteCache(mContext);
        super.onDestroy();
    }
}
