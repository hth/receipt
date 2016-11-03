Follow the steps mentioned at http://tomcat.apache.org/tomcat-8.0-doc/logging.html#Using_Log4j

Pick log4j (Download Apache log4j 1.2.17) from 'lib' folder. Download `**tomcat-juli.jar**` and `**tomcat-juli-adapters.jar**` that are available as an "extras" component for Tomcat

If you want to configure Tomcat to use log4j globally:

Put from `local/lib log4j.jar` and `tomcat-juli-adapters.jar` from "extras" into `$CATALINA_HOME/lib`.
Replace `$CATALINA_HOME/bin/tomcat-juli.jar` with `tomcat-juli.jar` from "extras".

Delete `$CATALINA_BASE/conf/logging.properties` to prevent `java.util.logging` generating zero length log files.
