+ 简介

  dbchecksync是一款java编写的数据库同步检查工具，能够对主备数据库的同步情况做循环检查，例如Oracle的DataGuard、MySQL的主从复制，同步异常则会进行邮件告警通知管理员。

  目前支持Oracle、MySQL、MariaDB数据库。

+ 准备java环境

  一般来说，centOS7系统一般都默认安装了jdk程序，如果没有则自行安装jdk。

  执行以下命令检查系统是否安装java。建议jdk1.8以上版本。

  ```shell
  java -version
  ```

+ 部署dbchecksync（oracle）

  下载dbchecksync并解压。

  + 配置config.properties

    ```properties
    #目前支持oracke、mysql数据库
    jdbc.db.type=oracle
    #主库
    jdbc.url1=jdbc:oracle:thin:@//192.168.230.101:1521/fxdb
    #备库
    jdbc.url2=jdbc:oracle:thin:@//192.168.230.102:1522/fxdb
    jdbc.username=synccheck
    jdbc.password=Oracle123
    
    #jdbc.db.type=mysql
    #jdbc.url1=jdbc:mysql://192.168.230.102:3306/sync?useSSL=false&useUnicode=true&characterEncoding=UTF8
    #jdbc.url2=jdbc:mysql://192.168.230.102:3306/sync?useSSL=false&useUnicode=true&characterEncoding=UTF8
    #jdbc.username=synccheck
    #jdbc.password=123456
    
    mail.host=smtp.qq.com
    mail.username=xxxxxxxx@qq.com
    mail.password=xxxxxx
    #收件人,多个用逗号隔开
    mail.recipients=xxxxxxxx@qq.com
    
    #同步检查时间间隔,单位为秒
    time.interval=120
    
    #判断同步超时的时间长度,单位为秒
    sync.timeout=300
    ```

  + 配置同步账户和表

    ```sql
    create user synccheck identified by Oracle123;
    grant connect,resource to synccheck;
    alter user synccheck quota unlimited on users;
    conn synccheck/Oracle123
    create table checkpoint(dt date);
    ```

+ 部署dbchecksync（MySQL、MariaDB）

  + 配置config.properties

    ```properties
    #目前支持oracke、mysql数据库
    #jdbc.db.type=oracle
    #主库
    #jdbc.url1=jdbc:oracle:thin:@//192.168.230.101:1521/fxdb
    #备库
    #jdbc.url2=jdbc:oracle:thin:@//192.168.230.102:1522/fxdb
    #jdbc.username=synccheck
    #jdbc.password=Oracle123
    
    jdbc.db.type=mysql
    jdbc.url1=jdbc:mysql://192.168.230.102:3306/sync?useSSL=false&useUnicode=true&characterEncoding=UTF8
    jdbc.url2=jdbc:mysql://192.168.230.102:3306/sync?useSSL=false&useUnicode=true&characterEncoding=UTF8
    jdbc.username=synccheck
    jdbc.password=123456
    
    mail.host=smtp.qq.com
    mail.username=xxxxxxxx@qq.com
    mail.password=xxxxxx
    #收件人,多个用逗号隔开
    mail.recipients=xxxxxxxx@qq.com
    
    #同步检查时间间隔,单位为秒
    time.interval=120
    
    #判断同步超时的时间长度,单位为秒
    sync.timeout=300
    ```

  + 配置同步账户和表

    ```sql
    create database sync;
    create user synccheck@'%' identified by '123456';
    grant all privileges on sync.* to synccheck@'%';
    use sync;
    create table checkpoint(dt datetime);
    ```

+ 启动关闭dbchecksync

  ```shell
  #启动
  sh start.sh
  
  #关闭
  sh stop.sh
  
  #如果无法结束进程,可以使用如下命令查找到java进程号,然后使用kill命令结束进程
  ps -ef|grep java
  ```

+ 配置开机自启动(systemd方式)

  1.创建启动脚本
  
  vi run_dbchecksync.sh
  
  ```bash
  #!/bin/bash
  set -e
  
  DEPLOY_DIR=/home/soft/dbchecksync-1.2
  
  cd "${DEPLOY_DIR}" || exit 1
  exec java -jar dbchecksync-1.2.jar
  ```
  chmod 777 run_dbchecksync.sh
  
  2.创建systemd文件
  
  vi /etc/systemd/system/dbchecksync.service
  
  ```shell
  [Unit]
  Description=dbchecksync service
  After=syslog.target network.target remote-fs.target nss-lookup.target
  
  [Service]
  LimitNOFILE=1000000
  LimitSTACK=10485760
  
  User=root
  ExecStart=/home/soft/dbchecksync-1.2/run_dbchecksync.sh
  Restart=always
  
  RestartSec=15s
  
  [Install]
  WantedBy=multi-user.target
  ```
  执行如下命令:
  
  ```shell
  systemctl daemon-reload
  systemctl start dbchecksync
  systemctl status dbchecksync
  systemctl enable dbchecksync
  ```
  
+ 同步原理

  dbchecksync会定时向checkpoint表写入系统当前时间，然后去备库检查checkpoint表里对应的表记录，两者相减的时间在{sync.timeout}秒范围内，则认为是同步正常，否则认为是同步异常。

