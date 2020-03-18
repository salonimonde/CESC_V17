package com.cesc.mrbd.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cesc.mrbd.R;
import com.cesc.mrbd.adapters.ViewPagerAdapter;
import com.cesc.mrbd.callers.ServiceCaller;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.fragments.LandingHistoryFragment;
import com.cesc.mrbd.fragments.LandingReadingsFragment;
import com.cesc.mrbd.fragments.LandingSummaryFragment;
import com.cesc.mrbd.fragments.LandingTodayFragment;
import com.cesc.mrbd.models.Consumer;
import com.cesc.mrbd.models.HistoryCard;
import com.cesc.mrbd.models.JobCard;
import com.cesc.mrbd.models.JsonResponse;
import com.cesc.mrbd.models.MeterReading;
import com.cesc.mrbd.models.PendingCount;
import com.cesc.mrbd.models.Sequence;
import com.cesc.mrbd.models.SummaryCard;
import com.cesc.mrbd.models.SummaryCount;
import com.cesc.mrbd.models.UploadsHistory;
import com.cesc.mrbd.models.UserProfile;
import com.cesc.mrbd.preferences.SharedPrefManager;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.AppPreferences;
import com.cesc.mrbd.utils.CommonUtils;
import com.cesc.mrbd.utils.DialogCreator;
import com.cesc.mrbd.utils.LocationManagerReceiver;
import com.cesc.mrbd.webservice.WebRequests;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.Gson;
import com.mikepenz.actionitembadge.library.ActionItemBadge;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class LandingActivity extends ParentActivity implements View.OnClickListener, ServiceCaller, NavigationView.OnNavigationItemSelectedListener {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Context mContext;
    private Toolbar mToolbar;
    private Button openCountButton, revisitCountButton, pendingUploadButton, totalButton, btnRoute, btnTotal, btnOpen,
            btnCompleted, btnHistory, btnRevisitHistory, btnUnBillHistory;
    private Button normalPendingReadings, revisitPendingReadings, unBillPendingReadings, totalPendingReadings;
    private ImageButton btnSearch, btnScanQrCode, btnAddNewConsumer;
    private LinearLayout toolbarLinearProfile;
    private int mPageNumber = 1;
    private ArrayList<MeterReading> readingToUpload;
    private ProgressDialog pDialog;
    public static String meter_reader_id;
    private ArrayList<Consumer> unBillConsumerToUpload;
    private UserProfile userProfile;
    private ViewPagerAdapter adapter;
    private TextView txtName, total, open, pending, revisit, pendingOpen, pendingTotal, pendingRoutes, pendingCompleted,
            historyOpen, historyRevisit, historyUnBill, txtReadingTotal, txtReadingNormal, txtReadingRevisit, txtReadingNew,
            txtDrawerMRName, txtDrawerMobileNo;
    private CircleImageView imgMrProfile, imgDrawerProfile;
    private SummaryCount summaryCount;
    private Typeface regular, bold;
    private ArrayList<MeterReading> deleteJobs;
    private FrameLayout symmary, history, today, reading;
    public Menu MyMenu;
    private int isReadings = 0;
    private Menu menu;
    public static boolean isTodayfragment;
    private ArrayList<String> routes;
    private ArrayList<SummaryCard> mSummaryCardsArray;
    private int position = 0, versionCode;
    public static int checkIt = 0;
    private String binder = "All";
    public static Activity context;
    private Spinner spinnerBinder;
    private ArrayAdapter<String> dataAdapter;
    private DrawerLayout drawer;
    private Intent intent;
    private Timer buttonTimer;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    private GoogleApiClient client;
    private LocationManagerReceiver mLocationManagerReceiver;
    private static boolean show = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        mContext = this;
        context = this;
        AppPreferences.getInstance(mContext).putString(AppConstants.SCREEN_FROM_EXIT, getString(R.string.landing_screen));
        mLocationManagerReceiver = new LocationManagerReceiver(this);
        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();
        getUserProfileDetails();
        imgMrProfile = (CircleImageView) findViewById(R.id.image_profile);
        if (userProfile.profile_image != null && !userProfile.profile_image.equals("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgMrProfile);
        }

        if (App.welcome == true) {
            TextView title = new TextView(mContext);
            title.setText("Due Alert");
            title.setGravity(Gravity.CENTER);
            title.setTextSize(30);
            DialogCreator.showMessageDialog(mContext, getString(R.string.welcome_you_have_successfully_login), getString(R.string.success));
            App.welcome = false;
        }
        summaryCount = DatabaseManager.getSummary(mContext, meter_reader_id);
        /*if (summaryCount.pendingUpload == 0) {
            try {
                PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                versionCode = pInfo.versionCode;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (Integer.parseInt(userProfile.app_version) != versionCode && Integer.parseInt(userProfile.app_version) > versionCode) {
                if (userProfile.app_link != null)
                    DialogCreator.showUpdateDialog(mContext, getString(R.string.alert_app_ready_to_update), userProfile.app_link);
            }
        }*/
        initProgressDialog();
        today = (FrameLayout) findViewById(R.id.submenu_today_container);
        reading = (FrameLayout) findViewById(R.id.submenu_reading_upload_container);
        symmary = (FrameLayout) findViewById(R.id.submenu_summary_container);
        symmary.setVisibility(View.GONE);
        history = (FrameLayout) findViewById(R.id.submenu_history_container);
        history.setVisibility(View.GONE);
        toolbarLinearProfile = (LinearLayout) findViewById(R.id.linear_profile);
        toolbarLinearProfile.setOnClickListener(this);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtName.setTypeface(regular);
        txtName.setSelected(true);
        historyOpen = (TextView) findViewById(R.id.tv_open_history);
        historyOpen.setTypeface(regular);
        historyRevisit = (TextView) findViewById(R.id.tv_revisit_history);
        historyRevisit.setTypeface(regular);
        historyUnBill = (TextView) findViewById(R.id.tv_unbill_history);
        historyUnBill.setTypeface(regular);
        btnHistory = (Button) findViewById(R.id.open_history);
        btnHistory.setTypeface(regular);
        btnRevisitHistory = (Button) findViewById(R.id.revisit_history);
        btnRevisitHistory.setTypeface(regular);
        btnUnBillHistory = (Button) findViewById(R.id.unbill_history);
        btnUnBillHistory.setTypeface(regular);
        pendingOpen = (TextView) findViewById(R.id.tv_open_summary);
        pendingOpen.setTypeface(regular);
        pendingTotal = (TextView) findViewById(R.id.tv_total_summary);
        pendingTotal.setTypeface(regular);
        pendingCompleted = (TextView) findViewById(R.id.tv_completed_symmary);
        pendingCompleted.setTypeface(regular);
        pendingRoutes = (TextView) findViewById(R.id.tv_routes);
        pendingRoutes.setTypeface(regular);
        txtReadingTotal = (TextView) findViewById(R.id.txt_reading_total);
        txtReadingTotal.setTypeface(regular);
        txtReadingNormal = (TextView) findViewById(R.id.txt_reading_normal);
        txtReadingNormal.setTypeface(regular);
        txtReadingRevisit = (TextView) findViewById(R.id.txt_reading_revisit);
        txtReadingRevisit.setTypeface(regular);
        txtReadingNew = (TextView) findViewById(R.id.txt_reading_new);
        txtReadingNew.setTypeface(regular);
        txtName.setText(userProfile.meter_reader_name);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setFontOnTabs();
        btnRoute = (Button) findViewById(R.id.routes);
        btnRoute.setTypeface(regular);
        btnTotal = (Button) findViewById(R.id.total_summary);
        btnTotal.setTypeface(regular);
        btnOpen = (Button) findViewById(R.id.open_summary);
        btnOpen.setTypeface(regular);
        btnCompleted = (Button) findViewById(R.id.completed);
        btnCompleted.setTypeface(regular);
        total = (TextView) findViewById(R.id.total);
        total.setTypeface(regular);
        open = (TextView) findViewById(R.id.open);
        open.setTypeface(regular);
        revisit = (TextView) findViewById(R.id.revisit);
        revisit.setTypeface(regular);
        pending = (TextView) findViewById(R.id.pending);
        pending.setTypeface(regular);
        totalButton = (Button) findViewById(R.id.tv_total);
        totalButton.setTypeface(regular);
        openCountButton = (Button) findViewById(R.id.tv_open);
        openCountButton.setTypeface(regular);
        revisitCountButton = (Button) findViewById(R.id.tv_revisit);
        revisitCountButton.setTypeface(regular);
        pendingUploadButton = (Button) findViewById(R.id.tv_pending_upload);
        pendingUploadButton.setTypeface(regular);
        btnSearch = (ImageButton) findViewById(R.id.landing_search_action);
        btnSearch.setOnClickListener(this);
        btnAddNewConsumer = (ImageButton) findViewById(R.id.landing_add_consumer_action);
        btnAddNewConsumer.setOnClickListener(this);
        btnScanQrCode = (ImageButton) findViewById(R.id.landing_scan_qr_action);
        btnScanQrCode.setOnClickListener(this);
        openCountButton.setOnClickListener(this);
        revisitCountButton.setOnClickListener(this);
        pendingUploadButton.setOnClickListener(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        totalPendingReadings = (Button) findViewById(R.id.tv_total_pending_upload);
        totalPendingReadings.setTypeface(regular);
        normalPendingReadings = (Button) findViewById(R.id.tv_normal_Pending);
        normalPendingReadings.setTypeface(regular);
        revisitPendingReadings = (Button) findViewById(R.id.tv_revisit_pending);
        revisitPendingReadings.setTypeface(regular);
        unBillPendingReadings = (Button) findViewById(R.id.tv_Unbill_pending);
        unBillPendingReadings.setTypeface(regular);
        totalPendingReadings.setOnClickListener(this);
        normalPendingReadings.setOnClickListener(this);
        revisitPendingReadings.setOnClickListener(this);
        unBillPendingReadings.setOnClickListener(this);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final RelativeLayout mainView = (RelativeLayout) findViewById(R.id.mainView);

        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.app_name, R.string.app_name) {
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
        mToolbar.setNavigationIcon(null);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_right);
        View header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0).setChecked(true);

        txtDrawerMRName = (TextView) header.findViewById(R.id.txt_drawer_mr_name);
        txtDrawerMRName.setTypeface(bold);
        txtDrawerMobileNo = (TextView) header.findViewById(R.id.txt_drawer_mobile_no);
        txtDrawerMobileNo.setTypeface(regular);
        imgDrawerProfile = (CircleImageView) header.findViewById(R.id.img_drawer_profile);

        if (userProfile.profile_image != null && !userProfile.profile_image.equals("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
        }
        txtDrawerMRName.setText(userProfile.email_id + " | " + userProfile.meter_reader_name);
        txtDrawerMobileNo.setText(userProfile.contact_no);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                /*invalidateOptionsMenu();*/
                if (viewPager.getCurrentItem() == 0) {
                    isReadings = 0;
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.download_from_cloud));
                    today.setVisibility(View.VISIBLE);
                    reading.setVisibility(View.GONE);
                    symmary.setVisibility(View.GONE);
                    history.setVisibility(View.GONE);
                    btnScanQrCode.setEnabled(true);
                } else if (viewPager.getCurrentItem() == 1) {
                    isReadings = 1;
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.upload_to_the_cloud));
                    today.setVisibility(View.GONE);
                    reading.setVisibility(View.VISIBLE);
                    symmary.setVisibility(View.GONE);
                    history.setVisibility(View.GONE);
                    btnScanQrCode.setEnabled(false);
                } else if (viewPager.getCurrentItem() == 2) {
                    isReadings = 0;
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.download_from_cloud));
                    today.setVisibility(View.GONE);
                    reading.setVisibility(View.GONE);
                    symmary.setVisibility(View.VISIBLE);
                    history.setVisibility(View.GONE);
                    btnScanQrCode.setEnabled(false);
                } else if (viewPager.getCurrentItem() == 3) {
                    isReadings = 0;
                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.download_from_cloud));
                    today.setVisibility(View.GONE);
                    reading.setVisibility(View.GONE);
                    symmary.setVisibility(View.GONE);
                    history.setVisibility(View.VISIBLE);
                    btnScanQrCode.setEnabled(false);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 1) {
//                    today.setVisibility(View.VISIBLE);
                } else {
                    today.setVisibility(View.VISIBLE);
                    symmary.setVisibility(View.GONE);
                    history.setVisibility(View.GONE);
                    reading.setVisibility(View.GONE);
                }
                super.onTabSelected(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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

    @Override
    protected void onResume() {
        super.onResume();
        resetSequences();
        mLocationManagerReceiver = new LocationManagerReceiver(this);
        this.invalidateOptionsMenu();

        binder = AppPreferences.getInstance(mContext).getString(AppConstants.FILTER_BINDER, "");

        reinitializeViewPager("False", "False", binder);
        loadData();
        userProfile = DatabaseManager.getUserProfile(this, meter_reader_id);
        if (userProfile.profile_image != null && !userProfile.profile_image.equals("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgMrProfile);
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
        } else
            imgMrProfile.setImageResource(R.drawable.defaultprofile);

    }

    private void getUserProfileDetails() {
        userProfile = DatabaseManager.getUserProfile(this, SharedPrefManager.getStringValue(this, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;
            AppPreferences.getInstance(mContext).putString(AppConstants.METER_READER_ID, userProfile.meter_reader_id);
        }
    }

    private void initProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(LandingActivity.this);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
        }
    }

    private void dismissDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    private void loadData() {
        setFontOnTabs();
        totalButton.setTypeface(regular);
        revisitCountButton.setTypeface(regular);
        openCountButton.setTypeface(regular);
        pendingUploadButton.setTypeface(regular);
        pendingUploadButton.setTypeface(regular);

        if (binder.equals("All")) {
            summaryCount = DatabaseManager.getSummary(mContext, meter_reader_id);
            totalButton.setText(MessageFormat.format("{0}", summaryCount.total_jobs));
            openCountButton.setText(MessageFormat.format("{0}", summaryCount.open));
            revisitCountButton.setText(MessageFormat.format("{0}", summaryCount.revisit));
            pendingUploadButton.setText(MessageFormat.format("{0}", summaryCount.pendingUpload));
            if (summaryCount.open != 0) {
                routes = new ArrayList<>();
                routes.add("All");
                routes.addAll(DatabaseManager.getRoutes(this, meter_reader_id));
                setAdapter(routes);
            }
        } else {
            try {
                mSummaryCardsArray = DatabaseManager.getSummaryCard(mContext, meter_reader_id, binder);
                routes = new ArrayList<>();
                routes.add("All");
                routes.addAll(DatabaseManager.getRoutes(this, meter_reader_id));
                setAdapter(routes);

                if (position == 0) {
                    int pos = Integer.parseInt(AppPreferences.getInstance(mContext).getString(AppConstants.TOTAL, ""));
                    position = pos;
                }

                final SummaryCard item = mSummaryCardsArray.get(position - 1);
                totalButton.setText(MessageFormat.format("{0}", item.total));
                openCountButton.setText(MessageFormat.format("{0}", item.open));
                revisitCountButton.setText(MessageFormat.format("{0}", item.revisit));
                pendingUploadButton.setText(MessageFormat.format("{0}", item.completed));
            } catch (Exception e) {
                binder = "All";
                AppPreferences.getInstance(mContext).putString(AppConstants.FILTER_BINDER, binder);
                position = 0;
                reinitializeViewPager("False", "False", binder);
            }
        }
        loadReadingsData();
        loadSummaryData();
        loadHistoryData();
    }


    private void loadHistoryData() {
        HistoryCard lHistoryCard = DatabaseManager.getUploadsHistoryCounts(this, meter_reader_id);
        btnHistory.setText(String.valueOf(lHistoryCard.open));
        btnRevisitHistory.setText(String.valueOf(lHistoryCard.revisit));
        btnUnBillHistory.setText(String.valueOf(lHistoryCard.unbill));
    }

    public void loadHistoryData(int pOpen, int pRevisit, int pUnbill) {
        btnHistory.setText(String.valueOf(pOpen));
        btnRevisitHistory.setText(String.valueOf(pRevisit));
        btnUnBillHistory.setText(String.valueOf(pUnbill));
    }

    private void loadSummaryData() {
        summaryCount = DatabaseManager.getSummary(mContext, meter_reader_id);
        PendingCount pendingCount = DatabaseManager.getpendingcount(mContext, meter_reader_id);
        ArrayList<String> lRoutesList = DatabaseManager.getTotalRoutes(mContext, meter_reader_id);
        int lRoutes;

        if (lRoutesList != null)
            lRoutes = lRoutesList.size();
        else
            lRoutes = 0;

        btnRoute.setText(MessageFormat.format("{0}", lRoutes));
        btnTotal.setText(MessageFormat.format("{0}", summaryCount.total_jobs));
        btnOpen.setText(MessageFormat.format("{0}", summaryCount.open + summaryCount.revisit));
        btnCompleted.setText(MessageFormat.format("{0}", pendingCount.normalpending + pendingCount.revisitpending));
    }

    private void loadReadingsData() {
        summaryCount = DatabaseManager.getSummary(this, meter_reader_id);
        PendingCount pendingCount = DatabaseManager.getpendingcount(this, meter_reader_id);
        totalPendingReadings.setTypeface(regular);
        totalPendingReadings.setText(MessageFormat.format("{0}", summaryCount.pendingUpload));
        normalPendingReadings.setTypeface(regular);
        normalPendingReadings.setText(MessageFormat.format("{0}", pendingCount.normalpending));
        revisitPendingReadings.setTypeface(regular);
        revisitPendingReadings.setText(MessageFormat.format("{0}", pendingCount.revisitpending));
        unBillPendingReadings.setTypeface(regular);
        unBillPendingReadings.setText(MessageFormat.format("{0}", pendingCount.unbillpending));
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), false);
        adapter.addFragment(LandingTodayFragment.newInstance(meter_reader_id, "False", "False", "False"), getString(R.string.today));
        adapter.addFragment(new LandingReadingsFragment(), getString(R.string.readings));
        adapter.addFragment(new LandingSummaryFragment(), getString(R.string.summary));
        adapter.addFragment(new LandingHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);
    }

    private void reinitializeViewPager(String isRevisited, String showIsPending, String isFilterApply) {
        adapter = null;
        viewPager.removeAllViews();
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), false);
        adapter.addFragment(LandingTodayFragment.newInstance(meter_reader_id, isRevisited, showIsPending, isFilterApply), getString(R.string.today));
        adapter.addFragment(new LandingReadingsFragment(), getString(R.string.readings));
        adapter.addFragment(new LandingSummaryFragment(), getString(R.string.summary));
        adapter.addFragment(new LandingHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);
        loadData();
    }

    private void reinitializeViewPager1(int isNormal, int isRevisit, int isNewReading) {
        adapter = null;
        viewPager.removeAllViews();
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), false);
        adapter.addFragment(new LandingTodayFragment(), getString(R.string.today));
        adapter.addFragment(LandingReadingsFragment.newInstance(isNormal, isRevisit, isNewReading), getString(R.string.readings));
        adapter.addFragment(new LandingSummaryFragment(), getString(R.string.summary));
        adapter.addFragment(new LandingHistoryFragment(), getString(R.string.history));
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        loadData();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            DialogCreator.showExitDialog(this, getString(R.string.exit_app), getString(R.string.do_you_want_to_exit), getString(R.string.landing_screen));
        }
    }

    @Override
    public void onClick(View v) {
        if (openCountButton == v) {
            reinitializeViewPager("False", "False", binder);
            openCountButton.setBackgroundResource(R.drawable.ripple_oval_red);
            openCountButton.setTextColor(CommonUtils.getColor(this, R.color.black));
            revisitCountButton.setBackgroundResource(R.drawable.ripple_oval_black);
            revisitCountButton.setTextColor(CommonUtils.getColor(this, R.color.colorWhite));
            pendingUploadButton.setBackgroundResource(R.drawable.ripple_oval_black);

        }
        if (revisitCountButton == v) {
            checkIt = 1;
            reinitializeViewPager("True", "False", binder);
            openCountButton.setBackgroundResource(R.drawable.ripple_oval_black);
            openCountButton.setTextColor(CommonUtils.getColor(this, R.color.colorWhite));
            revisitCountButton.setBackgroundResource(R.drawable.ripple_oval_red);
            revisitCountButton.setTextColor(CommonUtils.getColor(this, R.color.black));
            pendingUploadButton.setBackgroundResource(R.drawable.ripple_oval_black);
        }
        if (v == btnSearch) {
            if (summaryCount != null && summaryCount.open > 0 || summaryCount.revisit > 0) {
                intent = new Intent(mContext, SearchActivity.class);
                intent.putExtra(AppConstants.CURRENT_METER_READER_ID, meter_reader_id);
                startActivity(intent);
            } else {
                Toast.makeText(mContext, getString(R.string.please_update_consumer_data_before_starting_search), Toast.LENGTH_LONG).show();
            }

        }
        if (v == toolbarLinearProfile) {
            intent = new Intent(mContext, ProfileActivity.class);
            startActivity(intent);

        }
        if (v == btnScanQrCode) {
            if (summaryCount != null && summaryCount.open > 0 || summaryCount.revisit > 0) {
                if (routes.size() > 2) {
                    showFilterDialog(mContext);
                } else {
                    Toast.makeText(mContext, getString(R.string.only_one_binder_is_assign_to_you), Toast.LENGTH_LONG).show();
                }
            } else
                Toast.makeText(mContext, getString(R.string.please_update_consumer_data_before_start_filter), Toast.LENGTH_LONG).show();
        }
        if (v == btnAddNewConsumer) {
            if (summaryCount != null && summaryCount.open > 0 || summaryCount.revisit > 0) {
                App.ConsumerAddedBy = getString(R.string.meter_reading_manual);
                intent = new Intent(mContext, AddNewConsumerActivity.class);
                startActivity(intent);
            } else
                Toast.makeText(mContext, getString(R.string.please_update_consumer_data_before_adding_new_consumer), Toast.LENGTH_LONG).show();
        }

        //Code For Reading Tab starts, Piyush : 03-03-17
        if (v == normalPendingReadings) {
            reinitializeViewPager1(1, 0, 0);
            normalPendingReadings.setBackgroundResource(R.drawable.ripple_oval_red);
            normalPendingReadings.setTextColor(CommonUtils.getColor(this, R.color.black));
            revisitPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            revisitPendingReadings.setTextColor(CommonUtils.getColor(this, R.color.colorWhite));
            unBillPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            unBillPendingReadings.setTextColor(CommonUtils.getColor(this, R.color.colorWhite));
        }
        if (revisitPendingReadings == v) {
            reinitializeViewPager1(0, 1, 0);
            normalPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            normalPendingReadings.setTextColor(CommonUtils.getColor(this, R.color.colorWhite));
            revisitPendingReadings.setBackgroundResource(R.drawable.ripple_oval_red);
            revisitPendingReadings.setTextColor(CommonUtils.getColor(this, R.color.black));
            unBillPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            unBillPendingReadings.setTextColor(CommonUtils.getColor(this, R.color.colorWhite));
        }
        if (unBillPendingReadings == v) {
            reinitializeViewPager1(0, 0, 1);
            normalPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            normalPendingReadings.setTextColor(CommonUtils.getColor(this, R.color.colorWhite));
            revisitPendingReadings.setBackgroundResource(R.drawable.ripple_oval_black);
            revisitPendingReadings.setTextColor(CommonUtils.getColor(this, R.color.colorWhite));
            unBillPendingReadings.setBackgroundResource(R.drawable.ripple_oval_red);
            unBillPendingReadings.setTextColor(CommonUtils.getColor(this, R.color.black));
        }
        //Code For Reading Tab starts, Piyush : 03-03-17

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
                        mLocationManagerReceiver = new LocationManagerReceiver(this);
//                        LocationReceiver locationReceiver = new LocationReceiver(this);
                        break;
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        this.menu = menu;
        menu.getItem(2).setIcon(getResources().getDrawable(R.drawable.ic_action_hamburger));
        //To show count of notification on icon starts, Piyush : 28-02-17
        Drawable dre = ContextCompat.getDrawable(this, R.drawable.notification_icon);
        int count = DatabaseManager.getCount(this, "false", meter_reader_id);
        if (count > 0)
            ActionItemBadge.update(this, menu.findItem(R.id.action_notifications), dre, ActionItemBadge.BadgeStyles.YELLOW, count);
        MyMenu = menu;
        //To show count of notification on icon starts, Piyush : 29-02-17
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sync:
                if (isReadings == 0) {
                    show = true;
                    mPageNumber = 1;
                    getAllJobCards();
                } else {
                    doMeterUpload();
                }
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
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_meter_reading) {
            drawer.closeDrawer(GravityCompat.END);
        } else if (id == R.id.nav_bill_distribution) {
            drawer.closeDrawer(GravityCompat.END);
            intent = new Intent(mContext, BillDistributionLandingScreen.class);
            startActivity(intent);
            finish();
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

    private void doMeterUpload() {
        if (CommonUtils.isNetworkAvaliable(this) == true) {
            readingToUpload = DatabaseManager.getMeterReadings(mContext, meter_reader_id, AppConstants.UPLOAD_COUNT);
            if (readingToUpload != null && readingToUpload.size() > 0) {
                JSONObject object = getMeterReadingJson(mContext, readingToUpload);
                uploadMeterReading(object);
            } else {
                Toast.makeText(mContext, getString(R.string.no_readings_available_to_be_uploaded), Toast.LENGTH_LONG).show();
                uploadConsumerReading();
            }
        } else {
            DialogCreator.showMessageDialog(this, getString(R.string.error_internet_not_connected), getString(R.string.error));
        }

    }

    public JSONObject getMeterReadingJson(Context context, ArrayList<MeterReading> readings) {
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

    private void uploadMeterReading(JSONObject object) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Your dialog code.
                if (pDialog != null) {
                    pDialog.setMessage(getString(R.string.uploading_meter_readings_please_wait));
                    pDialog.show();
                }
            }
        });
        JsonObjectRequest request = WebRequests.uploadMeterReading(mContext, meter_reader_id, object, Request.Method.POST, AppConstants.URL_UPLOAD_METER_READING, AppConstants.REQEST_UPLOAD_METER_READING, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
        App.getInstance().addToRequestQueue(request, AppConstants.REQEST_UPLOAD_METER_READING);
    }

    private void uploadConsumerReading() {
        unBillConsumerToUpload = DatabaseManager.getUnbillConsumers(mContext, meter_reader_id, AppConstants.UPLOAD_COUNT);
        if (unBillConsumerToUpload != null && unBillConsumerToUpload.size() > 0) {
            JSONObject jObject = getUnbillMeterReadingJson(mContext, unBillConsumerToUpload);
            uploadConsumerReading(jObject);
        } else {
            Toast.makeText(mContext, getString(R.string.consumer_readings_are_not_available_for_upload), Toast.LENGTH_LONG).show();
        }
    }

    private void uploadConsumerReading(JSONObject object) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Your dialog code.
                if (pDialog != null) {
                    pDialog.setMessage(getString(R.string.uploading_new_consumer_meter_readings_please_wait));
                    pDialog.show();
                }
            }
        });
        JsonObjectRequest request = WebRequests.uploadConsumerMeterReading(mContext, meter_reader_id, object, Request.Method.POST, AppConstants.URL_UPLOAD_UNBILL_METER_READING, AppConstants.REQEST_UPLOAD_UNBILL_METER_READING, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
        App.getInstance().addToRequestQueue(request, AppConstants.REQEST_UPLOAD_UNBILL_METER_READING);
    }

    private JSONObject getUnbillMeterReadingJson(Context mContext, ArrayList<Consumer> unBillConsumerToUpload) {
        JSONObject jsonObject = null;
        try {
            Gson gson = new Gson();
            String jsonString = gson.toJson(unBillConsumerToUpload);
            JSONArray jsonArray = new JSONArray(jsonString);
            jsonObject = new JSONObject();
            jsonObject.put("unbilled_consumers", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
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

    private void callNotification() {
//        mediaPlayer.start();
        intent = new Intent(mContext, NotificationActivity.class);
        intent.putExtra(AppConstants.CURRENT_METER_READER_ID, meter_reader_id);
        startActivity(intent);
    }

    private void getAllJobCards() {
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
                        pDialog.setMessage(getString(R.string.updating_your_assigned_consumer_information_please_wait));
                        pDialog.show();
                    }
                }
            });

//            PendingCount pc = DatabaseManager.getpendingcount(this, meter_reader_id);
            JsonObjectRequest request = WebRequests.getJobCards(Request.Method.GET, AppConstants.URL_GET_JOB_CARDS, AppConstants.REQUEST_GET_JOB_CARDS, this, mPageNumber, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
            App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_GET_JOB_CARDS);
        } else {
            dismissDialog();
            DialogCreator.showMessageDialog(this, getString(R.string.error_internet_not_connected), getString(R.string.error));
        }
    }


    private void getDeassignedReassignedJobCards() {
        if (summaryCount.pendingUpload == 0) {
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

            JsonObjectRequest request = WebRequests.getdeassignedreassignedjobcards(Request.Method.GET, AppConstants.URL_GET_DEASSIGNED_REASSIGNED_JOB_CARDS, AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_JOB_CARDS, this, mPageNumber, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
            App.getInstance().addToRequestQueue(request, AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_JOB_CARDS);
        } else {
            dismissDialog();
            DialogCreator.showMessageDialog(this, getString(R.string.upload_before_download_error), getString(R.string.error));
        }
    }


    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label) {

        switch (label) {
            case AppConstants.REQUEST_GET_JOB_CARDS: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.error_code != null && jsonResponse.error_code.equalsIgnoreCase("201")) {
                            dismissDialog();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogCreator.showMessageDialog(LandingActivity.this, getString(R.string.your_consumer_data_is_not_ready_please_try_after_sometime), getString(R.string.error));
                                }
                            });


                        } else {
                            if (jsonResponse.responsedata != null && jsonResponse.responsedata.jobcards != null && jsonResponse.responsedata.jobcards.size() > 0) {
                                DatabaseManager.saveJobCards(mContext, jsonResponse.responsedata.jobcards);
                                if (jsonResponse.responsedata.is_next.equals("true")) {
                                    mPageNumber += 1;
                                    reinitializeViewPager("False", "False", binder);
                                    position = 0;
                                    dismissDialog();
                                    getAllJobCards();

                                } else {
                                    mPageNumber = 1;

                                    //refresh summary on UI
                                    binder = "All";
                                    AppPreferences.getInstance(mContext).putString(AppConstants.FILTER_BINDER, binder);
                                    reinitializeViewPager("False", "False", binder);
                                    position = 0;
                                    dismissDialog();

                                    Toast.makeText(LandingActivity.this, getString(R.string.consumer_information_downloaded), Toast.LENGTH_LONG).show();
                                    getDeassignedReassignedJobCards();
                                }
                            } else {
                                mPageNumber = 1;
                                Toast.makeText(LandingActivity.this, getString(R.string.consumer_information_not_assigned_to_you_for_reading_as_of_now), Toast.LENGTH_LONG).show();

                                getDeassignedReassignedJobCards();
                            }

                            if (jsonResponse.authorization != null) {
                                CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                            }
                        }
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        Toast.makeText(mContext, R.string.please_contact_server_admin, Toast.LENGTH_LONG).show();
                        getDeassignedReassignedJobCards();
                    } else {
                        getDeassignedReassignedJobCards();
                    }
                } else
                    getDeassignedReassignedJobCards();
            }
            break;
            case AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_JOB_CARDS: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.error_code != null && jsonResponse.error_code.equalsIgnoreCase("201")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogCreator.showMessageDialog(LandingActivity.this, getString(R.string.your_consumer_data_is_not_ready_please_try_after_sometime), getString(R.string.error));
                                }
                            });
                        }
                        if (jsonResponse.responsedata != null && jsonResponse.responsedata.re_de_assigned_jobcards != null && jsonResponse.responsedata.re_de_assigned_jobcards.size() > 0) {
                            DatabaseManager.handleAssignedDeassignedJobs(mContext, jsonResponse.responsedata.re_de_assigned_jobcards, meter_reader_id);

                            //refresh summary UI
                            binder = "All";

                            AppPreferences.getInstance(mContext).putString(AppConstants.FILTER_BINDER, binder);
                            AppPreferences.getInstance(mContext).getString(AppConstants.TOTAL, "0");
                            reinitializeViewPager("False", "False", binder);
//                            position = 0;
                            if (jsonResponse.responsedata.is_next.equals("true")) {
                                mPageNumber += 1;
                                dismissDialog();
                                getDeassignedReassignedJobCards();
                            } else {
                                position = 0;
                                dismissDialog();
                                Toast.makeText(LandingActivity.this, getString(R.string.updated_reassign_deassign_consumer_info_successfully), Toast.LENGTH_LONG).show();
                            }
                        }
                        if (jsonResponse.authorization != null) {
                            CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                        }
                        resetSequences();
                        dismissDialog();
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        dismissDialog();
                    }
                }
            }
            break;

            case AppConstants.REQEST_UPLOAD_METER_READING: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.authorization != null) {
//                            CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                        }
                        //changes for Delete Job cards Starts Avinesh
                        if (jsonResponse.responsedata.new_meter_readings != null && jsonResponse.responsedata.new_meter_readings.size() > 0) {
                            for (int i = 0; i < jsonResponse.responsedata.new_meter_readings.size(); i++) {
                                deleteJobs = DatabaseManager.getMeterReading(this, readingToUpload.get(i).meter_reader_id, jsonResponse.responsedata.new_meter_readings.get(i));
                                ArrayList<JobCard> lJobCardArray = DatabaseManager.getJobCard(this, readingToUpload.get(i).meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, jsonResponse.responsedata.new_meter_readings.get(i));
                                if (lJobCardArray != null) {
                                    if (!lJobCardArray.get(0).phone_no.equalsIgnoreCase("0") && lJobCardArray.get(0).phone_no.length() == 10)
                                        if (readingToUpload.get(i).reader_status.equalsIgnoreCase(getString(R.string.obstacle)) ||
                                                (readingToUpload.get(i).reader_status.equalsIgnoreCase(getString(R.string.premises_locked)))) {
                                            sentSMS(lJobCardArray.get(0), readingToUpload.get(i).reading_date);
                                        }
                                    UploadsHistory lUploadsHistory = new UploadsHistory();
                                    lUploadsHistory.consumer_no = lJobCardArray.get(0).consumer_no;
                                    lUploadsHistory.route_code = lJobCardArray.get(0).route_code;
                                    lUploadsHistory.bill_cycle_code = lJobCardArray.get(0).bill_cycle_code;
                                    lUploadsHistory.month = lJobCardArray.get(0).schedule_month;
                                    lUploadsHistory.meter_reader_id = lJobCardArray.get(0).meter_reader_id;
                                    if (readingToUpload.get(0).isRevisit.equalsIgnoreCase("True")) {
                                        lUploadsHistory.upload_status = getString(R.string.revisit);
                                    } else {
                                        lUploadsHistory.upload_status = getString(R.string.meter_status_normal);
                                    }
                                    lUploadsHistory.reading_date = CommonUtils.getCurrentDate();

                                    DatabaseManager.saveUploadsHistory(mContext, lUploadsHistory);
                                }
                                DatabaseManager.deleteMeterReadings(mContext, deleteJobs);
                            }

                            //refresh summary UI
                            dismissDialog();
                            binder = "All";
                            AppPreferences.getInstance(mContext).putString(AppConstants.FILTER_BINDER, binder);
                            loadData();
                            reinitializeViewPager1(1, 1, 1);
//                            setNormalPendingAdapterRefresh();

                            readingToUpload = null;
                            readingToUpload = DatabaseManager.getMeterReadings(mContext, meter_reader_id, AppConstants.UPLOAD_COUNT);
                            if (readingToUpload != null && readingToUpload.size() > 0) {
                                JSONObject jObject = getMeterReadingJson(mContext, readingToUpload);
                                uploadMeterReading(jObject);
                            } else {
                                Toast.makeText(this, getString(R.string.consumer_readings_successfully_uploaded), Toast.LENGTH_LONG).show();
                                viewPager.setCurrentItem(0);
                                uploadConsumerReading();
                            }
//                            resetSequences();
                        }
                        //changes for Delete Job cards Ends Avinesh

                        else {
                            Toast.makeText(this, R.string.error_in_upload, Toast.LENGTH_LONG).show();
                            dismissDialog();
                            loadData();
                            reinitializeViewPager1(1, 1, 1);
                            uploadConsumerReading();
                        }
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        uploadConsumerReading();
                    } else {
                        uploadConsumerReading();
                    }
                } else {
                    uploadConsumerReading();
                }
            }
            break;

            case AppConstants.REQEST_UPLOAD_UNBILL_METER_READING: {
                if (jsonResponse != null) {
                    if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS)) {
                        if (jsonResponse.authorization != null) {
//                            CommonUtils.saveAuthToken(mContext, jsonResponse.authorization);
                        }
                        if (jsonResponse.responsedata.new_unbilled_consumers != null && jsonResponse.responsedata.new_unbilled_consumers.size() > 0) {
                            //changes for count in historyTab Starts Avinesh:01-03-17
                            for (int i = 0; i < jsonResponse.responsedata.new_unbilled_consumers.size(); i++) {
                                if (unBillConsumerToUpload.size() >= 0) {
                                    UploadsHistory lUploadsHistory = new UploadsHistory();
                                    lUploadsHistory.consumer_no = unBillConsumerToUpload.get(i).consumer_no;
                                    lUploadsHistory.route_code = unBillConsumerToUpload.get(i).route_code;
                                    lUploadsHistory.bill_cycle_code = unBillConsumerToUpload.get(i).bill_cycle_code;
                                    lUploadsHistory.month = unBillConsumerToUpload.get(i).reading_month;
                                    lUploadsHistory.upload_status = getString(R.string.addnewconsumer);
                                    lUploadsHistory.reading_date = CommonUtils.getCurrentDate();
                                    lUploadsHistory.meter_reader_id = unBillConsumerToUpload.get(i).meter_reader_id;

                                    DatabaseManager.saveUploadsHistory(mContext, lUploadsHistory);
                                }
                                DatabaseManager.deleteUnbillConsumer(mContext, jsonResponse.responsedata.new_unbilled_consumers.get(i), meter_reader_id);
                            }
                            //changes for count in historyTab Ends Avinesh:01-03-17

                            //refresh summary UI
                            dismissDialog();
                            loadData();
                            reinitializeViewPager1(1, 1, 1);
//                            setNormalPendingAdapterRefresh();

                            unBillConsumerToUpload = null;
                            unBillConsumerToUpload = DatabaseManager.getUnbillConsumers(mContext, meter_reader_id, AppConstants.UPLOAD_COUNT);
                            if (unBillConsumerToUpload != null && unBillConsumerToUpload.size() > 0) {
                                JSONObject jObject = getUnbillMeterReadingJson(mContext, unBillConsumerToUpload);
                                uploadConsumerReading(jObject);
                            } else {
                                mPageNumber = 1;
                            }
                        }
                        dismissDialog();
                        loadData();
                        reinitializeViewPager1(1, 1, 1);
                    } else if (jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.FAILURE)) {
                        dismissDialog();
                    }
                }
            }
            break;
        }
//        dismissDialog();
    }

    private void resetSequences() {
        ArrayList<Sequence> se = DatabaseManager.getAllSequence(this, meter_reader_id);
        if (se != null)
            for (Sequence s : se) {
                if ((DatabaseManager.getJobCardsCount(this, meter_reader_id, s.route_code)) != null) {
                    if ((DatabaseManager.getJobCardsCount(this, meter_reader_id, s.route_code)).size() == 0) {
                        DatabaseManager.deleteSequence(this, s);
                    }
                } else
                    DatabaseManager.deleteSequence(this, s);
            }

    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response) {
        switch (label) {
            case AppConstants.REQUEST_GET_JOB_CARDS: {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                getDeassignedReassignedJobCards();
            }
            break;
            case AppConstants.REQUEST_GET_DEASSIGNED_REASSIGNED_JOB_CARDS: {
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
            }
            default:
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                break;
        }
        dismissDialog();
        invalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        if (buttonTimer != null)
            buttonTimer.cancel();
        CommonUtils.deleteCache(mContext);
        super.onDestroy();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Landing Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    public static void landingFinish(Activity context) {
        context.finish();
    }

    //Dialog for validation Dialog start Piyush : 01-04-17
    public void showFilterDialog(final Context context) {
        Typeface regular = App.getSansationRegularFont();
        final Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        final AlertDialog alert2;
        View promptView = layoutInflater.inflate(R.layout.filter_dialog, null);

        final TextView txtTitle, txtError;
        final Button positive, negative;
        final EditText editText;
        final RadioButton radioAll, radioOne, radioTwo, radioThree, radioFour;

        //Initialising all fields starts
        alert2 = new AlertDialog.Builder(mContext).create();
        spinnerBinder = (Spinner) promptView.findViewById(R.id.spinner_binder);
        positive = (Button) promptView.findViewById(R.id.btn_yes);
        negative = (Button) promptView.findViewById(R.id.btn_no);
        txtError = (TextView) promptView.findViewById(R.id.txt_error);
        //Initialising all fields ends

        //Setting font style to all fields starts
        positive.setTypeface(regular);
        negative.setTypeface(regular);
        txtError.setTypeface(regular);
        //Setting font style to all fields ends

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, routes) {
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
        spinnerBinder.setAdapter(dataAdapter);
        spinnerBinder.setSelection(position);
        spinnerBinder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position1, long id) {
                position = position1;
                AppPreferences.getInstance(mContext).putString(AppConstants.TOTAL, String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        //Button code starts
        positive.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (binder.equals("Select Binder*")) {
                    txtError.setVisibility(View.VISIBLE);
                } else {
                    txtError.setVisibility(View.GONE);
                    binder = spinnerBinder.getItemAtPosition(position).toString();
                    AppPreferences.getInstance(mContext).putString(AppConstants.FILTER_BINDER, binder);
                    reinitializeViewPager("False", "False", binder);
                    alert2.dismiss();
                }
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtError.setVisibility(View.GONE);
                alert2.dismiss();
            }
        });

        alert2.setView(promptView);
        alert2.setCancelable(false);
        alert2.show();
        //OK button code ends>,jgrstw
    }
    //Dialog for validation Dialog ends Piyush : 20-05-17

    private void setAdapter(ArrayList<String> binders) {
        final Typeface bold = App.getSansationBoldFont();
    }

    private void sentSMS(final JobCard jobCard, String date) {
        String tag_json_obj = "json_obj_req";
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
        String url = null;
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        url = "http://www.cescrajasthan.co.in/kotawebservice/send_sms_thru_api.jsp?kno=" + jobCard.consumer_no +
                "&mobno=" + jobCard.phone_no + "&message=X&lang_type=H&msg_type=H1&crt_by=BYNRY&rdng_dt=X&rdng=X&adv=X&agency_contact_no=" + userProfile.contact_no;

        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Gson gson = new Gson();
                        JsonResponse jsonResponse = gson.fromJson(response.toString(), JsonResponse.class);
                        pDialog.hide();
                        pDialog.dismiss();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
                pDialog.dismiss();
            }
        });

// Adding request to request queue
        App.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }
}