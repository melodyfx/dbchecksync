package com.github.melodyfx.dbchecksync.model;

/**
 * @Author: huangzhimao
 * @Date: 2021-04-05
 */
public class SlaveStatusBean {

  private String ioRunning;
  private String sqlRunning;
  private Integer secondsBehind;
  private String lastError;

  public SlaveStatusBean() {
  }

  public String getIoRunning() {
    return ioRunning;
  }

  public void setIoRunning(String ioRunning) {
    this.ioRunning = ioRunning;
  }

  public String getSqlRunning() {
    return sqlRunning;
  }

  public void setSqlRunning(String sqlRunning) {
    this.sqlRunning = sqlRunning;
  }

  public Integer getSecondsBehind() {
    return secondsBehind;
  }

  public void setSecondsBehind(Integer secondsBehind) {
    this.secondsBehind = secondsBehind;
  }

  public String getLastError() {
    return lastError;
  }

  public void setLastError(String lastError) {
    this.lastError = lastError;
  }
}
