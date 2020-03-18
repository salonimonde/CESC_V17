package com.cesc.mrbd.services;

import android.util.Log;

import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.NotificationCard;
import com.cesc.mrbd.utils.AppPreferences;
import com.cesc.mrbd.utils.CommonUtils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by Piyush on 25-02-2017.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService
{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage)
    {
        super.onMessageReceived(remoteMessage);
        NotificationCard notificationCard = new NotificationCard();
        notificationCard.message = remoteMessage.getNotification().getBody();
        notificationCard.title = remoteMessage.getNotification().getTitle();
        notificationCard.date = CommonUtils.getCurrentDate();
        notificationCard.is_read = "false";
        notificationCard.meter_reader_id = AppPreferences.getInstance(this).getString(AppConstants.METER_READER_ID,"");
        DatabaseManager.saveNotification(this, notificationCard);
    }
}
