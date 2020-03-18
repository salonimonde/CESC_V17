package com.cesc.mrbd.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.models.Consumer;
import com.cesc.mrbd.models.JobCard;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Bynry01 on 25-08-2016.
 */
public class ConsumerItemAdapter extends RecyclerView.Adapter<ConsumerItemAdapter.ViewHolder>
{

    private ArrayList<Consumer> consumers;
    // Store the context for easy access
    private Context mContext;
    private OnRecycleItemClickListener mListener;
    private Typeface bold;

    // Pass in the contact array into the constructor
    public ConsumerItemAdapter(Context context, ArrayList<Consumer> consumers)
    {
        this.consumers = consumers;
        mContext = context;
    }

    @Override
    public ConsumerItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.cell_reading_card, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        mContext = parent.getContext();

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConsumerItemAdapter.ViewHolder viewHolder, int position)
    {
        viewHolder.bind(consumers.get(position), mListener);
        CommonUtils.setAnimation(viewHolder.itemView, position, -1, mContext);
        bold = App.getSansationRegularFont();

        viewHolder.txtConsumerName.setTypeface(bold);
        viewHolder.txtConsumerId.setTypeface(bold);
        viewHolder.txtMeterNo.setTypeface(bold);
        viewHolder.txtReading.setTypeface(bold);

        viewHolder.lblConsumerName.setTypeface(bold);
        viewHolder.lblConsumerId.setTypeface(bold);
        viewHolder.lblMeterNo.setTypeface(bold);
        viewHolder.lblReading.setTypeface(bold);
    }

    @Override
    public int getItemCount()
    {
        if (consumers != null)
            return consumers.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {

        private TextView txtConsumerName, txtConsumerId, txtMeterNo, txtReading;
        private TextView lblConsumerName, lblConsumerId, lblMeterNo, lblReading;

        public ViewHolder(View itemView)
        {
            super(itemView);

            txtConsumerName = (TextView) itemView.findViewById(R.id.consumerName);
            txtConsumerId = (TextView) itemView.findViewById(R.id.txt_consumer_no);
            txtMeterNo = (TextView) itemView.findViewById(R.id.txt_reading);
            txtReading = (TextView) itemView.findViewById(R.id.txt_reading_take);

            lblConsumerName = (TextView) itemView.findViewById(R.id.lbl_consumer);
            lblConsumerId = (TextView) itemView.findViewById(R.id.lbl_consumerno);
            lblConsumerId.setText("Consumer No");
            lblMeterNo = (TextView) itemView.findViewById(R.id.lbl_meternno);
            lblReading = (TextView) itemView.findViewById(R.id.lbl_duedate);

        }

        public void bind(final Consumer consumer, final OnRecycleItemClickListener listener) {
            txtConsumerName.setText(consumer.consumer_name);
            txtConsumerId.setText(consumer.consumer_no);
            txtMeterNo.setText(consumer.current_meter_reading);
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date1 = null;
            try {
                date1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(consumer.reading_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            txtReading.setText(dateFormat.format(date1));
        }
    }

    public interface OnRecycleItemClickListener
    {
        void onItemClick(JobCard jobCard);
    }
}
