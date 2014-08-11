export JAVA_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8
-XX:NewSize=256m -XX:MaxNewSize=256m -XX:PermSize=256m
-XX:MaxPermSize=256m -XX:+DisableExplicitGC"
export CATALINA_HOME=/Library/Tomcat
export JAVA_HOME=/Library/Java/Home
export CATALINA_OPTS="-Xms2048M -Xmx4096M -XX:+CMSClassUnloadingEnabled"
export CATALINA_PID=/tmp/catalina.pid