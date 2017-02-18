package com.ledway.scanmaster;

import android.content.Context;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created by togb on 2017/2/18.
 */
@Singleton
@Module
public class AppModule {
  private Context mContext ;
  public AppModule(Context context){
    mContext = context;
  }

  @Provides @ApplicationContext Context provideContext(){
    return mContext;
  }
}
