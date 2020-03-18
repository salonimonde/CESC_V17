package com.cesc.mrbd.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cesc.mrbd.R;
import com.cesc.mrbd.adapters.PaymentCardAdapter;
import com.cesc.mrbd.callers.ServiceCaller;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.JsonResponse;
import com.cesc.mrbd.models.PaymentCalculation;
import com.cesc.mrbd.models.Response;
import com.cesc.mrbd.models.UserProfile;
import com.cesc.mrbd.preferences.SharedPrefManager;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.AppPreferences;
import com.cesc.mrbd.utils.CommonUtils;
import com.cesc.mrbd.utils.DialogCreator;
import com.cesc.mrbd.webservice.WebRequests;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.cesc.mrbd.configuration.AppConstants.REQUEST_GET_PAYMENT_DETAILS;

/**
 * Created by Bynry01 on 10/10/2016.
 */
public class MyPaymentActivity extends ParentActivity implements View.OnClickListener, PaymentCardAdapter.OnPaymentClickListener, ServiceCaller,
        NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private Context mContext;
    private Typeface regular, bold;
    private ProgressDialog pDialog;
    private String bankName, consumerName, accountNo, ifsc;
    private DrawerLayout drawer;
    private Intent intent;
    private Toolbar toolbar;
    private CircleImageView imgMrProfile, imgDrawerProfile;
    private TextView txtName, txtDrawerMRName, txtDrawerMobileNo, lblBlankScreenMsg;
    private LinearLayout toolbarLinearProfile;
    private UserProfile userProfile;
    public static String meter_reader_id;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypayment);
        mContext = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();
        AppPreferences.getInstance(mContext).putString(AppConstants.SCREEN_FROM_EXIT, getString(R.string.payment));

        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(this, SharedPrefManager.USER_ID));
        if (userProfile != null) {
            meter_reader_id = userProfile.meter_reader_id;
        }

        imgMrProfile = (CircleImageView) findViewById(R.id.image_profile);
        txtName = (TextView) findViewById(R.id.txt_name);
        txtName.setTypeface(regular);
        txtName.setSelected(true);
        txtName.setText(userProfile.meter_reader_name);
        lblBlankScreenMsg = (TextView) findViewById(R.id.lbl_blank_msg);
        lblBlankScreenMsg.setTypeface(regular);

        toolbarLinearProfile = (LinearLayout) findViewById(R.id.linear_profile);
        toolbarLinearProfile.setOnClickListener(this);

        initProgressDialog();
        getPaymentDetails();
//        demoData();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
        navigationView.getMenu().getItem(3).setChecked(true);

        txtDrawerMRName = (TextView) header.findViewById(R.id.txt_drawer_mr_name);
        txtDrawerMRName.setTypeface(bold);
        txtDrawerMRName.setText(userProfile.email_id + " | " + userProfile.meter_reader_name);
        txtDrawerMobileNo = (TextView) header.findViewById(R.id.txt_drawer_mobile_no);
        txtDrawerMobileNo.setTypeface(regular);
        txtDrawerMobileNo.setText(userProfile.contact_no);
        imgDrawerProfile = (CircleImageView) header.findViewById(R.id.img_drawer_profile);

        if (userProfile.profile_image != null && !userProfile.profile_image.equals("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgMrProfile);
        }
    }


    private void loadRecyclerView(ArrayList<PaymentCalculation> mpay) {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        PaymentCardAdapter adapter = new PaymentCardAdapter(mContext, mpay, this);
        recyclerView.setAdapter(adapter);
    }

    private void initProgressDialog() {
        if (pDialog == null) {
            pDialog = new ProgressDialog(MyPaymentActivity.this);
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
        }
    }

    private void dismissDialog() {
        if (pDialog != null && pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == toolbarLinearProfile) {
            intent = new Intent(mContext, ProfileActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPayCardClick(PaymentCalculation pay) {
        Intent i = new Intent(mContext, PaymentDetailsActivity.class);
        i.putExtra("CURRENT_PAYMENT_CARD", pay);
        startActivity(i);
    }

    private void getPaymentDetails() {
        if (CommonUtils.isNetworkAvaliable(this) == true) {
            // Your dialog code.
            if (pDialog != null) {
                pDialog.setMessage(getString(R.string.downloading_payment_details_please_wait));
                pDialog.show();
            }

            JsonObjectRequest request = WebRequests.getPaymentData(Request.Method.GET, AppConstants.URL_GET_PAYMENT_DETAILS, REQUEST_GET_PAYMENT_DETAILS, this, SharedPrefManager.getStringValue(mContext, SharedPrefManager.AUTH_TOKEN));
            App.getInstance().addToRequestQueue(request, REQUEST_GET_PAYMENT_DETAILS);
        } else
            DialogCreator.showMessageDialog(this, getString(R.string.error_internet_not_connected), getString(R.string.error));

    }

    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label) {
        switch (label) {
            case REQUEST_GET_PAYMENT_DETAILS:
                if (jsonResponse != null) {
                    if (jsonResponse.responsedata != null) {
                        consumerName = jsonResponse.responsedata.ac_name;
                        bankName = jsonResponse.responsedata.bank_name;
                        accountNo = jsonResponse.responsedata.ac_no;
                        ifsc = jsonResponse.responsedata.ifsc;
                    }
                    if (jsonResponse.result != null) {
                        if (jsonResponse.responsedata != null)
                            setData(jsonResponse.responsedata);
                        dismissDialog();
                    } else {
                        lblBlankScreenMsg.setVisibility(View.VISIBLE);
                        DialogCreator.showMessageDialog(this, jsonResponse.message, getString(R.string.error));
                        dismissDialog();
                    }
                }
                break;
        }
    }

    private void setData(Response responseData) {
        lblBlankScreenMsg.setVisibility(View.GONE);
        ArrayList<PaymentCalculation> paymentCalculation = new ArrayList<>();

        //functional
        PaymentCalculation month = new PaymentCalculation();
        if (responseData.first_month != null) {
            month = responseData.first_month;
            pay(month, paymentCalculation);
        }
        if (responseData.second_month != null) {
            month = responseData.second_month;
            pay(month, paymentCalculation);
        }
        if (responseData.third_month != null) {
            month = responseData.third_month;
            pay(month, paymentCalculation);
        }


        loadRecyclerView(paymentCalculation);
        if (paymentCalculation.size() == 0) {
            lblBlankScreenMsg.setVisibility(View.VISIBLE);
            DialogCreator.showMessageDialog(this, getString(R.string.your_payment_detail_is_not_ready_please_contact_to_supervisor), getString(R.string.error));
        }
    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response) {
        switch (label) {
            case REQUEST_GET_PAYMENT_DETAILS:
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                dismissDialog();
                break;
        }
    }

    public void pay(PaymentCalculation months, ArrayList<PaymentCalculation> paymentCalculation) {

        if (months.domestic_payment_calculation != null || months.zero_payment_calculation != null || months.dc_payment_calculation != null)
            /*  d */
            if (months.domestic_payment_calculation != null && months.zero_payment_calculation == null && months.dc_payment_calculation == null) {
                float total = Float.parseFloat(months.domestic_payment_calculation.totalamount);
                months.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                months.esi = Double.valueOf(Double.valueOf(total) * .0175);
                months.other = months.domestic_payment_calculation.other_deduction;
                if (months.other != null)
                    months.grandtotal = total - months.pf - months.esi - Double.valueOf(months.other);
                else
                    months.grandtotal = total - months.pf - months.esi;
                paymentCalculation.add(months);
                //*z*//*
            } else if (months.zero_payment_calculation != null && months.domestic_payment_calculation == null && months.dc_payment_calculation == null) {
                float total = Float.parseFloat(months.zero_payment_calculation.totalamount);
                months.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                months.esi = Double.valueOf(Double.valueOf(total) * .0175);
                months.other = months.zero_payment_calculation.other_deduction;
                if (months.other != null)
                    months.grandtotal = total - months.pf - months.esi - Double.valueOf(months.other);
                else
                    months.grandtotal = total - months.pf - months.esi;
                paymentCalculation.add(months);
                //*dc*//*
            } else if (months.dc_payment_calculation != null && months.zero_payment_calculation == null && months.domestic_payment_calculation == null) {
                float total = Float.parseFloat(months.dc_payment_calculation.dcamount);
                months.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                months.esi = Double.valueOf(Double.valueOf(total) * .0175);
                months.other = months.dc_payment_calculation.other_deduction;
                if (months.other != null)
                    months.grandtotal = total - months.pf - months.esi - Double.valueOf(months.other);
                else
                    months.grandtotal = total - months.pf - months.esi;
                paymentCalculation.add(months);

                //*dz*//*
            } else if (months.domestic_payment_calculation != null && months != null && months.dc_payment_calculation == null) {
                float total = Float.parseFloat(months.domestic_payment_calculation.totalamount) + Float.parseFloat(months.zero_payment_calculation.totalamount);
                months.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                months.esi = Double.valueOf(Double.valueOf(total) * .0175);
                months.other = months.domestic_payment_calculation.other_deduction;
                if (months.other != null)
                    months.grandtotal = total - months.pf - months.esi - Double.valueOf(months.other);
                else
                    months.grandtotal = total - months.pf - months.esi;
                paymentCalculation.add(months);

                //*ddc*//*
            } else if (months.domestic_payment_calculation != null && months.zero_payment_calculation == null && months.dc_payment_calculation != null) {
                float total = Float.parseFloat(months.domestic_payment_calculation.totalamount) + Float.parseFloat(months.dc_payment_calculation.dcamount);
                months.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                months.esi = Double.valueOf(Double.valueOf(total) * .0175);
                months.other = months.domestic_payment_calculation.other_deduction;
                if (months.other != null)
                    months.grandtotal = total - months.pf - months.esi - Double.valueOf(months.other);
                else
                    months.grandtotal = total - months.pf - months.esi;
                paymentCalculation.add(months);

                //*zdc*//*
            } else if (months.domestic_payment_calculation == null && months.zero_payment_calculation != null && months.dc_payment_calculation == null) {
                float total = Float.parseFloat(months.zero_payment_calculation.totalamount) + Float.parseFloat(months.dc_payment_calculation.dcamount);
                months.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                months.esi = Double.valueOf(Double.valueOf(total) * .0175);
                months.other = months.domestic_payment_calculation.other_deduction;
                if (months.other != null)
                    months.grandtotal = total - months.pf - months.esi - Double.valueOf(months.other);
                else
                    months.grandtotal = total - months.pf - months.esi;
                paymentCalculation.add(months);

                //*dzdc*//*
            } else if (months.domestic_payment_calculation != null && months.zero_payment_calculation != null && months.dc_payment_calculation != null) {
                float total = Float.parseFloat(months.domestic_payment_calculation.totalamount) + Float.parseFloat(months.zero_payment_calculation.totalamount) + Float.parseFloat(months.dc_payment_calculation.dcamount);
                months.pf = Double.valueOf(Double.valueOf(total) * 0.20 * 0.12);
                months.esi = Double.valueOf(Double.valueOf(total) * .0175);
                months.other = months.domestic_payment_calculation.other_deduction;
                if (months.other != null)
                    months.grandtotal = total - months.pf - months.esi - Double.valueOf(months.other);
                else
                    months.grandtotal = total - months.pf - months.esi;
                paymentCalculation.add(months);

            }
    }

    public void showInfo() {
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_consumer_details, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();

        TextView lblConsumerMeterNo, lblConsumerNumber, lblCycle, lblSubDiv, lblMobile, lblBinder;
        TextView txtConsumerName, txtConsumerAddress, txtConsumerMeterNo, txtConsumerNumber, txtsubdiv, txtcycle, txtbinderNo, txtmobileNo;
        lblConsumerNumber = (TextView) promptView.findViewById(R.id.lbl_consumer_no);
        lblConsumerNumber.setTypeface(bold);
        lblConsumerNumber.setText("A/c Holder Name");
        lblConsumerMeterNo = (TextView) promptView.findViewById(R.id.lbl_meter_no);
        lblConsumerMeterNo.setTypeface(bold);
        lblConsumerMeterNo.setText("Account No.");
        lblCycle = (TextView) promptView.findViewById(R.id.lbl_cycle);
        lblCycle.setTypeface(bold);
        lblCycle.setText("IFSC Code");
        lblSubDiv = (TextView) promptView.findViewById(R.id.lbl_subdiv);
        lblSubDiv.setTypeface(bold);
        lblSubDiv.setText("Bank Name");
        lblMobile = (TextView) promptView.findViewById(R.id.lbl_mobile);
        lblMobile.setTypeface(bold);
        lblMobile.setVisibility(View.GONE);
        lblBinder = (TextView) promptView.findViewById(R.id.lbl_binder_no);
        lblBinder.setTypeface(bold);
        lblBinder.setVisibility(View.GONE);

        txtConsumerName = (TextView) promptView.findViewById(R.id.txt_consumer_name);
        txtConsumerName.setTypeface(bold);
        txtConsumerName.setText("Bank Details");
        txtConsumerAddress = (TextView) promptView.findViewById(R.id.txt_consumer_address);
        txtConsumerAddress.setTypeface(regular);
        txtConsumerAddress.setVisibility(View.INVISIBLE);
        txtConsumerNumber = (TextView) promptView.findViewById(R.id.txt_consumer_no);
        txtConsumerNumber.setTypeface(regular);
        if (consumerName != null)
            txtConsumerNumber.setText(consumerName);
        else
            txtConsumerNumber.setText("");

        txtConsumerMeterNo = (TextView) promptView.findViewById(R.id.txt_meter_no);
        txtConsumerMeterNo.setTypeface(regular);
        if (accountNo != null)
            txtConsumerMeterNo.setText(accountNo);
        else
            txtConsumerMeterNo.setText("");
        txtcycle = (TextView) promptView.findViewById(R.id.txt_cycle);
        txtcycle.setTypeface(regular);
        if (ifsc != null)
            txtcycle.setText(ifsc);
        else
            txtcycle.setText("");
        txtsubdiv = (TextView) promptView.findViewById(R.id.txt_subdiv);
        txtsubdiv.setTypeface(regular);
        if (bankName != null)
            txtsubdiv.setText(bankName);
        else
            txtsubdiv.setText("");
        txtbinderNo = (TextView) promptView.findViewById(R.id.txt_binder_no);
        txtbinderNo.setTypeface(regular);
        txtbinderNo.setVisibility(View.GONE);
        txtmobileNo = (TextView) promptView.findViewById(R.id.txt_mobile_no);
        txtmobileNo.setTypeface(regular);
        txtmobileNo.setVisibility(View.GONE);
        //Initialising all text ends

        //Setting values to the text starts
//        txtConsumerName.setText(consumerName);
//        txtConsumerAddress.setText(consumerAddress);
//        txtConsumerNumber.setText(ac_name);
//        txtConsumerMeterNo.setText(ac_no);
//        txtcycle.setText(ifsc);
//        txtsubdiv.setText(bank_name);
//        txtbinderNo.setText(consumerbinderNo);
//        txtmobileNo.setText(consumermobileNo);
        //Setting values to the text ends

        //OK button code starts
        Button yes = (Button) promptView.findViewById(R.id.btn_ok);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.setView(promptView);
        alert.show();
        //OK button code ends
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu_my_payment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_drawer:
                drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.END)) {
                    drawer.closeDrawer(GravityCompat.END);
                } else {
                    drawer.openDrawer(GravityCompat.END);
                }
                return true;
            case R.id.action_bank_info:
                showInfo();
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
        }

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.END);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.END)) {
            drawer.closeDrawer(GravityCompat.END);
        } else {
            DialogCreator.showExitDialog(this, getString(R.string.exit_app), getString(R.string.do_you_want_to_exit), getString(R.string.payment));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userProfile.profile_image != null && !userProfile.profile_image.equalsIgnoreCase("")) {
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgMrProfile);
            Picasso.with(this).load(AppConstants.PROFILE_IMAGE_URL + userProfile.profile_image).placeholder(R.drawable.defaultprofile).error(R.drawable.defaultprofile).into(imgDrawerProfile);
        } else {
            imgMrProfile.setImageResource(R.drawable.defaultprofile);
        }
    }

    @Override
    protected void onDestroy() {
        CommonUtils.deleteCache(mContext);
        super.onDestroy();
    }
}
