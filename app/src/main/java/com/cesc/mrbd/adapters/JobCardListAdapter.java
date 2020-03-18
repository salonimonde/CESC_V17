package com.cesc.mrbd.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.models.JobCard;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Bynry01 on 18-02-2016.
 */
public class JobCardListAdapter extends RecyclerView.Adapter<JobCardListAdapter.JobCardListHolder> {

    private OnJobCardClickListener mListener;
    public Context context;
    private ArrayList<JobCard> mJobCards;

    public JobCardListAdapter(Context context, ArrayList<JobCard> jobCards, OnJobCardClickListener listener) {
        this.context = context;
        this.mJobCards = jobCards;
        this.mListener = listener;
    }

    @Override
    public JobCardListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_job_card, null);
        JobCardListHolder viewHolder = new JobCardListHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final JobCardListHolder holder, final int position) {
        CommonUtils.setAnimation(holder.itemView, position, -1, context);
        final JobCard item = mJobCards.get(position);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        holder.mConsumerName.setTypeface(regular);
        holder.mConsumerName.setText(item.consumer_name);
        holder.mConsumerID.setTypeface(bold);
       /* holder.ConsumerName.setText("Consumer Name"+"  "+item.attempt+" ");
        holder.ConsumerName.setTypeface(bold);*/
        holder.mConsumerID.setText(item.account_no);
        holder.mScheduleEndDate.setTypeface(bold);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        long different = 0;
        String date1;
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(item.assigned_date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, 2);  // number of days to add
        date1 = sdf.format(c.getTime());  // dt is now the new date
        try {
            Date mendDate = new SimpleDateFormat("yyyy-MM-dd").parse(item.schedule_end_date);
            Date mstartDate = new SimpleDateFormat("yyyy-MM-dd").parse(date1);
            different = mendDate.getTime() - mstartDate.getTime();
//            Log.i("fdfjvfvnfjvfnf" + different, "ccnsacuaniiii    " + Long.signum(different));

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Long.signum(different) == 1)
            holder.mScheduleEndDate.setText(date1);
        else if (Long.signum(different) == 0)
            holder.mScheduleEndDate.setText(item.schedule_end_date);
        else
            holder.mScheduleEndDate.setText(item.schedule_end_date);
        holder.mMeterID.setTypeface(bold);
        holder.mMeterID.setText(item.meter_no);

        if (!item.attempt.equalsIgnoreCase(""))
            holder.rl_job_card_cellbg.setBackgroundColor(CommonUtils.getColor(context, R.color.colorText11));

        if (!item.attempt.equalsIgnoreCase("")) {
            holder.mattempt.setTypeface(bold);
            holder.mattempt.setText(item.attempt);
        }
        holder.rl_job_card_cell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onJobCardClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mJobCards != null && mJobCards.size() > 0)
            return mJobCards.size();
        else
            return 0;
    }

    public void setJobCard(ArrayList<JobCard> jobcard) {
        mJobCards = jobcard;
        notifyDataSetChanged();
    }

    public interface OnJobCardClickListener {
        void onJobCardClick(JobCard jobCard);
    }

    public class JobCardListHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rl_job_card_cell;
        public LinearLayout rl_job_card_cellbg;
        public TextView mattempt, mConsumerName, mScheduleEndDate, mConsumerID, mMeterID, ConsumerName, ScheduleEndDate, ConsumerID, MeterID;

        public JobCardListHolder(View itemView) {
            super(itemView);
            rl_job_card_cell = (RelativeLayout) itemView.findViewById(R.id.rl_job_card_cell);
            rl_job_card_cellbg = (LinearLayout) itemView.findViewById(R.id.rl_job_card_cell_bg);
            mConsumerName = (TextView) itemView.findViewById(R.id.consumerName);
            mConsumerID = (TextView) itemView.findViewById(R.id.consumerId);
            mScheduleEndDate = (TextView) itemView.findViewById(R.id.scheduleEnDate);
            mMeterID = (TextView) itemView.findViewById(R.id.meterID);
            mattempt = (TextView) itemView.findViewById(R.id.lbl_attempt);
            Typeface regular = App.getSansationRegularFont();
            ConsumerName = (TextView) itemView.findViewById(R.id.lbl_consumer);
            ConsumerName.setTypeface(regular);
            ConsumerID = (TextView) itemView.findViewById(R.id.lbl_consumerno);
            ConsumerID.setTypeface(regular);
            ScheduleEndDate = (TextView) itemView.findViewById(R.id.lbl_duedate);
            ScheduleEndDate.setTypeface(regular);
            MeterID = (TextView) itemView.findViewById(R.id.lbl_meternno);
            MeterID.setTypeface(regular);
        }
    }
}