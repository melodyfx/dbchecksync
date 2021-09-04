package com.github.melodyfx.dbchecksync.impl;

import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import com.github.melodyfx.dbchecksync.JdbcConnect;
import com.github.melodyfx.dbchecksync.ProcessDatabase;
import com.github.melodyfx.dbchecksync.SendMail;
import com.github.melodyfx.dbchecksync.model.SlaveStatusBean;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author: huangzhimao
 * @Date: 2021-04-02
 */
public class ProcessMysql implements ProcessDatabase {

  private static final Logger logger = LoggerFactory.getLogger(ProcessMysql.class);

  @Override
  public void process(JdbcConnect jdbcConnect, Properties properties) throws Exception {
    String url1 = properties.getProperty("jdbc.url1");
    String username = properties.getProperty("jdbc.username");
    String password = properties.getProperty("jdbc.password");
    String chktbl = properties.getProperty("chktbl");

    Connection conn1 = null;
    Connection conn2 = null;
    //处理主库
    conn1 = jdbcConnect.getConn(url1, username, password);
    conn1.setAutoCommit(false);
    String sqlstr = "delete from "+chktbl;
    jdbcConnect.update(conn1, sqlstr);
    DateTime dt = DateTime.now();
    String dtChar = dt.toString("yyyy-MM-dd HH:mm:ss");
    sqlstr = "insert into "+chktbl+"(dt) values('"+dtChar+"')";
    jdbcConnect.update(conn1, sqlstr);

    sqlstr = "select dt from "+chktbl+" order by dt desc";
    DateTime dateTime1 = jdbcConnect.getResult(conn1, sqlstr);
    logger.info("主库checkpoint时间:"+dateTime1.toString("yyyy-MM-dd HH:mm:ss"));
    conn1.commit();

    //处理从库
    String url2 = properties.getProperty("jdbc.url2");
    TimeUnit.SECONDS.sleep(3);
    conn2 = jdbcConnect.getConn(url2, username, password);
    conn2.setAutoCommit(false);
    DateTime dateTime2 = jdbcConnect.getResult(conn2, sqlstr);
    logger.info("备库checkpoint时间:"+dateTime2.toString("yyyy-MM-dd HH:mm:ss"));
    conn2.commit();

    //判断是否同步
    long sub_time = dateTime1.getMillis() - dateTime2.getMillis();
    logger.info("主备相差时间间隔(秒):"+(sub_time/1000));
    if (sub_time / 1000 >= Integer.parseInt(properties.getProperty("sync.timeout"))) {
      logger.info("同步异常,同步超时.");
      //同步故障,发送邮件
      StringBuilder sb=new StringBuilder();
      sb.append("同步异常,同步超时:");
      sb.append("<br/>");
      sb.append("jdbc.url1:"+properties.getProperty("jdbc.url1"));
      sb.append("<br/>");
      sb.append("jdbc.url2:"+properties.getProperty("jdbc.url2"));
      sb.append("<br/>");
      SlaveStatusBean slaveStatusBean=jdbcConnect.getError(conn2);
      sb.append("Slave_IO_Running:"+slaveStatusBean.getIoRunning()+"<br/>");
      sb.append("Slave_SQL_Running:"+slaveStatusBean.getSqlRunning()+"<br/>");
      sb.append("Seconds_Behind_Master:"+slaveStatusBean.getSecondsBehind()+"<br/>");
      sb.append("Last_SQL_Error:"+slaveStatusBean.getLastError());
      SendMail sendMail = new SendMail(properties, "数据库同步异常", sb.toString());
      sendMail.sendMail();
    } else {
      logger.info("同步正常.");
    }

    if (conn1 != null) {
      conn1.close();
    }
    if (conn2 != null) {
      conn2.close();
    }
  }

}
