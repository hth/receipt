### Changes to bash_profile
Load `bash_profile` changes using command

    source .bash_profile

### Sudoer
Make `db` as sudoer in file `/etc/sudoers` at the end of file. `Shift+g` gets you to the end of file.

    db  ALL=(ALL) NOPASSWD: ALL
    
After making `db` as `sudoer`, `init 0` does not work. It now takes `sudo init 0` for reboot instead.
    
### SSH
    
    sudo apt-get update
    sudo apt-get install openssh-server
    sudo ufw allow 22
    
### Update Ubuntu
Run all three commands
    
- sudo apt-get update        # Fetches the list of available updates
- sudo apt-get upgrade       # Strictly upgrades the current packages
- sudo apt-get dist-upgrade  # Installs updates (new ones)
- sudo apt autoremove        # To remove installs 

All commands 

    sudo apt-get update && sudo apt-get upgrade && sudo apt-get dist-upgrade && sudo apt autoremove
    
### Install java
    
Download `jdk-8u102-linux-x64.tar.gz`. 

    sftp l4@192.X.X.X
    put javax
    exit
    
    ssh l4@192.X.X.X
    
Installing Java in `/opt/java`

    tar -xvf jdk-8u102-linux-x64.tar.gz
    sudo mkdir /opt/java
    sudo mv jdk1.8.0_102 /opt/java/
    rm jdk-8u102-linux-x64.tar.gz 
    sudo ln -s /opt/java/jdk1.8.0_102 /usr/local/java

Screenshot sample    

    drwxr-xr-x  3 root root 4096 Oct  9 18:55 .
    drwxr-xr-x 23 root root 4096 Oct  9 18:40 ..
    drwxr-xr-x  3 root root 4096 Oct  9 18:56 java
    l4@l4:/opt$ cd java/
    l4@l4:/opt/java$ ls -al
    total 12
    drwxr-xr-x 3 root root 4096 Oct  9 18:56 .
    drwxr-xr-x 3 root root 4096 Oct  9 18:55 ..
    drwxr-xr-x 8 l4   l4   4096 Jun 22 18:56 jdk1.8.0_102
    l4@l4:/opt/java$     

Set JAVA_HOME
 
    JAVA_HOME=/usr/local/java
    export PATH=$JAVA_HOME/bin:$PATH

Source to import environment
    
    source ~/.bash_profile
    java -version

### Activemq

#### Install ActiveMQ

The command below creates `activemq` at `/usr/share/activemq`

    sudo apt-get install activemq 
    
To remove the activemq following command is used:

    sudo apt-get remove activemq
Following command is used to remove the activemq package along with its dependencies:

    sudo apt-get remove --auto-remove activemq
This will remove activemq and all its dependent packages which is no longer needed in the system.

##### Completely removing activemq with all configuration files:

Following command should be used with care as it deletes all the configuration files and data:

    sudo apt-get purge activemq
or you can use following command also:

    sudo apt-get purge --auto-remove activemq
Above command will remove all the configuration files and data associated with activemq package. You can can't recover the delete data, so, use this command with care.
    
    
##### Configure ActiveMQ

http://docs.motechproject.org/en/latest/get_started/installing.html

ActiveMQ needs an enabled instance to run. Use the following command to create a symbolic link from instances-available to instances-enabled.

    sudo ln -s /etc/activemq/instances-available/main /etc/activemq/instances-enabled/main
    sudo sed -e 's/<broker /<broker schedulerSupport="true" /' -i /etc/activemq/instances-enabled/main/activemq.xml

#### Then start ActiveMQ
    sudo service activemq start
    sudo update-rc.d activemq enable
    sudo update-rc.d activemq defaults
    
Check status    
    
    sudo service activemq status
    
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
    
#### Mongo
    
change user and group to `db`. And keep the config file as is.
Create `/data/db` with ownership to db


- Import the public key used by the package management system.

    
    sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927
    
- Create a list file for MongoDB.
 
    
    echo "deb http://repo.mongodb.org/apt/ubuntu xenial/mongodb-org/3.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.2.list

- Reload local package database.
    

    sudo apt-get update
    	
- Install the MongoDB packages.
    
Install the latest stable version of MongoDB.¶

    sudo apt-get install -y mongodb-org

Install a specific release of MongoDB.¶

    sudo apt-get install -y mongodb-org=3.2.10 mongodb-org-server=3.2.10 mongodb-org-shell=3.2.10 mongodb-org-mongos=3.2.10 mongodb-org-tools=3.2.10

Pin a specific version of MongoDB.

    echo "mongodb-org hold" | sudo dpkg --set-selections
    echo "mongodb-org-server hold" | sudo dpkg --set-selections
    echo "mongodb-org-shell hold" | sudo dpkg --set-selections
    echo "mongodb-org-mongos hold" | sudo dpkg --set-selections
    echo "mongodb-org-tools hold" | sudo dpkg --set-selections  
    
- (Ubuntu 16.04-only) Create systemd service file¶
    
Create a new file at `/lib/systemd/system/mongod.service` with the following contents        
    
    [Unit]
    Description=High-performance, schema-free document-oriented database
    After=network.target
    Documentation=https://docs.mongodb.org/manual
    
    [Service]
    User=db
    Group=db
    ExecStart=/usr/bin/mongod --quiet --config /etc/mongod.conf
    
    [Install]
    WantedBy=multi-user.target
    
##### Run MongoDB Community Edition
    
Change group for mongodb
    
    sudo chown -R db:db /var/log/mongodb
    sudo chown -R db:db /var/lib/mongodb
    sudo mkdir /data
    sudo mkdir /data/db
    sudo chown -R db:db /data/db
    
Disable Transparent Huge Pages
    
- 	Create the init.d script.

Create the following file at `/etc/init.d/disable-transparent-hugepages`:
    
        https://docs.mongodb.com/manual/tutorial/transparent-huge-pages/    

-  Make it executable.

        sudo chmod 755 /etc/init.d/disable-transparent-hugepages
        
-  Configure your operating system to run it on boot. 
       
       sudo update-rc.d disable-transparent-hugepages defaults
       
-  Test Your Changes
  
  You can check the status of THP support by issuing the following commands:
  
    cat /sys/kernel/mm/transparent_hugepage/enabled
    cat /sys/kernel/mm/transparent_hugepage/defrag   
        
-  Standalone run 
        
Instead of using `mongo` use following process 
        
    sudo apt install numactl
    numactl --interleave=all mongod start        
              
    
Update Yaml file to match above settings
    
    sudo service mongod start
    systemctl daemon-reload
    systemctl start mongod
    systemctl enable mongod
    netstat -plntu
    sudo service mongod stop
    
### Redis

https://www.globo.tech/learning-center/install-configure-redis-ubuntu-16/
    
Tutorial

Ensure that your installation of Ubuntu 16 is fully up to date with this command.

    apt-get update && apt-get upgrade -y

Install the redis server package, which is included in the base repositories.

    apt-get install redis-server -y

To prevent others from accessing your Redis keystore, you will need to make sure that Redis is bound to your local IP address. This is hands-down the safest security option, and the best one for this particular guide.

First, open the Redis configuration in a text editor.

    nano /etc/redis/redis.conf

Next, search for the line which starts with bind. Change it to this line instead:

    bind 127.0.0.1

Start the Redis server.

    service redis-server start

In order for Redis to start up again when the server is restarted, enable it to start on boot.

    update-rc.d redis-server enable
    update-rc.d redis-server defaults

Redis is installed! You can now enter the first data into your new Redis server.

    root@redis-node:~# redis-cli
    127.0.0.1:6379> set besthost "Globo.Tech"
    OK
    127.0.0.1:6379> get besthost
    "Globo.Tech"    
    
#### Check if port is open 
    
    netstat -plntu | grep 61616
    netstat -plntu | grep 61616
    
- Active Internet connections (servers and established)

    
    netstat -atn           # For tcp
    netstat -aun           # For udp
    netstat -atun          # For both

- Active Internet connections (only servers)

    
    netstat -plntu