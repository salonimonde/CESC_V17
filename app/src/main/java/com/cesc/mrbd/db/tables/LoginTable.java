/*
' History Header:      Version         - Date        - Developer Name   - Work Description
' History       :        1.0           - Aug-2016    - Bynry01  - class describes all necessary info about the UserLogin Table Table
 */

/*
 ##############################################################################################
 #####                                                                                    #####
 #####     FILE              : LoginTable.Java 	       								      #####
 #####     CREATED BY        : Bynry01                                                    #####
 #####     CREATION DATE     : Aug-2016                                                   #####
 #####                                                                                    #####
 #####     MODIFIED  BY      : Bynry01                                                    #####
 #####     MODIFIED ON       :                                                   	      #####
 #####                                                                                    #####
 #####     CODE BRIEFING     : LoginTable Class.         		 			   	          #####
 #####                         class describes all necessary info about LoginTable        #####
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
 * about the LoginTable Table of device database
 *
 * @author Bynry01
 */
public class LoginTable
{

    public static final String TABLE_NAME = "LoginTable";
    public static final String PATH = "LOGIN_TABLE";
    public static final int PATH_TOKEN = 10;
    public static final Uri CONTENT_URI = ContentDescriptor.BASE_URI.buildUpon().appendPath(PATH).build();
    /**
     * This class contains Constants to describe name of Columns of LoginTable
     * @author Bynry01
     */
    public static class Cols
    {
        public static final String ID = "_id";
        public static final String USER_LOGIN_ID = "user_login_id";
        public static final String METER_READER_ID = "meter_reader_id";
        public static final String LOGIN_DATE = "login_date";
        public static final String LOGIN_LAT= "login_lat";
        public static final String LOGIN_LNG= "login_lng";
    }
}