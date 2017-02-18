package com.ledway.scanmaster;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.ledway.scanmaster.data.DBCommand;
import com.ledway.scanmaster.data.Settings;
import com.ledway.scanmaster.ui.AppPreferences;
import javax.inject.Inject;

public class MainActivity extends AppCompatActivity {
  private static final int REQUEST_SET = 1;
  @Inject Settings settings;
  private DBCommand dbCommand = new DBCommand();

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ((MApp)getApplication()).getAppComponet().inject(this);
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

  }
}
