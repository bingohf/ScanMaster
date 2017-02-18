package com.ledway.scanmaster;

import android.content.Context;
import com.ledway.scanmaster.data.Settings;
import com.ledway.scanmaster.ui.AppPreferences;
import dagger.Component;
import javax.inject.Singleton;

/**
 * Created by togb on 2017/2/18.
 */
@Singleton
@Component(modules ={ AppModule.class})
public interface AppComponent {
  @ApplicationContext Context context();
  void inject(AppPreferences appPreferences);
  void inject(MainActivity mainActivity);
}
