package com.cesc.mrbd.db.tables;

import android.net.Uri;

import com.cesc.mrbd.db.ContentDescriptor;


/**
 * This class describes all necessary info
 * about the Consumer Table of device database
 *
 * @author Nikhil Patil
 */
public class UploadBillHistoryTable
{

    public static final String TABLE_NAME = "UploadsBillHistoryTable";
    public static final String PATH = "UPLOAD_BILL_HISTORY_TABLE";
    public static final int PATH_TOKEN = 55;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    /**
     * This class contains Constants to describe name of Columns of Consumer Table
     *
     * @author Bynry01
     */
    public static class Cols
    {
        public static final String ID = "_id";
        public static final String JOBCARD_ID = "jobcard_id";
        public static final String BILLMONTH = "billmonth";
        public static final String CYCLE_CODE = "cycle_code";
        public static final String BINDER_CODE = "binder_code";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String READING_DATE = "reading_date";
        public static final String CONSUMER_NO = "consmer_no";
        public static final String CONSUMER_NAME = "consumer_name";
        public static final String METER_NO = "meter_no";
        public static final String ZONE_CODE = "zone_code";
        public static final String ZONE_NAME = "zone_name";
        public static final String IS_NEW = "is_new";

    }
}