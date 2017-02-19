package com.ledway.scanmaster;

import android.app.Application;
import android.content.Intent;
import com.zkc.Service.CaptureService;
import timber.log.Timber;

/**
 * Created by togb on 2017/2/18.
 */

public class MApp extends Application {
  private AppComponent appComponent;

  @Override public void onCreate() {
    super.onCreate();
    Timber.plant(new Timber.DebugTree());
    appComponent = DaggerAppComponent.builder().appModule(new AppModule(this)).build();

    startScanService();
  }

  private void startScanService() {
    Intent newIntent = new Intent(this, CaptureService.class);
    //newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startService(newIntent);
  }

  public AppComponent getAppComponet(){
    return appComponent;
  }
}
