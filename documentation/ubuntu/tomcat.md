### Tomcat 8.5.5
Download tomcat. Copy to     
    
    sudo mkdir /opt/tomcat
    sudo mv ~/apache-tomcat /opt/tomcat
    sudo chown db -R apache-tomact
    
Link

    sudo ln -s /opt/tomcat/apache-tomcat /usr/local/tomcat
    

Add the file to location `/lib/systemd/system/tomcat.service`. We might need to make a link to `/etc/systemd/system/tomcat.serice`
    
    [Unit]
    Description=Apache Tomcat Web Application Container
    After=network.target
    
    [Service]
    Type=forking
    
    Environment=JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64/jre
    Environment=CATALINA_PID=/opt/tomcat/temp/tomcat.pid
    Environment=CATALINA_HOME=/opt/tomcat
    Environment=CATALINA_BASE=/opt/tomcat
    Environment='CATALINA_OPTS=-Xms2048M -Xmx7158M -server -XX:+UseParallelGC'
    Environment='JAVA_OPTS=-Djava.awt.headless=true -Djava.security.egd=file:/dev/./urandom'
    
    ExecStart=/opt/tomcat/bin/startup.sh
    ExecStop=/opt/tomcat/bin/shutdown.sh
    
    User=db
    Group=root
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
    sudo systemctl enable tomcat
