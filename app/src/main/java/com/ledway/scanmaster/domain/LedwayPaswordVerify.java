package com.ledway.scanmaster.domain;

import com.ledway.scanmaster.interfaces.PasswordVerify;
import java.util.Calendar;
import timber.log.Timber;

/**
 * Created by togb on 2017/3/5.
 */

public class LedwayPaswordVerify implements PasswordVerify{
  private String mPassword;

  public LedwayPaswordVerify(){
    calcPassword();
  }

  private void calcPassword() {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(System.currentTimeMillis());
    int month = calendar.get(Calendar.MONTH);
    mPassword = String.format("1868%d", month * month);
    Timber.v("password:%s", mPassword);
  }

  @Override public boolean verify(String input) {
    Timber.v("input:%s", input);
    return mPassword.equals(input);
  }
}
