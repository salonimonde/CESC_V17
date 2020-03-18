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
import com.cesc.mrbd.models.BillCard;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Piyush Kalmegh on 01-Jun-17.
 */

public class BillDistributionCompletedAdapter extends RecyclerView.Adapter<BillDistributionCompletedAdapter.BillDistributionCompletedHolder> {

    private Context mContext;
    private ArrayList<BillCard> mBillCards;
    private OnBillCardClickListener mListener;

    public BillDistributionCompletedAdapter() {

    }

    public BillDistributionCompletedAdapter(Context context, ArrayList<BillCard> billCards, OnBillCardClickListener listener) {
        this.mContext = context;
        this.mBillCards = billCards;
        this.mListener = listener;
    }

    public interface OnBillCardClickListener {
        void onBillCardClick(BillCard billCard);
    }

    @Override
    public BillDistributionCompletedAdapter.BillDistributionCompletedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bill_compleated_card, null);
        BillDistributionCompletedAdapter.BillDistributionCompletedHolder viewHolder = new BillDistributionCompletedAdapter.BillDistributionCompletedHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BillDistributionCompletedAdapter.BillDistributionCompletedHolder holder, int position) {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final BillCard item = mBillCards.get(position);

        holder.txtSubDivisionName.setText(item.consumer_name);
        holder.txtBinderNo.setText(item.cycle_code + "/" + item.binder_code);
        holder.txtConsumers.setText(item.consumer_no);
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date1 = null;
        try {
            date1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(item.reading_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.txtDistributed.setText(dateFormat.format(date1));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onBillCardClick(item);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (mBillCards != null && mBillCards.size() > 0)
            return mBillCards.size();
        else
            return 0;
    }

    public class BillDistributionCompletedHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayout;
        public TextView lblSubDivisionName, txtSubDivisionName, lblBinderNo, txtBinderNo, lblConsumers, txtConsumers, lblDistributed,
                txtDistributed;

        public BillDistributionCompletedHolder(View itemView) {
            super(itemView);

            Typeface regular = App.getSansationRegularFont();

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_card);
            lblSubDivisionName = (TextView) itemView.findViewById(R.id.lbl_sub_division_name);
            lblSubDivisionName.setTypeface(regular);
            lblBinderNo = (TextView) itemView.findViewById(R.id.lbl_binder_no);
            lblBinderNo.setTypeface(regular);
            lblConsumers = (TextView) itemView.findViewById(R.id.lbl_consumers);
            lblConsumers.setTypeface(regular);
            lblDistributed = (TextView) itemView.findViewById(R.id.lbl_distributed);
            lblDistributed.setTypeface(regular);
            txtSubDivisionName = (TextView) itemView.findViewById(R.id.txt_sub_division_name);
            txtSubDivisionName.setTypeface(regular);
            txtBinderNo = (TextView) itemView.findViewById(R.id.txt_binder_no);
            txtBinderNo.setTypeface(regular);
            txtConsumers = (TextView) itemView.findViewById(R.id.txt_consumers);
            txtConsumers.setTypeface(regular);
            txtDistributed = (TextView) itemView.findViewById(R.id.txt_distributed);
            txtDistributed.setTypeface(regular);

        }
    }
}
