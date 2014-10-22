#!/bin/sh
export JAVA_OPTS="
-Djava.awt.headless=true
-Dfile.encoding=UTF-8
-XX:NewSize=256m
-XX:MaxNewSize=256m
-XX:PermSize=256m
-XX:MaxPermSize=512m
-XX:+DisableExplicitGC
-XX:+UseConcMarkSweepGC
-XX:+CMSIncrementalMode"
export CATALINA_HOME=/Library/Tomcat
export JAVA_HOME=/Library/Java/Home
export CATALINA_OPTS="-Xms2G -Xmx6G -XX:MaxPermSize=512M -XX:+UserParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -Dcom.sun.management.jmxremote"
export CATALINA_PID=/tmp/catalina.pid
