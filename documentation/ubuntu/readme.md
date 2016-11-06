### Install SSH
    
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

### Sudoer
Make `db` as sudoer in file `/etc/sudoers` at the end of file. `Shift+g` gets you to the end of file.

    mkdir ~/.ssh
    sudo vi /etc/sudoers
    shift+g
    db  ALL=(ALL) NOPASSWD: ALL
    
### Steps to configure machine
    
01. [Java](java.md)
02. [ActiveMQ](activemq.md)
03. [Filebeat](filebeat.md)
04. [Mongo](mongo.md)
05. [Redis](redis.md)
06. [Tomcat](tomcat.md)
    
After making `db` as `sudoer`, `init 0` does not work. It now takes `sudo init 0` for reboot instead.
### Changes to bash_profile
Load `bash_profile` changes using command

    source .bash_profile

#### Check if port is open 
    
    netstat -plntu | grep 61616
    netstat -plntu | grep 61616
    
- Active Internet connections (servers and established)
    
        netstat -atn           # For tcp
        netstat -aun           # For udp
        netstat -atun          # For both

- Active Internet connections (only servers)
    
        netstat -plntu
    
### Ubuntu Server time to UTC
    
    sudo dpkg-reconfigure tzdata    
    
### Set hostname for Mongo replica setup
    
    sudo nano /etc/hosts
    
    192.168.1.30    s1
    192.168.1.31    s2
    192.168.1.32    s3
    
    
    sudo nano /etc/hosts
        
    192.168.1.20    r1
    192.168.1.21    r2
    192.168.1.22    r3
    192.168.1.23    r4
    
### Initiate replica
Mongo console to s1   
 
    mongo --host s2 --port 27017
    
    rs.initiate()
    rs.add("s2")
    rs.add("s3")
    
Mongo check replica status
    
    rs.status()
    
### Created dbftp user on loader
Do not set group for dbftp. It prevent password less access and continous deployment fails.  

    sudo adduser --no-create-home dbftp
    sudo chown dbftp:db /opt/receiptofi/expensofi
    
To delete user
    
    sudo deluser dbftp
    sudo deluser --remove-home dbftp
    
To change password
    
    sudo cat /etc/passwd
    
    
#### Elastic search background
    
    cd /opt/elastic/elasticsearch-5.0.0 &&
    nohup elasticsearch > /dev/null 2>&1 &
         
    cd /opt/logstash/logstash-5.0.0
    nohup logstash -f /etc/logstash.conf > /dev/null 2>&1 &               