package com.ledway.scanmaster.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by togb on 2017/2/18.
 */

public class DBCommand {
  private ConnectionPool connectionPool;
  private String connectionString;

  public void setConnectionString(String connectionString){
    this.connectionString = connectionString;
  }


  public Object execute(String sql, String... args)
      throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
    Connection connection = connectionPool.getConnection(connectionString);
    CallableStatement csmt = connection.prepareCall(sql);
    return "";
  }
}
