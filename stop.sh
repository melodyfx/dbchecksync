#!/bin/sh
PID=$(cat sync.pid)
kill -9 $PID
