package com.cesc.mrbd.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.activity.BillDistributionDetailActivity;
import com.cesc.mrbd.activity.BillDistributionLandingScreen;
import com.cesc.mrbd.activity.BillDistributionListingActivity;
import com.cesc.mrbd.activity.LandingActivity;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.BillCard;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Piyush Kalmegh on 30-May-17.
 */

public class BillDistributionOpenAdapter extends RecyclerView.Adapter<BillDistributionOpenAdapter.BillDistributionHolder> {

    private Context mContext;
    private ArrayList<BillCard> mBillCards;
    private boolean isListing;

    public BillDistributionOpenAdapter(Context context, ArrayList<BillCard> billCards, boolean isListing) {
        this.mContext = context;
        this.mBillCards = billCards;
        this.isListing = isListing;
    }

    @Override
    public BillDistributionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bill_open_card, null);
        BillDistributionOpenAdapter.BillDistributionHolder viewHolder = new BillDistributionOpenAdapter.BillDistributionHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BillDistributionHolder holder, final int position) {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final BillCard item = mBillCards.get(position);
        if(isListing){
            ArrayList<BillCard> openBillCards = DatabaseManager.getBinderWiseBillCard(mContext,
                    BillDistributionLandingScreen.meter_reader_id, AppConstants.BILL_CARD_STATUS_ALLOCATED, item.binder_code, item.zone_code);
            ArrayList<BillCard> completedBillCards = DatabaseManager.getBinderWiseBillCard(mContext,
                    BillDistributionLandingScreen.meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, item.binder_code, item.zone_code);

            holder.lblSubDivisionName.setText(mContext.getString(R.string.binder));
            holder.lblConsumers.setText(mContext.getString(R.string.sub_division_name));
            holder.txtSubDivisionName.setText(item.binder_code);

            if(completedBillCards != null) {
                holder.lblBinderNo.setText(mContext.getString(R.string.open) + "/" + mContext.getString(R.string.completed));
                holder.txtBinderNo.setText(openBillCards.size() + "/" + completedBillCards.size());
            }
            else {
                holder.lblBinderNo.setText(mContext.getString(R.string.open));
                holder.txtBinderNo.setText("" + openBillCards.size());
            }

            holder.txtConsumers.setText(item.zone_name);
            holder.txtEndDate.setText(item.end_date);
        }else{
            holder.txtSubDivisionName.setText(item.consumer_name);
            holder.txtBinderNo.setText(item.cycle_code + "/" + item.binder_code);
            holder.txtConsumers.setText(item.consumer_no);
            holder.txtEndDate.setText(item.end_date);
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (isListing){
                    intent = new Intent(mContext, BillDistributionListingActivity.class);
                    intent.putExtra(AppConstants.BINDER_NO, item.binder_code);
                    intent.putExtra(AppConstants.ZONE_CODE, item.zone_code);
                }else {
                    intent = new Intent(mContext, BillDistributionDetailActivity.class);
                    intent.putExtra(AppConstants.CURRENT_BIll_CARD, item);
                }
                mContext.startActivity(intent);
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

    public class BillDistributionHolder extends RecyclerView.ViewHolder {
        public RelativeLayout relativeLayout;
        public TextView lblSubDivisionName, txtSubDivisionName, lblBinderNo, txtBinderNo, lblConsumers, txtConsumers, lblEndDate,
                txtEndDate;
        public Typeface bold, regular;

        public BillDistributionHolder(View itemView) {
            super(itemView);

            bold = App.getSansationBoldFont();
            regular = App.getSansationRegularFont();

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_card);
            lblSubDivisionName = (TextView) itemView.findViewById(R.id.lbl_sub_division_name);
            lblSubDivisionName.setTypeface(regular);
            txtSubDivisionName = (TextView) itemView.findViewById(R.id.txt_sub_division_name);
            txtSubDivisionName.setTypeface(regular);
            lblBinderNo = (TextView) itemView.findViewById(R.id.lbl_binder_no);
            lblBinderNo.setTypeface(regular);
            txtBinderNo = (TextView) itemView.findViewById(R.id.txt_binder_no);
            txtBinderNo.setTypeface(regular);
            lblConsumers = (TextView) itemView.findViewById(R.id.lbl_consumers);
            lblConsumers.setTypeface(regular);
            txtConsumers = (TextView) itemView.findViewById(R.id.txt_consumers);
            txtConsumers.setTypeface(regular);
            lblEndDate = (TextView) itemView.findViewById(R.id.lbl_end_date);
            lblEndDate.setTypeface(regular);
            txtEndDate = (TextView) itemView.findViewById(R.id.txt_end_date);
            txtEndDate.setTypeface(regular);
        }
    }
}
