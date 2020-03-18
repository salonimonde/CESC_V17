package com.cesc.mrbd.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.cesc.mrbd.R;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.tables.DisconnectionHistoryTable;
import com.cesc.mrbd.db.tables.UploadBillHistoryTable;
import com.cesc.mrbd.db.tables.UploadsHistoryTable;
import com.cesc.mrbd.preferences.SharedPrefManager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * Created by Bynry01 on 22-08-2016.
 */
public class CommonUtils {

    public static void hideKeyBoard(Context context) {
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Here if condition check for WiFi and Mobile network is available or not.
     * If anyone of them is available or connected then it will return true,
     * otherwise false.
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvaliable(Context context) {
        final ConnectivityManager conn_manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo network_info = conn_manager.getActiveNetworkInfo();

        if (network_info != null && network_info.isConnected()) {
            if (network_info.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
            else if (network_info.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }

        return false;
    }

    public static void saveCredentials(Context context, String email, String authToken) {
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        SharedPrefManager.saveValue(context, SharedPrefManager.USER_ID, email);
        SharedPrefManager.saveValue(context, SharedPrefManager.AUTH_TOKEN, authToken);
        SharedPrefManager.saveValue(context, SharedPrefManager.USER_LOGGED_IN_DATE, date);
    }

    public static void saveAuthToken(Context context, String authToken) {
        SharedPrefManager.saveValue(context, SharedPrefManager.AUTH_TOKEN, authToken);
    }

    public static String getBitmapEncodedString(Bitmap pBitmap) {
        if (pBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pBitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream);
            byte[] byteArray = stream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        return "";
    }


    public static boolean isLoggedIn(Context context) {
        String logged_in_date = SharedPrefManager.getStringValue(context, SharedPrefManager.USER_LOGGED_IN_DATE);
        if (!logged_in_date.equals("")) {
            String current_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            if (logged_in_date.equals(current_date)) {
                String uname = SharedPrefManager.getStringValue(context, SharedPrefManager.USER_ID);
                // String password = SharedPrefManager.getStringValue(context, SharedPrefManager.PASSWORD);
                return !(uname.equals("")/* && password.equals("")*/);
            }
        }
        return false;
    }

    public static int getColor(Context context, int id) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 23) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static void askForPermissions(final Context context, RelativeLayout ll_main_view, ArrayList<String> permissions) {
        ArrayList<String> permissionsToRequest = findUnAskedPermissions(context, App.getInstance().permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.
        final ArrayList<String> permissionsRejected = findRejectedPermissions(context, App.getInstance().permissions);

        if (permissionsToRequest.size() > 0) {
            //we need to ask for permissions
            //but have we already asked for them?

            ((Activity) context).requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), AppConstants.ALL_PERMISSIONS_RESULT);

            //mark all these as asked..
            for (String perm : permissionsToRequest) {
                markAsAsked(context, perm);
            }
        } else {
            if (permissionsRejected.size() > 0) {
                //we have none to request but some previously rejected..tell the user.
                //It may be better to show a dialog here in a prod application
                showPostPermissionsShackBar(context, ll_main_view, permissionsRejected);
            }
        }
    }

    public static void showPostPermissionsShackBar(final Context context, RelativeLayout ll_mail_view, final ArrayList<String> permissionsRejected) {
        Snackbar snackBarView = Snackbar
                .make(ll_mail_view, String.valueOf(permissionsRejected.size()) + context.getString(R.string.permission_rejected_already), Snackbar.LENGTH_LONG)
                .setAction(R.string.allow_to_ask_permission_again, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (String perm : permissionsRejected) {
                            clearMarkAsAsked(context, perm);
                        }
                    }
                });
        ViewGroup group = (ViewGroup) snackBarView.getView();
        group.setBackgroundColor(getColor(context, R.color.colorPrimary));
        snackBarView.show();
    }


    /**
     * method that will return whether the permission is accepted. By default it is true if the user is using a device below
     * version 23
     *
     * @param context
     * @param permission
     * @return
     */

    public static boolean hasPermission(Context context, String permission) {
        if (canMakeSmores()) {
            return (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED);
        }
        return true;
    }


    /**
     * we will save that we have already asked the user
     *
     * @param permission
     */
    public static void markAsAsked(Context context, String permission) {
        SharedPrefManager.saveValue(context, permission, false);
    }

    /**
     * We may want to ask the user again at their request.. Let's clear the
     * marked as seen preference for that permission.
     *
     * @param permission
     */
    public static void clearMarkAsAsked(Context context, String permission) {
        SharedPrefManager.saveValue(context, permission, true);
    }


    /**
     * This method is used to determine the permissions we do not have accepted yet and ones that we have not already
     * bugged the user about.  This comes in handle when you are asking for multiple permissions at once.
     *
     * @param wanted
     * @return
     */
    public static ArrayList<String> findUnAskedPermissions(Context context, ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(context, perm) && SharedPrefManager.shouldWeAskPermission(context, perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    /**
     * this will return us all the permissions we have previously asked for but
     * currently do not have permission to use. This may be because they declined us
     * or later revoked our permission. This becomes useful when you want to tell the user
     * what permissions they declined and why they cannot use a feature.
     *
     * @param wanted
     * @return
     */
    public static ArrayList<String> findRejectedPermissions(Context context, ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(context, perm) && !SharedPrefManager.shouldWeAskPermission(context, perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    /**
     * Just a check to see if we have marshmallows (version 23)
     *
     * @return
     */
    private static boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    /**
     * this method use to  delete data of login information
     */

    public static void logout(Context context) {
        SharedPrefManager.saveValue(context, SharedPrefManager.USER_NAME, "");
        SharedPrefManager.saveValue(context, SharedPrefManager.PASSWORD, "");
        SharedPrefManager.saveValue(context, SharedPrefManager.USER_LOGGED_IN_DATE, "");
    }

    public static Bitmap getScaledBitmap(Context context, String filePath, float maxWidth, float maxHeight) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

        //by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
        //you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        if (bmp == null) {
            InputStream inputStream = null;
            try {
                inputStream = new FileInputStream(filePath);
                BitmapFactory.decodeStream(inputStream, null, options);
                inputStream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

        float imgRatio = (float) actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

        //width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        //setting inSampleSize value allows to load a scaled down version of the original image
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

        //inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

        //this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            //load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
            if (bmp == null) {

                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(filePath);
                    BitmapFactory.decodeStream(inputStream, null, options);
                    inputStream.close();
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        //check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return scaledBitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public static String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getPreviousDate(int prev) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -prev);
        Date newDate = calendar.getTime();
        return dateFormat.format(newDate);
    }

    public static String getPreviousDateCondition(String mr) {

        StringBuilder condition = new StringBuilder(UploadsHistoryTable.Cols.METER_READER_ID + "='" + mr + "' AND " + UploadsHistoryTable.Cols.READING_DATE + " NOT IN (");

        for (int i = 0; i < AppConstants.UPLOAD_HISTORY_DATE_COUNT; i++) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            Date newDate = calendar.getTime();
            if (i + 1 == AppConstants.UPLOAD_HISTORY_DATE_COUNT) {
                condition.append("'" + dateFormat.format(newDate) + "')");
            } else {
                condition.append("'" + dateFormat.format(newDate) + "',");
            }
        }

        return String.valueOf(condition);
    }

    public static String getPreviousDateConditionbill(String mr) {

        StringBuilder condition = new StringBuilder(UploadBillHistoryTable.Cols.METER_READER_ID + "='" + mr + "' AND " + UploadBillHistoryTable.Cols.READING_DATE + " NOT IN (");

        for (int i = 0; i < AppConstants.UPLOAD_BILL_HISTORY_DATE_COUNT; i++) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            Date newDate = calendar.getTime();
            if (i + 1 == AppConstants.UPLOAD_BILL_HISTORY_DATE_COUNT) {
                condition.append("'" + dateFormat.format(newDate) + "')");
            } else {
                condition.append("'" + dateFormat.format(newDate) + "',");
            }
        }

        return String.valueOf(condition);
    }

    public static String getPreviousDateConditionDCNotice(String mr) {

        StringBuilder condition = new StringBuilder(DisconnectionHistoryTable.Cols.METER_READER_ID + "='" + mr + "' AND " + DisconnectionHistoryTable.Cols.DATE + " NOT IN (");

        for (int i = 0; i < AppConstants.UPLOAD_BILL_HISTORY_DATE_COUNT; i++) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);
            Date newDate = calendar.getTime();
            if (i + 1 == AppConstants.UPLOAD_BILL_HISTORY_DATE_COUNT) {
                condition.append("'" + dateFormat.format(newDate) + "')");
            } else {
                condition.append("'" + dateFormat.format(newDate) + "',");
            }
        }

        return String.valueOf(condition);
    }

    public static Bitmap addWaterMarkDate(Bitmap src, String watermark) {
        int w = src.getWidth();
        int h = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);

        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        //paint.setAlpha(alpha);
        paint.setTextSize(20);
        paint.setAntiAlias(true);
        paint.setUnderlineText(false);
        canvas.drawText(watermark, 5, h - 5, paint);

        return result;
    }

    public static int sizeOfBitmap(Bitmap pBitmap) {
        if (pBitmap != null)
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                return pBitmap.getRowBytes() * pBitmap.getHeight();
            } else {
                return pBitmap.getByteCount();
            }
        return 0;
    }

    public static boolean checkAndRequestPermissions(Context mContext, Activity activity) {
        int permissionSendMessage = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.SEND_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.SEND_SMS);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), AppConstants.REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public static void sendSMS(String phoneNo, String sendMessage) {
        //Your authentication key
        String authkey = "100303Ag1chIxFw5673aa1d";
        //Multiple mobiles numbers separated by comma
        //String mobiles = "9595092582";
        //Sender ID,While using route4 sender id should be 6 characters long.
        String senderId = "Muhurt";
        //define txtBinderCode
        String route = "4";

        URLConnection myURLConnection = null;
        URL myURL = null;
        BufferedReader reader = null;

        //encoding message
        //String encoded_message = URLEncoder.encode(message);

        //Send SMS API   https://control.msg91.com/api/sendhttp.php
        String mainUrl = "https://control.msg91.com/api/sendhttp.php?";

        //Prepare parameter string
        StringBuilder sbPostData = new StringBuilder(mainUrl);
        sbPostData.append("authkey=" + authkey);
        sbPostData.append("&mobiles=" + phoneNo);
        sbPostData.append("&message=" + sendMessage);
        sbPostData.append("&txtBinderCode=" + route);
        sbPostData.append("&sender=" + senderId);

        //final string
        mainUrl = sbPostData.toString();
        try {
            //prepare connection
            myURL = new URL(mainUrl);
            myURLConnection = myURL.openConnection();
            myURLConnection.connect();
            reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

            //reading response
            String response;
            while ((response = reader.readLine()) != null)
                //print response
//                Log.i("Response", "Response of msg91" + response);

                //finally close connection
                reader.close();
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static void setAnimation(View viewToAnimate, int position, int lastPosition, Context mContext) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.push_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    /**
     * Return a localized string from the application's package's
     * default string table.
     *
     * @param resId Resource id for the string
     */
    public static final String getString(Context context, @StringRes int resId) {
        return context.getResources().getString(resId);
    }

    public static final void alertTone(Context mContext, int tone) {
//        int warnTime = 800;
//        final MediaPlayer mediaPlayer;
//
////        ((Vibrator) mContext.getSystemService(VIBRATOR_SERVICE)).vibrate(warnTime);
////        mediaPlayer = MediaPlayer.create(mContext, tone);
////        mediaPlayer.start();
//
//        Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                mediaPlayer.release();
//            }
//        };
//        handler.postDelayed(runnable, warnTime);
    }

    @NonNull
    public static String timeTaken(String startDate, String endDate) {
        //milliseconds
        Date mendDate = null, mstartDate = null;

        try {
            mstartDate = new SimpleDateFormat("dd/M/yyyy hh:mm:ss").parse(startDate);
            mendDate = new SimpleDateFormat("dd/M/yyyy hh:mm:ss").parse(endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long different = mendDate.getTime() - mstartDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        /*System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);*/
        if (elapsedHours != 0)
            return elapsedHours + " hr  " + elapsedMinutes + " min " + elapsedSeconds + " sec";
        else
            return elapsedMinutes + " min " + elapsedSeconds + " sec";
    }
}
