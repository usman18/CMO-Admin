package com.uk.cmo;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by usman on 28-08-2018.
 */

public class Application extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //For enabling offline capabilities.
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

    }
}
