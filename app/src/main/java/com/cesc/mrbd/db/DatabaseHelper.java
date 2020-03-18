/*
' History Header:      Version         - Date        - Developer Name   - Work Description
' History       :        1.0           - Aug-2016    - Bynry01  - SqliteOpenHeler class for app database
 */

/*
 ##############################################################################################
 #####                                                                                    #####                                                                        
 #####     FILE              : DatabaseHelper.Java 	       						          #####                 
 #####     CREATED BY        : Bynry01                                                    #####
 #####     CREATION DATE     : Aug-2016                                                   #####
 #####                                                                                    #####                                                                              
 #####     MODIFIED  BY      : Bynry01                                                    #####
 #####     MODIFIED ON       :                                                   	      #####                          
 #####                                                                                    #####                                                                              
 #####     CODE BRIEFING     : DatabaseHelper Class.          			   			      #####          
 #####                         SqliteOpenHeler class for application database			  #####
 #####                                                                                    #####                                                                              
 #####     COMPANY           : Bynry.                                                     #####
 #####                                                                                    #####                                                                              
 ##############################################################################################
 */
package com.cesc.mrbd.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
import com.cesc.mrbd.models.MeterReading;

import java.text.MessageFormat;


/**
 * SqliteOpenHeler class for application database
 *
 * @author Bynry01
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String KEY_CREATE_TABLE = "CREATE TABLE IF NOT EXISTS {0} ({1})";
    public static final String KEY_DROP_TABLE = "DROP TABLE IF EXISTS {0}";
    public final static String SQL = "SELECT COUNT(*) FROM sqlite_master WHERE name=?";
    private static final int CURRENT_DB_VERSION = 11;
    private static final String DB_NAME = "CESC.db";
    private static final String DROP_RECORD_TRIGGER = "drop_records";

    /**
     * Constructor using context for the class
     *
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, CURRENT_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createLoginTable(db);
        createConsumerTable(db);
        createJobCardTable(db);
        createMeterReadingTable(db);
        createUserProfileTable(db);
        createUploadsHistoryTable(db);
        deleteJobCardsTrigger(db, DROP_RECORD_TRIGGER);
        createNotificationTable(db);
        createBillTable(db);
        createUploadBillHistory(db);
        createSequenceTable(db);
        createDisconnectionTable(db);
        createUploadDisconnectionTable(db);
        createDisconnectionHistoryTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*dropTable(sqLiteDatabase, JobCardTable.TABLE_NAME);
        createJobCardTable(sqLiteDatabase);
        dropTable(sqLiteDatabase, MeterReadingTable.TABLE_NAME);
        createMeterReadingTable(sqLiteDatabase);
        dropTable(sqLiteDatabase, ConsumerTable.TABLE_NAME);
        createConsumerTable(sqLiteDatabase);
        dropTable(sqLiteDatabase, BillTable.TABLE_NAME);
        createBillTable(sqLiteDatabase);*/
        dropTable(sqLiteDatabase, UploadDisconnectionTable.TABLE_NAME);
        createUploadDisconnectionTable(sqLiteDatabase);
        try {
            /*String upgradeQuery = "ALTER TABLE JobCardTable ADD COLUMN pdc VARCHAR DEFAULT 'false' ";
            sqLiteDatabase.execSQL(upgradeQuery);

            String upgradeQuery_bill_timer = "ALTER TABLE BillTable ADD COLUMN time_taken VARCHAR DEFAULT '0' ";
            sqLiteDatabase.execSQL(upgradeQuery_bill_timer);
*/
           /* String upgradeQuery_consumer_category_remark = "ALTER TABLE MeterReadingTable ADD COLUMN consumer_category_remark VARCHAR DEFAULT '0' ";
            sqLiteDatabase.execSQL(upgradeQuery_consumer_category_remark);

            String upgradeQuery_suspicious_activity_status = "ALTER TABLE MeterReadingTable ADD COLUMN suspicious_activity_status VARCHAR DEFAULT '0' ";
            sqLiteDatabase.execSQL(upgradeQuery_suspicious_activity_status);

            String upgradeQuery_air_conditioner_exist = "ALTER TABLE MeterReadingTable ADD COLUMN air_conditioner_exist VARCHAR DEFAULT '0' ";
            sqLiteDatabase.execSQL(upgradeQuery_air_conditioner_exist);

            String upgradeQuery_no_of_air_conditioners = "ALTER TABLE MeterReadingTable ADD COLUMN no_of_air_conditioners VARCHAR DEFAULT '0' ";
            sqLiteDatabase.execSQL(upgradeQuery_no_of_air_conditioners);

            String upgradeQuery_is_plastic_cover_cut = "ALTER TABLE MeterReadingTable ADD COLUMN is_plastic_cover_cut VARCHAR DEFAULT '0' ";
            sqLiteDatabase.execSQL(upgradeQuery_is_plastic_cover_cut);*/



          /*  String upgradeQuery2 = "ALTER TABLE MeterReadingTable ADD COLUMN meter_location VARCHAR DEFAULT 'Outside' ";
            sqLiteDatabase.execSQL(upgradeQuery2);

            String upgradeQuery3 = "ALTER TABLE ConsumerTable ADD COLUMN time_taken VARCHAR DEFAULT '0 sec' ";
            sqLiteDatabase.execSQL(upgradeQuery3);

            String upgradeQuery4 = "ALTER TABLE ConsumerTable ADD COLUMN meter_location VARCHAR DEFAULT 'Outside' ";
            sqLiteDatabase.execSQL(upgradeQuery4);*/

            String upgradeQuery_meter_no = "ALTER TABLE UploadDisconnectionTable ADD COLUMN meter_no VARCHAR DEFAULT '0' ";
            sqLiteDatabase.execSQL(upgradeQuery_meter_no);

            String upgradeQuery_meter_image = "ALTER TABLE UploadDisconnectionTable ADD COLUMN meter_image BLOB DEFAULT '0' ";
            sqLiteDatabase.execSQL(upgradeQuery_meter_image);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * creates UserLogin in device database
     *
     * @param db SqliteDatabase instance
     */
    private void createLoginTable(SQLiteDatabase db) {
        String loginTableFields = LoginTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                LoginTable.Cols.USER_LOGIN_ID + " VARCHAR, " +
                LoginTable.Cols.METER_READER_ID + " VARCHAR, " +
                LoginTable.Cols.LOGIN_DATE + " VARCHAR, " +
                LoginTable.Cols.LOGIN_LAT + " VARCHAR, " +
                LoginTable.Cols.LOGIN_LNG + " VARCHAR";
        createTable(db, LoginTable.TABLE_NAME, loginTableFields);
    }

    /**
     * creates UserLogin in device database
     *
     * @param db SqliteDatabase instance
     */
    private void createUserProfileTable(SQLiteDatabase db) {
        String loginTableFields = UserProfileTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserProfileTable.Cols.METER_READER_NAME + " VARCHAR, " +
                UserProfileTable.Cols.METER_READER_ID + " VARCHAR, " +
                UserProfileTable.Cols.ADDRESS + " VARCHAR, " +
                UserProfileTable.Cols.CITY + " VARCHAR, " +
                UserProfileTable.Cols.STATE + " VARCHAR, " +
                UserProfileTable.Cols.EMP_ID + " VARCHAR, " +
                UserProfileTable.Cols.EMAIL_ID + " VARCHAR, " +
                UserProfileTable.Cols.ROLE + " VARCHAR, " +
                UserProfileTable.Cols.STATUS + " VARCHAR, " +
                UserProfileTable.Cols.IMAGE + " VARCHAR, " +
                UserProfileTable.Cols.DEVICE_MAKE + " VARCHAR, " +
                UserProfileTable.Cols.DEVICE_IMEI_ID + " VARCHAR, " +
                UserProfileTable.Cols.APP_LINK + " VARCHAR, " +
                UserProfileTable.Cols.APP_VERSION + " VARCHAR, " +
                UserProfileTable.Cols.DEVICE_TYPE + " VARCHAR, " +
                UserProfileTable.Cols.CONTACT_NO + " VARCHAR, " +
                UserProfileTable.Cols.EMP_TYPE + " VARCHAR, " +
                UserProfileTable.Cols.FCM_TOKEN + " VARCHAR";
        createTable(db, UserProfileTable.TABLE_NAME, loginTableFields);
    }

    private void deleteJobCardsTrigger(SQLiteDatabase db, String trigger_name) {
        if (!exists(db, trigger_name)) {
            db.execSQL("CREATE TRIGGER " + trigger_name + " AFTER DELETE " +
                    "ON " + JobCardTable.TABLE_NAME + " FOR EACH ROW " +
                    "BEGIN " +
                    "DELETE FROM " + MeterReadingTable.TABLE_NAME + " WHERE " + JobCardTable.Cols.JOB_CARD_ID + "=old." + MeterReadingTable.Cols.JOB_CARD_ID + ";" +
                    " END");
        }
    }

    /**
     * creates ConsumerTable in device database
     *
     * @param db SqliteDatabase instance
     */
    private void createConsumerTable(SQLiteDatabase db) {
        String consumerTableFields = ConsumerTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConsumerTable.Cols.CONSUMER_NAME + " VARCHAR, " +
                ConsumerTable.Cols.CONSUMER_ID + " VARCHAR, " +
                ConsumerTable.Cols.METER_NO + " VARCHAR, " +
                ConsumerTable.Cols.DT_CODE + " VARCHAR, " +
                ConsumerTable.Cols.BILL_CYCLE_CODE + " VARCHAR, " +
                ConsumerTable.Cols.POLE_NO + " VARCHAR, " +
                ConsumerTable.Cols.ROUTE_ID + " VARCHAR, " +
                ConsumerTable.Cols.CONNECTION_STATUS + " VARCHAR, " +
                ConsumerTable.Cols.PHONE_NO + " VARCHAR, " +
                ConsumerTable.Cols.ADDRESS + " VARCHAR, " +
                ConsumerTable.Cols.CURRENT_METER_READING + " VARCHAR, " +
                ConsumerTable.Cols.METER_STATUS + " VARCHAR, " +
                ConsumerTable.Cols.READER_STATUS + " VARCHAR, " +
                ConsumerTable.Cols.COMMENTS + " VARCHAR, " +
                ConsumerTable.Cols.MONTH + " VARCHAR, " +
                ConsumerTable.Cols.EMAIL_ID + " VARCHAR, " +
                ConsumerTable.Cols.SUSPICIOUS_READING_IMAGE + " BLOB, " +
                ConsumerTable.Cols.READING_IMAGE + " BLOB, " +
                ConsumerTable.Cols.LOCATION_GUIDANCE + " VARCHAR, " +
                ConsumerTable.Cols.NEW_SEQUENCE + " VARCHAR, " +
                ConsumerTable.Cols.CUR_LAT + " VARCHAR, " +
                ConsumerTable.Cols.CUR_LNG + " VARCHAR, " +
                ConsumerTable.Cols.METER_READER_ID + " VARCHAR, " +
                ConsumerTable.Cols.IS_SUSPICIOUS + " VARCHAR, " +
                ConsumerTable.Cols.SUSPICIOUS_REMARKS + " VARCHAR, " +
                ConsumerTable.Cols.READING_DATE + " VARCHAR, " +
                ConsumerTable.Cols.CURRENT_KVAH_READING + " VARCHAR, " +
                ConsumerTable.Cols.PANEL_NO + " VARCHAR, " +
                ConsumerTable.Cols.MOBILE_NO + " VARCHAR, " +
                ConsumerTable.Cols.CURRENT_KVA_READING + " VARCHAR, " +
                ConsumerTable.Cols.ISKVAHROUNDCOMPLETED + " VARCHAR, " +
                ConsumerTable.Cols.ISKWHROUNDCOMPLETED + " VARCHAR, " +
                ConsumerTable.Cols.METER_TYPE + " VARCHAR, " +
                ConsumerTable.Cols.ZONE_CODE + " VARCHAR, " +
                ConsumerTable.Cols.READING_TAKEN_BY + " VARCHAR, " +
                ConsumerTable.Cols.CURRENT_KW_READING + " VARCHAR, " +
                ConsumerTable.Cols.Meter_Location + " VARCHAR, " +
                ConsumerTable.Cols.TIME_TAKEN + " VARCHAR, " +





                ConsumerTable.Cols.AIR_CONDITIONER_EXIST + " VARCHAR, " +
                ConsumerTable.Cols.NO_OF_AIR_CONDITIONERS + " VARCHAR, " +
                ConsumerTable.Cols.IS_PLASTIC_COVER_CUT + " VARCHAR, " +





                ConsumerTable.Cols.CURRENT_PF_READING + " VARCHAR ";
        createTable(db, ConsumerTable.TABLE_NAME, consumerTableFields);
    }


    /**
     * creates JobCardTable in device database
     *
     * @param db SqliteDatabase instance
     */
    private void createJobCardTable(SQLiteDatabase db) {
        String consumerTableFields = JobCardTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                JobCardTable.Cols.CONSUMER_NAME + " VARCHAR, " +
                JobCardTable.Cols.CONSUMER_NO + " VARCHAR, " +
                JobCardTable.Cols.SCHEDULE_MONTH + " VARCHAR, " +
                JobCardTable.Cols.DT_CODE + " VARCHAR, " +
                JobCardTable.Cols.BILL_CYCLE_CODE + " VARCHAR, " +
                JobCardTable.Cols.POL_NO + " VARCHAR, " +
                JobCardTable.Cols.ROUTE_ID + " VARCHAR, " +
                JobCardTable.Cols.PHONE_NO + " VARCHAR, " +
                JobCardTable.Cols.ADDRESS + " VARCHAR, " +
                JobCardTable.Cols.PRV_METER_READING + " VARCHAR, " +
                JobCardTable.Cols.PRV_LAT + " VARCHAR, " +
                JobCardTable.Cols.PRV_LONG + " VARCHAR, " +
                JobCardTable.Cols.ASSIGNED_DATE + " VARCHAR, " +
                JobCardTable.Cols.LOCATION_GUIDANCE + " VARCHAR, " +
                JobCardTable.Cols.JOB_CARD_STATUS + " VARCHAR, " +
                JobCardTable.Cols.JOB_CARD_ID + " VARCHAR, " +
                JobCardTable.Cols.IS_REVISIT + " VARCHAR, " +
                JobCardTable.Cols.SCHEDULE_END_DATE + " VARCHAR, " +
                JobCardTable.Cols.CURRENT_SEQUENCE + " VARCHAR, " +
                JobCardTable.Cols.PRV_SEQUENCE + " VARCHAR, " +
                JobCardTable.Cols.METER_ID + " VARCHAR, " +
                JobCardTable.Cols.PRV_KVAH_READING + " VARCHAR, " +
                JobCardTable.Cols.PRV_KVA_READING + " VARCHAR, " +
                JobCardTable.Cols.ISKVAHROUNDCOMPLETED + " VARCHAR, " +
                JobCardTable.Cols.ISKWHROUNDCOMPLETED + " VARCHAR, " +
                JobCardTable.Cols.CATEGORY_ID + " VARCHAR, " +
                JobCardTable.Cols.SNF + " VARCHAR, " +
                JobCardTable.Cols.ZONE_CODE + " VARCHAR, " +
                JobCardTable.Cols.AVG_CONSUMTION + " VARCHAR, " +
                JobCardTable.Cols.METER_DIGIT + " VARCHAR, " +
                JobCardTable.Cols.ACCOUNT_NO + " VARCHAR, " +
                JobCardTable.Cols.ROUTE_IMAGE + " VARCHAR, " +
                JobCardTable.Cols.PRV_STATUS + " VARCHAR, " +
                JobCardTable.Cols.ATTEMPT + " VARCHAR, " +
                JobCardTable.Cols.ZONE_NAME + " VARCHAR, " +
                JobCardTable.Cols.PDC + " VARCHAR, " +
                JobCardTable.Cols.METER_READER_ID + " VARCHAR ";
        createTable(db, JobCardTable.TABLE_NAME, consumerTableFields);
    }

    /**
     * creates JobCardTable in device database
     *
     * @param db SqliteDatabase instance
     */
    private void createMeterReadingTable(SQLiteDatabase db) {
        String consumerTableFields = MeterReadingTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MeterReadingTable.Cols.CURRENT_METER_READING + " VARCHAR, " +
                MeterReadingTable.Cols.METER_STATUS + " VARCHAR, " +
                MeterReadingTable.Cols.READER_STATUS + " VARCHAR, " +
                MeterReadingTable.Cols.READING_IMAGE + " BLOB, " +
                MeterReadingTable.Cols.READING_MONTH + " VARCHAR, " +
                MeterReadingTable.Cols.READER_REMARK_COMMENT + " VARCHAR, " +
                MeterReadingTable.Cols.IS_SUSPICIOUS + " VARCHAR, " +
                MeterReadingTable.Cols.SUSPICIOUS_REMARKS + " VARCHAR, " +
                MeterReadingTable.Cols.SUSPICIOUS_READING_IMAGE + " BLOB, " +
                MeterReadingTable.Cols.CUR_LAT + " VARCHAR, " +
                MeterReadingTable.Cols.CUR_LNG + " VARCHAR, " +
                MeterReadingTable.Cols.IS_UPLOADED + " VARCHAR, " +
                MeterReadingTable.Cols.IS_REVISIT + " VARCHAR, " +
                MeterReadingTable.Cols.PRV_SEQUENCE + " VARCHAR, " +
                MeterReadingTable.Cols.NEW_SEQUENCE + " VARCHAR, " +
                MeterReadingTable.Cols.LOCATION_GUIDANCE + " VARCHAR, " +
                MeterReadingTable.Cols.POLE_NO + " VARCHAR, " +
                MeterReadingTable.Cols.JOB_CARD_ID + " VARCHAR, " +
                MeterReadingTable.Cols.METER_NO + " VARCHAR, " +
                MeterReadingTable.Cols.METER_READER_ID + " VARCHAR, " +
                MeterReadingTable.Cols.READING_DATE + " VARCHAR, " +
                MeterReadingTable.Cols.CURRENT_KVAH_READING + " VARCHAR, " +
                MeterReadingTable.Cols.CURRENT_KVA_READING + " VARCHAR, " +
                MeterReadingTable.Cols.ISKVAHROUNDCOMPLETED + " VARCHAR, " +
                MeterReadingTable.Cols.ISKWHROUNDCOMPLETED + " VARCHAR, " +
                MeterReadingTable.Cols.PANEL_NO + " VARCHAR, " +
                MeterReadingTable.Cols.METER_TYPE + " VARCHAR, " +
                MeterReadingTable.Cols.MOBILE_NO + " VARCHAR, " +
                MeterReadingTable.Cols.ZONE_CODE + " VARCHAR, " +
                MeterReadingTable.Cols.READING_TAKEN_BY + " VARCHAR, " +
                MeterReadingTable.Cols.CURRENT_KW_READING + " VARCHAR, " +
                MeterReadingTable.Cols.STATUS_CHANGED + " VARCHAR, " +
                MeterReadingTable.Cols.SMS_SENT + " VARCHAR, " +
                MeterReadingTable.Cols.Meter_Location + " VARCHAR, " +
                MeterReadingTable.Cols.TIME_TAKEN + " VARCHAR, " +

                MeterReadingTable.Cols.CONSUMER_CATEGORY_REMARK + " VARCHAR, " +
                MeterReadingTable.Cols.AIR_CONDITIONER_EXIST + " VARCHAR, " +
                MeterReadingTable.Cols.NO_OF_AIR_CONDITIONERS + " VARCHAR, " +
                MeterReadingTable.Cols.IS_PLASTIC_COVER_CUT + " VARCHAR, " +
                MeterReadingTable.Cols.IS_LAT_LONG_VERIFIED + " VARCHAR, " +
                MeterReadingTable.Cols.DISTANCE + " VARCHAR, " +


                MeterReadingTable.Cols.CURRENT_PF_READING + " VARCHAR ";

        createTable(db, MeterReadingTable.TABLE_NAME, consumerTableFields);
    }

    /**
     * creates UploadsHistoryTable in device database
     *
     * @param db SqliteDatabase instance
     */
    private void createUploadsHistoryTable(SQLiteDatabase db) {
        String uploadsHistoryTableFields = UploadsHistoryTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UploadsHistoryTable.Cols.CONSUMER_ID + " VARCHAR, " +
                UploadsHistoryTable.Cols.BILL_CYCLE_CODE + " VARCHAR, " +
                UploadsHistoryTable.Cols.ROUTE_ID + " VARCHAR, " +
                UploadsHistoryTable.Cols.MONTH + " VARCHAR, " +
                UploadsHistoryTable.Cols.UPLOAD_STATUS + " VARCHAR, " +
                UploadsHistoryTable.Cols.METER_READER_ID + " VARCHAR, " +
                UploadsHistoryTable.Cols.READING_DATE + " VARCHAR";

        createTable(db, UploadsHistoryTable.TABLE_NAME, uploadsHistoryTableFields);
    }

    // Create notification Table Piyush : 25-02-2017 starts
    private void createNotificationTable(SQLiteDatabase db) {
        String notificationTableFields = NotificationTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NotificationTable.Cols.TITLE + " VARCHAR, " +
                NotificationTable.Cols.MSG + " VARCHAR, " +
                NotificationTable.Cols.DATE + " VARCHAR, " +
                NotificationTable.Cols.IS_READ + " VARCHAR, " +
                NotificationTable.Cols.METER_READER_ID + " VARCHAR ";
        createTable(db, NotificationTable.TABLE_NAME, notificationTableFields);
    }
    // Create notification Table Piyush : 25-02-2017 ends

    // Create Bill Table Avinesh : 31-05-2017 starts
    private void createBillTable(SQLiteDatabase db) {
        String billTableFields = BillTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BillTable.Cols.JOBCARD_ID + " VARCHAR, " +
                BillTable.Cols.METER_READER_ID + " VARCHAR, " +
                BillTable.Cols.JOBCARD_STATUS + " VARCHAR, " +
                BillTable.Cols.BINDER_CODE + " VARCHAR, " +
                BillTable.Cols.CYCLE_CODE + " VARCHAR, " +
                BillTable.Cols.END_DATE + " VARCHAR, " +
                BillTable.Cols.START_DATE + " VARCHAR, " +
                BillTable.Cols.REMARK + " VARCHAR, " +
                BillTable.Cols.READING_DATE + " VARCHAR, " +
                BillTable.Cols.BILLMONTH + " VARCHAR, " +
                BillTable.Cols.BINDER_ID + " VARCHAR, " +
                BillTable.Cols.CONSUMER_NAME + " VARCHAR, " +
                BillTable.Cols.CONSUMER_NO + " VARCHAR, " +
                BillTable.Cols.ADDRESS + " VARCHAR, " +
                BillTable.Cols.PRV_LAT + " VARCHAR, " +
                BillTable.Cols.PRV_LON + " VARCHAR, " +
                BillTable.Cols.CUR_LAT + " VARCHAR, " +
                BillTable.Cols.CUR_LON + " VARCHAR, " +
                BillTable.Cols.METER_NO + " VARCHAR, " +
                BillTable.Cols.IS_NEW + " VARCHAR, " +
                BillTable.Cols.ZONE_CODE + " VARCHAR, " +
                BillTable.Cols.TAKEN_BY + " VARCHAR, " +
                BillTable.Cols.BILL_DISTRIBUTED + " VARCHAR, " +
                BillTable.Cols.BILL_RECEIVED + " VARCHAR, " +
                BillTable.Cols.PHONE_NO + " VARCHAR, " +
                BillTable.Cols.ACCOUNT_NO + " VARCHAR, " +


                BillTable.Cols.TIME_TAKEN + " VARCHAR, " +

                BillTable.Cols.ZONE_NAME + " VARCHAR ";

        createTable(db, BillTable.TABLE_NAME, billTableFields);
    }

    // Create uploadBill Table Avinesh : 31-05-2017 starts
    private void createUploadBillHistory(SQLiteDatabase db) {
        String uploadbillTableFields = UploadBillHistoryTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UploadBillHistoryTable.Cols.JOBCARD_ID + " VARCHAR, " +
                UploadBillHistoryTable.Cols.METER_READER_ID + " VARCHAR, " +
                UploadBillHistoryTable.Cols.BINDER_CODE + " VARCHAR, " +
                UploadBillHistoryTable.Cols.CYCLE_CODE + " VARCHAR, " +
                UploadBillHistoryTable.Cols.READING_DATE + " VARCHAR, " +
                UploadBillHistoryTable.Cols.ZONE_NAME + " VARCHAR, " +
                UploadBillHistoryTable.Cols.BILLMONTH + " VARCHAR, " +
                UploadBillHistoryTable.Cols.CONSUMER_NAME + " VARCHAR, " +
                UploadBillHistoryTable.Cols.CONSUMER_NO + " VARCHAR, " +
                UploadBillHistoryTable.Cols.IS_NEW + " VARCHAR, " +
                UploadBillHistoryTable.Cols.METER_NO + " VARCHAR, " +
                UploadBillHistoryTable.Cols.ZONE_CODE + " VARCHAR ";

        createTable(db, UploadBillHistoryTable.TABLE_NAME, uploadbillTableFields);
    }
    // Create notification Table Avinesh : 31-05-2017 ends


    private void createSequenceTable(SQLiteDatabase db) {
        String SequenceTableFields = SequenceTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                SequenceTable.Cols.METER_READER_ID + " VARCHAR, " +
                SequenceTable.Cols.BINDER_CODE + " VARCHAR, " +
                SequenceTable.Cols.CYCLE_CODE + " VARCHAR, " +
                SequenceTable.Cols.SEQUENCE + " VARCHAR, " +
                SequenceTable.Cols.ZONE_CODE + " VARCHAR ";

        createTable(db, SequenceTable.TABLE_NAME, SequenceTableFields);
    }

    /**
     * Drops Table from device database
     *
     * @param db   SqliteDatabase instance
     * @param name TableName
     */
    public void dropTable(SQLiteDatabase db, String name) {
        String query = MessageFormat.format(DatabaseHelper.KEY_DROP_TABLE, name);
        db.execSQL(query);
    }

    public static boolean exists(SQLiteDatabase db, String name) {
        Cursor cur = db.rawQuery(SQL, new String[]{name});
        cur.moveToFirst();
        int tables = cur.getInt(0);
        if (tables > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Creates Table in device database
     *
     * @param db     SqliteDatabase instance
     * @param name   TableName
     * @param fields ColumnFields
     */
    public void createTable(SQLiteDatabase db, String name, String fields) {
        String query = MessageFormat.format(DatabaseHelper.KEY_CREATE_TABLE, name, fields);
        db.execSQL(query);
    }

    private void createDisconnectionTable(SQLiteDatabase db) {
        String DisconnectionTableFields = DisconnectionTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DisconnectionTable.Cols.METER_READER_ID + " VARCHAR, " +
                DisconnectionTable.Cols.BINDER_CODE + " VARCHAR, " +
                DisconnectionTable.Cols.CONSUMER_NO + " VARCHAR, " +
                DisconnectionTable.Cols.NAME + " VARCHAR, " +
                DisconnectionTable.Cols.ADDRESS + " VARCHAR, " +
                DisconnectionTable.Cols.LATITUDE + " VARCHAR, " +
                DisconnectionTable.Cols.LONGITUDE + " VARCHAR, " +
                DisconnectionTable.Cols.JOB_CARD_STATUS + " VARCHAR, " +
                DisconnectionTable.Cols.JOB_CARD_ID + " VARCHAR, " +
                DisconnectionTable.Cols.ZONE_CODE + " VARCHAR, " +
                DisconnectionTable.Cols.CONTACT_NO + " VARCHAR, " +
                DisconnectionTable.Cols.BILL_MONTH + " VARCHAR, " +
                DisconnectionTable.Cols.DUE_DATE + " VARCHAR, " +
                DisconnectionTable.Cols.NOTICE_DATE + " VARCHAR, " +
                DisconnectionTable.Cols.DISCONNECTION_NOTICE_NO + " VARCHAR, " +
                DisconnectionTable.Cols.TOTOS + " VARCHAR ";

        createTable(db, DisconnectionTable.TABLE_NAME, DisconnectionTableFields);
    }

    private void createUploadDisconnectionTable(SQLiteDatabase db) {
        String UploadDisconnectionTableFields = UploadDisconnectionTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UploadDisconnectionTable.Cols.METER_READER_ID + " VARCHAR, " +
                UploadDisconnectionTable.Cols.BINDER_CODE + " VARCHAR, " +
                UploadDisconnectionTable.Cols.CONSUMER_NO + " VARCHAR, " +
                UploadDisconnectionTable.Cols.NAME + " VARCHAR, " +
                UploadDisconnectionTable.Cols.CURRENT_LATITUDE + " VARCHAR, " +
                UploadDisconnectionTable.Cols.CURRENT_LONGITUDE + " VARCHAR, " +
                UploadDisconnectionTable.Cols.JOB_CARD_ID + " VARCHAR, " +
                UploadDisconnectionTable.Cols.ZONE_CODE + " VARCHAR, " +
                UploadDisconnectionTable.Cols.BILL_MONTH + " VARCHAR, " +
                UploadDisconnectionTable.Cols.CURRENT_DATE + " VARCHAR, " +
                UploadDisconnectionTable.Cols.DELIVERY_STATUS + " VARCHAR, " +
                UploadDisconnectionTable.Cols.IS_NEW + " VARCHAR, " +
                UploadDisconnectionTable.Cols.DELIVERY_REMARK + " VARCHAR ";

        createTable(db, UploadDisconnectionTable.TABLE_NAME, UploadDisconnectionTableFields);
    }

    private void createDisconnectionHistoryTable(SQLiteDatabase db) {
        String DisconnectionHistoryTableFields = DisconnectionHistoryTable.Cols.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DisconnectionHistoryTable.Cols.METER_READER_ID + " VARCHAR, " +
                DisconnectionHistoryTable.Cols.BINDER_CODE + " VARCHAR, " +
                DisconnectionHistoryTable.Cols.BILL_MONTH + " VARCHAR, " +
                DisconnectionHistoryTable.Cols.JOB_CARD_ID + " VARCHAR, " +
                DisconnectionHistoryTable.Cols.DATE + " VARCHAR, " +
                DisconnectionHistoryTable.Cols.DELIVERY_STATUS + " VARCHAR";

        createTable(db, DisconnectionHistoryTable.TABLE_NAME, DisconnectionHistoryTableFields);
    }
}