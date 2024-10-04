#!/bin/bash

cd `dirname $0`
BIN_DIR=`pwd`

SERVICE_NAME=PongService
JAR_NAME="pong-0.0.1-SNAPSHOT.jar"
MAIN_NAME="com.philo.challenge.pong.PongApplication"
VM_ARGS_PERM_SIZE='PermSize'
VM_ARGS_METASPACE_SIZE='MetaspaceSize'
JAVA_8_VERSION="180"

STDOUT_FILE=stdout.log

if [ $# -gt 0 ]; then
    SERVICE_NAME="$SERVICE_NAME-$1"
    STDOUT_FILE="stdout-$1.log"
fi

PIDS=`ps -ef | grep java | grep -v grep | grep "$SERVICE_NAME" |awk '{print $2}'`
if [ -n "$PIDS" ]; then
    echo "ERROR: The $SERVICE_NAME already started!"
    echo "PID: $PIDS"
    exit
fi

JAVA_OPTS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
JAVA_MEM_OPTS=""
# set jvm args by different java version
JAVA_VERSION=`java -fullversion 2>&1 | awk -F[\"\.] '{print $2$3$4}' |awk -F"_" '{print $1}'`
# echo $JAVA_VERSION
VM_ARGS=${VM_ARGS_PERM_SIZE}
TEMP_VERSION=$(echo ${JAVA_VERSION} | grep "+")
if [[ "$TEMP_VERSION" != "" ]]; then
        JAVA_VERSION=$(echo ${JAVA_VERSION} | awk -F"+" '{print $1}')
fi
# compare java version
if [ "${JAVA_VERSION}" -ge ${JAVA_8_VERSION} ]; then
    VM_ARGS=${VM_ARGS_METASPACE_SIZE}
fi

# MaxInlineLevel=15 is the default since JDK 14 and can be removed once older JDKs are no longer supported
BITS=`java -version 2>&1 | grep -i 64-bit`
JAVA_MEM_OPTS=" -server -Xmx2g -Xms2g -Xmn256m -XX:${VM_ARGS}=128m -Xss512k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC -XX:+CMSParallelRemarkEnabled -XX:+UseCMSCompactAtFullCollection -XX:LargePageSizeInBytes=128m -XX:+UseFastAccessorMethods -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 "

if [ -n "$BITS" ]; then
    JAVA_MAJOR_VERSION=${JAVA_VERSION%%[.|-]*}
    JAVA_MINOR_VERSION=$(echo $JAVA_VERSION | awk -F\. '{ print $2 }')
    if [ $JAVA_MAJOR_VERSION -ge 9 ] || ([ $JAVA_MAJOR_VERSION -ge 1 ] && [ $JAVA_MINOR_VERSION -ge 8 ]); then
      JAVA_MEM_OPTS=" -server -Xmx2g -Xms2g -Xmn256m -XX:${VM_ARGS}=128m -Xss512k -XX:LargePageSizeInBytes=128m -XX:+DisableExplicitGC -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:G1HeapRegionSize=16m -XX:G1ReservePercent=25 -XX:InitiatingHeapOccupancyPercent=30 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:SurvivorRatio=8 -XX:G1ReservePercent=15 "
    fi
else
    JAVA_MEM_OPTS=" -server -Xms1g -Xmx1g -XX:${VM_ARGS}=128m -XX:SurvivorRatio=2 -XX:+UseParallelGC "
fi

echo -e "Starting the $SERVICE_NAME ...\c"
nohup java $JAVA_OPTS $JAVA_MEM_OPTS -DServerName=$SERVICE_NAME -jar $JAR_NAME $MAIN_NAME > $STDOUT_FILE 2>&1 &

COUNT=0
while [ $COUNT -lt 1 ]; do
    echo -e ".\c"
    sleep 1
    COUNT=`ps -ef | grep java | grep -v grep | grep "$SERVICE_NAME" | awk '{print $2}' | wc -l`
    if [ $COUNT -gt 0 ]; then
        break
    fi
done

echo "OK!"
PIDS=`ps -ef | grep java | grep -v grep | grep "$SERVICE_NAME" | awk '{print $2}'`
echo "PID: $PIDS"
echo "STDOUT: $STDOUT_FILE"
