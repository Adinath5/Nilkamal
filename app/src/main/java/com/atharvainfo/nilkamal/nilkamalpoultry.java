package com.atharvainfo.nilkamal;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.firebase.client.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;

public class nilkamalpoultry extends Application {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private boolean backgroundRunning = false;
    private FirebaseAnalytics firebaseAnalytics;

    synchronized public boolean isBackgroundRunning() {
        return backgroundRunning;
    }

    synchronized public void setBackgroundRunning(boolean backgroundRunning) {
        this.backgroundRunning = backgroundRunning;
    }

    public nilkamalpoultry() {
        super();
    }

    public FirebaseAnalytics getFirebaseAnalytics() {
        return firebaseAnalytics;
    }

    public void setFirebaseAnalytics(FirebaseAnalytics firebaseAnalytics) {
        this.firebaseAnalytics = firebaseAnalytics;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Fabric.with(this, new Crashlytics());
        FirebaseApp.initializeApp(nilkamalpoultry.this);
        Firebase.setAndroidContext(this);
        // enable disk persistence
        Firebase.getDefaultConfig().setPersistenceEnabled(true);

        //pre-load fonts for rtEditor
        //FontManager.preLoadFonts(this);

    }

    synchronized public FirebaseAnalytics getTracker() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        return firebaseAnalytics;
    }


    public void sendEvent(String id, String name) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }
    public void setUserProperty(String id, String name) {
        firebaseAnalytics.setUserProperty(id, name);

    }


}
