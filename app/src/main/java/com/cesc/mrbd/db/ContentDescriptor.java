

/*
' History Header:      Version         - Date        - Developer Name   - Work Description
' History       :        1.0           - Aug-2016   - Bynry01  - Class contains application database content provider description
 */

/*
 ##############################################################################################
 #####                                                                                    #####                                                                        
 #####     FILE              : ContentDescriptor.Java 	      		 			          #####                 
 #####     CREATED BY        : Bynry01                                                    #####
 #####     CREATION DATE     : Aug-2016                                                   #####
 #####                                                                                    #####
 #####     MODIFIED  BY      : Bynry01                                                    #####
 #####     MODIFIED ON       :                                                   	      #####                          
 #####                                                                                    #####                                                                              
 #####     CODE BRIEFING     : ContentDescriptor Class.      		       			      #####          
 #####                         Class contains application database  					  #####
 #####						   content provider description								  #####
 #####                                                                                    #####                                                                              
 #####     COMPANY           : Bynry.                                                     #####
 ##############################################################################################
 */
package com.cesc.mrbd.db;

import android.content.UriMatcher;
import android.net.Uri;

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


/**
 * This class contains description about
 * application database content providers
 *
 * @author Bynry01
 */
public class ContentDescriptor
{

    public static final String AUTHORITY = "com.cesc.mrbd";
    public static final Uri BASE_URI = Uri.parse("content://" + AUTHORITY);
    public static final UriMatcher URI_MATCHER = buildUriMatcher();


    /**
     * @return UriMatcher for database table Uris
     */
    private static UriMatcher buildUriMatcher()
    {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(AUTHORITY, LoginTable.PATH, LoginTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, ConsumerTable.PATH, ConsumerTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, JobCardTable.PATH, JobCardTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, MeterReadingTable.PATH, MeterReadingTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, UserProfileTable.PATH, UserProfileTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, UploadsHistoryTable.PATH, UploadsHistoryTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, NotificationTable.PATH, NotificationTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, BillTable.PATH, BillTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, UploadBillHistoryTable.PATH, UploadBillHistoryTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, SequenceTable.PATH, SequenceTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, DisconnectionTable.PATH, DisconnectionTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, UploadDisconnectionTable.PATH, UploadDisconnectionTable.PATH_TOKEN);
        matcher.addURI(AUTHORITY, DisconnectionHistoryTable.PATH, DisconnectionHistoryTable.PATH_TOKEN);
        return matcher;
    }
}