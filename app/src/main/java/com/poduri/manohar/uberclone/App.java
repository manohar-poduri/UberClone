package com.poduri.manohar.uberclone;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("BEotWdV03srfcpRkEJ4hsXtlkJB8rAR8j8ivPyA1")
                // if defined
                .clientKey("pxX5Co9Q41LJsnNKhSKFlz3UcYm0Zo83rhPFeMoD")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
