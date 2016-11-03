### Tomcat 8.5.5
Download tomcat. Copy to `/tmp`    
    
    tar -xvf apache-tomcat-8.5.6.tar.gz &&
    sudo mkdir /opt/tomcat &&
    sudo mv apache-tomcat-8.5.6 /opt/tomcat
    
Link

    sudo ln -snf /opt/tomcat/apache-tomcat-8.5.6 /usr/local/tomcat
    
Log files directory 
    
    sudo mkdir /var/log/tomcat && 
    sudo chown db:db /var/log/tomcat   
    
Add the file to location `/lib/systemd/system/tomcat.service`.

    sudo touch /lib/systemd/system/tomcat.service &&
    sudo chown db:db /lib/systemd/system/tomcat.service &&
    nano /lib/systemd/system/tomcat.service
     
Add the following content to `tomcat.service`     
    
    [Unit]
    Description=Apache Tomcat Web Application Container
    After=network.target
    
    [Service]
    Type=forking
    
    Environment=JAVA_HOME=/usr/local/java
    Environment=CATALINA_PID=/usr/local/tomcat/temp/tomcat.pid
    Environment=CATALINA_HOME=/usr/local/tomcat
    Environment=CATALINA_BASE=/usr/local/tomcat
    Environment='CATALINA_OPTS=-Xms2048M -Xmx7158M -server -XX:+UseParallelGC'
    Environment='JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom'
    
    ExecStart=/usr/local/tomcat/bin/startup.sh
    ExecStop=/usr/local/tomcat/bin/shutdown.sh
    
    User=db
    Group=db
    UMask=0007
    RestartSec=10
    Restart=always
    
    [Install]
    WantedBy=multi-user.target
    
### Other configurations

#### Stop request to tomcat before unloading
Tomcat supports unloadDelay which waits till the configured time for unloading servlets.
This should configured in `context.xml` as follows: 20 seconds below

    nano /usr/local/tomcat/conf/context.xml
    <Context unloadDelay="20000">  
    
#### Warnings [consider increasing the maximum size of the cache]
       
In Tomcat 8.5.4, $CATALINA_BASE/conf/context.xml add block below before </Context>
       
    <Resources cachingAllowed="true" cacheMaxSize="100000" />

http://stas-blogspot.blogspot.ch/2011/07/most-complete-list-of-xx-options-for.html

#### Logging setup
Follow the steps mentioned at http://tomcat.apache.org/tomcat-8.0-doc/logging.html#Using_Log4j

Pick log4j (Download Apache log4j 1.2.17) from `lib` folder. Download `tomcat-juli.jar` and `tomcat-juli-adapters.jar` that are available as an "extras" component for Tomcat

If you want to configure Tomcat to use log4j globally:

Put from `local/lib log4j.jar` and `tomcat-juli-adapters.jar` from "extras" into `$CATALINA_HOME/lib`.
Replace `$CATALINA_HOME/bin/tomcat-juli.jar` with `tomcat-juli.jar` from "extras".

Delete `$CATALINA_BASE/conf/logging.properties` to prevent `java.util.logging` generating zero length log files.


    cd /tmp
    wget http://mirror.cogentco.com/pub/apache/tomcat/tomcat-8/v8.0.38/bin/extras/tomcat-juli-adapters.jar &&
    wget http://mirror.cogentco.com/pub/apache/tomcat/tomcat-8/v8.0.38/bin/extras/tomcat-juli.jar
    
    mv tomcat-juli.jar /usr/local/tomcat/bin &&
    mv tomcat-juli-adapters.jar /usr/local/tomcat/lib &&
    mv log4j.properties /usr/local/tomcat/lib &&
    mv log4j-1.2.17.jar /usr/local/tomcat/lib &&
    rm /usr/local/tomcat/conf/logging.properties &&
    rm -rf /usr/local/tomcat/webapps/*
        
    
Next, reload the systemd daemon so that it knows about our service file:

    sudo systemctl daemon-reload
Enable the Service to Start at Boot : This creates a symlink
    
    sudo systemctl enable tomcat    
Start the Tomcat service by typing:

    sudo systemctl start tomcat
Double check that it started without errors by typing:

    sudo systemctl status tomcat
   