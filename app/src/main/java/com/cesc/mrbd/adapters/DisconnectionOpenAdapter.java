package com.cesc.mrbd.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.activity.DisconnectionNoticeDetailActivity;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.models.Disconnection;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Piyush on 06-12-2017.
 * Bynry
 */
public class DisconnectionOpenAdapter extends RecyclerView.Adapter<DisconnectionOpenAdapter.DisconnectionOpenHolder>
{
    private Context mContext;
    private ArrayList<Disconnection> disconnections;

    public DisconnectionOpenAdapter(Context context, ArrayList<Disconnection> disconnections)
    {
        this.mContext = context;
        this.disconnections = disconnections;
    }

    @Override
    public DisconnectionOpenAdapter.DisconnectionOpenHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bill_open_card, null);
        DisconnectionOpenAdapter.DisconnectionOpenHolder viewHolder = new DisconnectionOpenAdapter.DisconnectionOpenHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DisconnectionOpenAdapter.DisconnectionOpenHolder holder, final int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        holder.txtSubDivisionName.setText(disconnections.get(position).consumer_name);
        holder.txtBinderNo.setText(disconnections.get(position).consumer_no);
        holder.txtConsumers.setText(disconnections.get(position).binder_code);
        holder.txtEndDate.setText(disconnections.get(position).disconnection_notice_no);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, DisconnectionNoticeDetailActivity.class);
//                Log.d("---Checking Data---", "RefreshedToken: " + disconnections.get(position).job_card_id);
                intent.putExtra(AppConstants.DISCONNECTION_ADAPTER_VALUE, disconnections.get(position));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        if (disconnections != null && disconnections.size() > 0)
            return disconnections.size();
        else
            return 0;
    }

    public class DisconnectionOpenHolder extends RecyclerView.ViewHolder
    {
        public RelativeLayout relativeLayout;
        public TextView lblSubDivisionName, txtSubDivisionName, lblBinderNo, txtBinderNo, lblConsumers, txtConsumers, lblEndDate,
                txtEndDate;
        public Typeface bold, regular;

        public DisconnectionOpenHolder(View itemView)
        {
            super(itemView);

            bold = App.getSansationBoldFont();
            regular = App.getSansationRegularFont();

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_card);

            lblSubDivisionName = (TextView) itemView.findViewById(R.id.lbl_sub_division_name);
            lblSubDivisionName.setTypeface(regular);
            lblSubDivisionName.setText(CommonUtils.getString(mContext, R.string.consumer_name));
            txtSubDivisionName = (TextView) itemView.findViewById(R.id.txt_sub_division_name);
            txtSubDivisionName.setTypeface(regular);
            lblBinderNo = (TextView) itemView.findViewById(R.id.lbl_binder_no);
            lblBinderNo.setTypeface(regular);
            lblBinderNo.setText(CommonUtils.getString(mContext, R.string.consumer_no));
            txtBinderNo = (TextView) itemView.findViewById(R.id.txt_binder_no);
            txtBinderNo.setTypeface(regular);
            lblConsumers = (TextView) itemView.findViewById(R.id.lbl_consumers);
            lblConsumers.setTypeface(regular);
            lblConsumers.setText(CommonUtils.getString(mContext, R.string.binder));
            txtConsumers = (TextView) itemView.findViewById(R.id.txt_consumers);
            txtConsumers.setTypeface(regular);
            lblEndDate = (TextView) itemView.findViewById(R.id.lbl_end_date);
            lblEndDate.setTypeface(regular);
            lblEndDate.setText(CommonUtils.getString(mContext, R.string.dc_notice_no));
            txtEndDate = (TextView) itemView.findViewById(R.id.txt_end_date);
            txtEndDate.setTypeface(regular);
        }
    }
}