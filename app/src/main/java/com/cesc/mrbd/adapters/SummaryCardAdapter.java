package com.cesc.mrbd.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.models.SummaryCard;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Bynry01 on 10/7/2016.
 */
public class SummaryCardAdapter extends RecyclerView.Adapter<SummaryCardAdapter.SummaryCardListHolder>
{
    public Context mContext;
    private ArrayList<SummaryCard> mSummaryCards;

    public SummaryCardAdapter()
    {}

    public SummaryCardAdapter(Context context, ArrayList<SummaryCard> SummaryCards)
    {
        this.mContext = context;
        this.mSummaryCards = SummaryCards;

    }

    @Override
    public SummaryCardListHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_summary_card, null);
        SummaryCardListHolder viewHolder = new SummaryCardListHolder(view);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(final SummaryCardListHolder holder, final int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final SummaryCard item = mSummaryCards.get(position);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold=App.getSansationBoldFont();
        holder.total.setTypeface(bold);
        holder.total.setText(String.valueOf(item.total));

        holder.open.setTypeface(bold);
        holder.open.setText(String.valueOf(item.open));

        holder.routes.setTypeface(regular);
        holder.routes.setText(String.valueOf(item.route_id));

        holder.completed.setTypeface(bold);
        holder.completed.setText(String.valueOf(item.completed));

        holder.bill.setTypeface(regular);
        holder.bill.setText(String.valueOf(item.bill_cycle_code));

    }

    @Override
    public int getItemCount()
    {
        if(mSummaryCards != null && mSummaryCards.size() > 0)
            return mSummaryCards.size();
        else
            return 0;
    }

    public void setJobCard(ArrayList<SummaryCard> Summarycard)
    {
        mSummaryCards=Summarycard;
        notifyDataSetChanged();
    }



    public  class SummaryCardListHolder extends RecyclerView.ViewHolder
    {
        public RelativeLayout rl_summary_card;
        public TextView total,lbl_total,routes,open,lbl_open,completed,lbl_completed,bill;

        public SummaryCardListHolder(View itemView)
        {
            super(itemView);
            Typeface regular= App.getSansationRegularFont();
            Typeface bold=App.getSansationBoldFont();
            rl_summary_card=(RelativeLayout)itemView.findViewById(R.id.rl_summary_card);
            total = (TextView) itemView.findViewById(R.id.total);
            total.setTypeface(bold);
            lbl_total=(TextView)itemView.findViewById(R.id.lbl_total);
            lbl_total.setTypeface(regular);
            routes = (TextView) itemView.findViewById(R.id.routes);
            routes.setTypeface(regular);
            open = (TextView) itemView.findViewById(R.id.open);
            open.setTypeface(bold);
            lbl_open = (TextView) itemView.findViewById(R.id.lbl_open);
            lbl_open.setTypeface(regular);
            completed = (TextView) itemView.findViewById(R.id.completed);
            completed.setTypeface(bold);
            lbl_completed = (TextView) itemView.findViewById(R.id.lbl_completed);
            lbl_completed.setTypeface(regular);
            bill = (TextView) itemView.findViewById(R.id.bill_cycle_code);
            bill.setTypeface(regular);
        }
    }
}
