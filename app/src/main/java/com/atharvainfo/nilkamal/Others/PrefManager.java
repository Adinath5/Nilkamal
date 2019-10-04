package com.atharvainfo.nilkamal.Others;

/**
 * Created by this pc on 23-05-17.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Created by Ravi on 01/06/15.
 */
@SuppressLint("CommitPrefEdits")
public class PrefManager {
    // Shared Preferences
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;


    // Shared pref file name
    private static final String PREF_NAME = "AndroidHive";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    public static final String Event = "event";

    public static final String LOGIN = "login";


    // Constructor
    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();


    }

    public void setUserName(String UserName){
        editor.putString("UserName", UserName);
        editor.commit();
    }
    public void setNotifyStatus(String status) {

        editor.putString("status", status);
        editor.commit();
    }

    public void setName(String name) {

        editor.putString("name", name);
        editor.commit();
    }

    public void setPName(String name) {

        editor.putString("pname", name);
        editor.commit();
    }

    public void setRelation(String name) {

        editor.putString("relation", name);
        editor.commit();
    }

    public void setPContact(String name) {

        editor.putString("pcontact", name);
        editor.commit();
    }
    public void setEmail(String name) {

        editor.putString("email", name);
        editor.commit();
    }

    public void setTabNumber(int number) {

        editor.putInt("number", number);
        editor.commit();
    }

    public void setNotify(int number) {

        editor.putInt("notify", number);
        editor.commit();
    }

    public void setButtonNotify(int number) {

        editor.putInt("bnotify", number);
        editor.commit();
    }

    public void setButton2Notify(int number) {

        editor.putInt("bnotify2", number);
        editor.commit();
    }


    public String getNotifyStatus() {
        return pref.getString("status", null);
    }

    public String getName() {
        return pref.getString("name", null);
    }

    public String getPName() {
        return pref.getString("pname", null);
    }

    public String getRelation() {
        return pref.getString("relation", null);
    }

    public String getPContact() {
        return pref.getString("pcontact", null);
    }

    public String getEmail() {
        return pref.getString("email", null);
    }

    public int getTabNumber() {
        return pref.getInt("number", 0);
    }

    public int getNotify() {
        return pref.getInt("notify", 0);
    }

    public int getButtonNotify() {
        return pref.getInt("bnotify", 0);
    }

    public int getButton2Notify() {
        return pref.getInt("bnotify2", 0);
    }

    public String getUserName(){
        return pref.getString("UserName", null);
    }


}

