package com.ledway.scanmaster;

import android.app.Application;
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
  }
  public AppComponent getAppComponet(){
    return appComponent;
  }
}
