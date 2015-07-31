## Server Set Up (First Launch)

- Update Mac
- Change to UTC/GMT time 

        sudo ln -sf /usr/share/zoneinfo/UTC /etc/localtime
         
- Install XCode
  - Agree to XCode agreement
- Install HomeBrew
- Install Mongo
  - Update Mongo config
  
          systemLog:
            destination: file
            path: /usr/local/var/log/mongodb/mongo.log
            logAppend: true
            timeStampFormat: iso8601-utc
          storage:
            dbPath: /data/db
          net:
            #bindIp: "192.168.1.67,127.0.0.1" (remove me to connect from other machine)
            port: 27017
          replication:
            oplogSizeMB: 25600
            replSetName: "rs"
          processManagement:
            fork: true


  - Update [Plist] (mongo/mongodb.plist.xml) if required 
  - Start Mongo, Check for logs 
- Install Java manual

        sudo rm /usr/bin/java
        sudo ln -s /Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home/bin/java /usr/bin/java
        sudo rm /Library/Java/Home
        sudo ln -s /Library/Java/JavaVirtualMachines/jdk1.8.0_51.jdk/Contents/Home /Library/Java/Home

- [Install ActiveMQ manual] (activemq/readme.md)
- [Install Tomcat manual] (tomcat/readme.md)
  - Add setenv script and launchd
  - Mark them as excutable
  
- Install Logstash from brew
- Add script `restartActiveMQAndTomcat.sh` to start ActiveMQ and Tomcat

        echo "sudo launchctl unload -w /Library/LaunchDaemons/receiptofi.plist"
        sudo launchctl unload -w /Library/LaunchDaemons/receiptofi.plist
        echo "waiting 20 seconds"
        sleep 20
        echo "sudo launchctl unload -w /Library/LaunchDaemons/activemq.plist"
        sudo launchctl unload -w /Library/LaunchDaemons/activemq.plist
        echo "waiting 5 seconds"
        sleep 5
        echo "sudo launchctl load -w /Library/LaunchDaemons/activemq.plist"
        sudo launchctl load -w /Library/LaunchDaemons/activemq.plist
        echo "waiting 5 seconds"
        sleep 5
        echo "sudo launchctl load -w /Library/LaunchDaemons/receiptofi.plist"
        sudo launchctl load -w /Library/LaunchDaemons/receiptofi.plist
        echo "script executed successfully"
        
- Set /etc/hosts        
