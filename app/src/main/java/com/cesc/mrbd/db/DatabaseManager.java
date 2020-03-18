package com.cesc.mrbd.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.cesc.mrbd.R;
import com.cesc.mrbd.activity.LoginActivity;
import com.cesc.mrbd.configuration.AppConstants;
import com.cesc.mrbd.db.tables.BillTable;
import com.cesc.mrbd.db.tables.ConsumerTable;
import com.cesc.mrbd.db.tables.DisconnectionHistoryTable;
import com.cesc.mrbd.db.tables.DisconnectionTable;
import com.cesc.mrbd.db.tables.JobCardTable;
import com.cesc.mrbd.db.tables.LoginTable;
import com.cesc.mrbd.db.tables.MeterReadingTable;
import com.cesc.mrbd.db.tables.NotificationTable;
import com.cesc.mrbd.db.tables.SequenceTable;
import com.cesc.mrbd.db.tables.UploadBillHistoryTable;
import com.cesc.mrbd.db.tables.UploadDisconnectionTable;
import com.cesc.mrbd.db.tables.UploadsHistoryTable;
import com.cesc.mrbd.db.tables.UserProfileTable;
import com.cesc.mrbd.models.BillCard;
import com.cesc.mrbd.models.Consumer;
import com.cesc.mrbd.models.Disconnection;
import com.cesc.mrbd.models.DisconnectionHistory;
import com.cesc.mrbd.models.HistoryCard;
import com.cesc.mrbd.models.JobCard;
import com.cesc.mrbd.models.MeterImage;
import com.cesc.mrbd.models.MeterReading;
import com.cesc.mrbd.models.NotificationCard;
import com.cesc.mrbd.models.PendingCount;
import com.cesc.mrbd.models.Sequence;
import com.cesc.mrbd.models.SummaryCard;
import com.cesc.mrbd.models.SummaryCount;
import com.cesc.mrbd.models.UploadBillHistory;
import com.cesc.mrbd.models.UploadDisconnectionNotices;
import com.cesc.mrbd.models.UploadsHistory;
import com.cesc.mrbd.models.User;
import com.cesc.mrbd.models.UserProfile;
import com.cesc.mrbd.utils.CommonUtils;
import com.cesc.mrbd.utils.LocationManagerReceiver;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * This class acts as an interface between database and UI. It contains all the
 * methods to interact with device database.
 *
 * @author Bynry01
 */
public class DatabaseManager {
    /**
     * Save User to UserLogin table
     *
     * @param context Context
     * @param user    User
     */
    public static void saveUser(Context context, User user) {
        if (user != null) {
            ContentValues values = getContentValuesUserLoginTable(context, user);
            String condition = LoginTable.Cols.USER_LOGIN_ID + "='" + user.userid + "'";
            saveValues(context, LoginTable.CONTENT_URI, values, condition);
        }
    }

    public static String getDbPath(Context context) {
        return context.getDatabasePath("CESC.db").getAbsolutePath();
    }

    /**
     * Save User to UserProfileTable
     *
     * @param context
     * @param userProfiles
     */
    private static void saveUserProfile(Context context, String user_email, ArrayList<UserProfile> userProfiles, String fcmToken) {
        if (userProfiles != null && userProfiles.size() > 0) {
            for (UserProfile userProfile : userProfiles) {
                userProfile.email_id = user_email;
                ContentValues values = getContentValuesUserProfileTable(context, userProfile, fcmToken);
                String condition = UserProfileTable.Cols.METER_READER_ID + "='" + userProfile.meter_reader_id + "'";
                saveValues(context, UserProfileTable.CONTENT_URI, values, condition);
                SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                String date = df.format(new Date());
                User user = new User();
                user.userid = user_email;
                user.meter_reader_id = userProfile.meter_reader_id;
                user.login_date = date;
                LocationManagerReceiver receiver = new LocationManagerReceiver(context);
//                LocationReceiver receiver = new LocationReceiver(context);
                //receiver.saveDataToTable();
                receiver.saveLoginDetailsWithLocation(context, user);
            }
        }
    }

    /**
     * Save Consumer to ConsumerTable
     *
     * @param context  Context
     * @param consumer Consumer
     */
    public static void saveUnbillConsumer(Context context, Consumer consumer) {
        if (consumer != null) {
            ContentValues values = getContentValuesConsumerTable(context, consumer);
//            String condition = ConsumerTable.Cols.CONSUMER_ID + "='" + consumer.consumer_no + "'";
//            saveValues(context, ConsumerTable.CONTENT_URI, values, null);
            ContentResolver resolver = context.getContentResolver();
            resolver.insert(ConsumerTable.CONTENT_URI, values);

        }
    }

    public static void saveNewBillConsumer(Context context, BillCard consumer) {
        if (consumer != null) {
            ContentValues values = getContentValuesBillCardTable(context, consumer);
//            String condition = ConsumerTable.Cols.CONSUMER_ID + "='" + consumer.consumer_no + "'";
//            saveValues(context, ConsumerTable.CONTENT_URI, values, null);
            ContentResolver resolver = context.getContentResolver();
            resolver.insert(BillTable.CONTENT_URI, values);

        }
    }


    /**
     * Save UploadsHistory to UploadsHistoryTable
     *
     * @param context        Context
     * @param uploadsHistory uploadsHistory
     */

    public static void saveUploadsHistory(Context context, UploadsHistory uploadsHistory) {
        if (uploadsHistory != null) {
            ContentValues values = getContentValuesUploadsHistoryTable(context, uploadsHistory);
            //Changes for count Error Starts Avinesh:02-03-17
            String condition = UploadsHistoryTable.Cols.CONSUMER_ID + "='" + uploadsHistory.consumer_no + "' AND "
                    + UploadsHistoryTable.Cols.UPLOAD_STATUS + "='" + uploadsHistory.upload_status + "' AND "
                    + UploadsHistoryTable.Cols.METER_READER_ID + "='" + uploadsHistory.meter_reader_id + "'";
            //Changes for count Error Ends Avinesh:02-03-17
            saveValues(context, UploadsHistoryTable.CONTENT_URI, values, condition);
        }
    }

    public static void saveUploadbillHistory(Context context, UploadBillHistory uploadsHistory) {
        if (uploadsHistory != null) {
            ContentValues values = getContentValuesUploadbillHistoryTable(context, uploadsHistory);
            //Changes for count Error Starts Avinesh:02-03-17
            String condition;
            if (uploadsHistory.is_new.equalsIgnoreCase("false")) {
                condition = UploadBillHistoryTable.Cols.JOBCARD_ID + "='" + uploadsHistory.jobcard_id + "' AND "
                        + UploadBillHistoryTable.Cols.METER_READER_ID + "='" + uploadsHistory.meter_reader_id + "'";
                saveValues(context, UploadBillHistoryTable.CONTENT_URI, values, condition);
            } else {
                ContentResolver resolver = context.getContentResolver();
                Cursor cursor = resolver.query(UploadBillHistoryTable.CONTENT_URI, null,
                        null, null, null);
                if (cursor != null && cursor.getCount() > 0) {
                    resolver.insert(UploadBillHistoryTable.CONTENT_URI, values);
                }
                if (cursor != null) {
                    cursor.close();
                }
            }
            //Changes for count Error Ends Avinesh:02-03-17


        }
    }

    private static ContentValues getContentValuesUploadbillHistoryTable(Context context, UploadBillHistory uploadsHistory) {
        ContentValues values = new ContentValues();
        try {
            values.put(UploadBillHistoryTable.Cols.JOBCARD_ID, uploadsHistory.jobcard_id != null ? uploadsHistory.jobcard_id : "");
            values.put(UploadBillHistoryTable.Cols.CONSUMER_NAME, uploadsHistory.consumer_name != null ? uploadsHistory.consumer_name : "");
            values.put(UploadBillHistoryTable.Cols.CONSUMER_NO, uploadsHistory.consumer_no != null ? uploadsHistory.consumer_no : "");
            values.put(UploadBillHistoryTable.Cols.CYCLE_CODE, uploadsHistory.cycle_code != null ? uploadsHistory.cycle_code : "");
            values.put(UploadBillHistoryTable.Cols.BILLMONTH, uploadsHistory.billmonth != null ? uploadsHistory.billmonth : "");
            values.put(UploadBillHistoryTable.Cols.BINDER_CODE, uploadsHistory.binder_code != null ? uploadsHistory.binder_code : "");
            values.put(UploadBillHistoryTable.Cols.READING_DATE, uploadsHistory.reading_date != null ? uploadsHistory.reading_date : "");
            values.put(UploadBillHistoryTable.Cols.METER_READER_ID, uploadsHistory.meter_reader_id != null ? uploadsHistory.meter_reader_id : "");
            values.put(UploadBillHistoryTable.Cols.METER_NO, uploadsHistory.meter_no != null ? uploadsHistory.meter_no : "");
            values.put(UploadBillHistoryTable.Cols.ZONE_CODE, uploadsHistory.zone_code != null ? uploadsHistory.zone_code : "");
            values.put(UploadBillHistoryTable.Cols.ZONE_NAME, uploadsHistory.zone_name != null ? uploadsHistory.zone_name : "");
            values.put(UploadBillHistoryTable.Cols.IS_NEW, uploadsHistory.is_new != null ? uploadsHistory.is_new : "");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }


    /**
     * Save User to JobCardTable
     *
     * @param context Context
     * @param jobCard JobCard
     */
    public static void saveJobCard(Context context, JobCard jobCard) {
        if (jobCard != null) {
            ContentValues values = getContentValuesJobCardTable(context, jobCard);
            String condition = JobCardTable.Cols.JOB_CARD_ID + "='" + jobCard.job_card_id + "'";
            saveValues(context, JobCardTable.CONTENT_URI, values, condition);
        }
    }

    /**
     * Save User to JobCardTable
     *
     * @param context Context
     * @param jobCard JobCard
     */
    public static void saveJobCardStatus(Context context, JobCard jobCard, String status) {
        if (jobCard != null)

        {
            ContentValues values = getContentValuesJobCardTable(context, jobCard);
            values.put(JobCardTable.Cols.JOB_CARD_STATUS, status);
            // values.put(JobCardTable.Cols.IS_REVISIT, "False");
            String condition = JobCardTable.Cols.JOB_CARD_ID + "='" + jobCard.job_card_id + "'";
            saveValues(context, JobCardTable.CONTENT_URI, values, condition);
        }
    }

    public static void saveBillCardStatus(Context context, BillCard billCard, String status) {
        if (billCard != null) {
            ContentValues values = getContentValuesBillCardTable(context, billCard);
            values.put(BillTable.Cols.JOBCARD_STATUS, status);
            String condition = BillTable.Cols.JOBCARD_ID + "='" + billCard.jobcard_id + "'";
            saveValues(context, BillTable.CONTENT_URI, values, condition);
        }
    }

    // Method for Save MeterReading Starts Avinesh:02-03-17
    public static void saveMeterReadingRNT(Context context, MeterReading meterReading) {
        if (meterReading != null) {
            ContentValues values = getContentValuesMeterReadingTable(context, meterReading);
            saveMeterReadingsRNT(context, MeterReadingTable.CONTENT_URI, values, null);
        }
    }


    private static void saveMeterReadingsRNT(Context context, Uri table, ContentValues values, String condition) {
        ContentResolver resolver = context.getContentResolver();
        try {
            resolver.insert(table, values);
        } catch (Exception e) {
            Toast.makeText(context, "Failed to save reading", Toast.LENGTH_LONG).show();
        }
    }
// Method for Save MeterReading Ends Avinesh:02-03-17

    /**
     * Save User to JobCardTable
     *
     * @param context      Context
     * @param meterReading MeterReading
     */
    public static void saveMeterReading(Context context, MeterReading meterReading) {
        if (meterReading != null) {
            ContentValues values = getContentValuesMeterReadingTable(context, meterReading);
            saveMeterReadings(context, MeterReadingTable.CONTENT_URI, values, null);
        }
    }

    private static void saveMeterReadings(Context context, Uri table, ContentValues values, String condition) {
        ContentResolver resolver = context.getContentResolver();
        try {
            resolver.insert(table, values);
            Toast.makeText(context, "Reading punched successfully.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, "Failed to save reading", Toast.LENGTH_LONG).show();
        }
    }

    /* private static void saveuploadbillhistoryReadings(Context context, Uri table, ContentValues values, String condition) {
         try {
 //            resolver.insert(table, values);
             DatabaseHelper dbHelper = new DatabaseHelper(context);
             SQLiteDatabase db = dbHelper.getWritableDatabase();
             long newRowId = db.insert(UploadBillHistoryTable.TABLE_NAME, null, values);
             Log.i("divfdsjnijdnijn  ",""+newRowId);
             if(db.isOpen()) {
                 db.close();
             }
 //            Toast.makeText(context, "Reading punched successfully.", Toast.LENGTH_LONG).show();
         } catch(Exception e) {
 //            Toast.makeText(context, "Failed to save reading", Toast.LENGTH_LONG).show();
         }
     }*/
    private static void saveValues(Context context, Uri table, ContentValues values, String condition) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(table, null,
                condition, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            resolver.update(table, values, condition, null);
        } else {
            resolver.insert(table, values);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * @param context
     */
    public static ArrayList<UploadsHistory> getUploadsHistory(Context context) {
//        String condition = LoginTable.Cols.USER_LOGIN_ID + "='" + uname + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadsHistoryTable.CONTENT_URI, null,
                null, null, null);
        ArrayList<UploadsHistory> uploadsHistoryList = getUploadsHistoryFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return uploadsHistoryList;
    }

    public static int deleteUploadsHistory(Context context, String mr) {
        String condition = CommonUtils.getPreviousDateCondition(mr);
        ContentResolver resolver = context.getContentResolver();
        int deleted = resolver.delete(UploadsHistoryTable.CONTENT_URI, condition, null);
        //Toast.makeText(context,"Records delted "+deleted, Toast.LENGTH_LONG).show();
        return deleted;
    }

    public static int deleteUploadsBillHistory(Context context, String mr) {
        String condition = CommonUtils.getPreviousDateConditionbill(mr);
        ContentResolver resolver = context.getContentResolver();
        int deleted = resolver.delete(UploadBillHistoryTable.CONTENT_URI, condition, null);
        //Toast.makeText(context,"Records delted "+deleted, Toast.LENGTH_LONG).show();
        return deleted;
    }

    public static ArrayList<String> getUploadsHistoryRoutes(Context context, String date) {
        ArrayList<String> routes = null;
        String condition = UploadsHistoryTable.Cols.READING_DATE + "='" + date + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadsHistoryTable.CONTENT_URI, new String[]{"DISTINCT " + UploadsHistoryTable.Cols.ROUTE_ID},
                condition, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String route_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_ID));
                routes.add(route_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return routes;
    }

    public static ArrayList<UploadsHistory> getUploadsHistory(Context context, String date, String routeId, String meterreaderid) {
        String condition = UploadsHistoryTable.Cols.READING_DATE + "='" + date + "' AND "
                + UploadsHistoryTable.Cols.ROUTE_ID + "='" + routeId + "' AND "
                + UploadsHistoryTable.Cols.METER_READER_ID + "='" + meterreaderid + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadsHistoryTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<UploadsHistory> uploadsHistoryList = getUploadsHistoryFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return uploadsHistoryList;
    }

    public static HistoryCard getUploadsHistoryCounts(Context context, String meterreaderid) {
        HistoryCard lHistoryCard = new HistoryCard();
        ContentResolver resolver = context.getContentResolver();
        String condition = UploadsHistoryTable.Cols.UPLOAD_STATUS + "='" + context.getString(R.string.addnewconsumer) + "' AND "
                + UploadsHistoryTable.Cols.METER_READER_ID + "='" + meterreaderid + "'";
        Cursor cursor = resolver.query(UploadsHistoryTable.CONTENT_URI, null, condition, null, null);
        ArrayList<UploadsHistory> uploadsHistoryList = getUploadsHistoryFromCursor(cursor);
        if (uploadsHistoryList != null) {
            lHistoryCard.unbill = uploadsHistoryList.size();
        } else {
            lHistoryCard.unbill = 0;
        }
        condition = UploadsHistoryTable.Cols.UPLOAD_STATUS + "='" + context.getString(R.string.meter_status_normal) + "' AND "
                + UploadsHistoryTable.Cols.METER_READER_ID + "='" + meterreaderid + "'";
        cursor = resolver.query(UploadsHistoryTable.CONTENT_URI, null, condition, null, null);
        uploadsHistoryList = getUploadsHistoryFromCursor(cursor);
        if (uploadsHistoryList != null) {
            lHistoryCard.open = uploadsHistoryList.size();
        } else {
            lHistoryCard.open = 0;
        }
        condition = UploadsHistoryTable.Cols.UPLOAD_STATUS + "='" + context.getString(R.string.revisit) + "' AND "
                + UploadsHistoryTable.Cols.METER_READER_ID + "='" + meterreaderid + "'";
        cursor = resolver.query(UploadsHistoryTable.CONTENT_URI, null, condition, null, null);
        uploadsHistoryList = getUploadsHistoryFromCursor(cursor);
        if (uploadsHistoryList != null) {
            lHistoryCard.revisit = uploadsHistoryList.size();
        } else {
            lHistoryCard.revisit = 0;
        }

        if (cursor != null) {
            cursor.close();
        }
        return lHistoryCard;
    }

    public static ArrayList<SummaryCard> getSummaryCard(Context context, String reader_id) {

        ArrayList<SummaryCard> summaryCardArrayList = new ArrayList<>();

        ArrayList<String> routes = getTotalRoutes(context, reader_id);
        String route_id = "";
        String billcycle = "";
        if (routes != null)
            for (int i = 0; i < routes.size(); i++) {
                SummaryCard lSummaryCard = new SummaryCard();
                route_id = routes.get(i);
                ArrayList<String> bill = getTotalBillCycleCode(context, reader_id, route_id);
                billcycle = bill.get(0);

                lSummaryCard.route_id = route_id;
                lSummaryCard.bill_cycle_code = billcycle;

                //calculate txtDelivered job cards inside single txtBinderCode
                String conditionTotal = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "'";

                ContentResolver resolver = context.getContentResolver();
                Cursor cursorTotal = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionTotal, null, null);

                if (cursorTotal != null) {
                    lSummaryCard.total = cursorTotal.getCount();
                } else {
                    lSummaryCard.total = 0;
                }
                if (cursorTotal != null) {
                    cursorTotal.close();
                }

                //calculate Open job cards inside single txtBinderCode
                String conditionOpen = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "' AND "
                        + JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND "
                        + JobCardTable.Cols.IS_REVISIT + "='False'";

                Cursor cursorOpen = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionOpen, null, null);

                if (cursorOpen != null) {
                    lSummaryCard.open = cursorOpen.getCount();
                } else {
                    lSummaryCard.open = 0;
                }
                if (cursorOpen != null) {
                    cursorOpen.close();
                }

                //calculate Revisit job cards inside single txtBinderCode
                String conditionRevisit = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "' AND "
                        + JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND "
                        + JobCardTable.Cols.IS_REVISIT + "='True'";

                Cursor cursorRevisit = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionRevisit, null, null);

                if (conditionRevisit != null) {
                    lSummaryCard.revisit = cursorRevisit.getCount();
                } else {
                    lSummaryCard.revisit = 0;
                }
                if (conditionRevisit != null) {
                    cursorRevisit.close();
                }

                //calculate txtTotal job cards inside single txtBinderCode
                lSummaryCard.open = lSummaryCard.open + lSummaryCard.revisit;

                //calculate completed job cards inside single txtBinderCode
                lSummaryCard.completed = lSummaryCard.total - lSummaryCard.open;

                summaryCardArrayList.add(lSummaryCard);
            }

        return summaryCardArrayList;
    }

//    public static User getCurrentLoggedInUser(Context context) {
//        String condition = LoginTable.Cols.USER_LOGIN_ID + "='" + SharedPrefManager.getStringValue(context, SharedPrefManager.USER_NAME) + "' and " + LoginTable.Cols.USER_LOGIN_ID + "='" + SharedPrefManager.getStringValue(context, SharedPrefManager.USER_ID) + "'";
//        ContentResolver resolver = context.getContentResolver();
//        Cursor cursor = resolver.query(LoginTable.CONTENT_URI, null,
//                condition, null, null);
//        // ArrayList<User> userList = getUserListFromCurser(cursor);
//        User user = null;
//        if (cursor != null && cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            // userList = new ArrayList<User>();
//            while (!cursor.isAfterLast()) {
//                user = getUserFromCursor(cursor);
//                cursor.moveToNext();
//            }
//        }
//        if (cursor != null) {
//            cursor.close();
//        }
//        return user;
//    }

    /**
     * @param context
     * @param uname
     */
//    public static ArrayList<User> getUser(Context context, String uname) {
//        String condition = LoginTable.Cols.USER_LOGIN_ID + "='" + uname + "'";
//        ContentResolver resolver = context.getContentResolver();
//        Cursor cursor = resolver.query(LoginTable.CONTENT_URI, null,
//                condition, null, null);
//        ArrayList<User> userList = getUserListFromCurser(cursor);
//        if (cursor != null) {
//            cursor.close();
//        }
//        return userList;
//    }

    /**
     * @param context
     * @param meter_reader_id
     */
    public static SummaryCount getSummary(Context context, String meter_reader_id) {
        SummaryCount summaryCount = null;

//        System.out.println("DB PATH = "+ getDbPath(context));
        //Toast.makeText(context,"Summary count Query called",Toast.LENGTH_LONG).show();
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        String sql = "select  count(*) total_jobs," +
                " sum(case when " + JobCardTable.Cols.JOB_CARD_STATUS + "= '" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND " + JobCardTable.Cols.IS_REVISIT + "='False' then 1 else 0 end) txtTotal," +
                " sum(case when " + JobCardTable.Cols.JOB_CARD_STATUS + "= '" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND " + JobCardTable.Cols.IS_REVISIT + "= 'True' then 1 else 0 end) txtDelivered, " +
                "(select x.pendingUpload As pendingUpload " +
                "from (select count(*)+(select count(*)  " +
                "from " + ConsumerTable.TABLE_NAME + " where " + ConsumerTable.Cols.METER_READER_ID + "='" + meter_reader_id + "') As pendingUpload " +
                "from (select  distinct " + MeterReadingTable.Cols.JOB_CARD_ID + " " +
                "from " + MeterReadingTable.TABLE_NAME + " where " + ConsumerTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' )) x) As pendingUpload " +
                "from " + JobCardTable.TABLE_NAME + " t  where t." + JobCardTable.Cols.METER_READER_ID + "='" + meter_reader_id + "'";

        //select  count(*) total_jobs, sum(case when job_card_status= 'ALLOCATED' AND is_revisit='False' then 1 else 0 end) txtTotal, sum(case when job_card_status= 'ALLOCATED' AND is_revisit= 'True' then 1 else 0 end) txtDelivered, (select x.pendingUpload As pendingUpload from (select count(*)+(select count(*)  from ConsumerTable where meter_reader_id='81') As pendingUpload from (select  distinct job_card_id from MeterReadingTable )) x) As pendingUpload from JobCardTable t  where t.meter_reader_id='81'

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            summaryCount = new SummaryCount();
            summaryCount.total_jobs = cursor.getInt(cursor.getColumnIndex("total_jobs"));
            summaryCount.open = cursor.getInt(cursor.getColumnIndex("txtTotal"));
            summaryCount.revisit = cursor.getInt(cursor.getColumnIndex("txtDelivered"));
            summaryCount.pendingUpload = cursor.getInt(cursor.getColumnIndex("pendingUpload"));
        }
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return summaryCount;
    }


    public static PendingCount getpendingcount(Context context, String meter_reader_id) {
        PendingCount pendingCount = null;
        // Toast.makeText(context,"Pending Summary count Query called",Toast.LENGTH_LONG).show();
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select\n" +
                "    count(*) total_jobs,\n" +
                "    sum(case when " + JobCardTable.Cols.JOB_CARD_STATUS + "= 'COMPLETED' AND " + JobCardTable.Cols.IS_REVISIT + "='False' then 1 else 0 end) normalpending,\n" +
                "    sum(case when " + JobCardTable.Cols.JOB_CARD_STATUS + "= 'COMPLETED' AND " + JobCardTable.Cols.IS_REVISIT + "= 'True' then 1 else 0 end) revisitpending,\n" +
                "    t1.unbillpending As unbillpending\n" +
                "    from " + JobCardTable.TABLE_NAME + " t,(select count(*) As unbillpending from " + ConsumerTable.TABLE_NAME + ") t1 where t." + JobCardTable.Cols.METER_READER_ID + "='" + meter_reader_id + "'", null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            pendingCount = new PendingCount();
            pendingCount.normalpending = cursor.getInt(cursor.getColumnIndex("normalpending"));
            pendingCount.revisitpending = cursor.getInt(cursor.getColumnIndex("revisitpending"));
            pendingCount.unbillpending = cursor.getInt(cursor.getColumnIndex("unbillpending"));
            //Toast.makeText(context,"Normal Pending:"+pendingCount.normalpending+" Revisit Pending"+pendingCount.revisitpending+" Unbill_Pending"+pendingCount.unbillpending,Toast.LENGTH_LONG).show();

        }
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return pendingCount;
    }

//    private static ArrayList<User> getUserListFromCurser(Cursor cursor) {
//        ArrayList<User> userList = null;
//        if (cursor != null && cursor.getCount() > 0) {
//            cursor.moveToFirst();
//            User user;
//            userList = new ArrayList<User>();
//            while (!cursor.isAfterLast()) {
//                user = getUserFromCursor(cursor);
//                userList.add(user);
//                cursor.moveToNext();
//            }
//        }
//        return userList;
//    }

    @NonNull
//    private static User getUserFromCursor(Cursor cursor) {
//        User user;
//        user = new User();
//        user.userid = cursor.getString(cursor.getColumnIndex(LoginTable.Cols.USER_LOGIN_ID));
//        user.meter_reader_id = cursor.getString(cursor.getColumnIndex(LoginTable.Cols.METER_READER_ID));
//        user.login_date = cursor.getString(cursor.getColumnIndex(LoginTable.Cols.LOGIN_DATE));
//        user.login_lng = cursor.getString(cursor.getColumnIndex(LoginTable.Cols.LOGIN_LNG));
//        user.login_lat = cursor.getString(cursor.getColumnIndex(LoginTable.Cols.LOGIN_LAT));
//
//        return user;
//    }

    /**
     * @param context
     * @param reader_id
     */
    public static ArrayList<JobCard> getJobCards(Context context, String reader_id) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    private static ArrayList<JobCard> getJobCardListFromCursor(Cursor cursor) {
        ArrayList<JobCard> jobCards = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            JobCard user;
            jobCards = new ArrayList<JobCard>();
            while (!cursor.isAfterLast()) {
                user = getJobCardFromCursor(cursor);
                jobCards.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    private static JobCard getJobCardFromCursor(Cursor cursor) {
        JobCard jobCard = new JobCard();
        jobCard.consumer_name = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CONSUMER_NAME)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CONSUMER_NAME)) : "";
        jobCard.meter_reader_id = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_READER_ID)) : "";
        jobCard.consumer_no = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CONSUMER_NO)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CONSUMER_NO)) : "";
        jobCard.meter_no = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_ID)) : "";
        jobCard.dt_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.DT_CODE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.DT_CODE)) : "";
        jobCard.bill_cycle_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BILL_CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BILL_CYCLE_CODE)) : "";
        jobCard.schedule_month = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SCHEDULE_MONTH)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SCHEDULE_MONTH)) : "";
        jobCard.schedule_end_date = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SCHEDULE_END_DATE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SCHEDULE_END_DATE)) : "";
        jobCard.route_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_ID)) : "";
        jobCard.pole_no = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.POL_NO)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.POL_NO)) : "";
        jobCard.phone_no = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PHONE_NO)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PHONE_NO)) : "";
        jobCard.address = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ADDRESS)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ADDRESS)) : "";
        jobCard.meter_reader_id = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_READER_ID)) : "";
        jobCard.prv_meter_reading = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_METER_READING)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_METER_READING)) : "";
        jobCard.lattitude = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_LAT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_LAT)) : "";
        jobCard.longitude = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_LONG)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_LONG)) : "";
        jobCard.is_revisit = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.IS_REVISIT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.IS_REVISIT)) : "";
        jobCard.assigned_date = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ASSIGNED_DATE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ASSIGNED_DATE)) : "";
        jobCard.job_card_status = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.JOB_CARD_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.JOB_CARD_STATUS)) : "";
        jobCard.job_card_id = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.JOB_CARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.JOB_CARD_ID)) : "";
        jobCard.prv_sequence = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_SEQUENCE)) : "";
        jobCard.zone_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ZONE_CODE)) : "";
        jobCard.category_id = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CATEGORY_ID)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CATEGORY_ID)) : "";
        jobCard.avg_consumption = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.AVG_CONSUMTION)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.AVG_CONSUMTION)) : "";
        jobCard.meter_digit = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_DIGIT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.METER_DIGIT)) : "";
        jobCard.account_no = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ACCOUNT_NO)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ACCOUNT_NO)) : "";
        jobCard.route_image = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_IMAGE)) : "";
        jobCard.current_sequence = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CURRENT_SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.CURRENT_SEQUENCE)) : "";
        jobCard.snf = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SNF)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SNF)) : "";
        jobCard.prv_status = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PRV_STATUS)) : "";
        jobCard.zone_name = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ZONE_NAME)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ZONE_NAME)) : "";
        jobCard.attempt = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ATTEMPT)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ATTEMPT)) : "";
        jobCard.pdc = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PDC)) != null ? cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.PDC)) : "";

        return jobCard;
    }

    /**
     * @param context
     * @param reader_id
     */

    /*Context context,
    String reader_id, int limit*/
    public static ArrayList<Consumer> getUnbillConsumers(Context context, String reader_id, int limit) {
        String condition = ConsumerTable.Cols.METER_READER_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ConsumerTable.CONTENT_URI, null,
                condition, null, ConsumerTable.Cols.CONSUMER_ID + " ASC " + " LIMIT " + limit);
        ArrayList<Consumer> consumers = getConsumersFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return consumers;
    }

    public static ArrayList<Consumer> checkUnbillConsumers(Context context, String reader_id, String consumerno) {
        String condition = ConsumerTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " + ConsumerTable.Cols.CONSUMER_ID + "='" + consumerno + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(ConsumerTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<Consumer> consumers = getConsumersFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return consumers;
    }

    public static void deleteUnbillConsumer(Context mContext, String consumers, String mr) {


        String condition = ConsumerTable.Cols.ID + "='" + consumers + "' and " + ConsumerTable.Cols.METER_READER_ID + "='" + mr + "'";
        mContext.getContentResolver().delete(ConsumerTable.CONTENT_URI, condition, null);
    }

    private static ArrayList<Consumer> getConsumersFromCursor(Cursor cursor) {
        ArrayList<Consumer> consumers = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Consumer consumer;
            consumers = new ArrayList<Consumer>();
            while (!cursor.isAfterLast()) {
                consumer = getConsumerFromCursor(cursor);
                consumers.add(consumer);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return consumers;
    }

    private static Consumer getConsumerFromCursor(Cursor cursor) {
        Consumer consumer = new Consumer();
        consumer.consumer_name = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONSUMER_NAME)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONSUMER_NAME)) : "";
        consumer.meter_reader_id = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_READER_ID)) : "";
        consumer.consumer_no = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONSUMER_ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONSUMER_ID)) : "";
        consumer.meter_no = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_NO)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_NO)) : "";
        consumer.dtc = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.DT_CODE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.DT_CODE)) : "";
        consumer.bill_cycle_code = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.BILL_CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.BILL_CYCLE_CODE)) : "";
        consumer.pole_no = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.POLE_NO)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.POLE_NO)) : "";
        consumer.route_code = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ROUTE_ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ROUTE_ID)) : "";
        consumer.contact_no = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.PHONE_NO)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.PHONE_NO)) : "";
        consumer.address = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ADDRESS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ADDRESS)) : "";
        consumer.current_meter_reading = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_METER_READING)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_METER_READING)) : "";
        consumer.meter_status = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_STATUS)) : "";
        consumer.reader_status = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READER_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READER_STATUS)) : "";
        consumer.cur_lat = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CUR_LAT)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CUR_LAT)) : "";
        consumer.cur_lng = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CUR_LNG)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CUR_LNG)) : "";
        consumer.email_id = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.EMAIL_ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.EMAIL_ID)) : "";
        consumer.reader_remark_comment = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.COMMENTS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.COMMENTS)) : "";
        consumer.suspicious_activity = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.IS_SUSPICIOUS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.IS_SUSPICIOUS)) : "";
        consumer.suspicious_remark = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.SUSPICIOUS_REMARKS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.SUSPICIOUS_REMARKS)) : "";
        consumer.connection_status = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONNECTION_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CONNECTION_STATUS)) : "";
        consumer.reading_month = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.MONTH)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.MONTH)) : "";
        consumer.meter_reader_id = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_READER_ID)) : "";
        MeterImage meterImage = new MeterImage();
        meterImage.name = "mi_" + consumer.reading_month + "_" + consumer.bill_cycle_code + "_newconsumer_" + consumer.meter_reader_id + "_" + consumer.consumer_no + ".JPEG";
        meterImage.image = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READING_IMAGE)) : "";
        meterImage.content_type = "image/jpeg";
        consumer.meter_image = meterImage;

        MeterImage suspicious_activity_image = new MeterImage();
        suspicious_activity_image.name = "sp_" + consumer.reading_month + "_" + consumer.bill_cycle_code + "_newconsumer_" + consumer.meter_reader_id + "_" + consumer.consumer_no + ".JPEG";
        suspicious_activity_image.image = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SUSPICIOUS_READING_IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.SUSPICIOUS_READING_IMAGE)) : "";
        suspicious_activity_image.content_type = "image/jpeg";
        consumer.suspicious_activity_image = suspicious_activity_image;

        consumer.reading_date = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READING_DATE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READING_DATE)) : "";
        consumer.reading_taken_by = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READING_TAKEN_BY)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.READING_TAKEN_BY)) : "";
        consumer.location_guidance = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.LOCATION_GUIDANCE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.LOCATION_GUIDANCE)) : "";
        consumer.current_kvah_reading = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_KVAH_READING)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_KVAH_READING)) : "";
        consumer.current_kva_reading = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_KVA_READING)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_KVA_READING)) : "";
        consumer.current_meter_reading = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_METER_READING)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_METER_READING)) : "";
        consumer.iskvahroundcompleted = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ISKVAHROUNDCOMPLETED)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ISKVAHROUNDCOMPLETED)) : "";
        consumer.iskwhroundcompleted = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ISKWHROUNDCOMPLETED)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ISKWHROUNDCOMPLETED)) : "";
        consumer.mobile_no = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.MOBILE_NO)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.MOBILE_NO)) : "";
        consumer.panel_no = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.PANEL_NO)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.PANEL_NO)) : "";
        consumer.new_sequence = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.NEW_SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.NEW_SEQUENCE)) : "";
        consumer.meter_type = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_TYPE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.METER_TYPE)) : "";
        consumer.id = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ID)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ID)) : "";
        consumer.zone_code = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.ZONE_CODE)) : "";
        consumer.current_kw_reading = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_KW_READING)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_KW_READING)) : "";
        consumer.current_pf_reading = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_PF_READING)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.CURRENT_PF_READING)) : "";
        consumer.meter_location = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.Meter_Location)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.Meter_Location)) : "";
        consumer.time_taken = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.TIME_TAKEN)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.TIME_TAKEN)) : "";



        consumer.air_conditioner_exist = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.AIR_CONDITIONER_EXIST)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.AIR_CONDITIONER_EXIST)) : "";
        consumer.no_of_air_conditioners = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.NO_OF_AIR_CONDITIONERS)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.NO_OF_AIR_CONDITIONERS)) : "";
        consumer.is_plastic_cover_cut = cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.IS_PLASTIC_COVER_CUT)) != null ? cursor.getString(cursor.getColumnIndex(ConsumerTable.Cols.IS_PLASTIC_COVER_CUT)) : "";

        return consumer;
    }

    private static ArrayList<UploadsHistory> getUploadsHistoryFromCursor(Cursor cursor) {
        ArrayList<UploadsHistory> uploadsHistoryArray = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            UploadsHistory uploadsHistory;
            uploadsHistoryArray = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                uploadsHistory = getUploadHistoryFromCursor(cursor);
                uploadsHistoryArray.add(uploadsHistory);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return uploadsHistoryArray;
    }

    private static UploadsHistory getUploadHistoryFromCursor(Cursor cursor) {
        UploadsHistory uploadsHistory = new UploadsHistory();
        uploadsHistory.consumer_no = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.CONSUMER_ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.CONSUMER_ID)) : "";
        uploadsHistory.bill_cycle_code = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.BILL_CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.BILL_CYCLE_CODE)) : "";
        uploadsHistory.route_code = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.ROUTE_ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.ROUTE_ID)) : "";
        uploadsHistory.month = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.MONTH)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.MONTH)) : "";
        uploadsHistory.upload_status = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.UPLOAD_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.UPLOAD_STATUS)) : "";
        uploadsHistory.reading_date = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.READING_DATE)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.READING_DATE)) : "";
        uploadsHistory.meter_reader_id = cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadsHistoryTable.Cols.METER_READER_ID)) : "";

        return uploadsHistory;
    }

    /**
     * @param context
     * @param reader_id
     */
    public static ArrayList<MeterReading> getMeterReadings(Context context, String reader_id, int limit) {
        String condition = MeterReadingTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + MeterReadingTable.Cols.IS_UPLOADED + "='False'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MeterReadingTable.CONTENT_URI, null,
                condition, null, MeterReadingTable.Cols.METER_READER_ID + " ASC " + " LIMIT " + limit);
        ArrayList<MeterReading> meterReadings = getMeterReadingsFromCursor(context, cursor);
        if (cursor != null) {
            cursor.close();
        }
        return meterReadings;
    }

    public static ArrayList<MeterReading> getMeterReading(Context context, String reader_id) {
        String condition = MeterReadingTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + MeterReadingTable.Cols.IS_UPLOADED + "='False'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(MeterReadingTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<MeterReading> meterReadings = getMeterReadingsFromCursor(context, cursor);
        if (cursor != null) {
            cursor.close();
        }
        return meterReadings;
    }

    public static ArrayList<MeterReading> getMeterReading(Context context, String reader_id, String jbid) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + MeterReadingTable.TABLE_NAME + " where " + MeterReadingTable.Cols.JOB_CARD_ID + "='" + jbid + "' AND " + MeterReadingTable.Cols.METER_READER_ID + "= '" + reader_id + "'", null);
        ArrayList<MeterReading> meterReadings = getMeterReadingsFromCursor(context, cursor);
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return meterReadings;


//        String condition = MeterReadingTable.Cols.METER_READER_ID + "='" + reader_id +  " ' and " + MeterReadingTable.Cols.JOB_CARD_ID + " = ' "+ jbid +" ' ";
//        ContentResolver resolver = context.getContentResolver();
//        Cursor cursor = resolver.query(MeterReadingTable.CONTENT_URI, null,
//                condition, null, null);
//        ArrayList<MeterReading> meterReadings = getMeterReadingsFromCursor(context, cursor);
//        if (cursor != null)
//        {
//            cursor.close();
//        }
//        return meterReadings;
    }

    private static ArrayList<MeterReading> getMeterReadingsFromCursor(Context context, Cursor cursor) {
        ArrayList<MeterReading> meterReadings = null;
        if (cursor != null && cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                MeterReading meterReading;
                meterReadings = new ArrayList<MeterReading>();
                while (!cursor.isAfterLast()) {
                    meterReading = getMeterReadingFromCursor(context, cursor);
                    meterReadings.add(meterReading);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return meterReadings;
    }

    private static MeterReading getMeterReadingFromCursor(Context context, Cursor cursor) {
        MeterReading meterReading = new MeterReading();
        meterReading.meter_no = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_NO)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_NO)) : "";
        meterReading.meter_reader_id = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_READER_ID)) : "";
        meterReading.job_card_id = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.JOB_CARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.JOB_CARD_ID)) : "";
        meterReading.current_meter_reading = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_METER_READING)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_METER_READING)) : "";
        meterReading.meter_status = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_STATUS)) : "";
        meterReading.reader_status = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READER_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READER_STATUS)) : "";
        meterReading.reading_month = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_MONTH)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_MONTH)) : "";
        meterReading.reader_remark_comment = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READER_REMARK_COMMENT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READER_REMARK_COMMENT)) : "";
        meterReading.suspicious_activity = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_SUSPICIOUS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_SUSPICIOUS)) : "";
        meterReading.suspicious_remark = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SUSPICIOUS_REMARKS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SUSPICIOUS_REMARKS)) : "";


        meterReading.consumer_category_remark = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CONSUMER_CATEGORY_REMARK)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CONSUMER_CATEGORY_REMARK)) : "";
        meterReading.air_conditioner_exist = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.AIR_CONDITIONER_EXIST)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.AIR_CONDITIONER_EXIST)) : "";
        meterReading.no_of_air_conditioners = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.NO_OF_AIR_CONDITIONERS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.NO_OF_AIR_CONDITIONERS)) : "";
        meterReading.is_plastic_cover_cut = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_PLASTIC_COVER_CUT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_PLASTIC_COVER_CUT)) : "";

        meterReading.is_lat_long_verified = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_LAT_LONG_VERIFIED)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_LAT_LONG_VERIFIED)) : "";
        meterReading.distance = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.DISTANCE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.DISTANCE)) : "";




        MeterImage meterImage = new MeterImage();

        String meterReading_bill_cycle_code = "";
        String meterReading_consumer_no = "";
        ArrayList<JobCard> mJobCards = DatabaseManager.getJobCard(context, meterReading.meter_reader_id, AppConstants.JOB_CARD_STATUS_COMPLETED, meterReading.job_card_id);
        if (mJobCards != null && mJobCards.size() > 0) {
            meterReading_bill_cycle_code = mJobCards.get(0).bill_cycle_code;
            meterReading_consumer_no = mJobCards.get(0).consumer_no;
        }

        meterImage.name = "mi_" + meterReading.reading_month + "_" + meterReading_bill_cycle_code + "_" + meterReading.job_card_id + "_" + meterReading_consumer_no + ".JPEG";
        meterImage.image = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_IMAGE)) : "";
        meterImage.content_type = "image/jpeg";
        meterReading.meter_image = meterImage;

        MeterImage suspicious_activity_image = new MeterImage();
        suspicious_activity_image.name = "sp_" + meterReading.reading_month + "_" + meterReading_bill_cycle_code + "_" + meterReading.job_card_id + "_" + meterReading_consumer_no + ".JPEG";
        suspicious_activity_image.image = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SUSPICIOUS_READING_IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SUSPICIOUS_READING_IMAGE)) : "";
        suspicious_activity_image.content_type = "image/jpeg";
        meterReading.suspicious_activity_image = suspicious_activity_image;

        meterReading.cur_lat = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CUR_LAT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CUR_LAT)) : "";
        meterReading.cur_lng = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CUR_LNG)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CUR_LNG)) : "";
        meterReading.isUploaded = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_UPLOADED)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_UPLOADED)) : "";
        meterReading.isRevisit = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_REVISIT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_REVISIT)) : "";
        meterReading.reading_date = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_DATE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_DATE)) : "";
        meterReading.reading_taken_by = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_TAKEN_BY)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.READING_TAKEN_BY)) : "";
        meterReading.pole_no = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.POLE_NO)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.POLE_NO)) : "";
        meterReading.location_guidance = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.LOCATION_GUIDANCE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.LOCATION_GUIDANCE)) : "";
        meterReading.current_kvah_reading = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_KVAH_READING)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_KVAH_READING)) : "";
        meterReading.current_kva_reading = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_KVA_READING)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_KVA_READING)) : "";
        meterReading.mobile_no = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.MOBILE_NO)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.MOBILE_NO)) : "";
        meterReading.panel_no = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PANEL_NO)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PANEL_NO)) : "";
        meterReading.prv_sequence = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PRV_SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.PRV_SEQUENCE)) : "";
        meterReading.new_sequence = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.NEW_SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.NEW_SEQUENCE)) : "";
        meterReading.iskvahroundcompleted = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.ISKVAHROUNDCOMPLETED)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.ISKVAHROUNDCOMPLETED)) : "";
        meterReading.iskwhroundcompleted = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.ISKWHROUNDCOMPLETED)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.ISKWHROUNDCOMPLETED)) : "";
        meterReading.meter_type = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_TYPE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.METER_TYPE)) : "";
        meterReading.zone_code = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.ZONE_CODE)) : "";
        meterReading.current_kw_reading = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_KW_READING)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_KW_READING)) : "";
        meterReading.current_pf_reading = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_PF_READING)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CURRENT_PF_READING)) : "";
        meterReading.status_changed = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.STATUS_CHANGED)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.STATUS_CHANGED)) : "";
        meterReading.sms_sent = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SMS_SENT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.SMS_SENT)) : "";
        meterReading.meter_location = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.Meter_Location)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.Meter_Location)) : "";
        meterReading.time_taken = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.TIME_TAKEN)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.TIME_TAKEN)) : "";


        meterReading.consumer_category_remark = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CONSUMER_CATEGORY_REMARK)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.CONSUMER_CATEGORY_REMARK)) : "";
        meterReading.air_conditioner_exist = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.AIR_CONDITIONER_EXIST)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.AIR_CONDITIONER_EXIST)) : "";
        meterReading.no_of_air_conditioners = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.NO_OF_AIR_CONDITIONERS)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.NO_OF_AIR_CONDITIONERS)) : "";
        meterReading.is_plastic_cover_cut = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_PLASTIC_COVER_CUT)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_PLASTIC_COVER_CUT)) : "";
        meterReading.is_lat_long_verified = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_LAT_LONG_VERIFIED)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.IS_LAT_LONG_VERIFIED)) : "";
        meterReading.distance = cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.DISTANCE)) != null ? cursor.getString(cursor.getColumnIndex(MeterReadingTable.Cols.DISTANCE)) : "";

        return meterReading;
    }

    /**
     * @param context
     * @param reader_id
     */

    public static ArrayList<String> getRoutes(Context context, String reader_id) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.ROUTE_ID},
                condition, null, null);
        ArrayList<String> routes = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String route_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_ID));
                routes.add(route_code);
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return routes;

    }

    public static ArrayList<String> getRoutesall(Context context, String reader_id, String Screen) {
        Cursor cursor = null;
        if (Screen.equalsIgnoreCase("bill")) {
            String condition = BillTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                    BillTable.Cols.JOBCARD_STATUS + "='" + AppConstants.BILL_CARD_STATUS_ALLOCATED + "'";
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(BillTable.CONTENT_URI, new String[]{"DISTINCT " + BillTable.Cols.BINDER_CODE},
                    condition, null, null);
        } else {
            String condition = DisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                    DisconnectionTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(DisconnectionTable.CONTENT_URI, new String[]{"DISTINCT " + DisconnectionTable.Cols.BINDER_CODE},
                    condition, null, null);
        }
        ArrayList<String> routes = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<String>();
            String route_code = null;
            while (!cursor.isAfterLast()) {

                if (Screen.equalsIgnoreCase("bill"))
                    route_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BINDER_CODE));
                else
                    route_code = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.BINDER_CODE));

                routes.add(route_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return routes;

    }

    public static ArrayList<String> getZoneNames(Context context, String reader_id, String Screen) {
        Cursor cursor = null;
        if (Screen.equalsIgnoreCase("normal")) {
            String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                    JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.ZONE_NAME},
                    condition, null, null);
        } else if (Screen.equalsIgnoreCase("bill")) {
            String condition = BillTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                    BillTable.Cols.JOBCARD_STATUS + "='" + AppConstants.BILL_CARD_STATUS_ALLOCATED + "'";
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(BillTable.CONTENT_URI, new String[]{"DISTINCT " + BillTable.Cols.ZONE_CODE},
                    condition, null, null);
        } else {
            String condition = DisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                    DisconnectionTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(DisconnectionTable.CONTENT_URI, new String[]{"DISTINCT " + DisconnectionTable.Cols.ZONE_CODE},
                    condition, null, null);

        }

        ArrayList<String> routes = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<String>();
            String route_code = null;
            while (!cursor.isAfterLast()) {
                if (Screen.equalsIgnoreCase("normal"))
                    route_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ZONE_NAME));
                else if (Screen.equalsIgnoreCase("bill"))
                    route_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.ZONE_CODE));
                else
                    route_code = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.ZONE_CODE));

                routes.add(route_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return routes;
    }

    /**
     * @param
     * @param reader_id
     */

    public static ArrayList<String> getbillcyclecode(String screen, Context context, String reader_id) {
        Cursor cursor = null;
        if (screen.equalsIgnoreCase("normal")) {
            String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                    JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.BILL_CYCLE_CODE},
                    condition, null, null);
        } else if (screen.equalsIgnoreCase("bill")) {
            String condition = BillTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                    BillTable.Cols.JOBCARD_STATUS + "='" + AppConstants.BILL_CARD_STATUS_ALLOCATED + "'";
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(BillTable.CONTENT_URI, new String[]{"DISTINCT " + BillTable.Cols.CYCLE_CODE},
                    condition, null, null);
        } else {
            String condition = DisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                    DisconnectionTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(DisconnectionTable.CONTENT_URI, new String[]{"DISTINCT " + DisconnectionTable.Cols.ZONE_CODE},
                    condition, null, null);
        }
        ArrayList<String> billcyclecode = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String bill_cycle_code = null;
            billcyclecode = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                if (screen.equalsIgnoreCase("normal"))
                    bill_cycle_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BILL_CYCLE_CODE));
                else if (screen.equalsIgnoreCase("bill"))
                    bill_cycle_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.CYCLE_CODE));
                else
                    bill_cycle_code = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.ZONE_CODE));

                billcyclecode.add(bill_cycle_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billcyclecode;
    }


    /**
     * @param context
     * @param reader_id
     */

    public static ArrayList<String> getbillmonth(Context context, String reader_id, String screen) {
        Cursor cursor = null;
        if (screen.equalsIgnoreCase("normal")) {
            String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                    JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.SCHEDULE_MONTH},
                    condition, null, null);
        } else if (screen.equalsIgnoreCase("bill")) {
            String condition = BillTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                    BillTable.Cols.JOBCARD_STATUS + "='" + AppConstants.BILL_CARD_STATUS_ALLOCATED + "'";
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(BillTable.CONTENT_URI, new String[]{"DISTINCT " + BillTable.Cols.BILLMONTH},
                    condition, null, null);
        } else {
            String condition = DisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                    DisconnectionTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "'";
            ContentResolver resolver = context.getContentResolver();
            cursor = resolver.query(DisconnectionTable.CONTENT_URI, new String[]{"DISTINCT " + DisconnectionTable.Cols.BILL_MONTH},
                    condition, null, null);
        }
        ArrayList<String> billmonth = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String bill_month = null;
            billmonth = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                if (screen.equalsIgnoreCase("normal"))
                    bill_month = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.SCHEDULE_MONTH));
                else if (screen.equalsIgnoreCase("bill"))
                    bill_month = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BILLMONTH));
                else
                    bill_month = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.BILL_MONTH));


                billmonth.add(bill_month);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billmonth;
    }

    // Method to save image Starts Avinesh:04-03-17
    public static void saveImage(Context context, UserProfile con) {
        if (con != null) {
            ContentValues values = new ContentValues();
            try {
                values.put(UserProfileTable.Cols.IMAGE, con.profile_image);

            } catch (Exception e) {
                e.printStackTrace();
            }
            saveimage(context, values, con);

        }
    }

    private static void saveimage(Context context, ContentValues values, UserProfile con) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.update(UserProfileTable.TABLE_NAME, values, UserProfileTable.Cols.METER_READER_ID + " = ?",
                new String[]{String.valueOf(con.meter_reader_id)});
        if (db.isOpen()) {
            db.close();
        }

    }


    // Method to save image Ends Avinesh:04-03-17

    /**
     * @param route
     * @param reader_id
     */


    public static ArrayList<String> getbillcyclecode(Context context, String reader_id, String route) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND " + JobCardTable.Cols.ROUTE_ID + "='" + route + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.BILL_CYCLE_CODE},
                condition, null, null);
        ArrayList<String> billcyclecode = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            billcyclecode = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String bill_cycle_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BILL_CYCLE_CODE));
                billcyclecode.add(bill_cycle_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billcyclecode;
    }

    /**
     * @param context
     * @param reader_id
     */
/*    public static ArrayList<UserProfile> getUserProfiles(Context context, String reader_id) {
        String condition = UserProfileTable.Cols.METER_READER_ID + "='" + reader_id + "' OR " + UserProfileTable.Cols.EMAIL_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UserProfileTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<UserProfile> userProfiles = getUserProfilesFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return userProfiles;
    }*/

    /**
     * @param context
     * @param reader_id
     */
    public static UserProfile getUserProfile(Context context, String reader_id) {
        UserProfile userProfile = null;
        String condition = UserProfileTable.Cols.METER_READER_ID + "='" + reader_id + "' OR " +
                UserProfileTable.Cols.EMAIL_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UserProfileTable.CONTENT_URI, null, condition, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                userProfile = getUserProfileFromCursor(cursor);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return userProfile;
    }

/*    private static ArrayList<UserProfile> getUserProfilesFromCursor(Cursor cursor) {
        ArrayList<UserProfile> userProfiles = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            UserProfile userProfile;
            userProfiles = new ArrayList<UserProfile>();
            while (!cursor.isAfterLast()) {
                userProfile = getUserProfileFromCursor(cursor);
                userProfiles.add(userProfile);
                cursor.moveToNext();
            }
        }
        return userProfiles;
    }*/

    private static UserProfile getUserProfileFromCursor(Cursor cursor) {
        UserProfile userProfile = new UserProfile();
        userProfile.meter_reader_name = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.METER_READER_NAME)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.METER_READER_NAME)) : "";
        userProfile.meter_reader_id = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.METER_READER_ID)) : "";
        userProfile.address = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.ADDRESS)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.ADDRESS)) : "";
        userProfile.city = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.CITY)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.CITY)) : "";
        userProfile.state = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.STATE)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.STATE)) : "";
        userProfile.emp_id = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMP_ID)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMP_ID)) : "";
        userProfile.email_id = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMAIL_ID)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMAIL_ID)) : "";
        userProfile.emp_type = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMP_TYPE)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.EMP_TYPE)) : "";
        userProfile.role = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.ROLE)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.ROLE)) : "";
        userProfile.status = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.STATUS)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.STATUS)) : "";
        userProfile.device_make = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.DEVICE_MAKE)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.DEVICE_MAKE)) : "";
        userProfile.device_imei_id = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.DEVICE_IMEI_ID)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.DEVICE_IMEI_ID)) : "";
        userProfile.device_type = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.DEVICE_TYPE)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.DEVICE_TYPE)) : "";
        userProfile.contact_no = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.CONTACT_NO)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.CONTACT_NO)) : "";
        userProfile.fcm_token = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.FCM_TOKEN)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.FCM_TOKEN)) : "";
        userProfile.profile_image = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.IMAGE)) : "";
        userProfile.app_link = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.APP_LINK)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.APP_LINK)) : "";
        userProfile.app_version = cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.APP_VERSION)) != null ? cursor.getString(cursor.getColumnIndex(UserProfileTable.Cols.APP_VERSION)) : "";

        return userProfile;
    }

    /**
     * Get ContentValues from the Contact to insert it into UserLogin Table
     *
     * @param context Context
     * @param user    User
     */
    private static ContentValues getContentValuesUserLoginTable(Context context, User user) {
        ContentValues values = new ContentValues();
        try {
            values.put(LoginTable.Cols.USER_LOGIN_ID, user.userid != null ? user.userid : "");
            values.put(LoginTable.Cols.METER_READER_ID, user.meter_reader_id != null ? user.meter_reader_id : "");
            values.put(LoginTable.Cols.LOGIN_DATE, user.login_date != null ? user.login_date : "");
            values.put(LoginTable.Cols.LOGIN_LAT, user.login_lat != null ? user.login_lat : "");
            values.put(LoginTable.Cols.LOGIN_LNG, user.login_lng != null ? user.login_lng : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private static ContentValues getContentValuesMeterReadingTable(Context context, MeterReading meterReading) {
        ContentValues values = new ContentValues();
        try {
            values.put(MeterReadingTable.Cols.METER_NO, meterReading.meter_no != null ? meterReading.meter_no : "");
            values.put(MeterReadingTable.Cols.METER_READER_ID, meterReading.meter_reader_id != null ? meterReading.meter_reader_id : "");
            values.put(MeterReadingTable.Cols.JOB_CARD_ID, meterReading.job_card_id != null ? meterReading.job_card_id : "");
            values.put(MeterReadingTable.Cols.CURRENT_METER_READING, meterReading.current_meter_reading != null ? meterReading.current_meter_reading : "");
            values.put(MeterReadingTable.Cols.METER_STATUS, meterReading.meter_status != null ? meterReading.meter_status : "");
            values.put(MeterReadingTable.Cols.READER_STATUS, meterReading.reader_status != null ? meterReading.reader_status : "");
            values.put(MeterReadingTable.Cols.READING_MONTH, meterReading.reading_month != null ? meterReading.reading_month : "");
            values.put(MeterReadingTable.Cols.READING_IMAGE, meterReading.meter_image != null ? meterReading.meter_image.image != null ? meterReading.meter_image.image : "" : "");
            values.put(MeterReadingTable.Cols.SUSPICIOUS_READING_IMAGE, meterReading.suspicious_activity_image != null ? meterReading.suspicious_activity_image.image != null ? meterReading.suspicious_activity_image.image : "" : "");
            values.put(MeterReadingTable.Cols.READER_REMARK_COMMENT, meterReading.reader_remark_comment != null ? meterReading.reader_remark_comment : "");
            values.put(MeterReadingTable.Cols.IS_SUSPICIOUS, meterReading.suspicious_activity != null ? meterReading.suspicious_activity : "");
            values.put(MeterReadingTable.Cols.SUSPICIOUS_REMARKS, meterReading.suspicious_remark != null ? meterReading.suspicious_remark : "");
            values.put(MeterReadingTable.Cols.CUR_LAT, meterReading.cur_lat != null ? meterReading.cur_lat : "");
            values.put(MeterReadingTable.Cols.CUR_LNG, meterReading.cur_lng != null ? meterReading.cur_lng : "");
            values.put(MeterReadingTable.Cols.IS_UPLOADED, meterReading.isUploaded != null ? meterReading.isUploaded : "");
            values.put(MeterReadingTable.Cols.IS_REVISIT, meterReading.isRevisit != null ? meterReading.isRevisit : "");
            values.put(MeterReadingTable.Cols.READING_DATE, meterReading.reading_date != null ? meterReading.reading_date : "");
            values.put(MeterReadingTable.Cols.PRV_SEQUENCE, meterReading.prv_sequence != null ? meterReading.prv_sequence : "");
            values.put(MeterReadingTable.Cols.NEW_SEQUENCE, meterReading.new_sequence != null ? meterReading.new_sequence : "");
            values.put(MeterReadingTable.Cols.LOCATION_GUIDANCE, meterReading.location_guidance != null ? meterReading.location_guidance : "");
            values.put(MeterReadingTable.Cols.POLE_NO, meterReading.pole_no != null ? meterReading.pole_no : "");
            values.put(MeterReadingTable.Cols.CURRENT_KVAH_READING, meterReading.current_kvah_reading != null ? meterReading.current_kvah_reading : "");
            values.put(MeterReadingTable.Cols.CURRENT_KVA_READING, meterReading.current_kva_reading != null ? meterReading.current_kva_reading : "");
            values.put(MeterReadingTable.Cols.ISKVAHROUNDCOMPLETED, meterReading.iskvahroundcompleted != null ? meterReading.iskvahroundcompleted : "");
            values.put(MeterReadingTable.Cols.ISKWHROUNDCOMPLETED, meterReading.iskwhroundcompleted != null ? meterReading.iskwhroundcompleted : "");
            values.put(MeterReadingTable.Cols.PANEL_NO, meterReading.panel_no != null ? meterReading.panel_no : "");
            values.put(MeterReadingTable.Cols.MOBILE_NO, meterReading.mobile_no != null ? meterReading.mobile_no : "");
            values.put(MeterReadingTable.Cols.METER_TYPE, meterReading.meter_type != null ? meterReading.meter_type : "");
            values.put(MeterReadingTable.Cols.READING_TAKEN_BY, meterReading.reading_taken_by != null ? meterReading.reading_taken_by : "");
            values.put(MeterReadingTable.Cols.ZONE_CODE, meterReading.zone_code != null ? meterReading.zone_code : "");
            values.put(MeterReadingTable.Cols.CURRENT_KW_READING, meterReading.current_kw_reading != null ? meterReading.current_kw_reading : "");
            values.put(MeterReadingTable.Cols.CURRENT_PF_READING, meterReading.current_pf_reading != null ? meterReading.current_pf_reading : "");
            values.put(MeterReadingTable.Cols.STATUS_CHANGED, meterReading.status_changed != null ? meterReading.status_changed : "");
            values.put(MeterReadingTable.Cols.SMS_SENT, meterReading.sms_sent != null ? meterReading.sms_sent : "");
            values.put(MeterReadingTable.Cols.Meter_Location, meterReading.meter_location != null ? meterReading.meter_location : "");
            values.put(MeterReadingTable.Cols.TIME_TAKEN, meterReading.time_taken != null ? meterReading.time_taken : "");

            values.put(MeterReadingTable.Cols.CONSUMER_CATEGORY_REMARK, meterReading.consumer_category_remark != null ? meterReading.consumer_category_remark : "");
            values.put(MeterReadingTable.Cols.AIR_CONDITIONER_EXIST, meterReading.air_conditioner_exist != null ? meterReading.air_conditioner_exist: "");
            values.put(MeterReadingTable.Cols.NO_OF_AIR_CONDITIONERS, meterReading.no_of_air_conditioners != null ? meterReading.no_of_air_conditioners: "");
            values.put(MeterReadingTable.Cols.IS_PLASTIC_COVER_CUT, meterReading.is_plastic_cover_cut != null ? meterReading.is_plastic_cover_cut: "");
            values.put(MeterReadingTable.Cols.IS_LAT_LONG_VERIFIED, meterReading.is_lat_long_verified != null ? meterReading.is_lat_long_verified: "");
            values.put(MeterReadingTable.Cols.DISTANCE, meterReading.distance != null ? meterReading.distance: "");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private static ContentValues getContentValuesUserProfileTable(Context context, UserProfile userProfile, String FcmToken) {
        ContentValues values = new ContentValues();
        try {
            values.put(UserProfileTable.Cols.METER_READER_NAME, userProfile.meter_reader_name != null ? userProfile.meter_reader_name : "");
            values.put(UserProfileTable.Cols.METER_READER_ID, userProfile.meter_reader_id != null ? userProfile.meter_reader_id : "");
            values.put(UserProfileTable.Cols.ADDRESS, userProfile.address != null ? userProfile.address : "");
            values.put(UserProfileTable.Cols.CITY, userProfile.city != null ? userProfile.city : "");
            values.put(UserProfileTable.Cols.STATE, userProfile.state != null ? userProfile.state : "");
            values.put(UserProfileTable.Cols.EMP_ID, userProfile.emp_id != null ? userProfile.emp_id : "");
            values.put(UserProfileTable.Cols.EMAIL_ID, userProfile.email_id != null ? userProfile.email_id : "");
            values.put(UserProfileTable.Cols.EMP_TYPE, userProfile.emp_type != null ? userProfile.emp_type : "");
            values.put(UserProfileTable.Cols.ROLE, userProfile.role != null ? userProfile.role : "");
            values.put(UserProfileTable.Cols.STATUS, userProfile.status != null ? userProfile.state : "");
            values.put(UserProfileTable.Cols.DEVICE_MAKE, userProfile.device_make != null ? userProfile.device_make : "");
            values.put(UserProfileTable.Cols.DEVICE_IMEI_ID, userProfile.device_imei_id != null ? userProfile.device_imei_id : "");
            values.put(UserProfileTable.Cols.DEVICE_TYPE, userProfile.device_type != null ? userProfile.device_type : "");
            values.put(UserProfileTable.Cols.CONTACT_NO, userProfile.contact_no != null ? userProfile.contact_no : "");
            values.put(UserProfileTable.Cols.FCM_TOKEN, FcmToken != null ? FcmToken : "");
            values.put(UserProfileTable.Cols.APP_LINK, userProfile.app_link != null ? userProfile.app_link : "");
            values.put(UserProfileTable.Cols.APP_VERSION, userProfile.app_version != null ? userProfile.app_version : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private static ContentValues getContentValuesJobCardTable(Context context, JobCard jobCard) {
        ContentValues values = new ContentValues();
        try {
            values.put(JobCardTable.Cols.JOB_CARD_STATUS, jobCard.job_card_status != null ? jobCard.job_card_status : "");
            values.put(JobCardTable.Cols.JOB_CARD_ID, jobCard.job_card_id != null ? jobCard.job_card_id : "");
            values.put(JobCardTable.Cols.IS_REVISIT, jobCard.is_revisit != null ? jobCard.is_revisit : "");
            values.put(JobCardTable.Cols.METER_ID, jobCard.meter_no != null ? jobCard.meter_no : "");
            values.put(JobCardTable.Cols.BILL_CYCLE_CODE, jobCard.bill_cycle_code != null ? jobCard.bill_cycle_code : "");
            values.put(JobCardTable.Cols.METER_READER_ID, jobCard.meter_reader_id != null ? jobCard.meter_reader_id : "");
            values.put(JobCardTable.Cols.SCHEDULE_MONTH, jobCard.schedule_month != null ? jobCard.schedule_month : "");
            values.put(JobCardTable.Cols.SCHEDULE_END_DATE, jobCard.schedule_end_date != null ? jobCard.schedule_end_date : "");
            values.put(JobCardTable.Cols.ROUTE_ID, jobCard.route_code != null ? jobCard.route_code : "");
            values.put(JobCardTable.Cols.CONSUMER_NO, jobCard.consumer_no != null ? jobCard.consumer_no : "");
            values.put(JobCardTable.Cols.CONSUMER_NAME, jobCard.consumer_name != null ? jobCard.consumer_name : "");
            values.put(JobCardTable.Cols.POL_NO, jobCard.pole_no != null ? jobCard.pole_no : "");
            values.put(JobCardTable.Cols.DT_CODE, jobCard.dt_code != null ? jobCard.dt_code : "");
            values.put(JobCardTable.Cols.PHONE_NO, jobCard.phone_no != null ? jobCard.phone_no : "");
            values.put(JobCardTable.Cols.ADDRESS, jobCard.address != null ? jobCard.address : "");
            values.put(JobCardTable.Cols.PRV_METER_READING, jobCard.prv_meter_reading != null ? jobCard.prv_meter_reading : "");
            values.put(JobCardTable.Cols.PRV_LAT, jobCard.lattitude != null ? jobCard.lattitude : "");
            values.put(JobCardTable.Cols.PRV_LONG, jobCard.longitude != null ? jobCard.longitude : "");
            values.put(JobCardTable.Cols.ASSIGNED_DATE, jobCard.assigned_date != null ? jobCard.assigned_date : "");
            values.put(JobCardTable.Cols.PRV_SEQUENCE, jobCard.prv_sequence != null ? jobCard.prv_sequence : "");
            values.put(JobCardTable.Cols.ZONE_CODE, jobCard.zone_code != null ? jobCard.zone_code : "");
            values.put(JobCardTable.Cols.CATEGORY_ID, jobCard.category_id != null ? jobCard.category_id : "");
            values.put(JobCardTable.Cols.AVG_CONSUMTION, jobCard.avg_consumption != null ? jobCard.avg_consumption : "");
            values.put(JobCardTable.Cols.METER_DIGIT, jobCard.meter_digit != null ? jobCard.meter_digit : "");
            values.put(JobCardTable.Cols.ACCOUNT_NO, jobCard.account_no != null ? jobCard.account_no : "");
            values.put(JobCardTable.Cols.ROUTE_IMAGE, jobCard.route_image != null ? jobCard.route_image : "");
            values.put(JobCardTable.Cols.CURRENT_SEQUENCE, jobCard.current_sequence != null ? jobCard.current_sequence : "");
            values.put(JobCardTable.Cols.SNF, jobCard.snf != null ? jobCard.snf : "");
            values.put(JobCardTable.Cols.PRV_STATUS, jobCard.prv_status != null ? jobCard.prv_status : "");
            values.put(JobCardTable.Cols.ZONE_NAME, jobCard.zone_name != null ? jobCard.zone_name + " (" + jobCard.zone_code + ")" : "");
            values.put(JobCardTable.Cols.ATTEMPT, jobCard.attempt != null ? jobCard.attempt : "");
            values.put(JobCardTable.Cols.PDC, jobCard.pdc != null ? jobCard.pdc : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private static ContentValues getContentValuesConsumerTable(Context context, Consumer consumer) {
        ContentValues values = new ContentValues();
        try {
            values.put(ConsumerTable.Cols.CONSUMER_ID, consumer.consumer_no != null ? consumer.consumer_no : "");
            values.put(ConsumerTable.Cols.METER_READER_ID, consumer.meter_reader_id != null ? consumer.meter_reader_id : "");
            values.put(ConsumerTable.Cols.CONSUMER_NAME, consumer.consumer_name != null ? consumer.consumer_name : "");
            values.put(ConsumerTable.Cols.PHONE_NO, consumer.contact_no != null ? consumer.contact_no : "");
            values.put(ConsumerTable.Cols.ADDRESS, consumer.address != null ? consumer.address : "");
            values.put(ConsumerTable.Cols.POLE_NO, consumer.pole_no != null ? consumer.pole_no : "");
            values.put(ConsumerTable.Cols.ROUTE_ID, consumer.route_code != null ? consumer.route_code : "");
            values.put(ConsumerTable.Cols.BILL_CYCLE_CODE, consumer.bill_cycle_code != null ? consumer.bill_cycle_code : "");
            values.put(ConsumerTable.Cols.METER_NO, consumer.meter_no != null ? consumer.meter_no : "");
            values.put(ConsumerTable.Cols.DT_CODE, consumer.dtc != null ? consumer.dtc : "");
            values.put(ConsumerTable.Cols.MONTH, consumer.reading_month != null ? consumer.reading_month : "");
            values.put(ConsumerTable.Cols.CONNECTION_STATUS, consumer.connection_status != null ? consumer.connection_status : "");
            values.put(ConsumerTable.Cols.EMAIL_ID, consumer.email_id != null ? consumer.email_id : "");
            values.put(ConsumerTable.Cols.CURRENT_METER_READING, consumer.current_meter_reading != null ? consumer.current_meter_reading : "");
            values.put(ConsumerTable.Cols.METER_STATUS, consumer.meter_status != null ? consumer.meter_status : "");
            values.put(ConsumerTable.Cols.READER_STATUS, consumer.reader_status != null ? consumer.reader_status : "");
            values.put(ConsumerTable.Cols.READING_IMAGE, consumer.meter_image != null ? consumer.meter_image.image != null ? consumer.meter_image.image : "" : "");
            values.put(ConsumerTable.Cols.COMMENTS, consumer.reader_remark_comment != null ? consumer.reader_remark_comment : "");
            values.put(ConsumerTable.Cols.IS_SUSPICIOUS, consumer.suspicious_activity != null ? consumer.suspicious_activity : "");
            values.put(ConsumerTable.Cols.SUSPICIOUS_REMARKS, consumer.suspicious_remark != null ? consumer.suspicious_remark : "");
            values.put(ConsumerTable.Cols.SUSPICIOUS_READING_IMAGE, consumer.suspicious_activity_image != null ? consumer.suspicious_activity_image.image != null ? consumer.suspicious_activity_image.image : "" : "");
            values.put(ConsumerTable.Cols.CUR_LAT, consumer.cur_lat != null ? consumer.cur_lat : "");
            values.put(ConsumerTable.Cols.CUR_LNG, consumer.cur_lng != null ? consumer.cur_lng : "");
            values.put(ConsumerTable.Cols.READING_DATE, consumer.reading_date != null ? consumer.reading_date : "");
            values.put(ConsumerTable.Cols.READING_TAKEN_BY, consumer.reading_taken_by != null ? consumer.reading_taken_by : "");
            values.put(ConsumerTable.Cols.LOCATION_GUIDANCE, consumer.location_guidance != null ? consumer.location_guidance : "");
            values.put(ConsumerTable.Cols.CURRENT_METER_READING, consumer.current_meter_reading != null ? consumer.current_meter_reading : "");
            values.put(ConsumerTable.Cols.CURRENT_KVA_READING, consumer.current_kva_reading != null ? consumer.current_kva_reading : "");
            values.put(ConsumerTable.Cols.CURRENT_KVAH_READING, consumer.current_kvah_reading != null ? consumer.current_kvah_reading : "");
            values.put(ConsumerTable.Cols.ISKWHROUNDCOMPLETED, consumer.iskwhroundcompleted != null ? consumer.iskwhroundcompleted : "");
            values.put(ConsumerTable.Cols.ISKVAHROUNDCOMPLETED, consumer.iskvahroundcompleted != null ? consumer.iskvahroundcompleted : "");
            values.put(ConsumerTable.Cols.PANEL_NO, consumer.panel_no != null ? consumer.panel_no : "");
            values.put(ConsumerTable.Cols.MOBILE_NO, consumer.mobile_no != null ? consumer.mobile_no : "");
            values.put(ConsumerTable.Cols.METER_TYPE, consumer.meter_type != null ? consumer.meter_type : "");
            values.put(ConsumerTable.Cols.NEW_SEQUENCE, consumer.new_sequence != null ? consumer.new_sequence : "");
            values.put(ConsumerTable.Cols.ZONE_CODE, consumer.zone_code != null ? consumer.zone_code : "");
            values.put(ConsumerTable.Cols.CURRENT_KW_READING, consumer.current_kw_reading != null ? consumer.current_kw_reading : "");
            values.put(ConsumerTable.Cols.CURRENT_PF_READING, consumer.current_pf_reading != null ? consumer.current_pf_reading : "");
            values.put(ConsumerTable.Cols.Meter_Location, consumer.meter_location != null ? consumer.meter_location : "");
            values.put(ConsumerTable.Cols.TIME_TAKEN, consumer.time_taken != null ? consumer.time_taken : "");




            values.put(ConsumerTable.Cols.AIR_CONDITIONER_EXIST, consumer.air_conditioner_exist != null ? consumer.air_conditioner_exist : "");
            values.put(ConsumerTable.Cols.IS_PLASTIC_COVER_CUT, consumer.is_plastic_cover_cut != null ? consumer.is_plastic_cover_cut : "");
            values.put(ConsumerTable.Cols.NO_OF_AIR_CONDITIONERS, consumer.no_of_air_conditioners != null ? consumer.no_of_air_conditioners : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    private static ContentValues getContentValuesUploadsHistoryTable(Context context, UploadsHistory uploadsHistory) {
        ContentValues values = new ContentValues();
        try {
            values.put(UploadsHistoryTable.Cols.CONSUMER_ID, uploadsHistory.consumer_no != null ? uploadsHistory.consumer_no : "");
            values.put(UploadsHistoryTable.Cols.ROUTE_ID, uploadsHistory.route_code != null ? uploadsHistory.route_code : "");
            values.put(UploadsHistoryTable.Cols.BILL_CYCLE_CODE, uploadsHistory.bill_cycle_code != null ? uploadsHistory.bill_cycle_code : "");
            values.put(UploadsHistoryTable.Cols.MONTH, uploadsHistory.month != null ? uploadsHistory.month : "");
            values.put(UploadsHistoryTable.Cols.UPLOAD_STATUS, uploadsHistory.upload_status != null ? uploadsHistory.upload_status : "");
            values.put(UploadsHistoryTable.Cols.READING_DATE, uploadsHistory.reading_date != null ? uploadsHistory.reading_date : "");
            values.put(UploadsHistoryTable.Cols.METER_READER_ID, uploadsHistory.meter_reader_id != null ? uploadsHistory.meter_reader_id : "");


        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    /**
     * Clear all the table contents
     *
     * @param context Context
     */
/*    public static void clearAllCache(Context context) {
        deleteAllUserLoginDetails(context);
    }*/

    /**
     * Clear contents from UserLogin Table
     *
     * @param context
     */
/*    public static void deleteAllUserLoginDetails(Context context) {
        try {
            context.getContentResolver().delete(LoginTable.CONTENT_URI, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/
    public static ArrayList<JobCard> getJobCards(Context context, String reader_id, String jobCardStatus, String revist) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' and " + JobCardTable.Cols.IS_REVISIT + "='" + revist + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, null,
                condition, null, JobCardTable.Cols.ACCOUNT_NO + " ASC");
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }


    public static ArrayList<JobCard> getJobCards(Context context, String reader_id, String jobCardStatus) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + JobCardTable.TABLE_NAME + " where " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND " + JobCardTable.Cols.JOB_CARD_ID + " IN(Select DISTINCT " + MeterReadingTable.Cols.JOB_CARD_ID + " from " + MeterReadingTable.TABLE_NAME + " where " + MeterReadingTable.Cols.METER_READER_ID + "='" + reader_id + "')", null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen())
            db.close();
        return jobCards;
    }

    public static ArrayList<JobCard> getJobCard(Context context, String reader_id, String jobCardStatus, String jobCardId) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + JobCardTable.TABLE_NAME + " where " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND " + JobCardTable.Cols.JOB_CARD_ID + " IN(Select DISTINCT " + MeterReadingTable.Cols.JOB_CARD_ID + " from " + MeterReadingTable.TABLE_NAME + " where " + MeterReadingTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " + MeterReadingTable.Cols.JOB_CARD_ID + " = '" + jobCardId + "')", null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen())
            db.close();
        return jobCards;
    }

    public static ArrayList<JobCard> getJobCardsFilter(Context context, String reader_id, String jobCardStatus, String revist, String binder) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("Select * from " + JobCardTable.TABLE_NAME + " where " + JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' and " + JobCardTable.Cols.IS_REVISIT + "='" + revist + "' and " + JobCardTable.Cols.ROUTE_ID + "='" + binder + "'" , null);

        Cursor cursor = db.rawQuery("select * from " + JobCardTable.TABLE_NAME + " where " + JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' and " + JobCardTable.Cols.IS_REVISIT + "='" + revist + "' and " + JobCardTable.Cols.ROUTE_ID + "='" + binder + "' order by cast(" + JobCardTable.Cols.ACCOUNT_NO + " as signed)", null);

        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen())
            db.close();
        return jobCards;
    }

    public static ArrayList<JobCard> getJobCardBySequence(Context context, String reader_id, String jobCardStatus, String prv, String routid) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + JobCardTable.TABLE_NAME + " where " + JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND " + JobCardTable.Cols.PRV_SEQUENCE + " = '" + prv + "' AND " + JobCardTable.Cols.METER_READER_ID + " = '" + reader_id + "' AND " + JobCardTable.Cols.ROUTE_ID + " = '" + routid + "'", null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen())
            db.close();
        return jobCards;
    }

    public static JobCard getJobCardsbyConsumerNo(Context context, String consumerNo) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + JobCardTable.TABLE_NAME + " where " + JobCardTable.Cols.CONSUMER_NO + "='" + consumerNo.trim() + "' AND " + JobCardTable.Cols.JOB_CARD_STATUS + " != 'COMPLETED'", null);

        JobCard user = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            user = getJobCardFromCursor(cursor);
            if (cursor != null) {
                cursor.close();
            }
        }
        if (db.isOpen()) {
            db.close();
        }
        return user;
    }

    public static ArrayList<JobCard> getJobCardsbysearch(Context context, String query, String reader_id, String jobCardStatus) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' and " +
                JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND ( " +
                JobCardTable.Cols.ACCOUNT_NO + " LIKE '%" + query + "%'  OR " +
                JobCardTable.Cols.METER_ID + " LIKE '%" + query + "%'   OR " +
                JobCardTable.Cols.CONSUMER_NAME + " LIKE '%" + query + "%' ) ";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, null, condition, null, null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static ArrayList<JobCard> getJobCardsbysearch(Context context, String query, String reader_id,
                                                         String jobCardStatus, String binder) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
                JobCardTable.Cols.ROUTE_ID + "='" + binder + "' AND " +
                JobCardTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND ( " +
                JobCardTable.Cols.ACCOUNT_NO + " LIKE '%" + query + "%'  OR " +
                JobCardTable.Cols.METER_ID + " LIKE '%" + query + "%'   OR " +
                JobCardTable.Cols.CONSUMER_NAME + " LIKE '%" + query + "%' ) ";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, null, condition, null, null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }


    public static ArrayList<BillCard> getBillCardsbysearch(Context context, String query, String reader_id, String jobCardStatus) {
        String condition = BillTable.Cols.METER_READER_ID + "='" + reader_id + "' and " +
                BillTable.Cols.JOBCARD_STATUS + "='" + jobCardStatus + "' AND ( " +
                BillTable.Cols.METER_NO + " LIKE '%" + query + "%'  OR " +
                BillTable.Cols.CONSUMER_NO + " LIKE '%" + query + "%'   OR " +
                BillTable.Cols.CONSUMER_NAME + " LIKE '%" + query + "%' ) ";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(BillTable.CONTENT_URI, null, condition, null, null);
        ArrayList<BillCard> jobCards = getBillCardListFromCursor(cursor, false);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static ArrayList<Disconnection> getDCCardsbysearch(Context context, String query, String reader_id, String jobCardStatus) {
        String condition = BillTable.Cols.METER_READER_ID + "='" + reader_id + "' and " +
                DisconnectionTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "' AND ( " +
                DisconnectionTable.Cols.CONSUMER_NO + " LIKE '%" + query + "%'  OR " +
                DisconnectionTable.Cols.DISCONNECTION_NOTICE_NO + " LIKE '%" + query + "%'   OR " +
                DisconnectionTable.Cols.NAME + " LIKE '%" + query + "%' ) ";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DisconnectionTable.CONTENT_URI, null, condition, null, null);
        ArrayList<Disconnection> jobCards = getDisconnectionCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static ArrayList<JobCard> getJobCardsCount(Context context, String reader_id, String rout) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' and " +
                JobCardTable.Cols.ROUTE_ID + "='" + rout + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, null, condition, null, null);
        ArrayList<JobCard> jobCards = getJobCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static void saveJobCards(Context mContext, ArrayList<JobCard> jobCards) {
//        int i = 0001;
        for (JobCard jobcard : jobCards) {
//            jobcard.prv_sequence=""+i;
            saveJobCard(mContext, jobcard);
            DatabaseManager.createNewSequence(mContext, jobcard);
//            i++;
        }
    }


    public static void saveBillCards(Context mContext, ArrayList<BillCard> billCards) {
        for (BillCard billcard : billCards) {
            savebillCard(mContext, billcard);
        }

    }

    public static void savebillCard(Context context, BillCard billCard) {
        if (billCard != null) {
            ContentValues values = getContentValuesBillCardTable(context, billCard);
            String condition = BillTable.Cols.JOBCARD_ID + "='" + billCard.jobcard_id + "'";
            saveValues(context, BillTable.CONTENT_URI, values, condition);
        }
    }


    private static ContentValues getContentValuesBillCardTable(Context context, BillCard billCard) {
        ContentValues values = new ContentValues();
        try {
            values.put(BillTable.Cols.JOBCARD_ID, billCard.jobcard_id != null ? billCard.jobcard_id : "");
            values.put(BillTable.Cols.BINDER_CODE, billCard.binder_code != null ? billCard.binder_code : "");
            values.put(BillTable.Cols.CYCLE_CODE, billCard.cycle_code != null ? billCard.cycle_code : "");
            values.put(BillTable.Cols.START_DATE, billCard.start_date != null ? billCard.start_date : "");
            values.put(BillTable.Cols.TAKEN_BY, billCard.taken_by != null ? billCard.taken_by : "");
            values.put(BillTable.Cols.CONSUMER_NO, billCard.consumer_no != null ? billCard.consumer_no : "");
            values.put(BillTable.Cols.END_DATE, billCard.end_date != null ? billCard.end_date : "");
            values.put(BillTable.Cols.JOBCARD_STATUS, billCard.jobcard_status != null ? billCard.jobcard_status : "");
            values.put(BillTable.Cols.END_DATE, billCard.end_date != null ? billCard.end_date : "");
            values.put(BillTable.Cols.METER_READER_ID, billCard.meter_reader_id != null ? billCard.meter_reader_id : "");
            values.put(BillTable.Cols.REMARK, billCard.remark != null ? billCard.remark : "");
            values.put(BillTable.Cols.CONSUMER_NAME, billCard.consumer_name != null ? billCard.consumer_name : "");
            values.put(BillTable.Cols.BILLMONTH, billCard.billmonth != null ? billCard.billmonth : "");
            values.put(BillTable.Cols.READING_DATE, billCard.reading_date != null ? billCard.reading_date : "");
            values.put(BillTable.Cols.METER_NO, billCard.meter_no != null ? billCard.meter_no : "");
            values.put(BillTable.Cols.ADDRESS, billCard.address != null ? billCard.address : "");
            values.put(BillTable.Cols.BINDER_ID, billCard.binder_id != null ? billCard.binder_id : "");
            values.put(BillTable.Cols.PRV_LAT, billCard.prv_lat != null ? billCard.prv_lat : "");
            values.put(BillTable.Cols.PRV_LON, billCard.prv_lon != null ? billCard.prv_lon : "");
            values.put(BillTable.Cols.CUR_LAT, billCard.cur_lat != null ? billCard.cur_lat : "");
            values.put(BillTable.Cols.CUR_LON, billCard.cur_lon != null ? billCard.cur_lon : "");
            values.put(BillTable.Cols.ZONE_CODE, billCard.zone_code != null ? billCard.zone_code : "");
            values.put(BillTable.Cols.ZONE_NAME, billCard.zone_name != null ? billCard.zone_name : "");
            values.put(BillTable.Cols.IS_NEW, billCard.is_new != null ? billCard.is_new : "");
            values.put(BillTable.Cols.BILL_DISTRIBUTED, billCard.bill_distributed != null ? billCard.bill_distributed : "");
            values.put(BillTable.Cols.BILL_RECEIVED, billCard.bill_received != null ? billCard.bill_received : "");
            values.put(BillTable.Cols.ACCOUNT_NO, billCard.account_no != null ? billCard.account_no : "");
            values.put(BillTable.Cols.PHONE_NO, billCard.phone_no != null ? billCard.phone_no : "");
            values.put(BillTable.Cols.TIME_TAKEN, billCard.time_taken != null ? billCard.time_taken : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public static ArrayList<String> getUniqueBinders(Context context, String readerId, String jobCardStatus) {

        String condition = BillTable.Cols.METER_READER_ID + "='" + readerId + "' and " + BillTable.Cols.JOBCARD_STATUS + "='" + jobCardStatus + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(BillTable.CONTENT_URI, new String[]{"DISTINCT " + BillTable.Cols.BINDER_CODE + ", " + BillTable.Cols.BINDER_ID},
                condition, null, null);
        ArrayList<String> routes = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                String route_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BINDER_CODE));
                routes.add(route_code);
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return routes;
    }

    public static ArrayList<BillCard> getBillCards(Context context, String reader_id, String jobCardStatus) {
        String condition = BillTable.Cols.METER_READER_ID + "='" + reader_id
                + "' and " + BillTable.Cols.JOBCARD_STATUS + "='" + jobCardStatus + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(BillTable.CONTENT_URI, null, condition, null, null);
        ArrayList<BillCard> jobCards = getBillCardListFromCursor(cursor, false);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static ArrayList<BillCard> getBinderWiseBillCard(Context context, String readerId, String jobCardStatus,
                                                            String binderCode, String zoneCode) {
        String condition = BillTable.Cols.METER_READER_ID + "='" + readerId
                + "' and " + BillTable.Cols.JOBCARD_STATUS + "='" + jobCardStatus
                + "' and " + BillTable.Cols.ZONE_CODE + "='" + zoneCode
                + "' and " + BillTable.Cols.BINDER_CODE + "='" + binderCode + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(BillTable.CONTENT_URI, null, condition, null, null);
        ArrayList<BillCard> jobCards = getBillCardListFromCursor(cursor, false);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static ArrayList<BillCard> getBinderWiseBillCardTakenBy(Context context, String readerId, String jobCardStatus,
                                                            String binderCode, String zoneCode, String takenBy) {
        String condition = BillTable.Cols.METER_READER_ID + "='" + readerId
                + "' and " + BillTable.Cols.JOBCARD_STATUS + "='" + jobCardStatus
                + "' and " + BillTable.Cols.ZONE_CODE + "='" + zoneCode
                + "' and " + BillTable.Cols.BINDER_CODE + "='" + binderCode
                + "' and " + BillTable.Cols.BILL_DISTRIBUTED + "='" + "true"
                + "' and " + BillTable.Cols.TAKEN_BY + "='" + takenBy + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(BillTable.CONTENT_URI, null, condition, null, null);
        ArrayList<BillCard> jobCards = getBillCardListFromCursor(cursor, false);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static ArrayList<BillCard> getBinderWiseBillCardTotal(Context context, String readerId, String binderCode, String zoneCode) {
        String condition = BillTable.Cols.METER_READER_ID + "='" + readerId
                + "' and " + BillTable.Cols.BINDER_CODE + "='" + binderCode
                + "' and " + BillTable.Cols.ZONE_CODE + "='" + zoneCode + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(BillTable.CONTENT_URI, null, condition, null, null);
        ArrayList<BillCard> jobCards = getBillCardListFromCursor(cursor, false);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    public static ArrayList<BillCard> getBillCardsUnique(Context context, String readerId, String jobCardStatus, String binderCode, boolean isUnique) {
        String condition = BillTable.Cols.METER_READER_ID + "='" + readerId + "' and " + BillTable.Cols.JOBCARD_STATUS + "='" + jobCardStatus
                + "' and " + BillTable.Cols.BINDER_CODE + "='" + binderCode + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(BillTable.CONTENT_URI, new String[]{"DISTINCT " + BillTable.Cols.BINDER_CODE + ", "
                + BillTable.Cols.CYCLE_CODE + ", " + BillTable.Cols.ZONE_NAME + ", " + BillTable.Cols.END_DATE + ", " + BillTable.Cols.ZONE_CODE},
                condition, null, null);
        ArrayList<BillCard> jobCards = getBillCardListFromCursor(cursor, isUnique);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    private static ArrayList<BillCard> getBillCardListFromCursor(Cursor cursor, boolean isUnique) {
        ArrayList<BillCard> billCards = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            BillCard user;
            billCards = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                if (isUnique)
                    user = getBillCardFromCursorUnique(cursor);
                else
                    user = getBillCardFromCursor(cursor);
                billCards.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billCards;
    }

    private static BillCard getBillCardFromCursor(Cursor cursor) {
        BillCard billCard = new BillCard();
        billCard.meter_reader_id = cursor.getString(cursor.getColumnIndex(BillTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.METER_READER_ID)) : "";
        billCard.binder_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.BINDER_CODE)) : "";
        billCard.jobcard_id = cursor.getString(cursor.getColumnIndex(BillTable.Cols.JOBCARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.JOBCARD_ID)) : "";
        billCard.consumer_name = cursor.getString(cursor.getColumnIndex(BillTable.Cols.CONSUMER_NAME)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.CONSUMER_NAME)) : "";
        billCard.cycle_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.CYCLE_CODE)) : "";
        billCard.start_date = cursor.getString(cursor.getColumnIndex(BillTable.Cols.START_DATE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.START_DATE)) : "";
        billCard.end_date = cursor.getString(cursor.getColumnIndex(BillTable.Cols.END_DATE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.END_DATE)) : "";
        billCard.jobcard_status = cursor.getString(cursor.getColumnIndex(BillTable.Cols.JOBCARD_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.JOBCARD_STATUS)) : "";
        billCard.taken_by = cursor.getString(cursor.getColumnIndex(BillTable.Cols.TAKEN_BY)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.TAKEN_BY)) : "";
        billCard.remark = cursor.getString(cursor.getColumnIndex(BillTable.Cols.REMARK)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.REMARK)) : "";
        billCard.consumer_no = cursor.getString(cursor.getColumnIndex(BillTable.Cols.CONSUMER_NO)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.CONSUMER_NO)) : "";
        billCard.billmonth = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BILLMONTH)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.BILLMONTH)) : "";
        billCard.reading_date = cursor.getString(cursor.getColumnIndex(BillTable.Cols.READING_DATE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.READING_DATE)) : "";
        billCard.binder_id = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BINDER_ID)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.BINDER_ID)) : "";
        billCard.cur_lat = cursor.getString(cursor.getColumnIndex(BillTable.Cols.CUR_LAT)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.CUR_LAT)) : "";
        billCard.cur_lon = cursor.getString(cursor.getColumnIndex(BillTable.Cols.CUR_LON)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.CUR_LON)) : "";
        billCard.prv_lat = cursor.getString(cursor.getColumnIndex(BillTable.Cols.PRV_LAT)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.PRV_LAT)) : "";
        billCard.prv_lon = cursor.getString(cursor.getColumnIndex(BillTable.Cols.PRV_LON)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.PRV_LON)) : "";
        billCard.meter_no = cursor.getString(cursor.getColumnIndex(BillTable.Cols.METER_NO)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.METER_NO)) : "";
        billCard.address = cursor.getString(cursor.getColumnIndex(BillTable.Cols.ADDRESS)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.ADDRESS)) : "";
        billCard.zone_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.ZONE_CODE)) : "";
        billCard.zone_name = cursor.getString(cursor.getColumnIndex(BillTable.Cols.ZONE_NAME)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.ZONE_NAME)) : "";
        billCard.is_new = cursor.getString(cursor.getColumnIndex(BillTable.Cols.IS_NEW)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.IS_NEW)) : "";
        billCard.bill_distributed = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BILL_DISTRIBUTED)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.BILL_DISTRIBUTED)) : "";
        billCard.bill_received = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BILL_RECEIVED)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.BILL_RECEIVED)) : "";
        billCard.phone_no = cursor.getString(cursor.getColumnIndex(BillTable.Cols.PHONE_NO)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.PHONE_NO)) : "";
        billCard.account_no = cursor.getString(cursor.getColumnIndex(BillTable.Cols.ACCOUNT_NO)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.ACCOUNT_NO)) : "";


        billCard.time_taken = cursor.getString(cursor.getColumnIndex(BillTable.Cols.TIME_TAKEN)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.TIME_TAKEN)) : "";

        return billCard;
    }

    private static BillCard getBillCardFromCursorUnique(Cursor cursor) {
        BillCard billCard = new BillCard();
        billCard.binder_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.BINDER_CODE)) : "";
        billCard.cycle_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.CYCLE_CODE)) : "";
        billCard.zone_name = cursor.getString(cursor.getColumnIndex(BillTable.Cols.ZONE_NAME)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.ZONE_NAME)) : "";
        billCard.end_date = cursor.getString(cursor.getColumnIndex(BillTable.Cols.END_DATE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.END_DATE)) : "";
        billCard.zone_code = cursor.getString(cursor.getColumnIndex(BillTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(BillTable.Cols.ZONE_CODE)) : "";

        return billCard;
    }


    public static void handleAssignedDeassignedbillJobs(Context mContext, ArrayList<String> re_de_assigned_jobcards, String meter_reader_id) {
        for (String card_id : re_de_assigned_jobcards) {
            deletebillJobCard(mContext, card_id, meter_reader_id);
        }
    }

    public static void deletebillJobs(Context mContext, ArrayList<BillCard> jobcards, String meter_reader_id) {

        for (BillCard card_id : jobcards) {
            deletebillJobCard(mContext, card_id.jobcard_id, meter_reader_id);
        }
    }

    public static void deletebillJobCard(Context context, String card_id, String meter_reader_id) {
        try {
            String condition = BillTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' AND " + BillTable.Cols.JOBCARD_ID + "='" + card_id + "'";
            context.getContentResolver().delete(BillTable.CONTENT_URI, condition, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteJobCard(Context context, String card_id, String meter_reader_id) {
        try {
            String condition = JobCardTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' AND " + JobCardTable.Cols.JOB_CARD_ID + "='" + card_id + "'";
            context.getContentResolver().delete(JobCardTable.CONTENT_URI, condition, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteMeterReading(Context context, String card_id, String meter_reader_id) {
        try {
            String condition = MeterReadingTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' AND " + MeterReadingTable.Cols.JOB_CARD_ID + "='" + card_id + "'";
            context.getContentResolver().delete(MeterReadingTable.CONTENT_URI, condition, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void saveLoginDetails(LoginActivity loginActivity, String user_email, ArrayList<UserProfile> user_info, String fcmToken) {
        DatabaseManager.saveUserProfile(loginActivity, user_email, user_info, fcmToken);
    }

/*    public static void setReadingUploadedTrue(Context mContext, ArrayList<MeterReading> readingsToSend) {
        for (MeterReading meterReading : readingsToSend) {
            meterReading.isUploaded = "True";
            saveMeterReading(mContext, meterReading);
        }
    }*/

    public static void handleAssignedDeassignedJobs(Context mContext, ArrayList<String> re_de_assigned_jobcards, String meter_reader_id) {
        for (String card_id : re_de_assigned_jobcards) {
            deleteJobCard(mContext, card_id, meter_reader_id);
        }
    }

    public static void deleteMeterReadings(Context mContext, ArrayList<MeterReading> readingToUpload) {
        for (MeterReading meter_reading : readingToUpload) {
            deleteMeterReading(mContext, meter_reading.job_card_id, meter_reading.meter_reader_id);
            deleteJobCard(mContext, meter_reading.job_card_id, meter_reading.meter_reader_id);
        }
    }

    public static ArrayList<Consumer> getUnbilledConsumerRecords(Context mContext, String meterReaderId) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + ConsumerTable.TABLE_NAME + " where " + ConsumerTable.Cols.METER_READER_ID + "='" + meterReaderId.trim() + "'", null);
        ArrayList<Consumer> consumerList = getConsumersFromCursor(cursor);
        Consumer user = null;
        if (cursor != null) {
            cursor.close();
        }
        if (db.isOpen()) {
            db.close();
        }
        return consumerList;
    }

    public static void saveNotification(Context context, NotificationCard noti) {
        if (noti != null) {
            ContentValues values = new ContentValues();
            try {
                values.put(NotificationTable.Cols.TITLE, noti.title);
                values.put(NotificationTable.Cols.MSG, noti.message);
                values.put(NotificationTable.Cols.DATE, noti.date);
                values.put(NotificationTable.Cols.IS_READ, "false");
                values.put(NotificationTable.Cols.METER_READER_ID, noti.meter_reader_id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            long newRowId = db.insert(NotificationTable.TABLE_NAME, null, values);
            if (db.isOpen()) {
                db.close();
            }
        }
    }

    public static void setReadNotification(Context context, String title) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NotificationTable.Cols.IS_READ, "true");
        String[] args = new String[]{title};
        db.update(NotificationTable.TABLE_NAME, values, "title=?", args);
        if (db.isOpen()) {
            db.close();
        }
    }

    public static int getCount(Context context, String flag, String meterReaderId) {
        int i = 0;
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + NotificationTable.TABLE_NAME + " where  " + NotificationTable.Cols.IS_READ + " = '" + flag + "' AND "
                + NotificationTable.Cols.METER_READER_ID + " = '" + meterReaderId + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        ArrayList<NotificationCard> noti = new ArrayList<NotificationCard>();

        while (c.moveToNext()) {
            i++;
            NotificationCard notiCard = new NotificationCard();
            notiCard.title = c.getString(c.getColumnIndex("title"));
            notiCard.message = c.getString(c.getColumnIndex("msg"));
            notiCard.date = c.getString(c.getColumnIndex("date"));
            notiCard.is_read = c.getString(c.getColumnIndex("is_read"));
            notiCard.meter_reader_id = c.getString(c.getColumnIndex("meter_reader_id"));
            noti.add(notiCard);
        }
        db.close();
        return i;
    }

    public static ArrayList<NotificationCard> getAllNotification(Context context, String meterReaderId) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + NotificationTable.TABLE_NAME + " WHERE " + NotificationTable.Cols.METER_READER_ID + " = '" + meterReaderId + "'";
        Cursor c = db.rawQuery(selectQuery, null);
        ArrayList<NotificationCard> noti = new ArrayList<NotificationCard>();

        while (c.moveToNext()) {
            NotificationCard notiCard = new NotificationCard();
            notiCard.title = c.getString(c.getColumnIndex("title"));
            notiCard.message = c.getString(c.getColumnIndex("msg"));
            notiCard.date = c.getString(c.getColumnIndex("date"));
            notiCard.is_read = c.getString(c.getColumnIndex("is_read"));
            notiCard.meter_reader_id = c.getString(c.getColumnIndex("meter_reader_id"));
            noti.add(notiCard);
        }
        db.close();
        return noti;
    }

    public static void deleteAccount(Context context, String messageBody) {
        try {
            String condition = NotificationTable.Cols.MSG + "='" + messageBody + "'";
            DatabaseHelper dbHelper = new DatabaseHelper(context);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("Notification", condition, null);
            if (db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<BillCard> getBillMeterReadings(Context context, String reader_id, int limit) {
        String condition = BillTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + BillTable.Cols.JOBCARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_COMPLETED + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(BillTable.CONTENT_URI, null,
                condition, null, BillTable.Cols.METER_READER_ID + " ASC " + " LIMIT " + limit);
        ArrayList<BillCard> meterReadings = getbillMeterReadingsFromCursor(context, cursor);
        if (cursor != null) {
            cursor.close();
        }
        return meterReadings;
    }

    private static ArrayList<BillCard> getbillMeterReadingsFromCursor(Context context, Cursor cursor) {
        ArrayList<BillCard> meterReadings = null;
        if (cursor != null && cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                BillCard meterReading;
                meterReadings = new ArrayList<BillCard>();
                while (!cursor.isAfterLast()) {
                    meterReading = getBillCardFromCursor(cursor);
                    meterReadings.add(meterReading);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return meterReadings;
    }

    public static ArrayList<UploadBillHistory> gethistoryBillCards(Context context, String reader_id) {
        String condition = UploadBillHistoryTable.Cols.METER_READER_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadBillHistoryTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<UploadBillHistory> jobCards = getbillhistoryCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    private static ArrayList<UploadBillHistory> getbillhistoryCardListFromCursor(Cursor cursor) {
        ArrayList<UploadBillHistory> billCards = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            UploadBillHistory user;
            billCards = new ArrayList<UploadBillHistory>();
            while (!cursor.isAfterLast()) {
                user = getbillhistoryCardFromCursor(cursor);
                billCards.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billCards;
    }

    private static UploadBillHistory getbillhistoryCardFromCursor(Cursor cursor) {
        UploadBillHistory billCard = new UploadBillHistory();

        billCard.meter_reader_id = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.METER_READER_ID)) : "";
        billCard.binder_code = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.BINDER_CODE)) : "";
        billCard.jobcard_id = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.JOBCARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.JOBCARD_ID)) : "";
        billCard.consumer_name = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.CONSUMER_NAME)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.CONSUMER_NAME)) : "";
        billCard.cycle_code = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.CYCLE_CODE)) : "";
        billCard.consumer_no = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.CONSUMER_NO)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.CONSUMER_NO)) : "";
        billCard.zone_code = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.ZONE_CODE)) : "";
        billCard.billmonth = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.BILLMONTH)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.BILLMONTH)) : "";
        billCard.reading_date = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.READING_DATE)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.READING_DATE)) : "";
        billCard.meter_no = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.METER_NO)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.METER_NO)) : "";
        billCard.zone_name = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.ZONE_NAME)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.ZONE_NAME)) : "";
        billCard.zone_code = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.ZONE_CODE)) : "";
        billCard.is_new = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.IS_NEW)) != null ? cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.IS_NEW)) : "";

        return billCard;
    }

    public static ArrayList<SummaryCard> getSummaryCard(Context context, String reader_id, String binder) {

        ArrayList<SummaryCard> summaryCardArrayList = new ArrayList<>();

        ArrayList<String> routes = getRoutes(context, reader_id);
        String route_id = "";
        String billcycle = "";
        if (routes != null)
            for (int i = 0; i < routes.size(); i++) {
                SummaryCard lSummaryCard = new SummaryCard();
                route_id = routes.get(i);
                ArrayList<String> bill = getbillcyclecode(context, reader_id, route_id);
                billcycle = bill.get(0);

                lSummaryCard.route_id = route_id;
                lSummaryCard.bill_cycle_code = billcycle;

                //calculate txtDelivered job cards inside single txtBinderCode
                String conditionTotal = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "'";

                ContentResolver resolver = context.getContentResolver();
                Cursor cursorTotal = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionTotal, null, null);

                if (cursorTotal != null) {
                    lSummaryCard.total = cursorTotal.getCount();
                } else {
                    lSummaryCard.total = 0;
                }
                if (cursorTotal != null) {
                    cursorTotal.close();
                }

                //calculate Open job cards inside single txtBinderCode
                String conditionOpen = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "' AND "
                        + JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND "
                        + JobCardTable.Cols.IS_REVISIT + "='False'";

                Cursor cursorOpen = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionOpen, null, null);

                if (cursorOpen != null) {
                    lSummaryCard.open = cursorOpen.getCount();
                } else {
                    lSummaryCard.open = 0;
                }
                if (cursorOpen != null) {
                    cursorOpen.close();
                }

                //calculate Open job cards inside single txtBinderCode
                String conditionRevisit = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND "
                        + JobCardTable.Cols.ROUTE_ID + "='" + route_id + "' AND "
                        + JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND "
                        + JobCardTable.Cols.IS_REVISIT + "='True'";

                Cursor cursorRevisit = resolver.query(JobCardTable.CONTENT_URI, null,
                        conditionRevisit, null, null);

                if (conditionRevisit != null) {
                    lSummaryCard.revisit = cursorRevisit.getCount();
                } else {
                    lSummaryCard.revisit = 0;
                }
                if (conditionRevisit != null) {
                    cursorRevisit.close();
                }

                //calculate completed job cards inside single txtBinderCode
                if (conditionRevisit == null || lSummaryCard.revisit == 0) {
                    lSummaryCard.completed = lSummaryCard.total - lSummaryCard.open;
                } else {
                    lSummaryCard.completed = lSummaryCard.total - lSummaryCard.open - lSummaryCard.revisit;
                }
                summaryCardArrayList.add(lSummaryCard);
            }

        return summaryCardArrayList;
    }

    public static ArrayList<String> getTotalRoutes(Context context, String reader_id) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.ROUTE_ID},
                condition, null, null);
        ArrayList<String> routes = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                String route_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.ROUTE_ID));
                routes.add(route_code);
                cursor.moveToNext();
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return routes;
    }

    public static ArrayList<String> getTotalBillCycleCode(Context context, String reader_id, String route) {
        String condition = JobCardTable.Cols.METER_READER_ID + "='" + reader_id + "' AND " +
//                JobCardTable.Cols.JOB_CARD_STATUS + "='" + AppConstants.JOB_CARD_STATUS_ALLOCATED + "' AND " +
                JobCardTable.Cols.ROUTE_ID + "='" + route + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(JobCardTable.CONTENT_URI, new String[]{"DISTINCT " + JobCardTable.Cols.BILL_CYCLE_CODE},
                condition, null, null);
        ArrayList<String> billcyclecode = null;

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            billcyclecode = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String bill_cycle_code = cursor.getString(cursor.getColumnIndex(JobCardTable.Cols.BILL_CYCLE_CODE));
                billcyclecode.add(bill_cycle_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return billcyclecode;
    }


    public static void createNewSequence(Context context, JobCard seq) {
        if (seq != null) {
            Sequence sequence = new Sequence();
            sequence.cycle_code = seq.bill_cycle_code;
            sequence.route_code = seq.route_code;
            sequence.sequence = seq.current_sequence;
            sequence.meter_reader_id = seq.meter_reader_id;
            sequence.zone_code = seq.zone_code;
            String condition = SequenceTable.Cols.METER_READER_ID + "='" + sequence.meter_reader_id + "' And " +
                    SequenceTable.Cols.ZONE_CODE + "='" + sequence.zone_code + "' AND " +
                    SequenceTable.Cols.BINDER_CODE + "='" + sequence.route_code + "' AND " +
                    SequenceTable.Cols.CYCLE_CODE + "='" + sequence.cycle_code + "'";
            ContentValues values = getContentValuesSequenceTable(context, sequence);
            saveValues(context, SequenceTable.CONTENT_URI, values, condition);
        }
    }

    private static ContentValues getContentValuesSequenceTable(Context context, Sequence sequence) {
        ContentValues values = new ContentValues();
        try {

            values.put(SequenceTable.Cols.ZONE_CODE, sequence.zone_code != null ? sequence.zone_code : "");
            values.put(SequenceTable.Cols.CYCLE_CODE, sequence.cycle_code != null ? sequence.cycle_code : "");
            values.put(SequenceTable.Cols.BINDER_CODE, sequence.route_code != null ? sequence.route_code : "");
            values.put(SequenceTable.Cols.METER_READER_ID, sequence.meter_reader_id != null ? sequence.meter_reader_id : "");
            values.put(SequenceTable.Cols.SEQUENCE, sequence.sequence != null ? sequence.sequence : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public static void UpdateSequence(Context context, Sequence sequence) {
        if (sequence != null) {
            ContentValues values = getContentValuesSequenceTable(context, sequence);
            String condition = SequenceTable.Cols.METER_READER_ID + "='" + sequence.meter_reader_id + "' And " +
                    SequenceTable.Cols.ZONE_CODE + "='" + sequence.zone_code + "' AND " +
                    SequenceTable.Cols.BINDER_CODE + "='" + sequence.route_code + "' AND " +
                    SequenceTable.Cols.CYCLE_CODE + "='" + sequence.cycle_code + "'";
            saveValues(context, SequenceTable.CONTENT_URI, values, condition);
        }
    }

    public static void deleteSequence(Context context, Sequence sequence) {
        String condition = SequenceTable.Cols.METER_READER_ID + "='" + sequence.meter_reader_id + "' And " +
                SequenceTable.Cols.ZONE_CODE + "='" + sequence.zone_code + "' AND " +
                SequenceTable.Cols.BINDER_CODE + "='" + sequence.route_code + "' AND " +
                SequenceTable.Cols.CYCLE_CODE + "='" + sequence.cycle_code + "'";
        ContentResolver resolver = context.getContentResolver();
        resolver.delete(SequenceTable.CONTENT_URI, condition, null);
    }

    public static ArrayList<Sequence> getSequence(Context context, Sequence sequence) {
        String condition = SequenceTable.Cols.METER_READER_ID + "='" + sequence.meter_reader_id + "' And " +
                SequenceTable.Cols.ZONE_CODE + "='" + sequence.zone_code + "' AND " +
                SequenceTable.Cols.BINDER_CODE + "='" + sequence.route_code + "' AND " +
                SequenceTable.Cols.CYCLE_CODE + "='" + sequence.cycle_code + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(SequenceTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<Sequence> seq = getsequenceFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return seq;
    }

    public static ArrayList<Sequence> getAllSequence(Context context, String mr) {
        String condition = SequenceTable.Cols.METER_READER_ID + "='" + mr + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(SequenceTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<Sequence> seq = getsequenceFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return seq;
    }

    private static ArrayList<Sequence> getsequenceFromCursor(Cursor cursor) {
        ArrayList<Sequence> jobCards = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Sequence user;
            jobCards = new ArrayList<Sequence>();
            while (!cursor.isAfterLast()) {
                user = getSequenceFromCursor(cursor);
                jobCards.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    private static Sequence getSequenceFromCursor(Cursor cursor) {
        Sequence jobCard = new Sequence();
        jobCard.meter_reader_id = cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.METER_READER_ID)) : "";
        jobCard.zone_code = cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.ZONE_CODE)) : "";
        jobCard.route_code = cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.BINDER_CODE)) : "";
        jobCard.sequence = cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.SEQUENCE)) != null ? cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.SEQUENCE)) : "";
        jobCard.cycle_code = cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.CYCLE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(SequenceTable.Cols.CYCLE_CODE)) : "";

        return jobCard;
    }

    private static ContentValues getContentValuesDisconnectionTable(Context context, Disconnection disconnection) {
        ContentValues values = new ContentValues();
        try {
            values.put(DisconnectionTable.Cols.METER_READER_ID, disconnection.meter_reader_id != null ? disconnection.meter_reader_id : "");
            values.put(DisconnectionTable.Cols.BINDER_CODE, disconnection.binder_code != null ? disconnection.binder_code : "");
            values.put(DisconnectionTable.Cols.CONSUMER_NO, disconnection.consumer_no != null ? disconnection.consumer_no : "");
            values.put(DisconnectionTable.Cols.NAME, disconnection.consumer_name != null ? disconnection.consumer_name : "");
            values.put(DisconnectionTable.Cols.ADDRESS, disconnection.address != null ? disconnection.address : "");
            values.put(DisconnectionTable.Cols.CONTACT_NO, disconnection.contact_no != null ? disconnection.contact_no : "");
            values.put(DisconnectionTable.Cols.LATITUDE, disconnection.latitude != null ? disconnection.latitude : "");
            values.put(DisconnectionTable.Cols.LONGITUDE, disconnection.longitude != null ? disconnection.longitude : "");
            values.put(DisconnectionTable.Cols.JOB_CARD_STATUS, disconnection.job_card_status != null ? disconnection.job_card_status : "");
            values.put(DisconnectionTable.Cols.JOB_CARD_ID, disconnection.job_card_id != null ? disconnection.job_card_id : "");
            values.put(DisconnectionTable.Cols.ZONE_CODE, disconnection.zone_code != null ? disconnection.zone_code : "");
            values.put(DisconnectionTable.Cols.BILL_MONTH, disconnection.bill_month != null ? disconnection.bill_month : "");
            values.put(DisconnectionTable.Cols.DUE_DATE, disconnection.due_date != null ? disconnection.due_date : "");
            values.put(DisconnectionTable.Cols.NOTICE_DATE, disconnection.notice_date != null ? disconnection.notice_date : "");
            values.put(DisconnectionTable.Cols.DISCONNECTION_NOTICE_NO, disconnection.disconnection_notice_no != null ? disconnection.disconnection_notice_no : "");
            values.put(DisconnectionTable.Cols.TOTOS, disconnection.totos != null ? disconnection.totos : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public static void saveDisconnectionCards(Context mContext, ArrayList<Disconnection> disconnections) {
        for (Disconnection disconnection : disconnections) {
            saveDisconnectionCard(mContext, disconnection);
        }
    }

    public static void saveDisconnectionCard(Context context, Disconnection disconnection) {
        if (disconnection != null) {
            ContentValues values = getContentValuesDisconnectionTable(context, disconnection);
            String condition = DisconnectionTable.Cols.JOB_CARD_ID + "='" + disconnection.job_card_id + "'";
            saveDisconnectionValues(context, DisconnectionTable.CONTENT_URI, values, condition);
        }
    }

    private static void saveDisconnectionValues(Context context, Uri table, ContentValues values, String condition) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(table, null,
                condition, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            resolver.update(table, values, condition, null);
        } else {
            resolver.insert(table, values);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static ArrayList<Disconnection> getDisconnectionOpenCards(Context context, String reader_id, String jobCardStatus) {
        String condition = DisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + DisconnectionTable.Cols.JOB_CARD_STATUS + "='" + jobCardStatus + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DisconnectionTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<Disconnection> jobCards = getDisconnectionCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return jobCards;
    }

    private static ArrayList<Disconnection> getDisconnectionCardListFromCursor(Cursor cursor) {
        ArrayList<Disconnection> disconnections = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            Disconnection user;
            disconnections = new ArrayList<Disconnection>();
            while (!cursor.isAfterLast()) {
                user = getDisconnectionNoticesFromCursor(cursor);
                disconnections.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return disconnections;
    }

    private static Disconnection getDisconnectionNoticesFromCursor(Cursor cursor) {
        Disconnection disconnection = new Disconnection();
        disconnection.meter_reader_id = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.METER_READER_ID)) : "";
        disconnection.binder_code = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.BINDER_CODE)) : "";
        disconnection.consumer_no = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.CONSUMER_NO)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.CONSUMER_NO)) : "";
        disconnection.consumer_name = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.NAME)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.NAME)) : "";
        disconnection.address = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.ADDRESS)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.ADDRESS)) : "";
        disconnection.contact_no = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.CONTACT_NO)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.CONTACT_NO)) : "";
        disconnection.latitude = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.LATITUDE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.LATITUDE)) : "";
        disconnection.longitude = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.LONGITUDE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.LONGITUDE)) : "";
        disconnection.job_card_status = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.JOB_CARD_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.JOB_CARD_STATUS)) : "";
        disconnection.job_card_id = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.JOB_CARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.JOB_CARD_ID)) : "";
        disconnection.zone_code = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.ZONE_CODE)) : "";
        disconnection.bill_month = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.BILL_MONTH)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.BILL_MONTH)) : "";
        disconnection.due_date = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.DUE_DATE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.DUE_DATE)) : "";
        disconnection.notice_date = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.NOTICE_DATE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.NOTICE_DATE)) : "";
        disconnection.disconnection_notice_no = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.DISCONNECTION_NOTICE_NO)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.DISCONNECTION_NOTICE_NO)) : "";
        disconnection.totos = cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.TOTOS)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.TOTOS)) : "";

        return disconnection;
    }

    public static void saveUploadDisconnectionNotices(Context context, UploadDisconnectionNotices uploadDisconnectionNotices) {
        if (uploadDisconnectionNotices != null) {
            ContentValues values = getContentValuesUploadDisconnectionTable(context, uploadDisconnectionNotices);
            saveUploadDisconnectionNotices(context, UploadDisconnectionTable.CONTENT_URI, values);
        }
    }

    private static void saveUploadDisconnectionNotices(Context context, Uri table, ContentValues values) {
        ContentResolver resolver = context.getContentResolver();
        try {
            resolver.insert(table, values);
            Toast.makeText(context, CommonUtils.getString(context, R.string.disconnection_notice_data_saved_successfully), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(context, CommonUtils.getString(context, R.string.failed_to_save_data), Toast.LENGTH_LONG).show();
        }
    }

    private static ContentValues getContentValuesUploadDisconnectionTable(Context context, UploadDisconnectionNotices disconnection) {
        ContentValues values = new ContentValues();
        try {
            values.put(UploadDisconnectionTable.Cols.METER_READER_ID, disconnection.meter_reader_id != null ? disconnection.meter_reader_id : "");
            values.put(UploadDisconnectionTable.Cols.BINDER_CODE, disconnection.binder_code != null ? disconnection.binder_code : "");
            values.put(UploadDisconnectionTable.Cols.CONSUMER_NO, disconnection.consumer_no != null ? disconnection.consumer_no : "");
            values.put(UploadDisconnectionTable.Cols.NAME, disconnection.consumer_name != null ? disconnection.consumer_name : "");
            values.put(UploadDisconnectionTable.Cols.CURRENT_LATITUDE, disconnection.current_latitude != null ? disconnection.current_latitude : "");
            values.put(UploadDisconnectionTable.Cols.CURRENT_LONGITUDE, disconnection.current_longitude != null ? disconnection.current_longitude : "");
            values.put(UploadDisconnectionTable.Cols.JOB_CARD_ID, disconnection.job_card_id != null ? disconnection.job_card_id : "");
            values.put(UploadDisconnectionTable.Cols.ZONE_CODE, disconnection.zone_code != null ? disconnection.zone_code : "");
            values.put(UploadDisconnectionTable.Cols.BILL_MONTH, disconnection.bill_month != null ? disconnection.bill_month : "");
            values.put(UploadDisconnectionTable.Cols.CURRENT_DATE, disconnection.current_date != null ? disconnection.current_date : "");
            values.put(UploadDisconnectionTable.Cols.DELIVERY_STATUS, disconnection.delivery_status != null ? disconnection.delivery_status : "");
            values.put(UploadDisconnectionTable.Cols.DELIVERY_REMARK, disconnection.delivery_remark != null ? disconnection.delivery_remark : "");
            values.put(UploadDisconnectionTable.Cols.IS_NEW, disconnection.is_new != null ? disconnection.is_new : "");

            values.put(UploadDisconnectionTable.Cols.METER_NO, disconnection.meter_no != null ? disconnection.meter_no != null ? disconnection.meter_no : "" : "");
            values.put(UploadDisconnectionTable.Cols.METER_IMAGE, disconnection.meter_image != null ? disconnection.meter_image.image != null ? disconnection.meter_image.image : "" : "");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public static void updateDCJobCardStatus(Context context, Disconnection disconnection, String status) {
        if (disconnection != null)

        {
            ContentValues values = getContentValuesDisconnectionTable(context, disconnection);
            values.put(DisconnectionTable.Cols.JOB_CARD_STATUS, status);
            // values.put(JobCardTable.Cols.IS_REVISIT, "False");
            String condition = DisconnectionTable.Cols.JOB_CARD_ID + "='" + disconnection.job_card_id + "'";
            saveValues(context, DisconnectionTable.CONTENT_URI, values, condition);
        }
    }

    public static ArrayList<UploadDisconnectionNotices> getCompletedDCNoticesCards(Context context, String reader_id) {
        String condition = UploadDisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadDisconnectionTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices = getCompletedDCNoticesCardsFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return uploadDisconnectionNotices;
    }


    private static ArrayList<UploadDisconnectionNotices> getCompletedDCNoticesCardsFromCursor(Cursor cursor) {
        ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            UploadDisconnectionNotices user;
            uploadDisconnectionNotices = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                user = getUploadDCNoticesFromCursor(cursor);
                uploadDisconnectionNotices.add(user);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return uploadDisconnectionNotices;
    }

    private static UploadDisconnectionNotices getUploadDCNoticesFromCursor(Cursor cursor) {
        UploadDisconnectionNotices uploadDisconnectionNotices = new UploadDisconnectionNotices();
        uploadDisconnectionNotices.meter_reader_id = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionTable.Cols.METER_READER_ID)) : "";
        uploadDisconnectionNotices.binder_code = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.BINDER_CODE)) : "";
        uploadDisconnectionNotices.consumer_no = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CONSUMER_NO)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CONSUMER_NO)) : "";
        uploadDisconnectionNotices.consumer_name = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.NAME)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.NAME)) : "";
        uploadDisconnectionNotices.current_latitude = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_LATITUDE)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_LATITUDE)) : "";
        uploadDisconnectionNotices.current_longitude = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_LONGITUDE)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_LONGITUDE)) : "";
        uploadDisconnectionNotices.job_card_id = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.ID)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.ID)) : "";
        uploadDisconnectionNotices.zone_code = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.ZONE_CODE)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.ZONE_CODE)) : "";
        uploadDisconnectionNotices.bill_month = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.BILL_MONTH)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.BILL_MONTH)) : "";
        uploadDisconnectionNotices.current_date = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_DATE)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.CURRENT_DATE)) : "";
        uploadDisconnectionNotices.delivery_status = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.DELIVERY_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.DELIVERY_STATUS)) : "";
        uploadDisconnectionNotices.delivery_remark = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.DELIVERY_REMARK)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.DELIVERY_REMARK)) : "";
        uploadDisconnectionNotices.is_new = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.IS_NEW)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.IS_NEW)) : "";
        uploadDisconnectionNotices.meter_no = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.METER_NO)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.METER_NO)) : "";

        MeterImage meterImage = new MeterImage();
        meterImage.name = "dc_" + uploadDisconnectionNotices.consumer_no + ".JPEG";
        meterImage.image = cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.METER_IMAGE)) != null ? cursor.getString(cursor.getColumnIndex(UploadDisconnectionTable.Cols.METER_IMAGE)) : "";
        meterImage.content_type = "image/jpeg";
        uploadDisconnectionNotices.meter_image = meterImage;


        return uploadDisconnectionNotices;
    }

    public static ArrayList<UploadDisconnectionNotices> getDisconnectionNotices(Context context, String reader_id, int limit) {
//        String condition = DisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "' and " + DisconnectionTable.Cols.IS_UPLOADED + "='False'";
        String condition = UploadDisconnectionTable.Cols.METER_READER_ID + "='" + reader_id + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadDisconnectionTable.CONTENT_URI, null,
                condition, null, UploadDisconnectionTable.Cols.METER_READER_ID + " ASC " + " LIMIT "
                        + limit);
        ArrayList<UploadDisconnectionNotices> disconnectionNotices = getDCNoticesFromCursor(context, cursor);
        if (cursor != null) {
            cursor.close();
        }
        return disconnectionNotices;
    }

    private static ArrayList<UploadDisconnectionNotices> getDCNoticesFromCursor(Context context, Cursor cursor) {
        ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices = null;
        if (cursor != null && cursor.getCount() > 0) {
            try {
                cursor.moveToFirst();
                UploadDisconnectionNotices uploadDisconnectionNotices1;
                uploadDisconnectionNotices = new ArrayList<UploadDisconnectionNotices>();
                while (!cursor.isAfterLast()) {
                    uploadDisconnectionNotices1 = getUploadDCNoticesFromCursor(cursor);
                    uploadDisconnectionNotices.add(uploadDisconnectionNotices1);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return uploadDisconnectionNotices;
    }

    public static void handleDeassignedDCNotices(Context mContext, ArrayList<String> re_de_assigned_jobcards, String meter_reader_id) {
        for (String card_id : re_de_assigned_jobcards) {
            deleteDCNoticeCard(mContext, card_id, meter_reader_id, 0);
        }
    }

    public static void deleteDCNoticeCard(Context context, String card_id, String meter_reader_id, int fromTable) {
        try {
            if (fromTable == 0) {
                String condition = DisconnectionTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' AND "
                        + DisconnectionTable.Cols.JOB_CARD_ID + "='" + card_id + "'";
                context.getContentResolver().delete(DisconnectionTable.CONTENT_URI, condition, null);
            } else if (fromTable == 1) {
                String condition = UploadDisconnectionTable.Cols.METER_READER_ID + "='" + meter_reader_id + "' AND "
                        + UploadDisconnectionTable.Cols.CONSUMER_NO + "='" + card_id + "'";
                context.getContentResolver().delete(UploadDisconnectionTable.CONTENT_URI, condition, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deleteDCNotices(Context mContext, ArrayList<UploadDisconnectionNotices> uploadDisconnectionNotices, String meter_reader_id) {
        for (UploadDisconnectionNotices disconnectionNotices : uploadDisconnectionNotices) {
            deleteDCNoticeCard(mContext, disconnectionNotices.consumer_no, meter_reader_id, 1);
        }
    }

    public static void saveDCNoticesHistory(Context context, DisconnectionHistory disconnectionHistory) {
        if (disconnectionHistory != null) {
            ContentValues values = getContentValuesDisconnectionHistoryTable(disconnectionHistory);
            String condition = DisconnectionHistoryTable.Cols.METER_READER_ID + "='" + disconnectionHistory.meter_reader_id + "' AND "
                    + DisconnectionHistoryTable.Cols.JOB_CARD_ID + "='" + disconnectionHistory.job_card_id + "'";
            saveValues(context, DisconnectionHistoryTable.CONTENT_URI, values, condition);
        }
    }

    private static ContentValues getContentValuesDisconnectionHistoryTable(DisconnectionHistory disconnectionHistory) {
        ContentValues values = new ContentValues();
        try {
            values.put(DisconnectionHistoryTable.Cols.METER_READER_ID, disconnectionHistory.meter_reader_id != null ? disconnectionHistory.meter_reader_id : "");
            values.put(DisconnectionHistoryTable.Cols.BINDER_CODE, disconnectionHistory.binder_code != null ? disconnectionHistory.binder_code : "");
            values.put(DisconnectionHistoryTable.Cols.BILL_MONTH, disconnectionHistory.bill_month != null ? disconnectionHistory.bill_month : "");
            values.put(DisconnectionHistoryTable.Cols.JOB_CARD_ID, disconnectionHistory.job_card_id != null ? disconnectionHistory.job_card_id : "");
            values.put(DisconnectionHistoryTable.Cols.DATE, disconnectionHistory.date != null ? disconnectionHistory.date : "");
            values.put(DisconnectionHistoryTable.Cols.DELIVERY_STATUS, disconnectionHistory.delivery_status != null ? disconnectionHistory.delivery_status : "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return values;
    }

    public static int deleteDCNoticesHistory(Context context, String mr) {
        String condition = CommonUtils.getPreviousDateCondition(mr);
        ContentResolver resolver = context.getContentResolver();
        int deleted = resolver.delete(UploadsHistoryTable.CONTENT_URI, condition, null);
        return deleted;
    }

    public static ArrayList<String> getDCNoticeHistoryBinders(Context context, String date) {
        ArrayList<String> routes = null;
        String condition = DisconnectionHistoryTable.Cols.DATE + "='" + date + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DisconnectionHistoryTable.CONTENT_URI, new String[]{"DISTINCT " + DisconnectionHistoryTable.Cols.BINDER_CODE},
                condition, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String route_code = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.BINDER_CODE));
                routes.add(route_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return routes;
    }

    public static ArrayList<DisconnectionHistory> getDCNoticesHistory(Context context, String date, String routeId, String meterReaderId) {
        String condition = DisconnectionHistoryTable.Cols.DATE + "='" + date + "' AND "
                + DisconnectionHistoryTable.Cols.BINDER_CODE + "='" + routeId + "' AND "
                + DisconnectionHistoryTable.Cols.METER_READER_ID + "='" + meterReaderId + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(DisconnectionHistoryTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<DisconnectionHistory> uploadsHistoryFromCursor = getDCNOticeHistoryFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return uploadsHistoryFromCursor;
    }

    private static ArrayList<DisconnectionHistory> getDCNOticeHistoryFromCursor(Cursor cursor) {
        ArrayList<DisconnectionHistory> disconnectionHistories = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            DisconnectionHistory uploadsHistory;
            disconnectionHistories = new ArrayList<>();
            while (!cursor.isAfterLast()) {
                uploadsHistory = getDCNoticesHistoryFromCursor(cursor);
                disconnectionHistories.add(uploadsHistory);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return disconnectionHistories;
    }

    private static DisconnectionHistory getDCNoticesHistoryFromCursor(Cursor cursor) {
        DisconnectionHistory disconnectionHistory = new DisconnectionHistory();
        disconnectionHistory.meter_reader_id = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.METER_READER_ID)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.METER_READER_ID)) : "";
        disconnectionHistory.binder_code = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.BINDER_CODE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.BINDER_CODE)) : "";
        disconnectionHistory.job_card_id = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.JOB_CARD_ID)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.JOB_CARD_ID)) : "";
        disconnectionHistory.date = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.DATE)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.DATE)) : "";
        disconnectionHistory.bill_month = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.BILL_MONTH)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.BILL_MONTH)) : "";
        disconnectionHistory.delivery_status = cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.DELIVERY_STATUS)) != null ? cursor.getString(cursor.getColumnIndex(DisconnectionHistoryTable.Cols.DELIVERY_STATUS)) : "";

        return disconnectionHistory;
    }


    public static BillCard getBillCardsbyConsumerNo(Context context, String consumerNo) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + BillTable.TABLE_NAME + " where " + BillTable.Cols.CONSUMER_NO + "='" + consumerNo.trim() + "' AND " + BillTable.Cols.JOBCARD_STATUS + " = 'Started'", null);

        BillCard user = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            user = getBillCardFromCursor(cursor);
            if (cursor != null) {
                cursor.close();
            }
        }
        if (db.isOpen()) {
            db.close();
        }
        return user;
    }

    public static Disconnection getDcCardsbyConsumerNo(Context context, String consumerNo) {
        SQLiteDatabase db = DatabaseProvider.dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + DisconnectionTable.TABLE_NAME + " where " + DisconnectionTable.Cols.CONSUMER_NO + "='" + consumerNo.trim() + "' AND " + DisconnectionTable.Cols.JOB_CARD_STATUS + " != 'COMPLETED'", null);

        Disconnection user = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            user = getDisconnectionNoticesFromCursor(cursor);
            if (cursor != null) {
                cursor.close();
            }
        }
        if (db.isOpen()) {
            db.close();
        }
        return user;
    }


    public static ArrayList<String> getUploadsHistoryBillRoutes(Context context, String date, String meterreaderid) {
        ArrayList<String> routes = null;
        String condition = UploadBillHistoryTable.Cols.READING_DATE + "='" + date + "' AND "
                + UploadBillHistoryTable.Cols.METER_READER_ID + "='" + meterreaderid + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadBillHistoryTable.CONTENT_URI, new String[]{"DISTINCT " + UploadBillHistoryTable.Cols.BINDER_CODE},
                condition, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            routes = new ArrayList<String>();
            while (!cursor.isAfterLast()) {
                String route_code = cursor.getString(cursor.getColumnIndex(UploadBillHistoryTable.Cols.BINDER_CODE));
                routes.add(route_code);
                cursor.moveToNext();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return routes;
    }

    public static ArrayList<UploadBillHistory> getUploadsHistoryBill(Context context, String date, String routeId, String meterreaderid) {
        String condition = UploadBillHistoryTable.Cols.READING_DATE + "='" + date + "' AND "
                + UploadBillHistoryTable.Cols.BINDER_CODE + "='" + routeId + "' AND "
                + UploadBillHistoryTable.Cols.METER_READER_ID + "='" + meterreaderid + "'";
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(UploadBillHistoryTable.CONTENT_URI, null,
                condition, null, null);
        ArrayList<UploadBillHistory> uploadsHistoryList = getbillhistoryCardListFromCursor(cursor);
        if (cursor != null) {
            cursor.close();
        }
        return uploadsHistoryList;
    }
}