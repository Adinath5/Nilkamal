package com.atharvainfo.nilkamal.Others;

/**
 * Created by info on 17/12/2017.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private final static String TAG = "DatabaseHelper";
    public static String DB_NAME = "poultrydata.db";
    private final Context myContext;
    public static String DB_PATH ="";// "/data/data/com.atharvainfosolutions.myleader/databases/";
    public static String mPath = DB_PATH+DB_NAME;
    private static final String DATABASE_NAME = "poultrydata.db";

    private static final int DATABASE_VERSION = 1;

    public String pathToSaveDBFile;
    public DatabaseErrorHandler err;
    public String path;
    String backupDBPath = "/data/data/databases/";
    File sd = Environment.getExternalStorageDirectory();
    File backupDB = new File(sd, backupDBPath);
    private SQLiteDatabase mDataBase;
    private static final int DB_VERSION = 1;


    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        Log.d("DBVERSION","The Database Version (as hard coded) is " + String.valueOf(DB_VERSION));

        int dbversion = DatabaseAssetHandler.getVersionFromDBFile(context,DB_NAME);
        Log.d("DBVERSION","The Database Version (as per the database file) is " + String.valueOf(dbversion));

        // Copy the Database if no database exists
        if (!DatabaseAssetHandler.checkDataBase(context,DB_NAME)) {
            DatabaseAssetHandler.copyDataBase(context,DB_NAME,true,DB_VERSION);
        } else {
            // Copy the database if DB_VERSION is greater then the version stored in the database (user_version value in the db header)
            if (DB_VERSION > dbversion && DatabaseAssetHandler.checkDataBase(context, DB_NAME)) {
                DatabaseAssetHandler.copyDataBase(context, DB_NAME, true,DB_VERSION);
                // DatabaseAssetHandler.restoreTable(context,DB_NAME,????THE_TABLE_NAME????); // Example of restoring a table (note ????THE_TABLE_NAME???? must be changed accordingly)
                DatabaseAssetHandler.clearForceBackups(context, DB_NAME); // Clear the backups
            }
        }
        mDataBase = this.getWritableDatabase();
    }
    public boolean openDatabase() throws SQLiteException
    {
        Log.d(TAG, "Database open");
        Log.d(TAG, DB_PATH.toString());
        mDataBase = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.CREATE_IF_NECESSARY);
        return mDataBase != null;

    }
    public synchronized void close(){
        if(mDataBase != null)
            mDataBase.close();
        SQLiteDatabase.releaseMemory();
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    // onUpgrade should not be used for the copy as may be issues due to db being opened
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void createDataBase() throws IOException {
        boolean dbexist = checkDataBase();
        if(!dbexist) {
            this.getReadableDatabase();
            Log.d("CopingDatabase", "Create Database");

            try {
                copyDataBase();
            } catch(IOException e) {
                throw new Error("Error copying database");
            }

        }
    }

    private boolean checkDataBase() {
        boolean checkDB = false;
        try {
            File file = new File(DB_PATH);
            checkDB = file.exists();
            Log.d("Database", "Database Exist-2");
        } catch(SQLiteException e) {
            Log.d(TAG, e.getMessage());
        }
        return checkDB;

        // File DbFile = new File(DB_PATH + DB_NAME);
        // Log.d("Database", "Database Exist");
        // return DbFile.exists();
    }

    private void copyDataBase() throws IOException {
        String outfileName = DB_PATH;
        OutputStream os = new FileOutputStream(outfileName);
        InputStream is = myContext.getAssets().open(DATABASE_NAME);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }
        is.close();
        os.flush();
        os.close();
    }

    public boolean isTableExists(String tableName, boolean openDb) {
        if(openDb) {
            if(mDataBase == null || !mDataBase.isOpen()) {
                mDataBase = getReadableDatabase();
            }

            if(!mDataBase.isReadOnly()) {
                mDataBase.close();
                mDataBase = getReadableDatabase();
            }
        }

        String query = "select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'";
        try (Cursor cursor = mDataBase.rawQuery(query, null)) {
            if(cursor!=null) {
                if(cursor.getCount()>0) {
                    return true;
                }
            }
            return false;
        }
    }
    public void CreateSaleTranTbl(){
        String query = "Create Table salestran(tempvochno TEXT, vdate Date, grid TEXT,glcode TEXT, addedby TEXT, prodno TEXT, prodname TEXT, " +
                "qty NUMERIC DEFAULT '0.00',netqty NUMERIC DEFAULT '0.00', fqty NUMERIC DEFAULT '0.00', mrp NUMERIC DEFAULT '0.00'," +
                "rateinctax NUMERIC DEFAULT '0.00',amountinctax NUMERIC DEFAULT '0.00',rateexctax NUMERIC DEFAULT '0.00',amountexctax NUMERIC DEFAULT '0.00'," +
                "igstp NUMERIC DEFAULT '0.00',igstamount NUMERIC DEFAULT '0.00', mfgcomp TEXT,prodtype TEXT,prodcategory TEXT,prodhsn TEXT,prodpack TEXT,batchno TEXT," +
                "expdate TEXT,prodprate TEXT,typcode TEXT,prodacd TEXT,gname TEXT,addeddate TEXT,ledgername TEXT,paddress TEXT, vochid INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT)";
        mDataBase = this.getWritableDatabase();
        mDataBase.execSQL(query);

    }

}
