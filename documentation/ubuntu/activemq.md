### ActiveMQ

`sftp` to `/tmp` latest version of `ActiveMQ`
Create directory `/opt/activemq` as `root` 

#### Directory to install

    Unzip `activemq` in `/tmp`
    mv /tmp/apache-activemq-5.14.1 /opt/activemq/
     
#### Create Link     

    ln -snf /opt/activemq/apache-activemq-5.14.1/ /usr/local/activemq
    
#### Copy ENV file as config activemq file & Change the user to DB   
 
    sudo cp /opt/activemq/apache-activemq-5.14.1/bin/env /etc/default/activemq 
    sudo sed -i '~s/^ACTIVEMQ_USER=""/ACTIVEMQ_USER="db"/' /etc/default/activemq

Inside `/etc/default/activemq` add `JAVA_HOME`    
    
    JAVA_HOME = /usr/local/java
    
Change Log place for `filebeat` to access
    
    /var/log/activemq/activemq.log    
    
#### Comment STOMP and WEB SOCKET 
    
Conf File - comment - Transporter - stomp & web socket in - activemq.xml 
    
    <!-- transportConnector name="stomp" uri="stomp://0.0.0.0:61613? maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/ -->
    <!-- transportConnector name="ws" uri="ws://0.0.0.0:61614? maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/ -->
        
#### Create Link in init.d directory
Use command to create link
    
    sudo ln -snf /usr/local/activemq/bin/activemq /etc/init.d/activemq
    sudo update-rc.d activemq defaults
    
### Start Active MQ

    sudo /etc/init.d/activemq start