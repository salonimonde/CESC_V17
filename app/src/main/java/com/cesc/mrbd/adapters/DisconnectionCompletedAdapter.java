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
import com.cesc.mrbd.models.UploadDisconnectionNotices;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Piyush on 06-12-2017.
 * Bynry
 */
public class DisconnectionCompletedAdapter extends RecyclerView.Adapter<DisconnectionCompletedAdapter.DisconnectionCompletedHolder>
{

    private Context mContext;
    private ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices;
    
    public DisconnectionCompletedAdapter(Context context, ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices)
    {
        this.mContext = context;
        this.uploadDisconnectionNotices = uploadDisconnectionNotices;
    }

    @Override
    public DisconnectionCompletedAdapter.DisconnectionCompletedHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_bill_compleated_card, null);
        DisconnectionCompletedAdapter.DisconnectionCompletedHolder viewHolder = new DisconnectionCompletedAdapter.DisconnectionCompletedHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DisconnectionCompletedAdapter.DisconnectionCompletedHolder holder, int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);

        holder.txtSubDivisionName.setText(uploadDisconnectionNotices.get(position).consumer_name);
        holder.txtBinderNo.setText(uploadDisconnectionNotices.get(position).consumer_no);
        holder.txtConsumers.setText(uploadDisconnectionNotices.get(position).binder_code);
        holder.txtDistributed.setText(uploadDisconnectionNotices.get(position).delivery_status);
    }

    @Override
    public int getItemCount()
    {
        if(uploadDisconnectionNotices != null && uploadDisconnectionNotices.size() > 0)
            return uploadDisconnectionNotices.size();
        else
            return 0;
    }

    public class DisconnectionCompletedHolder extends RecyclerView.ViewHolder
    {
        public RelativeLayout relativeLayout;
        public TextView lblSubDivisionName, txtSubDivisionName, lblBinderNo, txtBinderNo, lblConsumers, txtConsumers, lblDistributed,
                txtDistributed;

        public DisconnectionCompletedHolder(View itemView)
        {
            super(itemView);

            Typeface regular = App.getSansationRegularFont();

            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_card);
            lblSubDivisionName = (TextView) itemView.findViewById(R.id.lbl_sub_division_name);
            lblSubDivisionName.setTypeface(regular);
            lblSubDivisionName.setText(CommonUtils.getString(mContext, R.string.consumer_name));
            lblBinderNo = (TextView) itemView.findViewById(R.id.lbl_binder_no);
            lblBinderNo.setTypeface(regular);
            lblBinderNo.setText(CommonUtils.getString(mContext, R.string.consumer_no));
            lblConsumers = (TextView) itemView.findViewById(R.id.lbl_consumers);
            lblConsumers.setTypeface(regular);
            lblConsumers.setText(CommonUtils.getString(mContext, R.string.binder));
            lblDistributed = (TextView) itemView.findViewById(R.id.lbl_distributed);
            lblDistributed.setTypeface(regular);
            lblDistributed.setText(CommonUtils.getString(mContext, R.string.delivery_status));
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
