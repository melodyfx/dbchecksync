package com.github.melodyfx.dbchecksync;

import java.util.Properties;

/**
 * @Author: huangzhimao
 * @Date: 2021-04-02
 */
public interface ProcessDatabase {
  public void process(JdbcConnect jdbcConnect, Properties properties) throws Exception;
}
