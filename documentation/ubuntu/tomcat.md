### Tomcat 8.5.5
Download tomcat. Copy to     
    
    sudo mkdir /opt/tomcat
    sudo mv ~/apache-tomcat /opt/tomcat
    sudo chown db -R apache-tomact
    
Link

    sudo ln -s /opt/tomcat/apache-tomcat /usr/local/tomcat
    

Add the file to location `/lib/systemd/system/tomcat.service`.

    sudo touch /lib/systemd/system/tomcat.service
    sudo chown db:db /lib/systemd/system/tomcat.service
    nano /lib/systemd/system/tomcat.service
     
Add the following content     
    
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
    
Next, reload the systemd daemon so that it knows about our service file:

    sudo systemctl daemon-reload
Start the Tomcat service by typing:

    sudo systemctl start tomcat
Double check that it started without errors by typing:

    sudo systemctl status tomcat
    
Enable the Service to Start at Boot : This creates a symlink
    
    sudo systemctl enable tomcat
