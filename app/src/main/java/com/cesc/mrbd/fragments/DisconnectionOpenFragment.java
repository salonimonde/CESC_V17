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
import com.cesc.mrbd.adapters.DisconnectionOpenAdapter;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.Disconnection;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.AppPreferences;

import java.util.ArrayList;

public class DisconnectionOpenFragment extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<Disconnection> disconnections;
    private String meterReaderId = "";

    private TextView lblBlankScreenMsg;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DisconnectionOpenFragment() {
        // Required empty public constructor
    }

    public static DisconnectionOpenFragment newInstance(String param1, String param2) {
        DisconnectionOpenFragment fragment = new DisconnectionOpenFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mContext = getActivity();
        meterReaderId = AppPreferences.getInstance(mContext).getString(AppConstants.METER_READER_ID, "");

        disconnections = DatabaseManager.getDisconnectionOpenCards(this.getContext(), meterReaderId, AppConstants.JOB_CARD_STATUS_ALLOCATED);

        DisconnectionOpenAdapter adapter = new DisconnectionOpenAdapter(this.getContext(), disconnections);

        View rootView =  inflater.inflate(R.layout.fragment_disconnection_open, container, false);

        lblBlankScreenMsg = (TextView) rootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if(disconnections != null && disconnections.size() > 0)
            lblBlankScreenMsg.setVisibility(View.GONE);
        else
            lblBlankScreenMsg.setVisibility(View.VISIBLE);

        return rootView;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
