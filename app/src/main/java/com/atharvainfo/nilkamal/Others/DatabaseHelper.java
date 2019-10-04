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
    //public static String mPath = DB_NAME;
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



    public DatabaseHelper(Context context)
    {
        super(context, DATABASE_NAME ,null, DATABASE_VERSION);
        this.myContext = context;
        DB_PATH = context.getDatabasePath(DB_NAME).getPath();
        //pathToSaveDBFile = filePath.toString();
        boolean dbexist = checkDataBase();
        if (dbexist) {
            openDatabase();
        } else {
            System.out.println("Database doesn't exist");
            try {
                createDataBase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
            Log.d("Database", "Database Exist");
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


    public void deleteDB(){
        File file = new File(pathToSaveDBFile);

        if (file.exists()){
            file.delete();
            Log.d(TAG, "Database Deleted");
        }
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
    public void onCreate(SQLiteDatabase db){

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    private int getVersionId() {
        SQLiteDatabase db = SQLiteDatabase.openDatabase(pathToSaveDBFile, null, SQLiteDatabase.OPEN_READONLY);
        String query = "SELECT version_id FROM dbVersion";
        Cursor cursor = db.rawQuery(query, null);
        cursor.moveToFirst();
        int v =  cursor.getInt(0);
        db.close();
        return v;
    }

    /*public void openDatabase() {
        String dbpath = myContext.getDatabasePath(DB_NAME).getPath();
        Log.d(TAG, "opening connection");

        if(mDatabase !=null && mDatabase.isOpen())
        {
            return;
        }

        mDatabase = SQLiteDatabase.openDatabase("/data/data/com.info.tredonline/databases/agrodata.db", null, SQLiteDatabase.OPEN_READWRITE);
    }*/



    /*public List<MyObject> read(String searchTerm) {

        List<MyObject> recordsList = new ArrayList<MyObject>();

        // select query
        String sql = "";
        sql += "SELECT * FROM messmast_tbl WHERE  msname LIKE '%" + searchTerm + "%'";
        sql += " ORDER BY msname DESC";
        sql += " LIMIT 0,5";

        SQLiteDatabase db = this.getWritableDatabase();

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                String objectName = cursor.getString(cursor.getColumnIndex("msname"));
                MyObject myObject = new MyObject(objectName);

                // add to list
                recordsList.add(myObject);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return the list of records
        return recordsList;
    }

    public List<MyProduct> newread(String searchProduct) {

        List<MyProduct> productList = new ArrayList<MyProduct>();

        // select query
        String sql = "";
        sql += "SELECT prodname FROM productmast WHERE prodname LIKE '%" + searchProduct + "%'";
        sql += " ORDER BY prodname DESC";
        sql += " LIMIT 0,5";

        SQLiteDatabase db = this.getWritableDatabase();

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                String objectName = cursor.getString(cursor.getColumnIndex("prodname"));
                MyProduct myProduct = new MyProduct(objectName);

                // add to list
                productList.add(myProduct);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return the list of records
        return productList;
    }

    public List<myPurchaseLedger> plgread(String searchTerm) {

        List<myPurchaseLedger> recordsList = new ArrayList<myPurchaseLedger>();

        // select query
        String sql = "";
        sql += "SELECT * FROM subledger_tbl WHERE  sledgername LIKE '%" + searchTerm + "%'";
        sql += " ORDER BY sledgername DESC";
        sql += " LIMIT 0,5";

        SQLiteDatabase db = this.getWritableDatabase();

        // execute the query
        Cursor cursor = db.rawQuery(sql, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                // int productId = Integer.parseInt(cursor.getString(cursor.getColumnIndex(fieldProductId)));
                String objectName = cursor.getString(cursor.getColumnIndex("sledgername"));
                myPurchaseLedger myPurchaseLedger = new myPurchaseLedger(objectName);

                // add to list
                recordsList.add(myPurchaseLedger);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        // return the list of records
        return recordsList;
    }*/

    public List<String> getLoksabha() {
        List<String> list = new ArrayList<String>();
        String sql = "";
        sql += "SELECT parliment FROM booth_master group by parliment";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(0));//adding 2nd column data
            } while (cursor.moveToNext());
        }
        // closing connection
        cursor.close();
        db.close();

        return list;
    }

    public ArrayList<HashMap<String, String>> GetVoterListAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> voterList = new ArrayList<>();
        String query = "Select voter_name_r,voter_middle_r,voter_surname_r,voter_village_r,voter_taluka_r,voter_voterid,voter_gender,voter_age,voter_unipque_id,voter_boothno,voterparliment,voterparlno,voterassmno from voter_mast order by voter_name_r,voter_middle_r,voter_surname_r";
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            HashMap<String, String> user = new HashMap<>();
            user.put("voternamef",cursor.getString(cursor.getColumnIndex("voter_name_r")));
            user.put("voternamem",cursor.getString(cursor.getColumnIndex("voter_middle_r")));
            user.put("voternames",cursor.getString(cursor.getColumnIndex("voter_surname_r")));
            voterList.add(user);
        }
        return  voterList;
    }

}
