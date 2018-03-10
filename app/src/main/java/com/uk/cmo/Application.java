package com.uk.cmo;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.content.Context;
/**
 * Created by usman on 07-03-2018.
 */

public class Application extends MultiDexApplication {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}


