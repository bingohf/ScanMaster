package com.ledway.scanmaster.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.TwoStatePreference;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import com.ledway.scanmaster.BuildConfig;
import com.ledway.scanmaster.MApp;
import com.ledway.scanmaster.R;
import com.ledway.scanmaster.data.Settings;
import javax.inject.Inject;
import rx.Observable;

/**
 * Created by togb on 2016/4/10.
 */
public class AppPreferences extends PreferenceActivity
    implements SharedPreferences.OnSharedPreferenceChangeListener {
  private Preference mPfServer;
  @Inject Settings settings;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ((MApp)getApplication()).getAppComponet().inject(this);

    addPreferencesFromResource(R.xml.preferences);
    //PreferenceManager.setDefaultValues(AppPreferences.this, R.xml.preferences, false);
    mPfServer = findPreference("Server");
    setPreference(mPfServer, new PreferenceInterface() {
      @Override public String getValue() {
        return settings.getServer();
      }

      @Override public void setValue(String newValue) {
        settings.setServer(newValue);
      }
    });


    setPreference(findPreference("DB"), new PreferenceInterface() {
      @Override public String getValue() {
        return settings.getDb();
      }

      @Override public void setValue(String newValue) {
        settings.setDb(newValue);
      }
    });

    setPreference(findPreference("Line"), new PreferenceInterface() {
      @Override public String getValue() {
        return settings.getLine();
      }

      @Override public void setValue(String newValue) {
        settings.setLine(newValue);
      }
    });

    setPreference(findPreference("Reader"), new PreferenceInterface() {
      @Override public String getValue() {
        return settings.getReader();
      }

      @Override public void setValue(String newValue) {
        settings.setReader(newValue);
      }
    });
    //nitSummary(getPreferenceScreen());



    findPreference("about").setOnPreferenceClickListener(preference -> {
      Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "+886222799889"));
      AppPreferences.this.startActivity(intent);
      return true;
    });
    findPreference("version").setSummary(BuildConfig.VERSION_NAME);
  }

  private void setPreference(Preference preference, PreferenceInterface preferenceInterface){
    preference.setSummary(preferenceInterface.getValue());
    preference.setOnPreferenceClickListener((preference2) -> {
      EditText editText = new EditText(AppPreferences.this);
      editText.setText(preference.getSummary());
      AlertDialog.Builder builder = new AlertDialog.Builder(AppPreferences.this);
      builder.setTitle(R.string.setting_server_title)
          .setView(editText)
          .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {
              preference.setSummary(editText.getText().toString());
              preferenceInterface.setValue(editText.getText().toString());
            }
          })
          .setNegativeButton(R.string.cancel_button, (dialogInterface, i) -> {

          });
      builder.create().show();

      return true;
    });
  }


  private void setEnableSetting(boolean b) {
    PreferenceGroup pGrp = (PreferenceGroup) getPreferenceScreen();
    for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
      Preference p = pGrp.getPreference(i);
      if (p instanceof DialogPreference || p instanceof TwoStatePreference) {
        pGrp.getPreference(i).setEnabled(b);
        pGrp.getPreference(i).setSelectable(b);
      }
    }
  }

  @Override protected void onResume() {
    super.onResume();
    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
  }

  @Override public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
    updatePrefSummary(findPreference(key));
  }

  private void initSummary(Preference p) {
    if (p instanceof PreferenceGroup) {
      PreferenceGroup pGrp = (PreferenceGroup) p;
      for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
        initSummary(pGrp.getPreference(i));
      }
    } else {
      updatePrefSummary(p);
    }
  }

  private void updatePrefSummary(Preference p) {
    if (p instanceof ListPreference) {
      ListPreference listPref = (ListPreference) p;
      p.setSummary(listPref.getEntry());
    }
    if (p instanceof EditTextPreference) {
      EditTextPreference editTextPref = (EditTextPreference) p;
      if (p.getTitle().toString().toLowerCase().contains("password")) {
        p.setSummary("******");
      } else {
        p.setSummary(editTextPref.getText());
      }
      editTextPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
        @Override public boolean onPreferenceChange(Preference preference, Object newValue) {
          return newValue.toString().length() > 0;
        }
      });
    }
    if (p instanceof MultiSelectListPreference) {
      EditTextPreference editTextPref = (EditTextPreference) p;
      p.setSummary(editTextPref.getText());
    }
  }

  private interface PreferenceInterface{
    String getValue();
    void setValue(String newValue);
  }

}
