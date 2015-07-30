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
                storage:
                  dbPath: /usr/local/var/mongodb
                net:
                  bindIp: 127.0.0.1

- Start Mongo, Check for logs 
- Install Java manual
- [Install ActiveMQ manual] (activemq/readme.md)
- Install Tomcat manual
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
