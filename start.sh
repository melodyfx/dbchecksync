#!/bin/sh
nohup java -jar dbchecksync-1.3.jar >> dbchecksync.log 2>&1 &
echo $! > sync.pid
