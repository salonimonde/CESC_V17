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

public class PaymentReadingFragment extends Fragment {
    private View mRootView;
    private TextView lblbinder, txtbinder, txtconsumer, lblconsumer, txtallocated, lblallocated, lblreading,
            txtreading, txtsnf, lblsnf, txtrnt, lblrnt, lblrate, txtrate, txtbillable, lblbillable, lblamount, txtamoumt;
    private Payment mpayment;
    private Typeface regular, bold;
    private String title;

    public PaymentReadingFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_payment_reading, container, false);
        mpayment = (Payment) getArguments().getSerializable("CURRENT_PAYMENT_CARD");
        bold = App.getSansationBoldFont();
        regular = App.getSansationRegularFont();
        initUI();
        return mRootView;

    }

    private void initUI() {

        lblbillable = (TextView) mRootView.findViewById(R.id.lbl_billablereading);
        lblbillable.setTypeface(regular);

        lblbinder = (TextView) mRootView.findViewById(R.id.lbl_binder);
        lblbinder.setTypeface(regular);

        lblconsumer = (TextView) mRootView.findViewById(R.id.lbl_consumers);
        lblconsumer.setTypeface(regular);

        lblallocated = (TextView) mRootView.findViewById(R.id.lbl_allocated);
        lblallocated.setTypeface(regular);

        lblreading = (TextView) mRootView.findViewById(R.id.lbl_readings);
        lblreading.setTypeface(regular);

        lblsnf = (TextView) mRootView.findViewById(R.id.lbl_sitenotfound);
        lblsnf.setTypeface(regular);

        lblrnt = (TextView) mRootView.findViewById(R.id.lbl_rnt);
        lblrnt.setTypeface(regular);

        lblamount = (TextView) mRootView.findViewById(R.id.lbl_amount);
        lblamount.setTypeface(regular);

        lblrate = (TextView) mRootView.findViewById(R.id.lbl_rate);
        lblrate.setTypeface(regular);

        txtreading = (TextView) mRootView.findViewById(R.id.txt_reading);
        txtreading.setTypeface(regular);
        txtreading.setText(mpayment.totalreading);

        txtbillable = (TextView) mRootView.findViewById(R.id.txt_billablereading);
        txtbillable.setTypeface(regular);
        txtbillable.setText(mpayment.billablereading);

        txtallocated = (TextView) mRootView.findViewById(R.id.txt_allocated);
        txtallocated.setTypeface(regular);
        txtallocated.setText(mpayment.allocated);

        txtsnf = (TextView) mRootView.findViewById(R.id.txt_sitenotfound);
        txtsnf.setTypeface(regular);
        txtsnf.setText(mpayment.snf);

        txtrnt = (TextView) mRootView.findViewById(R.id.txt_rnt);
        txtrnt.setTypeface(regular);
        txtrnt.setText(mpayment.rnt);

        txtbinder = (TextView) mRootView.findViewById(R.id.txt_binder);
        txtbinder.setTypeface(regular);
        txtbinder.setText(mpayment.readingbinderassigned);


        txtrate = (TextView) mRootView.findViewById(R.id.txt_rate);
        txtrate.setTypeface(regular);
        txtrate.setText(mpayment.mr_rate);

        txtconsumer = (TextView) mRootView.findViewById(R.id.txt_consumers);
        txtconsumer.setTypeface(regular);
        txtconsumer.setText(mpayment.readingconsumers);

        txtamoumt = (TextView) mRootView.findViewById(R.id.txt_amount);
        txtamoumt.setTypeface(bold);
        txtamoumt.setText("Rs. " + Math.round(Math.floor(Double.parseDouble(mpayment.readingamount))));

    }
}
