package com.github.melodyfx.dbchecksync;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.github.melodyfx.dbchecksync.impl.MysqlJdbcConnect;
import com.github.melodyfx.dbchecksync.impl.OracleJdbcConnect;
import com.github.melodyfx.dbchecksync.impl.ProcessMysql;
import com.github.melodyfx.dbchecksync.impl.ProcessOracle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class App {

    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        //InputStream in = App.class.getClassLoader().getResourceAsStream("config.properties");
        InputStream in = new BufferedInputStream(new FileInputStream("config.properties"));
        properties.load(in);

        try {
            doExec(properties);
        } catch (Exception e) {
            logger.error("程序异常.", e);
        }
    }


    private static void doExec(Properties properties) throws Exception {
        JdbcConnect jdbcConnect = null;
        String dbType = properties.getProperty("jdbc.db.type");
        if (null == dbType) {
            String msg = "未设置数据库类型jdbc.db.type";
            logger.error(msg);
            throw new Exception(msg);
        }
        if ("mysql".equals(dbType)) {
            jdbcConnect = new MysqlJdbcConnect();
            new ProcessMysql().process(jdbcConnect, properties);
        } else if ("oracle".equals(dbType)) {
            jdbcConnect = new OracleJdbcConnect();
            new ProcessOracle().process(jdbcConnect, properties);
        } else {
            String msg = "未知数据库类型jdbc.db.type:" + dbType;
            logger.error(msg);
            throw new Exception(msg);
        }
    }

}
