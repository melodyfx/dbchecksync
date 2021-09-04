package com.github.melodyfx.dbchecksync;

import java.sql.Connection;
import com.github.melodyfx.dbchecksync.model.SlaveStatusBean;
import org.joda.time.DateTime;

/**
 * @Author: huangzhimao
 * @Date: 2021-03-11
 */
public interface JdbcConnect {

  Connection getConn(String url, String username, String password) throws Exception;

  DateTime getResult(Connection conn, String sql) throws Exception;

  int update(Connection conn, String sql) throws Exception;

  SlaveStatusBean getError(Connection conn) throws Exception;
}
