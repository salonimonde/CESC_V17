package com.cesc.mrbd.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesc.mrbd.R;
import com.cesc.mrbd.activity.LoginActivity;
import com.cesc.mrbd.configuration.AppConstants;

/**
 * Created by Bynry01 on 23-08-2016.
 */
public class DialogCreator
{

    //Dialog without Sub-title start Piyush : 02-03-17
    public static void showMessageDialog(Context mContext, String message, String mImageDisplay)
    {
//        CommonUtils.alertTone(mContext, R.raw.ping);
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_without_title, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();
        ImageView imageView = (ImageView) promptView.findViewById(R.id.image_view);
        if(mImageDisplay.equals("error"))
        {
            imageView.setImageResource(R.drawable.high_importance);
        }
        else
        {
            imageView.setImageResource(R.drawable.checked_green);
        }
        TextView msg = (TextView) promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.show();
    }
    //Dialog without Sub-title ends Piyush : 02-03-17


    //Dialog with Sub-title start Piyush : 02-03-17
    public static void showExitDialog(final Activity activity, String title, String message, final String screenName)
    {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View promptView = layoutInflater.inflate(R.layout.dialog_with_title, null);
        final AlertDialog alert = new AlertDialog.Builder(activity).create();
        TextView t= (TextView) promptView.findViewById(R.id.tv_title);
        t.setTypeface(regular);
        t.setText(title);
        TextView msg = (TextView) promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button yes = (Button) promptView.findViewById(R.id.btn_yes);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                CommonUtils.deleteCache(activity);
                activity.finish();
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_NO_HISTORY);
                activity.startActivity(a);
                AppPreferences.getInstance(activity).putString(AppConstants.SCREEN_FROM_EXIT, screenName);
                alert.dismiss();
            }
        });

        Button no = (Button) promptView.findViewById(R.id.btn_no);
        no.setTypeface(regular);
        no.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.show();
    }
    //Dialog with Sub-title ends Piyush : 02-03-17

    //Dialog for Logout start Piyush : 02-03-17
    public static void showLogoutDialog(final Activity activity, String title, String message)
    {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View promptView = layoutInflater.inflate(R.layout.dialog_with_title, null);
        final AlertDialog alert = new AlertDialog.Builder(activity).create();
        TextView t= (TextView) promptView.findViewById(R.id.tv_title);
        t.setTypeface(regular);
        t.setText(title);
        TextView msg = (TextView) promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button yes = (Button) promptView.findViewById(R.id.btn_yes);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                CommonUtils.logout(activity);
                Intent in = new Intent(activity, LoginActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(in);
                activity.finish();
//                SharedPreferences settings = activity.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//                settings.edit().clear().commit();
                alert.dismiss();
            }
        });

        Button no = (Button) promptView.findViewById(R.id.btn_no);
        no.setTypeface(regular);
        no.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                alert.dismiss();
            }
        });
        alert.setView(promptView);
        alert.show();
    }
    //Dialog for Logout ends Piyush : 02-03-17

    //Dialog for Consumer Dialog start Piyush : 04-03-17
    public static void showConsumerDetailsDialog(final Context context, String consumerName, String consumerAddress, String consumerNumber, String consumerMeterNo, String cycle, String subDiv, String consumerMobileNo, String consumerBinderNo, int value)
    {
//        CommonUtils.alertTone(context, R.raw.ping);
        Typeface regular = App.getSansationRegularFont();
        Typeface bold = App.getSansationBoldFont();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.dialog_consumer_details, null);
        final AlertDialog alert = new AlertDialog.Builder(context).create();

        TextView lblConsumerMeterNo, lblConsumerNumber, lblCycle, lblSubDiv, lblMobile, lblBinder;
        TextView txtConsumerName, txtConsumerAddress, txtConsumerMeterNo, txtConsumerNumber, txtsubdiv, txtcycle, txtbinderNo, txtmobileNo;
        LinearLayout linearLayoutMobile;
        //Initialising all labels starts
        linearLayoutMobile = (LinearLayout) promptView.findViewById(R.id.linear_mobile);
        lblConsumerNumber = (TextView) promptView.findViewById(R.id.lbl_consumer_no);
        lblConsumerNumber.setTypeface(bold);
        lblConsumerMeterNo = (TextView) promptView.findViewById(R.id.lbl_meter_no);
        lblConsumerMeterNo.setTypeface(bold);
        lblCycle = (TextView) promptView.findViewById(R.id.lbl_cycle);
        lblCycle.setTypeface(bold);
        lblSubDiv = (TextView) promptView.findViewById(R.id.lbl_subdiv);
        lblSubDiv.setTypeface(bold);
        lblMobile = (TextView) promptView.findViewById(R.id.lbl_mobile);
        lblMobile.setTypeface(bold);
        lblBinder = (TextView) promptView.findViewById(R.id.lbl_binder_no);
        lblBinder.setTypeface(bold);
        //Initialising all labels ends

        //Initialising all text starts
        txtConsumerName = (TextView) promptView.findViewById(R.id.txt_consumer_name);
        txtConsumerName.setTypeface(bold);
        txtConsumerAddress = (TextView) promptView.findViewById(R.id.txt_consumer_address);
        txtConsumerAddress.setTypeface(regular);
        txtConsumerNumber = (TextView) promptView.findViewById(R.id.txt_consumer_no);
        txtConsumerNumber.setTypeface(regular);
        txtConsumerMeterNo = (TextView) promptView.findViewById(R.id.txt_meter_no);
        txtConsumerMeterNo.setTypeface(regular);
        txtcycle = (TextView) promptView.findViewById(R.id.txt_cycle);
        txtcycle.setTypeface(regular);
        txtsubdiv = (TextView) promptView.findViewById(R.id.txt_subdiv);
        txtsubdiv.setTypeface(regular);
        txtbinderNo = (TextView) promptView.findViewById(R.id.txt_binder_no);
        txtbinderNo.setTypeface(regular);
        txtmobileNo = (TextView) promptView.findViewById(R.id.txt_mobile_no);
        txtmobileNo.setTypeface(regular);
        //Initialising all text end

        //Setting values to the text starts
        txtConsumerName.setText(consumerName);
        txtConsumerAddress.setText(consumerAddress);
        txtConsumerNumber.setText(consumerNumber);
        txtConsumerMeterNo.setText(consumerMeterNo);
        txtcycle.setText(cycle);
        txtsubdiv.setText(subDiv);
        txtbinderNo.setText(consumerBinderNo);
        txtmobileNo.setText(consumerMobileNo);
        //Setting values to the text ends

        if(value == 1)
        {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.MATCH_PARENT, 0f);
            linearLayoutMobile.setLayoutParams(param);
            lblConsumerMeterNo.setText(CommonUtils.getString(context, R.string.sub_division_name));
            lblSubDiv.setText(CommonUtils.getString(context, R.string.binder));
            lblCycle.setText(CommonUtils.getString(context, R.string.mobile_number));
            lblBinder.setText(CommonUtils.getString(context, R.string.dc_notice_no));
            lblMobile.setVisibility(View.GONE);
            txtmobileNo.setVisibility(View.GONE);
        }

        //OK button code starts
        Button yes = (Button) promptView.findViewById(R.id.btn_ok);
        yes.setTypeface(regular);
        yes.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                alert.dismiss();
            }
        });

        alert.setView(promptView);
        alert.show();
        //OK button code ends
    }
    //Dialog for Consumer Dialog ends Piyush : 04-03-17

    public static void showUpdateDialog(final Context mContext, String message, final String link)
    {
        Typeface regular = App.getSansationRegularFont();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.dialog_without_title, null);
        final AlertDialog alert = new AlertDialog.Builder(mContext).create();
        ImageView imageView = (ImageView) promptView.findViewById(R.id.image_view);
        imageView.setImageResource(R.drawable.checked_green);
        TextView msg = (TextView) promptView.findViewById(R.id.tv_msg);
        msg.setTypeface(regular);
        msg.setText(message);
        Button ok = (Button) promptView.findViewById(R.id.btn_ok);
        ok.setTypeface(regular);
        ok.setText("Update");
        ok.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try {
                    Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    mContext.startActivity(myIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alert.setView(promptView);
        alert.setCancelable(false);
        alert.show();
    }

}
