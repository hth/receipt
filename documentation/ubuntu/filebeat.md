### Filebeat Install

Download file for ubuntu. 
 
    `sftp` to `/tmp` latest version of `filebeat-5.0.0-linux-x86_64.tar.gz`
    Create directory `/opt/filebeat` as `root` 
    sudo mkdir /opt/filebeat
    
#### Directory to install    
Inside `/tmp` perform `untar` of `mongodb`

    tar -xvf filebeat-5.0.0-linux-x86_64.tar.gz
    sudo mv /tmp/filebeat-5.0.0-linux-x86_64 /opt/filebeat/
    
Make directory for `log` and `data`    
    
    sudo mkdir /var/log/filebeat && sudo chown db:db /var/log/filebeat
    
#### Create Link  

    sudo ln -snf /opt/filebeat/filebeat-5.0.0-linux-x86_64/ /usr/local/filebeat
    
#### Create file `filebeat.conf` 
    
    sudo touch /etc/filebeat.conf && sudo chown db:db /etc/filebeat.conf && nano /etc/filebeat.conf
     
#### Copy the content to `filebeat.conf` file
     
    ---
    filebeat.prospectors:
    - input_type: log
      paths:
        - /var/log/mongodb/mongo.log
        - /var/log/activemq/activemq.log
        - /var/log/activemq/audit.log
      multiline.pattern: ^\[
      multiline.negate: false
      multiline.match: after
    name: s2
    output.logstash:
      hosts: ["192.168.1.13:5044"]
    logging:
       level: info
       to_files: true
       to_syslog: false
       files:
           path: /var/log/filebeat
           name: filebeat.log
           keepfiles: 7
           rotateeverybytes: 10485760
           
After every change in config. Do a system reload and then restart.            
           
#### Create `filebeat.service` file
           
Create filebeat.service (file shown below) in /etc/systemd/system  directory.     
      
    sudo touch /etc/systemd/system/filebeat.service &&
    sudo chown db:db /etc/systemd/system/filebeat.service &&
    nano /etc/systemd/system/filebeat.service
    
#### Copy below code to - filebeat.service  
  
    [Unit]
    Description=Filebeat Service
    After=network.target
    
    [Service]
    ExecStart=/usr/local/filebeat/filebeat -c /etc/filebeat.conf
    
    User=db
    Group=db
    Restart=always
    UMask=0007
    RestartSec=10
    
    [Install]
    WantedBy=multi-user.target
    
#### Check status     
    
- Reload System Demon to get changes in service file


	sudo systemctl daemon-reload
- Start Filebeat Service


	sudo systemctl start filebeat
- Check Status of Service


    sudo systemctl status filebeat
- Stop Service


    sudo systemctl stop filebeat
- Enable the Service to Start at Boot - This create a symlink
 
 
    sudo systemctl enable filebeat
    
Above command does this Example -- Created symlink from /etc/systemd/system/multi-user.target.wants/filebeat.service to /etc/systemd/system/filebeat.service.