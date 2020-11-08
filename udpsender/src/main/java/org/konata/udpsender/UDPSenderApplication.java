package org.konata.udpsender;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPSenderApplication extends Application {
    public AppContainer appContainer = new AppContainer();
}
