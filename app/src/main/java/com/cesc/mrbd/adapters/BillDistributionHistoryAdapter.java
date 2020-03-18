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
import com.cesc.mrbd.fragments.BillDistributionHistoryFragment;
import com.cesc.mrbd.models.UploadBillHistory;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Piyush Kalmegh on 01-Jun-17.
 */

public class BillDistributionHistoryAdapter extends RecyclerView.Adapter<BillDistributionHistoryAdapter.BillDistributionHistoryHolder> {

    private Context mContext;
    private ArrayList<UploadBillHistory> mBillCards;

    public BillDistributionHistoryAdapter(Context context, ArrayList<UploadBillHistory> mhistorycards, BillDistributionHistoryFragment billDistributionHistoryFragment) {
        mContext = context;
        mBillCards = mhistorycards;
    }

    @Override
    public BillDistributionHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bill_history_card, null);
        BillDistributionHistoryHolder viewHolder = new BillDistributionHistoryHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BillDistributionHistoryHolder holder, int position) {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final UploadBillHistory item = mBillCards.get(position);
        holder.txtBillCycleCode.setText(item.cycle_code);
        holder.txtBinders.setText(item.binder_code);
        holder.txtDate.setText(item.reading_date);
        holder.txtTotalConsumers.setText(item.consumer_no);
        holder.txtCompleted.setText(item.is_new);
    }

    @Override
    public int getItemCount() {
        if (mBillCards != null && mBillCards.size() > 0)
            return mBillCards.size();
        else
            return 0;
    }

    public class BillDistributionHistoryHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayout;
        public TextView txtBillCycleCode, txtBinders, txtDate, txtTotalConsumers, lblConsumers, txtCompleted, lblCompleted;
        public Typeface bold, regular;

        public BillDistributionHistoryHolder(View itemView) {
            super(itemView);

            bold = App.getSansationBoldFont();
            regular = App.getSansationRegularFont();

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_card);
            txtBillCycleCode = (TextView) itemView.findViewById(R.id.bill_cycle_code);
            txtBillCycleCode.setTypeface(regular);
            txtBinders = (TextView) itemView.findViewById(R.id.binder);
            txtBinders.setTypeface(regular);
            txtDate = (TextView) itemView.findViewById(R.id.date);
            txtDate.setTypeface(regular);
            txtTotalConsumers = (TextView) itemView.findViewById(R.id.txt_consumers);
            txtTotalConsumers.setTypeface(regular);
            lblConsumers = (TextView) itemView.findViewById(R.id.lbl_consumer);
            lblConsumers.setTypeface(regular);
            txtCompleted = (TextView) itemView.findViewById(R.id.txt_completed);
            txtCompleted.setTypeface(regular);
            lblCompleted = (TextView) itemView.findViewById(R.id.lbl_completed);
            lblCompleted.setTypeface(regular);
            lblCompleted.setText("New");

        }
    }
}
