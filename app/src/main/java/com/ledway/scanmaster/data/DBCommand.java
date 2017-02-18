package com.ledway.scanmaster.data;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created by togb on 2017/2/18.
 */

public class DBCommand {
  private ConnectionPool connectionPool = new ConnectionPool();
  private String connectionString;

  public void setConnectionString(String connectionString) {
    this.connectionString = connectionString;
  }

  public String execute(String sql, String... args)
      throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
    Connection connection = connectionPool.getConnection(connectionString);
    CallableStatement csmt = connection.prepareCall(sql);
    int i = 0;
    for (; i < args.length; ++i) {
      csmt.setString(i + 1, args[i]);
    }
    csmt.registerOutParameter(i + 1, Types.VARCHAR);
    csmt.execute();
    return csmt.getString(i + 1);
  }
}
