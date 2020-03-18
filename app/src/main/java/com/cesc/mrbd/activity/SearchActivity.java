package com.cesc.mrbd.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.adapters.JobCardListAdapter;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.JobCard;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.AppPreferences;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.ArrayList;

public class SearchActivity extends ParentActivity implements View.OnClickListener, JobCardListAdapter.OnJobCardClickListener {

    private Context mContext;
    private ImageView imgBack, imgSearch, imgClose, txt;
    private RelativeLayout relativeLayoutfilter;
    private EditText edtSearch;
    private RecyclerView rv_searched_job_cards;
    private ArrayList<JobCard> mJobCards = new ArrayList<JobCard>();
    private JobCardListAdapter jobCardListAdapter;
    private TextView txt_result_found, title;
    private String meter_reader_id;
    public static SearchActivity searchActivity;
    private Typeface regular;
    private String searchQuery = "", position = "", binder = "";
    private SearchView searchView;
    private Toolbar mToolbar;
    private RelativeLayout relativeMain;
    private Spinner spinnerBinder;
    private ArrayAdapter<String> dataAdapter;
    private ArrayList<String> routes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mContext = this;
        regular = App.getSansationRegularFont();
        searchActivity = this;

        Intent i = getIntent();
        if (i != null) {
            meter_reader_id = i.getStringExtra(AppConstants.CURRENT_METER_READER_ID);
        }

        //For search menu on toolbar starts, Piyush : 06-03-17
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_back);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        relativeMain = (RelativeLayout) findViewById(R.id.relative_main);
        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        //For search menu on toolbar ends, Piyush : 06-03-17

        title = (TextView) findViewById(R.id.title_bar);
        title.setTypeface(regular);
        imgSearch = (ImageView) findViewById(R.id.img_search);
        imgSearch.setOnClickListener(this);
       /* imgClose = (ImageView) findViewById(R.id.img_close);
        imgClose.setOnClickListener(this);*/
        edtSearch = (EditText) findViewById(R.id.edt_search);
        edtSearch.setTypeface(regular);
        relativeLayoutfilter = (RelativeLayout) findViewById(R.id.relative_filter);
        txt_result_found = (TextView) findViewById(R.id.txt_result_found);
        txt_result_found.setText(R.string.search_text_no);
        txt = (ImageView) findViewById(R.id.txt);
        txt.setOnClickListener(this);
        txt.setImageDrawable(getResources().getDrawable(R.drawable.keyboard));
        txt_result_found.setTypeface(regular);
        rv_searched_job_cards = (RecyclerView) findViewById(R.id.rv_searched_job_cards);
        jobCardListAdapter = new JobCardListAdapter(this, mJobCards, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        rv_searched_job_cards.setLayoutManager(layoutManager);
        rv_searched_job_cards.setAdapter(jobCardListAdapter);

        spinnerBinder = (Spinner) findViewById(R.id.spinner_binder);

        position = AppPreferences.getInstance(mContext).getString(AppConstants.TOTAL, "");
        binder = AppPreferences.getInstance(mContext).getString(AppConstants.FILTER_BINDER, "");
        final Typeface bold = App.getSansationBoldFont();
        routes = new ArrayList<>();
        routes.add("All");
        routes.addAll(DatabaseManager.getRoutes(this, meter_reader_id));
        setAdapter(routes);
        if (binder.equalsIgnoreCase("All"))
            position = "0";

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
        try {
            if (!position.equalsIgnoreCase(""))
                spinnerBinder.setSelection(Integer.parseInt(position));

        } catch (Exception e) {
            spinnerBinder.setSelection(Integer.parseInt("0"));
        }
        spinnerBinder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position1, long id) {
                binder = String.valueOf(parentView.getItemAtPosition(position1));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
    }

    @Override
    protected void onResume() {
        invalidateOptionsMenu();
        if (mJobCards != null)
            mJobCards.clear();
        txt_result_found.setText(R.string.search_text_no);
        txt.setImageDrawable(getResources().getDrawable(R.drawable.keyboard));
        txt_result_found.setGravity(Gravity.CENTER);
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == imgBack) {
            finish();
        }
        if (v == txt) {
            edtSearch.setText("");
        }
        if (searchView.getInputType() == InputType.TYPE_CLASS_PHONE) {
            searchView.setInputType(InputType.TYPE_CLASS_TEXT);
            txt_result_found.setText(R.string.search_text_name);
            txt.setImageDrawable(getResources().getDrawable(R.drawable.keypad));
        } else {
            searchView.setInputType(InputType.TYPE_CLASS_PHONE);
            txt_result_found.setText(R.string.search_text_no);
            txt.setImageDrawable(getResources().getDrawable(R.drawable.keyboard));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onJobCardClick(JobCard jobCard) {
        App.ReadingTakenBy = getString(R.string.meter_reading_manual);
        edtSearch.setText(" ");
        Intent i = new Intent(this, AddMeterReadingActivity.class);
        i.putExtra(AppConstants.CURRENT_JOB_CARD, jobCard);
        startActivity(i);
    }

    //For search menu on toolbar starts, Piyush : 06-03-17
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_view, menu);

        MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.LEFT));
        searchView.onActionViewExpanded();
        searchView.setInputType(InputType.TYPE_CLASS_PHONE);
        searchView.setQuery(searchQuery, false);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(final String newText) {
                jobCardListAdapter = null;
                if (newText.length() >= 1) {
                    if (mJobCards != null) {
                        mJobCards.clear();
                    }

                    if (binder.equals("All"))
                        mJobCards = DatabaseManager.getJobCardsbysearch(SearchActivity.this, newText, meter_reader_id, AppConstants.JOB_CARD_STATUS_ALLOCATED);
                    else
                        mJobCards = DatabaseManager.getJobCardsbysearch(SearchActivity.this, newText, meter_reader_id, AppConstants.JOB_CARD_STATUS_ALLOCATED, binder);

                    if (jobCardListAdapter != null) {
                        jobCardListAdapter.setJobCard(mJobCards);
                    } else {
                        jobCardListAdapter = new JobCardListAdapter(SearchActivity.this, mJobCards, SearchActivity.this);
                        rv_searched_job_cards.setAdapter(jobCardListAdapter);
                    }
                    txt_result_found.setTypeface(regular);
                    if (mJobCards == null) {
                        jobCardListAdapter.notifyDataSetChanged();
                        txt_result_found.setText(R.string.No_Consumer_found);
                    } else {
                        if (mJobCards.size() == 1) {
                            txt_result_found.setText(mJobCards.size() + " " + getString(R.string.record_found));
                            txt_result_found.setGravity(Gravity.LEFT);
                        } else {
                            txt_result_found.setText(mJobCards.size() + " " + getString(R.string.records_found));
                            txt_result_found.setGravity(Gravity.LEFT);
                        }
                    }
                } else {
                    if (mJobCards != null) {
                        mJobCards.clear();
                    }
                    if (jobCardListAdapter != null) {
                        jobCardListAdapter.setJobCard(mJobCards);
                    } else {
                        jobCardListAdapter = new JobCardListAdapter(SearchActivity.this, mJobCards, SearchActivity.this);
                        rv_searched_job_cards.setAdapter(jobCardListAdapter);
                    }
                    txt_result_found.setText(R.string.search_text_no);
                    txt.setImageDrawable(getResources().getDrawable(R.drawable.keyboard));
                    txt_result_found.setGravity(Gravity.CENTER);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
        });
        return true;
    }
    //For search menu on toolbar ends, Piyush : 06-03-17

    private void setAdapter(ArrayList<String> binders) {
        final Typeface bold = App.getSansationBoldFont();

    }
}
