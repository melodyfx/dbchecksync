package com.github.melodyfx.dbchecksync.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import com.github.melodyfx.dbchecksync.JdbcConnect;
import com.github.melodyfx.dbchecksync.model.SlaveStatusBean;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

/**
 * @Author: huangzhimao
 * @Date: 2021-03-11
 */
public class MysqlJdbcConnect implements JdbcConnect {

  private String driver = "com.mysql.cj.jdbc.Driver";

  public MysqlJdbcConnect() {
  }

  @Override
  public Connection getConn(String url, String username, String password) throws Exception {
    Connection conn = null;
    Class.forName(driver);
    conn = DriverManager.getConnection(url, username, password);
    return conn;
  }

  @Override
  public DateTime getResult(Connection conn, String sql) throws Exception {
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
    rs.next();
    String dt = rs.getString("dt");
    rs.close();
    pstmt.close();
    DateTime beginDate = DateTime.parse(dt, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
    return beginDate;

  }

  @Override
  public int update(Connection conn, String sql) throws Exception {
    PreparedStatement pstmt = conn.prepareStatement(sql);
    return pstmt.executeUpdate();
  }

  @Override
  public SlaveStatusBean getError(Connection conn) throws Exception {
    SlaveStatusBean slaveStatusBean=new SlaveStatusBean();
    String sql="show slave status";
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();
    while (rs.next()) {
      slaveStatusBean.setIoRunning(rs.getString("Slave_IO_Running"));
      slaveStatusBean.setSqlRunning(rs.getString("Slave_SQL_Running"));
      slaveStatusBean.setSecondsBehind(rs.getInt("Seconds_Behind_Master"));
      slaveStatusBean.setLastError(rs.getString("Last_SQL_Error"));
    }
    return slaveStatusBean;
  }
}
