#! /bin/sh

cd `dirname $0`

BIN_HOME=`pwd`

cd ../

HOME=`pwd`

MAIN_CLASS="com.surfilter.mass.SfDataTransferJson"

LIB_PATH=$HOME/lib/*

LIB_JARS=.
for jar in `ls $LIB_PATH`
do
    LIB_JARS=$LIB_JARS:$jar
done

CONF_PATH=$HOME/conf/*
for jar in `ls $CONF_PATH`
do
    LIB_JARS=$LIB_JARS:$jar
done

JAVA_OPTS="-server -Xmx2g -Xms2g -Xmn256m -XX:PermSize=128m -Xss256k -XX:+DisableExplicitGC -XX:+UseConcMarkSweepGC"

DIR_LOG=$HOME/logs/log.log

java $JAVA_OPTS -classpath $LIB_JARS $MAIN_CLASS $1> $DIR_LOG 2>&1 &
