package com.cesc.mrbd.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.models.PaymentCalculation;
import com.cesc.mrbd.utils.App;

/**
 * Created by Admin on 29-08-2017.
 */

public class PaymentDeductionFragment extends Fragment {
    long a, b, c;
    private View mRootView;
    private TextView lblpf, txtpf, lbltd, txttd, txtesi, lblesi, txtother, lblother, lblptax,
            txtptax;
    private PaymentCalculation mpayment;
    private Typeface regular, bold;

    public PaymentDeductionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_payment_deduction, container, false);
        mpayment = (PaymentCalculation) getArguments().getSerializable("CURRENT_PAYMENT_CARD");
        bold = App.getSansationBoldFont();
        regular = App.getSansationRegularFont();
        a = Math.round(Math.floor(Double.valueOf(mpayment.pf)));
        b =  Math.round(Math.floor(Double.valueOf(mpayment.esi)));
        if (mpayment.other != null)
            c = a + b + Math.round(Math.floor(Double.valueOf(Double.valueOf(mpayment.other))));
        else
            c = a + b;
        initUI();
        return mRootView;

    }

    private void initUI() {

        lblpf = (TextView) mRootView.findViewById(R.id.lbl_pftax);
        lblpf.setTypeface(regular);

        lblother = (TextView) mRootView.findViewById(R.id.lbl_other);
        lblother.setTypeface(regular);

        lblesi = (TextView) mRootView.findViewById(R.id.lbl_esi);
        lblesi.setTypeface(regular);

        lblpf = (TextView) mRootView.findViewById(R.id.lbl_pf);
        lblpf.setTypeface(regular);

        lblptax = (TextView) mRootView.findViewById(R.id.lbl_pftax);
        lblptax.setTypeface(regular);

        lbltd = (TextView) mRootView.findViewById(R.id.lbl_total_deduction);
        lbltd.setTypeface(regular);

        txtptax = (TextView) mRootView.findViewById(R.id.txt_pftax);
        txtptax.setTypeface(regular);
        txtptax.setText("Rs. " + "0");

        txttd = (TextView) mRootView.findViewById(R.id.txt_total_deduction);
        txttd.setTypeface(bold);

        txttd.setText("Rs. " + Math.round(Math.floor(c)));


        txtpf = (TextView) mRootView.findViewById(R.id.txt_pf);
        txtpf.setTypeface(regular);

        txtpf.setText("Rs. " + Math.round(Math.floor(Double.valueOf(mpayment.pf))));


        txtother = (TextView) mRootView.findViewById(R.id.txt_other);
        txtother.setTypeface(regular);
        if (mpayment.other != null)
            txtother.setText("Rs. " +Math.round(Math.floor(Double.valueOf(mpayment.other))));
        else
            txtother.setText("Rs. " + "0");

        txtesi = (TextView) mRootView.findViewById(R.id.txt_esi);
        txtesi.setTypeface(regular);
        txtesi.setText("Rs. " + Math.round(Math.floor(Double.valueOf(mpayment.esi))));
//


    }
}
