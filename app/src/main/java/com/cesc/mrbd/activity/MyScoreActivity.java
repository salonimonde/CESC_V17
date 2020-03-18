package com.cesc.mrbd.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.cesc.mrbd.R;
import com.cesc.mrbd.callers.ServiceCaller;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.JsonResponse;
import com.cesc.mrbd.models.UserProfile;
import com.cesc.mrbd.preferences.SharedPrefManager;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;
import com.cesc.mrbd.webservice.WebRequests;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MyScoreActivity extends ParentActivity implements View.OnClickListener, ServiceCaller
{
    private Context mContext;
    private Typeface bold, regular;
    private TextView txtDate, txtTotalScore, txtPositiveScore, txtNegativeScore, txtToShow, lblTotalScore, lblPassScore,
            lblRevisitScore, txtReadingSubmitted, txtMoreThanAvg, lblTermsAndCondition, lblReadingSubmitted, lblMoreThanAvg,
            txtPass;
    private ImageView imgBack;
    private Spinner monthSpinner;
    private String monthToSend;
    private ArrayList<String> arrayOfMonths = new ArrayList<String>();
    private ArrayList<String> arrayOfMonthsToSend = new ArrayList<String>();
    private UserProfile userProfile;
    private String meter_reader_id;
    private AppBarLayout appBarLayout;
    private boolean isCollapsed = false;
    private LinearLayout linearTermsNConditions;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_score);

        mContext = this;
        bold = App.getSansationBoldFont();
        regular = App.getSansationRegularFont();

        getUserProfileDetails();

        txtDate = (TextView)findViewById(R.id.txt_date);
        txtDate.setTypeface(bold);
        txtTotalScore = (TextView)findViewById(R.id.txt_total_score);
        txtTotalScore.setTypeface(bold);
        txtPositiveScore = (TextView)findViewById(R.id.txt_positive_score);
        txtPositiveScore.setTypeface(bold);
        txtNegativeScore = (TextView)findViewById(R.id.txt_negative_score);
        txtNegativeScore.setTypeface(bold);
        txtToShow = (TextView)findViewById(R.id.txt_to_show);
        txtToShow.setTypeface(bold);
        txtReadingSubmitted = (TextView)findViewById(R.id.txt_reading_submitted);
        txtReadingSubmitted.setTypeface(bold);
        txtMoreThanAvg = (TextView)findViewById(R.id.txt_more_than_average);
        txtMoreThanAvg.setTypeface(bold);

        lblTotalScore = (TextView)findViewById(R.id.lbl_total_score);
        lblTotalScore.setTypeface(regular);
        lblPassScore = (TextView)findViewById(R.id.lbl_pass_score);
        lblPassScore.setTypeface(regular);
        lblRevisitScore = (TextView)findViewById(R.id.lbl_revisit_score);
        lblRevisitScore.setTypeface(regular);
        lblReadingSubmitted = (TextView)findViewById(R.id.lbl_reading_submitted);
        lblReadingSubmitted.setTypeface(bold);
        lblMoreThanAvg = (TextView)findViewById(R.id.lbl_more_than_average);
        lblMoreThanAvg.setTypeface(bold);
        lblTermsAndCondition = (TextView)findViewById(R.id.lbl_terms_condition);
        lblTermsAndCondition.setTypeface(bold);
        lblTermsAndCondition.setOnClickListener(this);

        imgBack = (ImageView)findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);

        txtPass = (TextView)findViewById(R.id.txt_pass);
        txtPass.setTypeface(regular);
        linearTermsNConditions = (LinearLayout)findViewById(R.id.linear_terms_n_conditions);
        linearTermsNConditions.setVisibility(View.GONE);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener()
        {
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)
            {
                if (scrollRange == -1)
                {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0)
                {
                    collapsingToolbarLayout.setTitle(getString(R.string.my_score));
                    collapsingToolbarLayout.setCollapsedTitleTextColor(CommonUtils.getColor(getApplicationContext(), R.color.colorWhite));
                    collapsingToolbarLayout.setCollapsedTitleTypeface(regular);
                    isCollapsed = true;
                }
                else if(isCollapsed)
                {
                    collapsingToolbarLayout.setTitle(" ");
                    isCollapsed = false;
                }
            }
        });


        for(int i = -1; i <= 5; i++)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, -i);
            Date date = calendar.getTime();
            SimpleDateFormat format = new SimpleDateFormat("MMM yyyy");
            String months = format.format(date);
            arrayOfMonths.add(months);
            SimpleDateFormat format1 = new SimpleDateFormat("yyyyMM");
            String monthsToSend = format1.format(date);
            arrayOfMonthsToSend.add(monthsToSend);
        }

        monthSpinner = (Spinner) findViewById(R.id.month_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_dropdown_item,
                arrayOfMonths)
        {
            public View getView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.colorWhite));
                ((TextView) v).setTextSize(15f);
                return v;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                View v = super.getDropDownView(position, convertView, parent);
                ((TextView) v).setTypeface(bold);
                ((TextView) v).setTextColor(CommonUtils.getColor(getContext(), R.color.text_color));
                ((TextView) v).setTextSize(15f);
                return v;
            }
        };
        monthSpinner.setAdapter(adapter);
//        monthSpinner.setSelection(1);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                monthToSend = arrayOfMonthsToSend.get(position);
                getPointsDetails();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {

            }

        });

    }

    private void getUserProfileDetails()
    {
        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));
        if (userProfile != null)
        {
            meter_reader_id = userProfile.meter_reader_id;
        }
    }

    private void getPointsDetails()
    {
        showLoadingDialog();
        JsonObjectRequest request = WebRequests.getMyScoreDetails(this, Request.Method.POST, AppConstants.URL_MY_SCORE_DETAILS, AppConstants.URL_MY_SCORE_DETAILS, this, meter_reader_id, monthToSend);
        App.getInstance().addToRequestQueue(request, AppConstants.URL_MY_SCORE_DETAILS);
    }

    @Override
    public void onClick(View v)
    {
        if(v == imgBack)
        {
            finish();
        }
        if(v == lblTermsAndCondition)
        {
            if(isCollapsed)
            {
                appBarLayout.setExpanded(true);
                isCollapsed = false;
                linearTermsNConditions.setVisibility(View.GONE);
            }
            else
            {
                linearTermsNConditions.setVisibility(View.VISIBLE);
                appBarLayout.setExpanded(false);
                isCollapsed = true;
            }
        }
    }

    @Override
    public void onAsyncSuccess(JsonResponse jsonResponse, String label)
    {
        dismissLoadingDialog();
        switch(label)
        {
            case AppConstants.URL_MY_SCORE_DETAILS:
            {
                if(jsonResponse != null)
                {
                    if(jsonResponse.result != null && jsonResponse.result.equals(JsonResponse.SUCCESS))
                    {
                        txtTotalScore.setText(jsonResponse.total != null ? jsonResponse.total : "---");
                        txtPositiveScore.setText(jsonResponse.completed != null ? jsonResponse.completed : "---");
                        txtNegativeScore.setText(jsonResponse.revisit != null ? jsonResponse.revisit : "---");
                        if(jsonResponse.total == null || jsonResponse.total.equals("0"))
                            txtToShow.setText("");
                        else
                            txtToShow.setText(getString(R.string.great_you_have_Scored)+"   "+ jsonResponse.total);
                        txtReadingSubmitted.setText(jsonResponse.reading != null ? jsonResponse.reading : "---");
                        txtMoreThanAvg.setText(jsonResponse.gtavgr != null ? jsonResponse.gtavgr : "---");

                    }
                }
                else
                {
                    Toast.makeText(this, R.string.er_data_not_avaliable, Toast.LENGTH_LONG).show();
                }
            }
            break;
        }
    }

    @Override
    public void onAsyncFail(String message, String label, NetworkResponse response)
    {
        dismissLoadingDialog();
        switch (label)
        {
            case AppConstants.URL_MY_SCORE_DETAILS:
            {
                Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }
}
