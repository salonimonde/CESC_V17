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
 * about the JobCardTable of device database
 *
 * @author Bynry01
 */
public class JobCardTable
{

    public static final String TABLE_NAME = "JobCardTable";
    public static final String PATH = "JOB_CARD_TABLE";
    public static final int PATH_TOKEN = 30;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    /**
     * This class contains Constants to describe name of Columns of Consumer Table
     *
     * @author Bynry01
     */
    public static class Cols
    {
        public static final String ID = "_id";
        public static final String BILL_CYCLE_CODE = "bill_cycle_code";
        public static final String SCHEDULE_MONTH = "schedule_month";
        public static final String SCHEDULE_END_DATE = "schedule_end_date";
        public static final String ROUTE_ID = "route_code";
        public static final String CONSUMER_NO = "consumer_no";
        public static final String CONSUMER_NAME = "consumer_name";
        public static final String POL_NO = "pole_no";
        public static final String DT_CODE = "dt_code";
        public static final String PHONE_NO = "phone_no";
        public static final String ADDRESS = "address";
        public static final String METER_ID = "meter_no";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String LOCATION_GUIDANCE = "location_guidance";
        public static final String PRV_METER_READING= "prv_meter_reading";
        public static final String PRV_LAT= "prv_lat";
        public static final String PRV_LONG= "prv_long";
        public static final String ASSIGNED_DATE = "assigned_date";
        public static final String JOB_CARD_STATUS = "job_card_status";
        public static final String JOB_CARD_ID = "job_card_id";
        public static final String IS_REVISIT = "is_revisit";
        public static final String PRV_SEQUENCE = "prv_sequence";
        public static final String PRV_KVAH_READING = "prv_kvah_reading";
        public static final String PRV_KVA_READING = "prv_kva_reading";
        public static final String ISKVAHROUNDCOMPLETED = "is_kvahroundcompleted";
        public static final String ISKWHROUNDCOMPLETED = "is_kwhroundcompleted";
        public static final String ZONE_CODE = "zone_code";
        public static final String CATEGORY_ID = "category_id";
        public static final String AVG_CONSUMTION = "avg_consumption";
        public static final String METER_DIGIT = "meter_digit";
        public static final String ACCOUNT_NO = "account_no";
        public static final String ROUTE_IMAGE = "route_image";
        public static final String CURRENT_SEQUENCE = "current_sequence";
        public static final String SNF = "snf";
        public static final String PRV_STATUS = "prv_status";
        public static final String ZONE_NAME = "zone_name";
        public static final String ATTEMPT = "attempt";
        public static final String PDC = "pdc";



    }
}