#!/bin/bash

SERVER_NAME="${project.artifactId}"
JAR_NAME="${project.build.finalName}.jar"
MAIN_CLASS="${main.class}"

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`

if [[ -f $DEPLOY_DIR/$SERVER_NAME.pid ]]; then
  PID=`cat $DEPLOY_DIR/$SERVER_NAME.pid`
  if [[ `jps -mlv |grep ${PID} |wc -l` -gt 0 ]]; then
    kill -9 ${PID}
    echo ${APPLICATION} stopped successfully
  fi
  exit 0
fi
echo 'there is no pid file, find program by app name'
PID=`ps -ef |grep java |grep ${MAIN_CLASS} |awk '{ print $2 }'`
if [[ -z "$PID" ]]
then
    echo ${APPLICATION} is already stopped
else
    echo kill  ${PID}
    kill -9 ${PID}
    echo ${APPLICATION} stopped successfully
fi