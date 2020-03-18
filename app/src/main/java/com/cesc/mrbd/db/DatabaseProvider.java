/*
' History Header:      Version         - Date        - Developer Name   - Work Description
' History       :        1.0           - Aug-2016    - Bynry01  - ContentProvider for Application database
 */

/*
 ##############################################################################################
 #####                                                                                    #####                                                                        
 #####     FILE              : DatabaseProvider.Java 	       						      #####                 
 #####     CREATED BY        : Bynry01                                                    #####
 #####     CREATION DATE     : Aug-2015                                                   #####
 #####                                                                                    #####                                                                              
 #####     MODIFIED  BY      : Bynry01                                                    #####
 #####     MODIFIED ON       :                                                   	      #####                          
 #####                                                                                    #####                                                                              
 #####     CODE BRIEFING     : DatabaseProvider Class.          			   			  #####          
 #####                         ContentProvider for Application database					  #####
 #####                                                                                    #####                                                                              
 #####     COMPANY           : Bynry                                                     #####
 #####                                                                                    #####                                                                              
 ##############################################################################################
 */
package com.cesc.mrbd.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.cesc.mrbd.db.tables.BillTable;
import com.cesc.mrbd.db.tables.ConsumerTable;
import com.cesc.mrbd.db.tables.DisconnectionHistoryTable;
import com.cesc.mrbd.db.tables.DisconnectionTable;
import com.cesc.mrbd.db.tables.JobCardTable;
import com.cesc.mrbd.db.tables.LoginTable;
import com.cesc.mrbd.db.tables.MeterReadingTable;
import com.cesc.mrbd.db.tables.SequenceTable;
import com.cesc.mrbd.db.tables.UploadBillHistoryTable;
import com.cesc.mrbd.db.tables.UploadDisconnectionTable;
import com.cesc.mrbd.db.tables.UploadsHistoryTable;
import com.cesc.mrbd.db.tables.UserProfileTable;
import com.cesc.mrbd.models.Disconnection;


/**
 * This class provides Content Provider for application database
 *
 * @author Bynry01
 */
public class DatabaseProvider extends ContentProvider
{

    private static final String UNKNOWN_URI = "Unknown URI";

    public static DatabaseHelper dbHelper;


    @Override
    public boolean onCreate()
    {
        dbHelper = new DatabaseHelper(getContext());
        dbHelper.getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final int token = ContentDescriptor.URI_MATCHER.match(uri);

        Cursor result = null;

        switch (token)
        {
            case LoginTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, LoginTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }

            case ConsumerTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, ConsumerTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }

            case JobCardTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, JobCardTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }

            case MeterReadingTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, MeterReadingTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }

            case UserProfileTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, UserProfileTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }

            case UploadsHistoryTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, UploadsHistoryTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }

            case BillTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, BillTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }
            case UploadBillHistoryTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, UploadBillHistoryTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }
            case SequenceTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, SequenceTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }
            case DisconnectionTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, DisconnectionTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }
            case UploadDisconnectionTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, UploadDisconnectionTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }
            case DisconnectionHistoryTable.PATH_TOKEN:
            {
                result = doQuery(db, uri, DisconnectionHistoryTable.TABLE_NAME, projection,
                        selection, selectionArgs, sortOrder);
                break;
            }

        }

        return result;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = ContentDescriptor.URI_MATCHER.match(uri);

        Uri result = null;

        switch (token)
        {
            case LoginTable.PATH_TOKEN:
            {
                result = doInsert(db, LoginTable.TABLE_NAME,
                        LoginTable.CONTENT_URI, uri, values);
                break;
            }

            case ConsumerTable.PATH_TOKEN:
            {
                result = doInsert(db, ConsumerTable.TABLE_NAME,
                        ConsumerTable.CONTENT_URI, uri, values);
                break;
            }

            case JobCardTable.PATH_TOKEN:
            {
                result = doInsert(db, JobCardTable.TABLE_NAME,
                        JobCardTable.CONTENT_URI, uri, values);
                break;
            }

            case MeterReadingTable.PATH_TOKEN:
            {
                result = doInsert(db, MeterReadingTable.TABLE_NAME,
                        MeterReadingTable.CONTENT_URI, uri, values);
                break;
            }

            case UserProfileTable.PATH_TOKEN:
            {
                result = doInsert(db, UserProfileTable.TABLE_NAME,
                        UserProfileTable.CONTENT_URI, uri, values);
                break;
            }

            case UploadsHistoryTable.PATH_TOKEN:
            {
                result = doInsert(db, UploadsHistoryTable.TABLE_NAME,
                        UploadsHistoryTable.CONTENT_URI, uri, values);
                break;
            }
            case BillTable.PATH_TOKEN:
            {
                result = doInsert(db, BillTable.TABLE_NAME,
                        BillTable.CONTENT_URI, uri, values);
                break;
            }
            case UploadBillHistoryTable.PATH_TOKEN:
            {
                result = doInsert(db, UploadBillHistoryTable.TABLE_NAME,
                        UploadBillHistoryTable.CONTENT_URI, uri, values);
                break;
            }
            case SequenceTable.PATH_TOKEN:
            {
                result = doInsert(db, SequenceTable.TABLE_NAME,
                        SequenceTable.CONTENT_URI, uri, values);
                break;
            }
            case DisconnectionTable.PATH_TOKEN:
            {
                result = doInsert(db, DisconnectionTable.TABLE_NAME,
                        DisconnectionTable.CONTENT_URI, uri, values);
                break;
            }
            case UploadDisconnectionTable.PATH_TOKEN:
            {
                result = doInsert(db, UploadDisconnectionTable.TABLE_NAME,
                        UploadDisconnectionTable.CONTENT_URI, uri, values);
                break;
            }
            case DisconnectionHistoryTable.PATH_TOKEN:
            {
                result = doInsert(db, DisconnectionHistoryTable.TABLE_NAME,
                        DisconnectionHistoryTable.CONTENT_URI, uri, values);
                break;
            }
        }

        if (result == null)
        {
            throw new IllegalArgumentException(UNKNOWN_URI + uri);
        }

        return result;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values)
    {
        String table = null;
        int token = ContentDescriptor.URI_MATCHER.match(uri);

        switch (token)
        {
            case LoginTable.PATH_TOKEN:
            {
                table = LoginTable.TABLE_NAME;
                break;
            }
            case ConsumerTable.PATH_TOKEN:
            {
                table = ConsumerTable.TABLE_NAME;
                break;
            }
            case JobCardTable.PATH_TOKEN:
            {
                table = JobCardTable.TABLE_NAME;
                break;
            }

            case MeterReadingTable.PATH_TOKEN:
            {
                table = MeterReadingTable.TABLE_NAME;
                break;
            }
            case UserProfileTable.PATH_TOKEN:
            {
                table = UserProfileTable.TABLE_NAME;
                break;
            }
            case UploadsHistoryTable.PATH_TOKEN:
            {
                table = UploadsHistoryTable.TABLE_NAME;
                break;
            }
            case BillTable.PATH_TOKEN:
            {
                table = BillTable.TABLE_NAME;
                break;
            }
            case UploadBillHistoryTable.PATH_TOKEN:
            {
                table = UploadBillHistoryTable.TABLE_NAME;
                break;
            }
            case SequenceTable.PATH_TOKEN:
            {
                table = SequenceTable.TABLE_NAME;
                break;
            }
            case DisconnectionTable.PATH_TOKEN:
            {
                table = DisconnectionTable.TABLE_NAME;
                break;
            }
            case UploadDisconnectionTable.PATH_TOKEN:
            {
                table = UploadDisconnectionTable.TABLE_NAME;
                break;
            }
            case DisconnectionHistoryTable.PATH_TOKEN:
            {
                table = DisconnectionHistoryTable.TABLE_NAME;
                break;
            }
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.beginTransaction();

        for (ContentValues cv : values)
        {
            db.insert(table, null, cv);
        }

        db.setTransactionSuccessful();
        db.endTransaction();

        return values.length;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = ContentDescriptor.URI_MATCHER.match(uri);

        int result = 0;

        switch (token)
        {
            case LoginTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, LoginTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }

            case ConsumerTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, ConsumerTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }

            case JobCardTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, JobCardTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }

            case MeterReadingTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, MeterReadingTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }

            case UserProfileTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, UserProfileTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }

            case UploadsHistoryTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, UploadsHistoryTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
            case BillTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, BillTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
            case UploadBillHistoryTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, UploadBillHistoryTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
            case SequenceTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, SequenceTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
            case DisconnectionTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, DisconnectionTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
            case UploadDisconnectionTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, UploadDisconnectionTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
            case DisconnectionHistoryTable.PATH_TOKEN:
            {
                result = doDelete(db, uri, DisconnectionHistoryTable.TABLE_NAME, selection,
                        selectionArgs);
                break;
            }
        }

        return result;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int token = ContentDescriptor.URI_MATCHER.match(uri);

        int result = 0;

        switch (token)
        {
            case LoginTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, LoginTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }

            case ConsumerTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, ConsumerTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }

            case JobCardTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, JobCardTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }

            case MeterReadingTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, MeterReadingTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case UserProfileTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, UserProfileTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case UploadsHistoryTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, UploadsHistoryTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case BillTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, BillTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case UploadBillHistoryTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, UploadBillHistoryTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case SequenceTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, SequenceTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case DisconnectionTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, DisconnectionTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case UploadDisconnectionTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, UploadDisconnectionTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
            case DisconnectionHistoryTable.PATH_TOKEN:
            {
                result = doUpdate(db, uri, DisconnectionHistoryTable.TABLE_NAME, selection,
                        selectionArgs, values);
                break;
            }
        }

        return result;
    }

    /**
     * Performs query to specified table using the projection, selection and
     * sortOrder
     *
     * @param db            SQLiteDatabase instance
     * @param uri           ContentUri for watch
     * @param tableName     Name of table on which query has to perform
     * @param projection    needed projection
     * @param selection     needed selection cases
     * @param selectionArgs needed selection arguments
     * @param sortOrder     sort order if necessary
     * @return Cursor cursor from the table tableName matching all the criterion
     */
    private Cursor doQuery(SQLiteDatabase db, Uri uri, String tableName,
                           String[] projection, String selection, String[] selectionArgs,
                           String sortOrder)
    {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tableName);
        Cursor result = builder.query(db, projection, selection, selectionArgs,
                null, null, sortOrder);

        result.setNotificationUri(getContext().getContentResolver(), uri);

        return result;
    }

    /**
     * performs update to the specified table row or rows
     *
     * @param db            SQLiteDatabase instance
     * @param uri           uri of the content that was changed
     * @param tableName     Name of table on which query has to perform
     * @param selection     needed selection cases
     * @param selectionArgs needed selection arguments
     * @param values        content values to update
     * @return success or failure
     */
    private int doUpdate(SQLiteDatabase db, Uri uri, String tableName,
                         String selection, String[] selectionArgs, ContentValues values)
    {
        int result = db.update(tableName, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    /**
     * deletes the row/rows from the table
     *
     * @param db            SQLiteDatabase instance
     * @param uri           uri of the content that was changed
     * @param tableName     Name of table on which query has to perform
     * @param selection     needed selection cases
     * @param selectionArgs needed selection arguments
     * @return success or failure
     */
    private int doDelete(SQLiteDatabase db, Uri uri, String tableName,
                         String selection, String[] selectionArgs)
    {
        int result = db.delete(tableName, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }

    /**
     * insert rows to the specified table
     *
     * @param db         SQLiteDatabase instance
     * @param tableName  Name of table on which query has to perform
     * @param contentUri ContentUri to build the path
     * @param uri        uri of the content that was changed
     * @param values     content values to update
     * @return success or failure
     */
    private Uri doInsert(SQLiteDatabase db, String tableName, Uri contentUri,
                         Uri uri, ContentValues values)
    {
        long id = db.insert(tableName, null, values);
        Uri result = contentUri.buildUpon().appendPath(String.valueOf(id))
                .build();
        getContext().getContentResolver().notifyChange(uri, null);
        return result;
    }
}