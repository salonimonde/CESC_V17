package com.cesc.mrbd.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.models.Payment;
import com.cesc.mrbd.models.PaymentCalculation;
import com.cesc.mrbd.utils.App;

/**
 * Created by Admin on 29-08-2017.
 */

public class PaymentBillDistributionFragment extends Fragment {
    private View mRootView;
    private TextView lblbinder, txtbinder, lblrate, txtrate, txtconsumer, lbldistributed, txtdistributed, lblconsumer, lblamount, txtamoumt;
    private Payment mpayment;
    private Typeface bold, regular;
    private String title;

    public PaymentBillDistributionFragment() {
// Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mpayment = (Payment) getArguments().getSerializable("CURRENT_PAYMENT_CARD");
        mRootView = inflater.inflate(R.layout.fragment_payment_billdistribution, container, false);
        bold = App.getSansationBoldFont();
        regular = App.getSansationRegularFont();
        initUI();
        return mRootView;

    }

    private void initUI() {
        lblbinder = (TextView) mRootView.findViewById(R.id.lbl_binder);
        lblbinder.setTypeface(regular);

        lblconsumer = (TextView) mRootView.findViewById(R.id.lbl_consumers);
        lblconsumer.setTypeface(regular);

        lblamount = (TextView) mRootView.findViewById(R.id.lbl_amount);
        lblamount.setTypeface(regular);

        lblrate = (TextView) mRootView.findViewById(R.id.lbl_rate);
        lblrate.setTypeface(regular);

        lbldistributed = (TextView) mRootView.findViewById(R.id.lbl_distributed);
        lbldistributed.setTypeface(regular);

        txtdistributed = (TextView) mRootView.findViewById(R.id.txt_distributed);
        txtdistributed.setTypeface(regular);
        if (mpayment.dcconsumers != null) {
            if (!mpayment.dcconsumers.equalsIgnoreCase("0"))
                txtdistributed.setText(mpayment.dcconsumers);
        } else
            txtdistributed.setText(mpayment.distributed);

        txtrate = (TextView) mRootView.findViewById(R.id.txt_rate);
        txtrate.setTypeface(regular);
        if (mpayment.dcconsumers != null) {
            if (!mpayment.dcconsumers.equalsIgnoreCase("0"))
                txtrate.setText(mpayment.dc_rate);
        } else
            txtrate.setText(mpayment.bd_rate);

        txtbinder = (TextView) mRootView.findViewById(R.id.txt_binder);
        txtbinder.setTypeface(regular);
        if (mpayment.dcconsumers != null) {
            if (!mpayment.dcconsumers.equalsIgnoreCase("0"))
                txtbinder.setText(mpayment.dcbinderassigned);
        } else
            txtbinder.setText(mpayment.distributedbinderassigned);


        txtconsumer = (TextView) mRootView.findViewById(R.id.txt_consumers);
        txtconsumer.setTypeface(regular);
        if (mpayment.dcconsumers != null) {
            if (!mpayment.dcconsumers.equalsIgnoreCase("0"))
                txtconsumer.setText(mpayment.dc_distributed);
        } else
            txtconsumer.setText(mpayment.distributedconsumer);

        txtamoumt = (TextView) mRootView.findViewById(R.id.txt_amount);
        txtamoumt.setTypeface(bold);
        if (mpayment.dcconsumers != null) {
            if (!mpayment.dcconsumers.equalsIgnoreCase("0"))
                txtamoumt.setText("Rs. " + Math.round(Math.floor(Double.parseDouble(mpayment.dcamount))));
        } else
            txtamoumt.setText("Rs. " + Math.round(Math.floor(Double.parseDouble(mpayment.distributedamount))));

    }
}
