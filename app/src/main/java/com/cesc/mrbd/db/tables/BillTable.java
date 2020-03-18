/*
' History Header:      Version         - Date        - Developer Name   - Work Description
' History       :        1.0           - Aug-2016    - Bynry01  - class describes all necessary info about the UserLogin Table Table
 */

/*
 ##############################################################################################
 #####                                                                                    #####
 #####     FILE              : ConsumerTable.Java 	       								  #####
 #####     CREATED BY        : Bynry01                                                    #####
 #####     CREATION DATE     : Aug-2016                                                   #####
 #####                                                                                    #####
 #####     MODIFIED  BY      : Bynry01                                                    #####
 #####     MODIFIED ON       :                                                   	      #####
 #####                                                                                    #####
 #####     CODE BRIEFING     : Consumer Class.         		 			   	              #####
 #####                         class describes all necessary info about Consumer Table    #####
 #####                                                                                    #####
 #####     COMPANY           : Bynry.                                                     #####
 #####                                                                                    #####
 ##############################################################################################
 */
package com.cesc.mrbd.db.tables;

import android.net.Uri;

import com.cesc.mrbd.db.ContentDescriptor;


/**
 * This class describes all necessary info
 * about the Consumer Table of device database
 *
 * @author Bynry01
 */
public class BillTable {

    public static final String TABLE_NAME = "BillTable";
    public static final String PATH = "Bill_TABLE";
    public static final int PATH_TOKEN = 22;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();

    /**
     * This class contains Constants to describe name of Columns of Consumer Table
     *
     * @author Bynry01
     */
    public static class Cols {
        public static final String ID = "_id";
        public static final String JOBCARD_ID = "jobcard_id";
        public static final String BINDER_ID = "binder_id";
        public static final String CYCLE_CODE = "cycle_code";
        public static final String BINDER_CODE = "binder_code";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String JOBCARD_STATUS = "jobcard_status";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String REMARK = "remark";
        public static final String BILLMONTH = "billmonth";
        public static final String READING_DATE = "reading_date";
        public static final String CONSUMER_NO = "consmer_no";
        public static final String PRV_LAT = "prv_lat";
        public static final String PRV_LON = "prv_lon";
        public static final String ADDRESS = "address";
        public static final String CONSUMER_NAME = "consumer_name";
        public static final String CUR_LAT = "cur_lat";
        public static final String CUR_LON = "cur_lon";
        public static final String METER_NO = "meter_no";
        public static final String ZONE_CODE = "zone_code";
        public static final String ZONE_NAME = "zone_name";
        public static final String IS_NEW = "is_new";
        public static final String TAKEN_BY = "taken_by";
        public static final String BILL_DISTRIBUTED = "bill_distributed";
        public static final String BILL_RECEIVED = "bill_received";
        public static final String ACCOUNT_NO = "account_no";
        public static final String PHONE_NO = "phone_no";

        public static final String TIME_TAKEN = "time_taken";





    }
}