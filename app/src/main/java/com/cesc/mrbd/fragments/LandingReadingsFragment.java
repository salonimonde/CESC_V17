package com.cesc.mrbd.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.adapters.ConsumerItemAdapter;
import com.cesc.mrbd.adapters.JobCardListAdapter;
import com.cesc.mrbd.adapters.ReadingCardListAdapter;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.Consumer;
import com.cesc.mrbd.models.JobCard;
import com.cesc.mrbd.models.MeterReading;
import com.cesc.mrbd.models.UserProfile;
import com.cesc.mrbd.preferences.SharedPrefManager;
import com.cesc.mrbd.utils.App;

import java.util.ArrayList;

public class LandingReadingsFragment extends Fragment implements JobCardListAdapter.OnJobCardClickListener
{
    private static final String ARG_NORMAL_READINGS = "";
    private static final String ARG_REVISIT_READINGS = "";
    private static final String ARG_NEW_READINGS = "";

    private OnFragmentInteractionListener mListener;
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private static int normalReadings = 1, revisitReadings = 0, newReadings = 0;

    private UserProfile userProfile;
    private String meter_reader_id;

    private TextView lblBlankScreenMsg;

    public LandingReadingsFragment()
    {
        // Required empty public constructor
    }

    public static LandingReadingsFragment newInstance(int normal, int revisit, int newReading)
    {
        LandingReadingsFragment fragment = new LandingReadingsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_NORMAL_READINGS, normal);
        args.putInt(ARG_REVISIT_READINGS, revisit);
        args.putInt(ARG_NEW_READINGS, newReading);
        normalReadings = normal;
        revisitReadings = revisit;
        newReadings = newReading;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            normalReadings = getArguments().getInt(ARG_NORMAL_READINGS);
            revisitReadings = getArguments().getInt(ARG_REVISIT_READINGS);
            newReadings = getArguments().getInt(ARG_NEW_READINGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_landing_readings, container, false);

        mContext = getActivity();
        getUserProfileDetails();
        lblBlankScreenMsg = (TextView) rootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        setAdapters();
        return rootView;
    }

    private void setAdapters()
    {
        if(normalReadings == 1)
        {
            ArrayList<JobCard> mJobCards = DatabaseManager.getJobCards(mContext, meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, "False");
            ArrayList<MeterReading> mReadingCards = DatabaseManager.getMeterReading(mContext, meter_reader_id);
            if (mReadingCards == null && mJobCards == null)
            {
                mReadingCards = new ArrayList<>();
                mJobCards = new ArrayList<>();
            }
            ReadingCardListAdapter adapter = new ReadingCardListAdapter(mContext, mReadingCards, mJobCards);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);
            if(mReadingCards != null && mReadingCards.size() > 0)
                lblBlankScreenMsg.setVisibility(View.GONE);
            else
                lblBlankScreenMsg.setVisibility(View.VISIBLE);
        }
        else if(revisitReadings == 1)
        {
            ArrayList<JobCard> mJobCards = DatabaseManager.getJobCards(mContext, meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, "True");
            ArrayList<MeterReading> mReadingCards = DatabaseManager.getMeterReading(mContext, meter_reader_id);
            if (mJobCards==null)
            {
                mJobCards = new ArrayList<>();
            }

            ReadingCardListAdapter adapter = new ReadingCardListAdapter(mContext, mReadingCards, mJobCards);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            if(mJobCards != null && mJobCards.size() > 0)
                lblBlankScreenMsg.setVisibility(View.GONE);
            else
                lblBlankScreenMsg.setVisibility(View.VISIBLE);
        }
        else if(newReadings == 1)
        {
            ArrayList<Consumer> mConsumers = DatabaseManager.getUnbilledConsumerRecords(mContext, meter_reader_id);

            if (mConsumers==null)
            {
                mConsumers = new ArrayList<>();
            }

            ConsumerItemAdapter adapter = new ConsumerItemAdapter(mContext, mConsumers);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(adapter);

            if(mConsumers != null && mConsumers.size() > 0)
                lblBlankScreenMsg.setVisibility(View.GONE);
            else
                lblBlankScreenMsg.setVisibility(View.VISIBLE);
        }
    }

    private void getUserProfileDetails()
    {
        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));
        if (userProfile != null)
        {
            meter_reader_id = userProfile.meter_reader_id;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onJobCardClick(JobCard jobCard)
    {}

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
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
