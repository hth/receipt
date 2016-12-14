### Mongo Install

Download file for ubuntu. 
 
    `sftp` to `/tmp` latest version of `mongodb-linux-x86_64-ubuntu1604-3.4.0.tgz`
    
#### Directory to install      
    
    sudo mkdir /opt/mongo
    
### Numactl

#### Install
        
    sudo apt install numactl
    
#### Disable Transparent Huge Pages        
    
- Create the init.d script.

    Create the following file at `/etc/init.d/disable-transparent-hugepages`:
    
    
    sudo touch /etc/init.d/disable-transparent-hugepages
    
https://docs.mongodb.com/manual/tutorial/transparent-huge-pages/    

-  Make it executable.

 
    sudo chmod 755 /etc/init.d/disable-transparent-hugepages &&
    sudo nano /etc/init.d/disable-transparent-hugepages 
        
- File content 
        
        
    #!/bin/bash
    ### BEGIN INIT INFO
    # Provides:          disable-transparent-hugepages
    # Required-Start:    $local_fs
    # Required-Stop:
    # X-Start-Before:    mongod mongodb-mms-automation-agent
    # Default-Start:     2 3 4 5
    # Default-Stop:      0 1 6
    # Short-Description: Disable Linux transparent huge pages
    # Description:       Disable Linux transparent huge pages, to improve
    #                    database performance.
    ### END INIT INFO
    
    case $1 in
      start)
        if [ -d /sys/kernel/mm/transparent_hugepage ]; then
          thp_path=/sys/kernel/mm/transparent_hugepage
        elif [ -d /sys/kernel/mm/redhat_transparent_hugepage ]; then
          thp_path=/sys/kernel/mm/redhat_transparent_hugepage
        else
          return 0
        fi
    
        echo 'never' > ${thp_path}/enabled
        echo 'never' > ${thp_path}/defrag
    
        re='^[0-1]+$'
        if [[ $(cat ${thp_path}/khugepaged/defrag) =~ $re ]]
        then
          # RHEL 7
          echo 0  > ${thp_path}/khugepaged/defrag
        else
          # RHEL 6
          echo 'no' > ${thp_path}/khugepaged/defrag
        fi
    
        unset re
        unset thp_path
        ;;
    esac
        
-  Configure your operating system to run it on boot.

       
    sudo update-rc.d disable-transparent-hugepages defaults
       
-  Test Your Changes after reboot


    sudo reboot

You can check the status of THP support by issuing the following commands:
  
    cat /sys/kernel/mm/transparent_hugepage/enabled &&
    cat /sys/kernel/mm/transparent_hugepage/defrag  
         
Response 
         
    always madvise [never]         
    
#### Directory to install    
Inside `/tmp` perform `untar` of `mongodb`

    tar -xvf mongodb-linux-x86_64-ubuntu1604-3.4.0.tgz &&
    sudo mv /tmp/mongodb-linux-x86_64-ubuntu1604-3.4.0 /opt/mongo/
    
Make directory for `log` and `data`    
    
    sudo mkdir /var/log/mongodb && 
    sudo chown db:db /var/log/mongodb &&
    sudo mkdir /data && 
    sudo mkdir /data/db && 
    sudo chown db:db /data/db
    
#### Create Link    

    sudo ln -snf /opt/mongo/mongodb-linux-x86_64-ubuntu1604-3.4.0/ /usr/local/mongodb
    
#### Add Path to bashrc
    
Add Path (at the end of file .bashrc)  

    nano ~/.bashrc
    export PATH=/usr/local/mongodb/bin:$PATH
    
#### Add link in /usr/bin
    
    sudo ln -snf /usr/local/mongodb/bin/mongod /usr/bin/mongod &&
    sudo ln -snf /usr/local/mongodb/bin/mongo /usr/bin/mongo &&
    sudo ln -snf /usr/local/mongodb/bin/mongodump /usr/bin/mongodump &&
    sudo ln -snf /usr/local/mongodb/bin/mongorestore /usr/bin/mongorestore
   
#### Mongod.conf create

    sudo touch /etc/mongod.conf && 
    sudo chown db:db /etc/mongod.conf && 
    nano /etc/mongod.conf

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
      
After every change in config. Do a system reload and then restart.      
      
#### Create `mongod.service` file
      
Create mongod.service ( file shown below) in /etc/systemd/system  directory.
    
    sudo touch /etc/systemd/system/mongod.service && 
    sudo chown db:db /etc/systemd/system/mongod.service && 
    nano /etc/systemd/system/mongod.service
    
#### Copy below code to - mongod.service

    [Unit]
    Description=High-performance, schema-free document-oriented database
    After=network.target
    Documentation=https://docs.mongodb.org/manual
    
    [Service]
    User=db
    Group=db
    ExecStart=/usr/bin/numactl --interleave=all /usr/bin/mongod --quiet --config /etc/mongod.conf
    
    [Install]
    WantedBy=multi-user.target
    
#### Check status 
- Reload the systemd daemon :


        sudo systemctl daemon-reload
- Enable the Service to Start at Boot : This creates a symlink


        sudo systemctl enable mongod 
- Check Status of Mongod :


        sudo systemctl status mongod
- Stop Mongod :


        sudo systemctl stop mongod
- Start Mongod :


        sudo systemctl start mongod    
- Re-Start Mongod :


        sudo systemctl restart mongod       
    