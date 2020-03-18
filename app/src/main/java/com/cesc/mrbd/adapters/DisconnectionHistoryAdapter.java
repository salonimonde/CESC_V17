package com.cesc.mrbd.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.models.DisconnectionHistory;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Piyush on 06-12-2017.
 * Bynry
 */
public class DisconnectionHistoryAdapter extends RecyclerView.Adapter<DisconnectionHistoryAdapter.DisconnectionHistoryHolder>
{

    private Context mContext;
    private ArrayList<DisconnectionHistory> disconnectionHistories;

    public DisconnectionHistoryAdapter(Context context, ArrayList<DisconnectionHistory> disconnectionHistories)
    {
        mContext = context;
        this.disconnectionHistories = disconnectionHistories;
    }

    @Override
    public DisconnectionHistoryAdapter.DisconnectionHistoryHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_history_card, null);
        DisconnectionHistoryAdapter.DisconnectionHistoryHolder viewHolder = new DisconnectionHistoryAdapter.DisconnectionHistoryHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DisconnectionHistoryAdapter.DisconnectionHistoryHolder holder, int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);

        holder.txtBillMonth.setText(disconnectionHistories.get(position).bill_month);
        holder.txtBinderCode.setText(disconnectionHistories.get(position).binder_code);
        holder.txtDate.setText(disconnectionHistories.get(position).date);
        holder.txtTotal.setText(disconnectionHistories.get(position).total);
        holder.txtDelivered.setText(disconnectionHistories.get(position).totalDelivered);
        holder.txtNotDelivered.setText(disconnectionHistories.get(position).totalNotDDelivered);
    }

    @Override
    public int getItemCount()
    {
        if(disconnectionHistories != null && disconnectionHistories.size() > 0)
            return disconnectionHistories.size();
        else
            return 0;
    }

    public class DisconnectionHistoryHolder extends RecyclerView.ViewHolder 
    {
        public TextView txtDelivered, lblDelivered, txtBinderCode, txtTotal, lblTotal, txtNotDelivered, lblNotDelivered, txtDate, txtBillMonth;
        public Typeface bold, regular;

        public DisconnectionHistoryHolder(View itemView)
        {
            super(itemView);

            bold = App.getSansationBoldFont();
            regular = App.getSansationRegularFont();

            lblTotal = (TextView) itemView.findViewById(R.id.lbl_open);
            lblTotal.setTypeface(regular);
            lblTotal.setText(CommonUtils.getString(mContext, R.string.total));
            lblDelivered = (TextView) itemView.findViewById(R.id.lbl_revisit);
            lblDelivered.setTypeface(regular);
            lblDelivered.setText(CommonUtils.getString(mContext, R.string.delivered));
            lblNotDelivered = (TextView) itemView.findViewById(R.id.lbl_unbill);
            lblNotDelivered.setTypeface(regular);
            lblNotDelivered.setText(CommonUtils.getString(mContext, R.string.not_delivered));

            txtBillMonth = (TextView) itemView.findViewById(R.id.bill_cycle_code);
            txtBillMonth.setTypeface(regular);
            txtBinderCode = (TextView) itemView.findViewById(R.id.route);
            txtBinderCode.setTypeface(regular);
            txtDate = (TextView) itemView.findViewById(R.id.date);
            txtDate.setTypeface(regular);
            txtTotal = (TextView) itemView.findViewById(R.id.open);
            txtTotal.setTypeface(bold);
            txtDelivered = (TextView) itemView.findViewById(R.id.revisit);
            txtDelivered.setTypeface(bold);
            txtNotDelivered = (TextView) itemView.findViewById(R.id.unbill);
            txtNotDelivered.setTypeface(bold);
        }
    }
}