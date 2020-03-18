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
 * about the MeterReadingTable of device database
 *
 * @author Bynry01
 */
public class MeterReadingTable
{

    public static final String TABLE_NAME = "MeterReadingTable";
    public static final String PATH = "METER_READING_TABLE";
    public static final int PATH_TOKEN = 40;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    /**
     * This class contains Constants to describe name of Columns of Consumer Table
     *
     * @author Bynry01
     */
    public static class Cols
    {
        public static final String ID = "_id";
        public static final String METER_NO = "meter_no";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String JOB_CARD_ID = "job_card_id";
        public static final String CURRENT_METER_READING = "current_meter_reading";
        public static final String METER_STATUS = "meter_status";
        public static final String READER_STATUS = "reader_status";
        public static final String READING_IMAGE = "reading_image";
        public static final String READING_MONTH = "reading_month";
        public static final String READER_REMARK_COMMENT = "reader_remark_comment";
        public static final String IS_SUSPICIOUS = "isSuspicious";
        public static final String SUSPICIOUS_REMARKS = "suspicious_remark";
        public static final String SUSPICIOUS_READING_IMAGE = "suspicious_reading_image";
        public static final String CUR_LAT= "cur_lat";
        public static final String CUR_LNG = "cur_lng";
        public static final String IS_UPLOADED = "isUploaded";
        public static final String IS_REVISIT = "is_revisit";
        public static final String POLE_NO = "pole_no";
        public static final String READING_DATE = "reading_date";
        public static final String PRV_SEQUENCE = "prv_sequence";
        public static final String NEW_SEQUENCE = "new_sequence";
        public static final String READING_TAKEN_BY = "reading_taken_by";
        public static final String LOCATION_GUIDANCE = "location_guidance";
        public static final String CURRENT_KVAH_READING = "current_kvah_reading";
        public static final String CURRENT_KVA_READING = "current_kva_reading";
        public static final String ISKVAHROUNDCOMPLETED = "is_kvahroundcopmleted";
        public static final String ISKWHROUNDCOMPLETED = "is_kwhroundcopmleted";
        public static final String MOBILE_NO = "mobile_no";
        public static final String PANEL_NO = "panel_no";
        public static final String METER_TYPE = "meter_type";
        public static final String ZONE_CODE = "zone_code";
        public static final String CURRENT_KW_READING = "current_kw_reading";
        public static final String CURRENT_PF_READING = "current_pf_reading";
        public static final String STATUS_CHANGED = "status_changed";
        public static final String SMS_SENT = "sms_sent";
        public static final String Meter_Location = "meter_location";
        public static final String TIME_TAKEN = "time_taken";


        public static final String CONSUMER_CATEGORY_REMARK = "consumer_category_remark";
        public static final String AIR_CONDITIONER_EXIST = "air_conditioner_exist";
        public static final String NO_OF_AIR_CONDITIONERS = "no_of_air_conditioners";
        public static final String IS_PLASTIC_COVER_CUT = "is_plastic_cover_cut";
        public static final String IS_LAT_LONG_VERIFIED = "is_lat_long_verified";
        public static final String DISTANCE = "distance";
    }
}