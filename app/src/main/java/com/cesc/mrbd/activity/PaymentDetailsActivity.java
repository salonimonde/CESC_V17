package com.cesc.mrbd.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.adapters.ViewPagerAdapter;
import com.cesc.mrbd.fragments.PaymentBillDistributionFragment;
import com.cesc.mrbd.fragments.PaymentDeductionFragment;
import com.cesc.mrbd.fragments.PaymentReadingFragment;
import com.cesc.mrbd.models.PaymentCalculation;
import com.cesc.mrbd.utils.App;

public class PaymentDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView lblbinder, txtbinder, lblmonth, txtmonth, txtconsumer, lblconsumer, txtallocated, lblallocated, lblreading,
            txtreading, txtsnf, lblsnf, txtrnt, lblrnt, txtbillable, lblbillable, lbltype, txttype, lblcatgory, txtcatgory, lblamount, txtamoumt;
    private ImageView imgBack;
    private LinearLayout ll_rnt, ll_snf, ll_allocated, ll_reading;
    private PaymentCalculation mPaymentCard;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private Typeface regular, bold;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);
        Intent i = this.getIntent();
        regular = App.getSansationRegularFont();
        bold = App.getSansationBoldFont();
        if (i != null) {
            if (mPaymentCard == null) {
                mPaymentCard = (PaymentCalculation) i.getSerializableExtra("CURRENT_PAYMENT_CARD");

            }
        }
        Typeface bold = App.getSansationBoldFont();
        Typeface regular = App.getSansationRegularFont();
        initUI();

        imgBack = (ImageView) findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
        lblmonth = (TextView) findViewById(R.id.lbl_bill_month);
        lblmonth.setTypeface(regular);
        lblamount = (TextView) findViewById(R.id.lbl_amount);
        lblamount.setTypeface(regular);


        txtmonth = (TextView) findViewById(R.id.txt_bill_month);
        txtmonth.setTypeface(regular);
        if (mPaymentCard.domestic_payment_calculation != null && mPaymentCard.zero_payment_calculation != null)
            txtmonth.setText(mPaymentCard.domestic_payment_calculation.billmonth);
        else if (mPaymentCard.domestic_payment_calculation != null)
            txtmonth.setText(mPaymentCard.domestic_payment_calculation.billmonth);
        else if (mPaymentCard.zero_payment_calculation != null)
            txtmonth.setText(mPaymentCard.zero_payment_calculation.billmonth);


        txtamoumt = (TextView) findViewById(R.id.txt_amount);
        txtamoumt.setTypeface(bold);
        txtamoumt.setText("Rs. " + Math.round(mPaymentCard.grandtotal));

    }

    private void initUI() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setCurrentItem(0);
        tabLayout = (TabLayout) findViewById(R.id.simpleTabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setFontOnTabs();

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

    private void setupViewPager(ViewPager viewPager) {
        PaymentReadingFragment paymentReadingFragment = new PaymentReadingFragment();
        PaymentBillDistributionFragment  paymentBillDistributionFragment = new PaymentBillDistributionFragment();
        PaymentReadingFragment dompaymentReadingFragment = new PaymentReadingFragment();
        PaymentBillDistributionFragment dompaymentBillDistributionFragment = new PaymentBillDistributionFragment();
        PaymentDeductionFragment divFragment = new PaymentDeductionFragment();
        PaymentBillDistributionFragment  paymentdcNoticeFragment = new PaymentBillDistributionFragment();

        adapter = new ViewPagerAdapter(getSupportFragmentManager(), false);
        if (mPaymentCard.domestic_payment_calculation != null)
            if (mPaymentCard.domestic_payment_calculation.readingconsumers != null)
                    if (!mPaymentCard.domestic_payment_calculation.readingconsumers.equalsIgnoreCase("0")) {
                    Bundle bundle1 = new Bundle();
                    bundle1.putSerializable("CURRENT_PAYMENT_CARD", mPaymentCard.domestic_payment_calculation);
                    dompaymentReadingFragment.setArguments(bundle1);
                    adapter.addFragment(dompaymentReadingFragment, "Domestic-Reading");
                }
        if (mPaymentCard.domestic_payment_calculation != null)
            if (mPaymentCard.domestic_payment_calculation.distributedconsumer != null)
                if (!mPaymentCard.domestic_payment_calculation.distributedconsumer.equalsIgnoreCase("0")) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putSerializable("CURRENT_PAYMENT_CARD", mPaymentCard.domestic_payment_calculation);
                    dompaymentBillDistributionFragment.setArguments(bundle2);
                    adapter.addFragment(dompaymentBillDistributionFragment, "Domestic-Distribution");
                }
        if (mPaymentCard.zero_payment_calculation != null)
            if (mPaymentCard.zero_payment_calculation.readingconsumers != null)
                if (!mPaymentCard.zero_payment_calculation.readingconsumers.equalsIgnoreCase("0")) {
                    Bundle bundle3 = new Bundle();
                    bundle3.putSerializable("CURRENT_PAYMENT_CARD", mPaymentCard.zero_payment_calculation);
                    paymentReadingFragment.setArguments(bundle3);
                    adapter.addFragment(paymentReadingFragment, "Zero-Reading");
                }
        if (mPaymentCard.zero_payment_calculation != null)
            if (mPaymentCard.zero_payment_calculation.distributedconsumer != null)
                if (!mPaymentCard.zero_payment_calculation.distributedconsumer.equalsIgnoreCase("0")) {
                    Bundle bundle4 = new Bundle();
                    bundle4.putSerializable("CURRENT_PAYMENT_CARD", mPaymentCard.zero_payment_calculation);
                    paymentBillDistributionFragment.setArguments(bundle4);
                    adapter.addFragment(paymentBillDistributionFragment, "Zero-Distribution");
                }

        if (mPaymentCard.dc_payment_calculation != null)
            if (mPaymentCard.dc_payment_calculation.dcconsumers != null)
                if (!mPaymentCard.dc_payment_calculation.dcconsumers.equalsIgnoreCase("0")) {
                    Bundle bundle6 = new Bundle();
                    bundle6.putSerializable("CURRENT_PAYMENT_CARD", mPaymentCard.dc_payment_calculation);
                    paymentdcNoticeFragment.setArguments(bundle6);
                    adapter.addFragment(paymentdcNoticeFragment, "DC-Notice");
                }

        Bundle bundle5 = new Bundle();
        bundle5.putSerializable("CURRENT_PAYMENT_CARD", mPaymentCard);
        divFragment.setArguments(bundle5);
        adapter.addFragment(divFragment, "Deductions");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view == imgBack) {
            finish();
        }
    }
}
