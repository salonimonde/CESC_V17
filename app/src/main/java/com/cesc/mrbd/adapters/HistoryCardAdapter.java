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
import com.cesc.mrbd.models.HistoryCard;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.ArrayList;

/**
 * Created by Bynry01 on 10/14/2016.
 */

public class HistoryCardAdapter  extends RecyclerView.Adapter<HistoryCardAdapter.HistoryCardListHolder>
{

    public Context mContext;
    private ArrayList<HistoryCard> mHistoryCards;

    public HistoryCardAdapter()
    {

    }

    public HistoryCardAdapter(Context context, ArrayList<HistoryCard> HistoryCards)
    {
        this.mContext = context;
        this.mHistoryCards = HistoryCards;
    }

    public HistoryCardAdapter(Context context)
    {
        this.mContext = context;
    }

    @Override
    public HistoryCardListHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_history_card, null);
        HistoryCardListHolder viewHolder = new HistoryCardListHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final HistoryCardListHolder holder, final int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final HistoryCard item = mHistoryCards.get(position);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold=App.getSansationBoldFont();
        holder.open.setTypeface(bold);
        holder.open.setText(String.valueOf(item.open));
        holder.revisit.setTypeface(bold);
        holder.revisit.setText(String.valueOf(item.revisit));
        holder.date.setTypeface(regular);
        holder.date.setText(String.valueOf(item.date));
        holder.billcycle.setTypeface(regular);
        holder.billcycle.setText(String.valueOf(item.billcycle));
        holder.route.setTypeface(regular);
        holder.route.setText(String.valueOf(item.route));
        holder.unbill.setTypeface(bold);
        holder.unbill.setText(String.valueOf(item.unbill));

    }

    @Override
    public int getItemCount()
    {
        if(mHistoryCards != null && mHistoryCards.size() > 0)
            return mHistoryCards.size();
        else
            return 0;
    }

    public void setJobCard(ArrayList<HistoryCard> HistoryCards)
    {
        mHistoryCards=HistoryCards;
        notifyDataSetChanged();
    }

    public  class HistoryCardListHolder extends RecyclerView.ViewHolder
    {
        public RelativeLayout rl_history_card;
        public TextView revisit, lbl_revisit, route, open, lbl_open, unbill, lbl_unbill, date, billcycle;

        public HistoryCardListHolder(View itemView)
        {
            super(itemView);
            Typeface regular= App.getSansationRegularFont();
            Typeface bold=App.getSansationBoldFont();
            rl_history_card= (RelativeLayout) itemView.findViewById(R.id.rl_history_card);
            revisit = (TextView) itemView.findViewById(R.id.revisit);
            revisit.setTypeface(bold);
            lbl_revisit = (TextView) itemView.findViewById(R.id.lbl_revisit);
            lbl_revisit.setTypeface(regular);
            route = (TextView) itemView.findViewById(R.id.route);
            route.setTypeface(regular);
            date = (TextView) itemView.findViewById(R.id.date);
            date.setTypeface(regular);
            open = (TextView) itemView.findViewById(R.id.open);
            open.setTypeface(bold);
            lbl_open = (TextView) itemView.findViewById(R.id.lbl_open);
            lbl_open.setTypeface(regular);
            unbill = (TextView) itemView.findViewById(R.id.unbill);
            unbill.setTypeface(bold);
            lbl_unbill = (TextView) itemView.findViewById(R.id.lbl_unbill);
            lbl_unbill.setTypeface(regular);
            billcycle = (TextView) itemView.findViewById(R.id.bill_cycle_code);
            billcycle.setTypeface(regular);

        }
    }
}
