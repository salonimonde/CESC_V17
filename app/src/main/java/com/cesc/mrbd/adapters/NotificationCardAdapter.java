package com.cesc.mrbd.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cesc.mrbd.R;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.hepler.ItemTouchHelperAdapter;
import com.cesc.mrbd.models.NotificationCard;
import com.cesc.mrbd.utils.App;
import com.cesc.mrbd.utils.CommonUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Bynry01 on 10/10/2016.
 */
public class NotificationCardAdapter extends RecyclerView.Adapter<NotificationCardAdapter.NotificationCardHolder>
        implements ItemTouchHelperAdapter
{

    public Context mContext;
    private ArrayList<NotificationCard> mNotificationCard;

    public NotificationCardAdapter()
    {}

    public NotificationCardAdapter(Context context, ArrayList<NotificationCard> NotificationCards)
    {
        this.mContext = context;
        this.mNotificationCard = NotificationCards;

    }

    public NotificationCardAdapter(Context context)
    {
        this.mContext = context;
    }

    @Override
    public NotificationCardHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_notification_card, null);
        NotificationCardHolder viewHolder = new NotificationCardHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final NotificationCardHolder holder, final int position)
    {
        CommonUtils.setAnimation(holder.itemView, position, -1, mContext);
        final NotificationCard item = mNotificationCard.get(position);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold=App.getSansationBoldFont();
        holder.date.setTypeface(bold);
        holder.date.setText(String.valueOf(item.date));
        holder.msg.setTypeface(regular);
        holder.msg.setText(String.valueOf(item.message));
        holder.title.setText(String.valueOf(item.title));

    }

    @Override
    public int getItemCount()
    {
        if(mNotificationCard != null && mNotificationCard.size() > 0)
            return mNotificationCard.size();
        else
            return 0;
    }

    public void setJobCard(ArrayList<NotificationCard> NotificationCards)
    {
        mNotificationCard = NotificationCards;
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition)
    {
        Collections.swap(mNotificationCard, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position)
    {
        DatabaseManager.deleteAccount(mContext, mNotificationCard.get(position).message);
        mNotificationCard.remove(position);
        notifyItemRemoved(position);
    }

    public  class NotificationCardHolder extends RecyclerView.ViewHolder
    {
        public RelativeLayout rl_notification_card;
        public TextView msg, date, title;
        public LinearLayout card;

        public NotificationCardHolder(View itemView)
        {
            super(itemView);
            Typeface regular= App.getSansationRegularFont();
            Typeface bold=App.getSansationBoldFont();
            rl_notification_card=(RelativeLayout)itemView.findViewById(R.id.rl_notification_card);
            msg = (TextView) itemView.findViewById(R.id.tv_notifications);
            msg.setTypeface(regular);
            date=(TextView)itemView.findViewById(R.id.tv_date);
            date.setTypeface(bold);
            title = (TextView)itemView.findViewById(R.id.tv_notifications_title);
            title.setTypeface(bold);
            card = (LinearLayout)itemView.findViewById(R.id.card);
        }

        public void bind(final Context context, final NotificationCard notificationCard)
        {
            msg.setText(notificationCard.message);
            date.setText(notificationCard.date);
            title.setText(notificationCard.title);
            card.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    DatabaseManager.setReadNotification(mContext, notificationCard.title);
                }
            });

        }
    }
}



