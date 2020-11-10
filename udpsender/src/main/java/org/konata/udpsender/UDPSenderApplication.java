package org.konata.udpsender;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPSenderApplication extends Application {
    private static Context context;

    public void onCreate() {
        super.onCreate();
        UDPSenderApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return UDPSenderApplication.context;
    }

    public AppContainer appContainer = new AppContainer();
}
