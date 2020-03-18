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
import com.cesc.mrbd.models.JobCard;
import com.cesc.mrbd.models.MeterReading;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Piyush on 22-03-2017.
 * Bynry
 */
public class ReadingCardListAdapter extends RecyclerView.Adapter<ReadingCardListAdapter.ReadingCardListHolder> {
    public Context mContext;
    private ArrayList<MeterReading> mReadingCards;
    private ArrayList<JobCard> mJobCard;

    public ReadingCardListAdapter(Context context, ArrayList<MeterReading> readingCard, ArrayList<JobCard> jobCards) {
        this.mContext = context;
        this.mReadingCards = readingCard;
        this.mJobCard = jobCards;
    }

    @Override
    public ReadingCardListAdapter.ReadingCardListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_reading_card, null);
        ReadingCardListAdapter.ReadingCardListHolder viewHolder = new ReadingCardListAdapter.ReadingCardListHolder(view);
        mContext = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ReadingCardListAdapter.ReadingCardListHolder holder, int position) {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        try {

            final MeterReading meterReadingItem = mReadingCards.get(position);
            final JobCard jobCardItem = mJobCard.get(position);
            Typeface regular = App.getSansationRegularFont();
            Typeface bold = App.getSansationBoldFont();
            holder.mConsumerName.setTypeface(regular);
            holder.mConsumerName.setText(jobCardItem.consumer_name);
            holder.mConsumerID.setTypeface(bold);
            holder.mConsumerID.setText(jobCardItem.account_no);
            holder.mScheduleEndDate.setTypeface(bold);
            /*int index = meterReadingItem.reading_date.indexOf(" ");
            if(index == -1)
            {}
            else
            {
                String date = meterReadingItem.reading_date.substring(0, index);
                holder.mScheduleEndDate.setText(date);
            }*/

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(meterReadingItem.reading_date);
            holder.mScheduleEndDate.setText(dateFormat.format(date1));
            holder.mMeterID.setTypeface(bold);
            holder.mMeterID.setText(meterReadingItem.current_meter_reading);
//            holder.mMeterID.setText(meterReadingItem.cur_lat+"  "+meterReadingItem.cur_lng);
        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        if (mJobCard != null && mJobCard.size() > 0)
            return mJobCard.size();
        else
            return 0;
    }

    public class ReadingCardListHolder extends RecyclerView.ViewHolder {
        public RelativeLayout rl_job_card_cell;
        public TextView mConsumerName, mScheduleEndDate, mConsumerID, mMeterID, ConsumerName, ReadingDate, ConsumerID, MeterID;

        public ReadingCardListHolder(View itemView) {
            super(itemView);
            rl_job_card_cell = (RelativeLayout) itemView.findViewById(R.id.rl_job_card_cell);
            mConsumerName = (TextView) itemView.findViewById(R.id.consumerName);
            mConsumerID = (TextView) itemView.findViewById(R.id.txt_consumer_no);
            mScheduleEndDate = (TextView) itemView.findViewById(R.id.txt_reading_take);
            mMeterID = (TextView) itemView.findViewById(R.id.txt_reading);
            Typeface regular = App.getSansationRegularFont();
            ConsumerName = (TextView) itemView.findViewById(R.id.lbl_consumer);
            ConsumerName.setTypeface(regular);
            ConsumerID = (TextView) itemView.findViewById(R.id.lbl_consumerno);
            ConsumerID.setTypeface(regular);
            ReadingDate = (TextView) itemView.findViewById(R.id.lbl_duedate);
            ReadingDate.setTypeface(regular);
            MeterID = (TextView) itemView.findViewById(R.id.lbl_meternno);
            MeterID.setTypeface(regular);
        }
    }
}
