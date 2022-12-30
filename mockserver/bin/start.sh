#!/bin/bash

SERVER_NAME="${project.artifactId}"
JAR_NAME="${project.build.finalName}.jar"
MAIN_CLASS="${main.class}"

cd `dirname $0`
BIN_DIR=`pwd`
cd ..
DEPLOY_DIR=`pwd`

CONF_DIR=$DEPLOY_DIR/config
SERVER_PORT=9010

PIDS=`ps -f | grep java | grep "$CONF_DIR" |awk '{print $2}'`
if [ "$1" = "status" ]; then
    if [ -n "$PIDS" ]; then
        echo "The $SERVER_NAME is running...!"
        echo "PID: $PIDS"
        exit 0
    else
        echo "The $SERVER_NAME is stopped"
        exit 0
    fi
fi

if [ -n "$PIDS" ]; then
    echo "ERROR: The $SERVER_NAME already started!"
    echo "PID: $PIDS"
    exit 1
fi

LOGS_DIR=$DEPLOY_DIR/logs
if [ ! -d $LOGS_DIR ]; then
    mkdir $LOGS_DIR
fi
STDOUT_FILE=/dev/null
if [[ -z ${STATTER_STDOUT} ]];then
    STDOUT_FILE=$LOGS_DIR/$SERVER_NAME.`date +"%Y%m%d%H%M%S"`.out
fi

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
JAVA_DEBUG_OPTS=""
if [ "$1" = "debug" ]; then
    JAVA_DEBUG_OPTS=" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n "
fi

JAVA_JMX_OPTS=""
if [ "$1" = "jmx" ]; then
    JAVA_JMX_OPTS=" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

JAVA_MEM_OPTS=""
BITS=`java -version 2>&1 | grep -i 64-bit`
if [ -n "$BITS" ]; then
    JAVA_MEM_OPTS=" -server -Xmx512m -Xms512m -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "
else
    JAVA_MEM_OPTS=" -server -Xms512m -Xmx512m -XX:PermSize=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi

LOG_IMPL_FILE=log4j2.properties
LOGGING_CONFIG=""
if [ -f "$CONF_DIR/$LOG_IMPL_FILE" ];then
    LOGGING_CONFIG="-Dlogging.config=$CONF_DIR/$LOG_IMPL_FILE"
fi
CONFIG_FILES=" -Dlogging.path=$LOGS_DIR $LOGGING_CONFIG -Dspring.config.location=$CONF_DIR/ "

if [[ -f $DEPLOY_DIR/$SERVER_NAME.pid ]]; then
  PID=`cat $DEPLOY_DIR/$SERVER_NAME.pid`
  if [[ `jps -mlv |grep ${PID} |wc -l` -gt 0 ]]; then
    echo 'exist another program['${APP_NAME}'] is running'
    exit 1
  fi
fi

echo -e "Starting the $SERVER_NAME ..."
nohup java $JAVA_OPTS $JAVA_MEM_OPTS $JAVA_DEBUG_OPTS $JAVA_JMX_OPTS $CONFIG_FILES -cp $DEPLOY_DIR/config:$DEPLOY_DIR/lib/* $MAIN_CLASS > $STDOUT_FILE 2>&1 &
if [[ -z ${STATTER_STDOUT} ]];then
    echo "STDOUT: $STDOUT_FILE"
fi

sleep 5

echo "OK!"
PID=`ps -f |grep java |grep ${MAIN_CLASS} |grep $DEPLOY_DIR | awk '{print $2}'`
echo $PID > $DEPLOY_DIR/$SERVER_NAME.pid
echo "PID: $PID"


