### Mongo Install

Download file for ubuntu. 
 
    `sftp` to `/tmp` latest version of `mongodb-linux-x86_64-ubuntu1604-3.2.10.tgz`
    Create directory `/opt/mongo` as `root` 
    
#### Directory to install    
Inside `/tmp` perform `untar` of `mongodb`

    tar -xvf mongodb-linux-x86_64-ubuntu1604-3.2.10.tgz
    mv /tmp/mongodb-linux-x86_64-ubuntu1604-3.2.10 /opt/mongo/
    
Make directory for `log` and `data`    
    
    sudo mkdir /var/log/mongodb && sudo chown db:db /var/log/mongodb
    sudo mkdir /data && sudo mkdir /data/db && sudo chown db:db /data/db
    
#### Create Link    

    sudo ln -snf /opt/mongo/mongodb-linux-x86_64-ubuntu1604-3.2.10/ /usr/local/mongodb
    
#### Add Path to bashrc
    
Add Path (at the end of file .bashrc)  

    nano ~/.bashrc
    export PATH=/usr/local/mongodb/bin:$PATH
    
#### Add link in /usr/bin
    
    sudo ln -snf /usr/local/mongodb/bin/mongod /usr/bin/mongod
   
#### Mongod.conf create

    sudo touch /etc/mongod.conf && sudo chown db:db /etc/mongod.conf && nano /etc/mongod.conf

#### Copy the content to `mongod.conf` file

    ---
    systemLog:
      destination: file
      path: /var/log/mongodb/mongo.log
      logAppend: true
      timeStampFormat: iso8601-utc
    storage:
      dbPath: /data/db
      directoryPerDB: true
    net:
      #bindIp: 127.0.0.1,192.168.1.15,192.168.1.16,19.168.1.17,192.168.1.18
      port: 27017
    replication:
      oplogSizeMB: 25000
      replSetName: rs
    processManagement:
      fork: false
      
#### Create `mongod.service` file
      
Create mongod.service ( file shown below) in /etc/systemd/system  directory.
    
    sudo touch /etc/systemd/system/mongod.service
    sudo chown db:db /etc/systemd/system/mongod.service
    nano /etc/systemd/system/mongod.service
    
    
#### Copy below code to - mongod.service

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
    
#### Check status 
    
- Check Status of Mongod :


	sudo systemctl status mongod
- Stop Mongod :


    sudo systemctl stop mongod
- Start Mongod :


    sudo systemctl start mongod
    
- Re-Start Mongod :


    sudo systemctl restart mongod    
- Reload the systemd daemon :


    sudo systemctl daemon-reload
- Enable the Service to Start at Boot : This creates a symlink


    sudo systemctl enable mongod    
    