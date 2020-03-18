package com.cesc.mrbd.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.models.NotificationCard;
import com.cesc.mrbd.models.PaymentCalculation;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Bynry01 on 10/10/2016.
 */
public class PaymentCardAdapter extends RecyclerView.Adapter<PaymentCardAdapter.PaymentCardHolder> {
    public Context mContext;
    private OnPaymentClickListener mListener;
    private ArrayList<PaymentCalculation> mPaymentCard;

    public PaymentCardAdapter() {
    }

    public PaymentCardAdapter(Context context, ArrayList<PaymentCalculation> PaymentCards, OnPaymentClickListener listener) {
        this.mContext = context;
        this.mPaymentCard = PaymentCards;
        this.mListener = listener;

    }

    public PaymentCardAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public PaymentCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_payment_card, null);
        PaymentCardHolder viewHolder = new PaymentCardHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PaymentCardHolder holder, final int position) {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final PaymentCalculation item = mPaymentCard.get(position);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        holder.month.setTypeface(bold);
        if (item.domestic_payment_calculation != null)
            holder.month.setText(String.valueOf(item.domestic_payment_calculation.billmonth));
        else if (item.zero_payment_calculation != null)
            holder.month.setText(String.valueOf(item.zero_payment_calculation.billmonth));
        else
            holder.month.setText(String.valueOf(item.dc_payment_calculation.billmonth));

        holder.amount.setText(String.valueOf("Rs. " + Math.round(item.grandtotal)));
        holder.amount.setTypeface(bold);
        holder.rl_payment_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onPayCardClick(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mPaymentCard != null && mPaymentCard.size() > 0)
            return mPaymentCard.size();
        else
            return 0;
    }

    public void setJobCard(ArrayList<PaymentCalculation> Cards) {
        mPaymentCard = Cards;
        notifyDataSetChanged();
    }

    public interface OnPaymentClickListener {
        void onPayCardClick(PaymentCalculation pay);
    }

    public class PaymentCardHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rl_payment_card;
        public TextView month, billablereading, amount, catgory, totalreading, lbltotalconsumer, billdistributed, totalconsumer, lblbilldistributed, genrateddate, lblmonth,
                lblbillablereading, lblamount, lblcatgory, lbltotalreading, lblgenrateddate;


        public PaymentCardHolder(View itemView) {
            super(itemView);
            Typeface regular = App.getSansationRegularFont();
            Typeface bold = App.getSansationBoldFont();
            rl_payment_card = (RelativeLayout) itemView.findViewById(R.id.rl_job_payment_cell);
            month = (TextView) itemView.findViewById(R.id.month);
            month.setTypeface(regular);
//            catgory = (TextView) itemView.findViewById(R.id.catgory);
//            catgory.setTypeface(regular);
            billablereading = (TextView) itemView.findViewById(R.id.billabereading);
            billablereading.setTypeface(regular);
            amount = (TextView) itemView.findViewById(R.id.amount);
            amount.setTypeface(regular);
            totalreading = (TextView) itemView.findViewById(R.id.totalreading);
            totalreading.setTypeface(regular);
            genrateddate = (TextView) itemView.findViewById(R.id.genrated_on);
            genrateddate.setTypeface(regular);
            billdistributed = (TextView) itemView.findViewById(R.id.billdistributed);
            billdistributed.setTypeface(regular);
            totalconsumer = (TextView) itemView.findViewById(R.id.totalbills);
            totalconsumer.setTypeface(regular);

            lblmonth = (TextView) itemView.findViewById(R.id.lbl_month);
            lblmonth.setTypeface(regular
            );

            lblbillablereading = (TextView) itemView.findViewById(R.id.lbl_billablereading);
            lblbillablereading.setTypeface(regular);
            lbltotalreading = (TextView) itemView.findViewById(R.id.lbl_totalreading);
            lbltotalreading.setTypeface(regular);
            lblamount = (TextView) itemView.findViewById(R.id.lbl_amount);
            lblamount.setTypeface(regular);
            lblgenrateddate = (TextView) itemView.findViewById(R.id.lbl_genrated_on);
            lblgenrateddate.setTypeface(regular);
            lbltotalconsumer = (TextView) itemView.findViewById(R.id.lbl_totalcomsumer);
            lbltotalconsumer.setTypeface(regular);
            lblbilldistributed = (TextView) itemView.findViewById(R.id.lbl_billdistributed);
            lblbilldistributed.setTypeface(regular);

        }

        public void bind(final Context context, final NotificationCard notificationCard) {

        }
    }
}



