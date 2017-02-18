package com.ledway.scanmaster;

import android.content.Intent;
import android.os.health.TimerStat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.ledway.scanmaster.data.DBCommand;
import com.ledway.scanmaster.data.Settings;
import com.ledway.scanmaster.ui.AppPreferences;
import java.sql.SQLException;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {
  private static final int REQUEST_SET = 1;
  @Inject Settings settings;
  private DBCommand dbCommand = new DBCommand();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ((MApp)getApplication()).getAppComponet().inject(this);
    settingChanged();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
   switch (item.getItemId()){
     case R.id.action_settings:{
       startActivityForResult(new Intent(this, AppPreferences.class), REQUEST_SET);
       break;
     }
   }
    return true;
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch (requestCode){
      case REQUEST_SET:{
        settingChanged();
        break;
      }
    }
  }

  private void settingChanged() {
    String connectionStr = String.format("jdbc:jtds:sqlserver://%s;DatabaseName=%s;charset=UTF8", settings.getServer(),settings.getDb());
    Timber.v(connectionStr);
    dbCommand.setConnectionString(connectionStr);
    Observable.create(new Observable.OnSubscribe<String>() {
      @Override public void call(Subscriber<? super String> subscriber) {
        try {
          String msg = dbCommand.execute("{call sp_getBill(?,?,?,?)}" ,"Line","Reader","bill No");
          subscriber.onNext(msg);
          subscriber.onCompleted();
        } catch (Exception e) {
          e.printStackTrace();
          subscriber.onError(e);
        }
      }
    }).subscribeOn(Schedulers.io()).subscribe(Timber::v, Timber::e);

  }
}
