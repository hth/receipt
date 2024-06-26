### ActiveMQ

Download file for ubuntu. 

`sftp` to `/tmp` latest version of `ActiveMQ`

#### Directory to install
Inside `/tmp` perform `untar` of `activemq`

    sudo mkdir /opt/activemq &&
    tar -xvf apache-activemq-5.14.1-bin.tar.gz &&
    sudo mv /tmp/apache-activemq-5.14.1 /opt/activemq/
     
#### Create Link     

    sudo ln -snf /opt/activemq/apache-activemq-5.14.1/ /usr/local/activemq
    
Ownership of link remains with `root` and cannot be converted to `db`    
    
#### Copy ENV file as config activemq file & Change the user to DB   
 
    sudo cp /opt/activemq/apache-activemq-5.14.1/bin/env /etc/default/activemq && 
    sudo sed -i '~s/^ACTIVEMQ_USER=""/ACTIVEMQ_USER="db"/' /etc/default/activemq
    
Change ownership to `db`
    
    sudo chown db:db /etc/default/activemq

Inside `/etc/default/activemq` add `JAVA_HOME`. Uncomment and add
    
    nano /etc/default/activemq 
    JAVA_HOME="/usr/local/java"
    
### Change Log place to access

Modify `/usr/local/activemq/conf/log4j.properties`
    
    nano /usr/local/activemq/conf/log4j.properties
    
Edit to
    
    log4j.appender.logfile.file=/var/log/activemq/activemq.log    
    log4j.appender.audit.file=/var/log/activemq/audit.log    
    
Make directory for logging activemq
    
    sudo mkdir /var/log/activemq && 
    sudo chown db:db /var/log/activemq
    
#### Comment STOMP and WEB SOCKET 
    
Conf File - comment - Transporter - stomp & web socket in - activemq.xml 
    
    nano /usr/local/activemq/conf/activemq.xml
    <!-- transportConnector name="stomp" uri="stomp://0.0.0.0:61613? maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/ -->
    <!-- transportConnector name="ws" uri="ws://0.0.0.0:61614? maximumConnections=1000&amp;wireFormat.maxFrameSize=104857600"/ -->
        
#### Create Link in init.d directory
Use command to create link
    
    sudo ln -snf /usr/local/activemq/bin/activemq /etc/init.d/activemq &&
    sudo update-rc.d activemq defaults
    
### Start Active MQ

    sudo /etc/init.d/activemq start