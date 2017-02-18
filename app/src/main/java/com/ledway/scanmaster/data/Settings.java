package com.ledway.scanmaster.data;

import javax.inject.Inject;

/**
 * Created by togb on 2017/2/18.
 */

public class Settings {
  private final SPModel spModel;
  public String server;
  public String db;
  public String line;
  public String reader;

  @Inject
  public Settings(SPModel spModel){
    this.spModel = spModel;
  }
  @Inject
  void restore(){
    restore(spModel.loadSetting());
  }

  private void restore(SettingSnap settingSnap){
    server = settingSnap.server;
    db = settingSnap.db;
    line = settingSnap.line;
    reader = settingSnap.reader;
  }

  public String getServer() {
    return server;
  }

  public void setServer(String server) {
    this.server = server;
  }

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }

  public String getLine() {
    return line;
  }

  public void setLine(String line) {
    this.line = line;
  }

  public String getReader() {
    return reader;
  }

  public void setReader(String reader) {
    this.reader = reader;
  }
}
