echo off

set APP_NAME=${project.build.finalName}.jar
set MAIN_CLASS=${main.class}
set LOG_IMPL_FILE=log4j2.properties
set LOGGING_CONFIG=
if exist ../config/%LOG_IMPL_FILE% (
    set LOGGING_CONFIG=-Dlogging.config=../config/%LOGGING_CONFIG%
)
set CONFIG= -Dlogging.path=../logs %LOGGING_CONFIG% -Dspring.config.location=../config/

set DEBUG_OPTS=
if ""%1"" == ""debug"" (
   set DEBUG_OPTS= -Xloggc:../logs/gc.log -verbose:gc -XX:+PrintGCDetails -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=../logs
   goto debug
)

set JMX_OPTS=
if ""%1"" == ""jmx"" (
   set JMX_OPTS= -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=9888 -Dcom.sun.management.jmxremote.ssl=FALSE -Dcom.sun.management.jmxremote.authenticate=FALSE
   goto jmx
)

echo "Starting the %APP_NAME%"
java -Xms512m -Xmx512m -server %DEBUG_OPTS% %JMX_OPTS% %CONFIG% -cp ../config:../lib/* %MAIN_CLASS%
goto end

:debug
echo "debug"
java -Xms512m -Xmx512m -server %DEBUG_OPTS% %CONFIG% -cp ../config:../lib/* %MAIN_CLASS%
goto end

:jmx
java -Xms512m -Xmx512m -server %JMX_OPTS% %CONFIG% -cp ../config:../lib/* %MAIN_CLASS%
goto end

:end
pause