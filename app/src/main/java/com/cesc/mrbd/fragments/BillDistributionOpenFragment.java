package com.cesc.mrbd.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.activity.BillDistributionDetailActivity;
import com.cesc.mrbd.activity.BillDistributionLandingScreen;
import com.cesc.mrbd.activity.LandingActivity;
import com.cesc.mrbd.adapters.BillDistributionOpenAdapter;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.BillCard;
import com.cesc.mrbd.utils.App;

import java.util.ArrayList;

public class BillDistributionOpenFragment extends Fragment {

    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;
    private ArrayList<BillCard> mBillCards = new ArrayList<>();
    private TextView lblBlankScreenMsg;

    public BillDistributionOpenFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBillCards.clear();

        ArrayList<String> binderCodes = DatabaseManager.getUniqueBinders(this.getContext(), BillDistributionLandingScreen.meter_reader_id, AppConstants.BILL_CARD_STATUS_ALLOCATED);
        if (binderCodes != null) {
            for (int i = 0; i < binderCodes.size(); i++) {
                mBillCards.addAll(DatabaseManager.getBillCardsUnique(this.getContext(), BillDistributionLandingScreen.meter_reader_id,
                        AppConstants.BILL_CARD_STATUS_ALLOCATED, binderCodes.get(i), true));
            }
        }

        BillDistributionOpenAdapter adapter = new BillDistributionOpenAdapter(this.getContext(), mBillCards, true);

        View rootView = inflater.inflate(R.layout.fragment_bill_distrubution_open, container, false);

        Typeface regularFont = App.getSansationRegularFont();

        lblBlankScreenMsg = (TextView) rootView.findViewById(R.id.lbl_blank_msg);
        lblBlankScreenMsg.setTypeface(regularFont);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (mBillCards != null && mBillCards.size() > 0)
            lblBlankScreenMsg.setVisibility(View.GONE);
        else
            lblBlankScreenMsg.setVisibility(View.VISIBLE);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
