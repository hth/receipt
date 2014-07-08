export JAVA_OPTS="-Djava.awt.headless=true -Dfile.encoding=UTF-8
-server -Xms1536m -Xmx2560m
-XX:NewSize=256m -XX:MaxNewSize=256m -XX:PermSize=256m
-XX:MaxPermSize=256m -XX:+DisableExplicitGC"
export CATALINA_HOME=/Library/Tomcat
export JAVA_HOME=/Library/Java/Home
export CATALINA_OPTS="-Xms1024M -Xmx4096M -XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled"
export CATALINA_PID=/tmp/catalina.pid